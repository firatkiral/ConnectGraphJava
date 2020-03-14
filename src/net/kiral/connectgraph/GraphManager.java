package net.kiral.connectgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GraphManager {
    private static ExecutorService executor = Executors.newCachedThreadPool();

    private static void terminateExecutor() {
        executor.shutdown();
    }

    public static Future submitTask(Callable task) {
        return executor.submit(task);
    }

    //if we need a global tracker to get notified when any of observable in the app gets invalidated
    private static final List<Listener> staticListenerList = new ArrayList<>();

    public static boolean addStaticListener(Listener listener) {
        Objects.requireNonNull(listener, "listener");
        if (!staticListenerList.contains(listener)) {
            staticListenerList.add(listener);
            return true;
        } else {
            return false;
        }
    }

    public static boolean removeStaticListener(Listener listener) {
        return staticListenerList.remove(listener);
    }

    private static void clearStaticListeners() {
        for (int i = 0; i < staticListenerList.size(); i++) {
            removeStaticListener(staticListenerList.get(0));
        }
    }

    protected static void invokeStaticListeners() {
        staticListenerList.forEach(l->l.invoke());
    }

    public static void terminateGraph() {
        clearStaticListeners();
        terminateExecutor();
    }

    public static final int SERIAL_COMPUTING = 0;
    public static final int PARALLEL_COMPUTING = 1;

    private static int evaluationMode = 0;

    public static void setEvaluationMode(int mode) {
        if (mode < 0 || mode > 1) {
            return;
        }
        evaluationMode = mode;
    }

    public static boolean isSerialComputing() {
        return evaluationMode == 0;
    }

    public static boolean isParallelComputing() {
        return evaluationMode == 1;
    }
}
