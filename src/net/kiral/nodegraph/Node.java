package net.kiral.nodegraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public abstract class Node<T> extends Input<T> implements Iterable<Input> {
    private final List<Input> inputList = new ArrayList<>();

    public final void bind(Input... inputs) {
        boolean markInvalid = false;
        for (Input input : inputs) {
            Objects.requireNonNull(input, "input");
            if (input.addListener(invalidationListener)) {
                markInvalid = true;
            }
        }
        if (markInvalid) {
            invalidate();
        }
    }

    public final void unbind(Input... inputs) {
        boolean markInvalid = false;
        for (Input input : inputs) {
            Objects.requireNonNull(input, "input");
            if (input.removeListener(invalidationListener)) {
                markInvalid = true;
            }
        }

        if (markInvalid) {
            invalidate();
        }
    }

    protected final void addInput(Input... inputs) {
        for (Input input : inputs) {
            Objects.requireNonNull(input, "input");
            inputList.add(input);
            bind(input);
        }
    }

    public final Input getInput(int i) {
        return this.inputList.get(i);
    }

    @Override
    public Iterator<Input> iterator() {
        return inputList.iterator();
    }

    public T get() {
        if (!isValid()) {
            if (this.incoming != null) {
                this.setCache(this.incoming.get());
            } else {
                if (GraphManager.isSerialComputing()) {
                    setCache(this.computeValue());
                } else {
                    submitTask();
                    setCache(this._computeValue());
                    submitted = false;
                }
            }
            validate();
            onValidate();
        }
        return getCache();
    }

    private Callable<T> task = () -> {
        return computeValue();
    };

    private Future<T> result = null;

    private boolean submitted = false;

    protected void submitTask() {
        if (!submitted) {
            inputList.forEach(input -> {
                if (!input.isValid()) {
                    input.submitTask();
                }
            });

            result = GraphManager.submitTask(task);
            submitted = true;
        }
    }

    private synchronized T _computeValue() {
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract T computeValue();
}
