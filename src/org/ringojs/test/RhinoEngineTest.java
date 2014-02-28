package org.ringojs.test;

import org.ringojs.engine.*;
import org.ringojs.repository.*;

import java.io.*;
import java.util.concurrent.ExecutionException;
import org.mozilla.javascript.*;

import junit.framework.TestCase;

public class RhinoEngineTest extends TestCase {

    private RhinoEngine engine;
    private MockRepository repository;

    public void setUp() throws Exception {
        repository = new MockRepository();
        RingoConfig config = new RingoConfig(new FileRepository(new File(".")));
        config.setReloading(true);
        config.addModuleRepository(repository);
        engine = new RhinoEngine(config, null);
    }


    public void testInvoke() throws InterruptedException, NoSuchMethodException, ExecutionException, IOException {

        // simple function call
        MockResource simpleResource = new MockResource(
                "simple",
                "var testFoo = function() {return 'fooresult';}"
        );
        repository.addResource(simpleResource);
        String result = (String) engine.invoke("simple", "testFoo");
        assertEquals(result, "fooresult");


        // script does not exist
        try {
            engine.invoke("doesnotexist", "doesNotMatter");
            fail("Expected FileNotFoundException");
        } catch(FileNotFoundException e) {
            assertEquals("Resource \"doesnotexist\" not found or not readable", e.getMessage());
        }

        // function does not exist
        try {
            engine.invoke("simple", "doesNotExist");
            fail("Excpected NoSuchMethodException");
        } catch (NoSuchMethodException e) {
            assertEquals("Function doesNotExist not defined", e.getMessage());
        }

        // compile time error in script
        MockResource compileErrorScript = new MockResource(
                "compileerror",
                "var syntax!! error??"
        );
        repository.addResource(compileErrorScript );
        try {
            engine.invoke("compileerror", "doesNotMatter");
            fail("Expected SyntaxError");
        } catch (EcmaError e) {
            assertEquals("SyntaxError: missing ; before statement", e.getMessage());
        }

        // runtime error in script
        MockResource runtimeErrorScript = new MockResource(
            "runtimeerror",
            "var foo = function() { throw 'my error';}"
        );
        repository.addResource(runtimeErrorScript);
        try {
            engine.invoke("runtimeerror", "foo");
            fail("Expected RuntimeError");
        } catch (JavaScriptException e) {
            assertEquals("my error", e.getMessage());
        }

    }

    public void testScriptReload() throws InterruptedException, NoSuchMethodException, ExecutionException, IOException {

        // first requires second; first calls function in second
        MockResource firstScript = new MockResource(
                "first",
                "var second = require('./second'); var foo = function() { return second.bar() }"
        );
        MockResource secondScript = new MockResource(
                "second",
                "exports.bar = function() {return 'barresult'};"
        );
        repository.addResource(firstScript);
        repository.addResource(secondScript);
        String result = (String) engine.invoke("first", "foo");
        assertEquals("barresult", result);

        // change the second scripts
        secondScript.setContent("exports.bar = function() {return 'otherbarresult'};");
        result = (String) engine.invoke("first", "foo");
        assertEquals("we see the new result", "otherbarresult", result);

        // disable reload and change secondScript again
        engine.getMainWorker().setReloading(false);
        secondScript.setContent("exports.bar = function() { return 'yetanotherresult'};");
        result = (String) engine.invoke("first", "foo");
        assertEquals("with disabled reloading we see the old result", "otherbarresult", result);

        // enable reloading so the change above in secondScript becomes visible
        engine.getMainWorker().setReloading(true);
        result = (String) engine.invoke("first", "foo");
        assertEquals("new result is now visible", "yetanotherresult", result);
    }
}
