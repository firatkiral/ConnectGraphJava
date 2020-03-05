package net.kiral.nodegraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
                this.set(this.incoming.get());
            } else {
                if (GraphManager.isSerialComputing()) {
                    set(this.computeValue());
                } else {
                    inputList.forEach(input -> {
                        if (input.getIncoming() instanceof Node) {
                            if (!input.incoming.isValid()) {
                                ((Node) input.incoming).submitTask();
                            }
                        }
                    });
                    if (!submitted) {
                        submitTask();
                    }
                    set(this._computeValue());
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

    private synchronized void submitTask() {
        if (!submitted) {
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
