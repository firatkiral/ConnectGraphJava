package net.kiral.nodegraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Observable {
    private boolean isValid = true;
    private final List<InvalidationListener> listenerList = new ArrayList<>();

    public Observable() {
        this.invalidate();
    }

    public final boolean isValid() {
        return this.isValid;
    }

    protected final void setValid(boolean val) {
        this.isValid = val;
    }

    public final boolean addListener(InvalidationListener listener) {
        Objects.requireNonNull(listener, "listener");
        if (!this.listenerList.contains(listener)) {
            this.listenerList.add(listener);
            if (!this.isValid) {
                listener.invoke();
            }

            listener.addDependency(this);
            return true;
        } else {
            return false;
        }
    }

    public final boolean removeListener(InvalidationListener listener) {
        if (this.listenerList.remove(listener)) {
            listener.removeDependency(this);
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

    public final void validate() {
        if (!this.isValid) {
            this.isValid = true;
        }
    }

    public final void invalidate() {
        if (this.isValid) {
            this.isValid = false;
            onInvalidate();
            listenerList.forEach(InvalidationListener::invoke);
            GraphManager.invokeStaticListeners();
        }
    }

    public final void onInvalidate() {
    }

    public final void onValidate() {
    }

}
