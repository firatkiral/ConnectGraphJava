package net.kiral.connectgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ObservableValue<T> extends Observable {

    private final List<ChangeListener<T>> listenerList = new ArrayList<>();

    protected T cache;

    public final T getCache() {
        return this.cache;
    }

    public final void setCache(T value) {
        this.cache = value;
    }

    public final boolean addListener(ChangeListener<T> listener) {
        Objects.requireNonNull(listener, "listener");
        if (!this.listenerList.contains(listener)) {
            this.listenerList.add(listener);
            if (!this.isValid()) {
                listener.invoke(this.cache, this.cache);
            }
            return true;
        } else {
            return false;
        }
    }

    public final boolean removeListener(ChangeListener<T> listener) {
        if (this.listenerList.remove(listener)) {
            return true;
        } else {
            return false;
        }
    }

    public final void invalidate( T oldValue) {
        if (this.isValid()) {
            this.onInvalidate();
            this.invalidate();
            listenerList.forEach(observerValue -> observerValue.invoke(get(), oldValue));
        }
    }


    public abstract T get();
}

