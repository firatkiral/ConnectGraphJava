package net.kiral.nodegraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class ChangeListener<T> {
    private final List<ObservableValue<T>> dependencyList = new ArrayList<>();

    public final boolean addDependency(ObservableValue<T> observableValue) {
        Objects.requireNonNull(observableValue, "observableValue");
        if (!this.dependencyList.contains(observableValue)) {
            this.dependencyList.add(observableValue);
            return true;
        } else {
            return false;
        }
    }

    public final boolean removeDependency(ObservableValue<T> observableValue) {
        Objects.requireNonNull(observableValue, "observableValue");
        return this.dependencyList.remove(observableValue);
    }

    public final void dispose() {
        this.dependencyList.forEach(e -> e.removeListener(this));
    }

    public abstract void invoke(T newValue, T oldValue);
}
