package net.kiral.nodegraph;

import java.util.Objects;

//todo: need outgoing list to track of observers
public class Socket<T> extends ObservableValue<T> {

    protected Socket<T> incoming;
    protected InvalidationListener invalidationListener;

    public Socket() {
        this.invalidationListener = new InvalidationListener() {
            public void invoke() {
                Socket.this.invalidate();
            }
        };
    }

    public Socket(T cache) {
        this();
        Objects.requireNonNull(cache, "cache");
        this.cache = cache;
    }

    public final Socket<T> getIncoming() {
        return this.incoming;
    }

    public final void connectFrom(Socket<T> incoming) {
        Objects.requireNonNull(incoming, "incoming");
        if (incoming != this.incoming) {
            this.disconnect();
            this.incoming = incoming;
            this.incoming.addListener(invalidationListener);
            invalidate();
        }
    }

    public final void connectTo(Socket<T> connection) {
        Objects.requireNonNull(connection, "connection");
        connection.connectFrom(this);
    }

    public final void disconnect() {
        if (incoming != null) {
            incoming.removeListener(invalidationListener);
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
        T old = cache;
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

    public final void dispose() {
        this.clearListeners();
    }

    public final boolean isConnected() {
        return this.incoming != null;
    }
}
