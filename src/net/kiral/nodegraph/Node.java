package net.kiral.nodegraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Node<T> extends ObservableValue<T> {
    private final List<Observable> observableList = new ArrayList<>();

    private InvalidationListener invalidationListener = (new InvalidationListener() {
        public void invoke() {
            Node.this.invalidate();
        }
    });

    public final void setInvalidationListener(InvalidationListener val) {
        Objects.requireNonNull(val, "<set-?>");
        this.invalidationListener = val;
    }

    public final void bind(Observable... observables) {
        boolean markInvalid = false;
        for (Observable observable : observables) {
            Objects.requireNonNull(observable, "observable");
            if (observable.addListener(invalidationListener)) {
                markInvalid = true;
            }
        }
        if (markInvalid) {
            invalidate();
        }

    }

    public final void unbind(Observable... observables) {
        boolean markInvalid = false;
        for (Observable observable : observables) {
            Objects.requireNonNull(observable, "observable");
            if (observable.removeListener(invalidationListener)) {
                markInvalid = true;
            }
        }

        if (markInvalid) {
            invalidate();
        }
    }

    public final void connectTo(Property<T> connection) {
        Objects.requireNonNull(connection, "connection");
        connection.connectFrom(this);
    }

    protected final void addProperty(Observable... observables) {
        for (Observable observable : observables) {
            Objects.requireNonNull(observable, "observable");
            observableList.add(observable);
            bind(observable);
        }
    }


    public final Observable getProperty(int i) {
        return this.observableList.get(i);
    }


    public final List<Observable> getPropertyList() {
        return this.observableList;
    }


    public T get() {
        if (!this.isValid()) {
            this.setCache(this.computeValue());
            this.validate();
            this.onValidate();
        }
        return this.getCache();
    }


    protected abstract T computeValue();
}
