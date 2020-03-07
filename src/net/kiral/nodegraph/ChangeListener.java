package net.kiral.nodegraph;

interface ChangeListener<T> {
    void invoke(T newValue, T oldValue);
}
