package net.kiral.nodegraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class Node<T> extends Input<T> implements Iterable<Input> {
    private final List<Input> inputList = new ArrayList<>();

    public final void bind(Input... properties) {
        boolean markInvalid = false;
        for (Input input : properties) {
            Objects.requireNonNull(input, "property");
            if (input.addListener(invalidationListener)) {
                markInvalid = true;
            }
        }
        if (markInvalid) {
            invalidate();
        }
    }

    public final void unbind(Input... properties) {
        boolean markInvalid = false;
        for (Input input : properties) {
            Objects.requireNonNull(input, "property");
            if (input.removeListener(invalidationListener)) {
                markInvalid = true;
            }
        }

        if (markInvalid) {
            invalidate();
        }
    }

    protected final void addProperty(Input... properties) {
        for (Input input : properties) {
            Objects.requireNonNull(input, "property");
            inputList.add(input);
            bind(input);
        }
    }

    public final Input getProperty(int i) {
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
                setCache(this.computeValue());
            }
            validate();
            onValidate();
        }
        return getCache();
    }

    protected abstract T computeValue();
}
