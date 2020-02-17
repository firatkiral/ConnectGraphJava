package net.kiral.nodegraph;

import java.util.Objects;

public class Property<T> extends ObservableValue<T> {

    protected Property<T> incoming;
    protected InvalidationListener invalidationListener;

    public Property() {
        this.invalidationListener = new InvalidationListener() {
            public void invoke() {
                Property.this.invalidate();
            }
        };
    }

    public Property(T cache) {
        this();
        Objects.requireNonNull(cache, "cache");
        this.setCache(cache);
    }

    public final Property<T> getIncoming() {
        return this.incoming;
    }

    public final void setIncoming( Property<T> val) {
        this.incoming = val;
    }

    public final void connectFrom(Property<T> incoming) {
        Objects.requireNonNull(incoming, "incoming");
        if (incoming != this.incoming) {
            this.disconnect();
            this.incoming = incoming;
            this.incoming.addListener(invalidationListener);
            invalidate();
        }
    }

    public final void connectTo(Property<T> connection) {
        Objects.requireNonNull(connection, "connection");
        connection.connectFrom(this);
    }

    public final void disconnect() {
        if(incoming != null){
            incoming.removeListener(invalidationListener);
            incoming = null;
            //no need to invalidate, cache stays same
        }
    }

    public void set(T newValue) {
        this.setCache(newValue);
    }


    public T get() {
        if (!this.isValid()) {
            if (this.incoming != null) {
                this.setCache(this.incoming.get());
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
