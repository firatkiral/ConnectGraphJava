package net.kiral.nodegraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public abstract class Node<T> extends Property<T> implements Iterable<Property> {
    private final List<Property> inputList = new ArrayList<>();

    public final void bind(Property... properties) {
        boolean markInvalid = false;
        for (Property property : properties) {
            Objects.requireNonNull(property, "property");
            if (property.addListener(invalidationListener)) {
                markInvalid = true;
            }
        }
        if (markInvalid) {
            invalidate();
        }
    }

    public final void unbind(Property... properties) {
        boolean markInvalid = false;
        for (Property property : properties) {
            Objects.requireNonNull(property, "property");
            if (property.removeListener(invalidationListener)) {
                markInvalid = true;
            }
        }

        if (markInvalid) {
            invalidate();
        }
    }

    protected final void addProperty(Property... properties) {
        for (Property property : properties) {
            Objects.requireNonNull(property, "property");
            inputList.add(property);
            bind(property);
        }
    }

    public final Property getProperty(int i) {
        return this.inputList.get(i);
    }

    @Override
    public Iterator<Property> iterator() {
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
