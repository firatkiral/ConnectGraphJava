package net.kiral.connectgraph;

interface ChangeListener<T> {
    void invoke(T newValue, T oldValue);
}
