package net.kiral.nodegraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ObservableValue<T> extends Observable {

    private final List<ChangeListener<T>> listenerList = new ArrayList<>();

    private T cache;

    public final T getCache() {
        return this.cache;
    }

    public final void setCache(T value) {
        T old = this.cache;
        this.cache = value;
        this.invalidate(this.cache, old);
    }

    public final boolean addListener(ChangeListener<T> listener) {
        Objects.requireNonNull(listener, "listener");
        if (!this.listenerList.contains(listener)) {
            this.listenerList.add(listener);
            if (!this.isValid()) {
                listener.invoke(this.cache, null);
            }

            listener.addDependency(this);
            return true;
        } else {
            return false;
        }
    }

    public final boolean removeListener(ChangeListener<T> listener) {
        if (this.listenerList.remove(listener)) {
            listener.removeDependency(this);
            return true;
        } else {
            return false;
        }
    }

    public final void invalidate(T newValue, T oldValue) {
        if (this.isValid()) {
            this.onInvalidate();
            this.invalidate();
            listenerList.forEach(observerValue -> observerValue.invoke(newValue, oldValue));
        }
    }


    public abstract T get();
}

