package common.extensions;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.time.Duration;

public class ThreadTimingExtencion implements BeforeEachCallback, AfterEachCallback {
    private static final ExtensionContext.Namespace NS = ExtensionContext.Namespace.create(ThreadTimingExtencion.class);
    private static final String START = "start";
    @Override
    public  void beforeEach(ExtensionContext context) throws Exception {
        context.getStore(NS).put(START, System.nanoTime());

    }

    @Override
    public  void afterEach(ExtensionContext context) throws Exception {
        long duration = System.nanoTime() - context.getStore(NS).get(START,Long.class);
        Duration d = Duration.ofNanos(duration);
        System.out.println("ThreadName: " +Thread.currentThread().getName()+ " TestName " +context.getRequiredTestMethod().getName() +"duration: " +d.toMillis() +" mc" );

    }

}
