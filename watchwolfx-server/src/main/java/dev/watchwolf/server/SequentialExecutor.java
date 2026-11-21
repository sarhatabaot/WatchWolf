package dev.watchwolf.server;

import java.util.concurrent.Callable;

public interface SequentialExecutor {
    public static interface ThrowableRunnable {
        void run() throws Exception;
    }

    public void run(ThrowableRunnable run);
}
