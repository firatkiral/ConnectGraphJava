package net.kiral.connectgraph;

public interface ChangeListener<T> {
    void invoke(T newValue, T oldValue);
}
