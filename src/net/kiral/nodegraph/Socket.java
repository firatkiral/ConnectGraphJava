package net.kiral.nodegraph;

import java.util.Objects;

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
        this.set(cache);
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
        T old = getCache();
        setCache(newValue);
        this.invalidate(old);
    }

    public T get() {
        if (!this.isValid()) {
            if (this.incoming != null) {
                this.set(this.incoming.get());
            }

            this.validate();
            this.onValidate();
        }
        return this.getCache();
    }

    public final void dispose() {
        this.clearListeners();
        this.invalidationListener.dispose();
    }

    public final boolean isConnected() {
        return this.incoming != null;
    }
}
