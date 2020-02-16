package net.kiral.nodegraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class InvalidationListener {
    private final List<Observable> dependencyList = new ArrayList<>();

    public final boolean addDependency(Observable observable) {
        Objects.requireNonNull(observable, "observable");
        if (!this.dependencyList.contains(observable)) {
            this.dependencyList.add(observable);
            return true;
        } else {
            return false;
        }
    }

    public final boolean removeDependency(Observable observable) {
        Objects.requireNonNull(observable, "observable");
        return this.dependencyList.remove(observable);
    }

    public final void dispose() {
        this.dependencyList.forEach(e -> e.removeListener(this));
    }

    public abstract void invoke();
}
