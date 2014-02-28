package org.ringojs.test;

import org.ringojs.engine.RhinoEngine;
import org.ringojs.engine.RingoConfig;
import org.ringojs.engine.RingoWorker;
import org.ringojs.repository.FileRepository;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;


public class RingoWorkerTest extends TestCase {
    private MockRepository repository;
    private RingoWorker worker;

    public void setUp() throws Exception {
        repository = new MockRepository();
        RingoConfig config = new RingoConfig(new FileRepository(new File(".")));
        config.setReloading(true);
        config.addModuleRepository(repository);
        RhinoEngine engine = new RhinoEngine(config, null);
        worker = engine.getWorker();
    }


    public void testSubmit() throws Exception {
        MockResource myScriptResource = new MockResource(
                "mymodule",
                "var myFn = function() {return 'my result';};"
        );
        repository.addResource(myScriptResource);
        Future result = worker.submit("mymodule", "myFn");
        try {
            String jsResult = (String) result.get();
            assertEquals("my result", jsResult);
        } catch (InterruptedException e) {
            fail("worker submit was interrupted");
        }
        assertTrue(result.isDone());
        assertFalse(result.isCancelled());
        assertFalse("worker is done", worker.isActive());
    }

    public void testSchedule() throws Exception {
        CountDownLatch signal = new CountDownLatch(1);
        MockResource delayedScript= new MockResource(
                "delayscript",
                "var myFn = function(signal) { signal.countDown(); java.lang.Thread.sleep(100);return 'delay result';}"
        );
        repository.addResource(delayedScript);
        Future result = worker.schedule(100, "delayscript", "myFn", signal);
        assertEquals(1, worker.countScheduledTasks());
        signal.await();
        // this is brittle: after signal fires the JS code sleep(100)
        // to give us time to check for isActive
        assertTrue(worker.isActive());
        try {
            String jsResult = (String) result.get(1000, TimeUnit.MILLISECONDS);
            assertEquals("delay result", jsResult);
        } catch (InterruptedException e) {
            fail("worker schedule took too long");
        }
        assertTrue(result.isDone());
        assertFalse(worker.isActive());
        assertEquals(0, worker.countScheduledTasks());
    }
}
