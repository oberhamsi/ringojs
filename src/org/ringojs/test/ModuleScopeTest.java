package org.ringojs.test;

import junit.framework.TestCase;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

import org.ringojs.engine.*;
import org.ringojs.repository.FileRepository;
import org.ringojs.repository.Resource;

import java.io.File;

public class ModuleScopeTest extends TestCase {

    public void testScope() throws Exception {
        MockRepository repository = new MockRepository();
        RingoConfig config = new RingoConfig(new FileRepository(new File(".")));
        config.setReloading(true);
        config.addModuleRepository(repository);
        RhinoEngine engine = new RhinoEngine(config, null);
        Context cx = Context.enter();

        MockResource firstScript = new MockResource(
                "first",
                "var second = require('./second'); exports.testFoo = function() { return true; }"
        );
        MockResource secondScript = new MockResource(
                "second",
                "exports.secondFoo = function() {return true};"
        );
        repository.addResource(firstScript);
        repository.addResource(secondScript);

        RingoGlobal globalScope = new RingoGlobal(cx, engine, true);
        Resource scriptResource = repository.getResource("first");
        ModuleScope scope = new ModuleScope(scriptResource.getModuleName(), scriptResource, globalScope, engine.getWorker());
        ReloadableScript script = new ReloadableScript(firstScript, engine);

        Scriptable exports = scope.getExports();
        assertEquals("scope is initially empty", 0, exports.getIds().length);

        script.evaluate(scope, cx, engine.getWorker());
        assertEquals("scope has one export", 1, exports.getIds().length);
        assertTrue("that export is called testFoo", ((NativeObject)exports).containsKey("testFoo"));
        assertFalse("that export is not visible in globalScope", globalScope.has("testFoo", globalScope));
        Context.exit();
    }
}