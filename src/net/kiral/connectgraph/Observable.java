package net.kiral.connectgraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Observable {
    private boolean valid = true;

    private final List<Listener> listenerList = new ArrayList<>();

    public Observable() {
        this.invalidate();
    }

    public final boolean isValid() {
        return this.valid;
    }

    protected final void setValid(boolean val) {
        this.valid = val;
    }

    public final boolean addListener(Listener listener) {
        Objects.requireNonNull(listener, "listener");
        if (!this.listenerList.contains(listener)) {
            this.listenerList.add(listener);
            if (!this.valid) {
                listener.invoke();
            }
            return true;
        } else {
            return false;
        }
    }

    public final boolean removeListener(Listener listener) {
        if (this.listenerList.remove(listener)) {
            return true;
        } else {
            return false;
        }
    }

    public final void clearListeners() {
        for (int i = 0; i < listenerList.size(); i++) {
            removeListener(listenerList.get(0));
        }
    }

    protected final void validate() {
        if (!this.valid) {
            this.valid = true;
        }
    }

    protected final void invalidate() {
        if (this.valid) {
            this.valid = false;
            onInvalidate();
            listenerList.forEach(Listener::invoke);
            GraphManager.invokeStaticListeners();
        }
    }

    public final void onInvalidate() {
    }

    public final void onValidate() {
    }

}
