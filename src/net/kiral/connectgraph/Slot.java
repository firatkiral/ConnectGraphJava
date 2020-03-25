package net.kiral.connectgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

//todo: need outgoing list to track of observers
public class Slot<T> extends Observable {

    protected Slot<T> incoming;
    protected Listener listener;
    private final List<ChangeListener<T>> listenerList = new ArrayList<>();

    protected T cache;

    public Slot() {
        this.listener = new Listener() {
            public void invoke() {
                Slot.this.invalidate();
            }
        };
    }

    public Slot(T cache) {
        this();
        Objects.requireNonNull(cache, "cache");
        this.cache = cache;
    }

    public final Slot<T> getIncoming() {
        return this.incoming;
    }

    public final void connectFrom(Slot<T> incoming) {
        Objects.requireNonNull(incoming, "incoming");
        if (incoming != this.incoming) {
            this.disconnect();
            this.incoming = incoming;
            this.incoming.addListener(listener);
            invalidate();
        }
    }

    public final void connectTo(Slot<T> connection) {
        Objects.requireNonNull(connection, "connection");
        connection.connectFrom(this);
    }

    public final void disconnect() {
        if (incoming != null) {
            incoming.removeListener(listener);
            incoming = null;
            //no need to invalidate, cache stays same
        }
    }

    protected void submitTask() {
        if (incoming != null && !incoming.isValid()) {
            incoming.submitTask();
        }
    }

    public void set(T newValue) {
        T old = null;
        if (cache != null) {
            old = cache;
        }
        this.cache = newValue;
        this.invalidate(old);
    }

    public T get() {
        if (!this.isValid()) {
            if (this.incoming != null) {
                this.cache = this.incoming.get();
            }

            this.validate();
            this.onValidate();
        }
        return cache;
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

    protected final void invalidate(T oldValue) {
        if (this.isValid()) {
            this.onInvalidate();
            this.invalidate();
            listenerList.forEach(observerValue -> observerValue.invoke(get(), oldValue));
        }
    }

    public final void dispose() {
        this.clearListeners();
    }

    public final boolean isConnected() {
        return this.incoming != null;
    }
}
