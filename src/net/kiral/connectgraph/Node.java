package net.kiral.connectgraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

//todo: need outgoing list to track of observers
public abstract class Node<T> extends Slot<T> implements Iterable<Slot> {
    private final List<Slot> slotList = new ArrayList<>();

    public final void bind(Slot... slots) {
        boolean markInvalid = false;
        for (Slot slot : slots) {
            Objects.requireNonNull(slot, "slot");
            if (slot.addListener(listener)) {
                markInvalid = true;
            }
        }
        if (markInvalid) {
            invalidate();
        }
    }

    public final void unbind(Slot... slots) {
        boolean markInvalid = false;
        for (Slot slot : slots) {
            Objects.requireNonNull(slot, "slot");
            if (slot.removeListener(listener)) {
                markInvalid = true;
            }
        }

        if (markInvalid) {
            invalidate();
        }
    }

    protected final void addSlot(Slot... slots) {
        for (Slot slot : slots) {
            Objects.requireNonNull(slot, "slot");
            slotList.add(slot);
            bind(slot);
        }
    }

    public final Slot getSlot(int i) {
        return this.slotList.get(i);
    }

    @Override
    public Iterator<Slot> iterator() {
        return slotList.iterator();
    }

    public T get() {
        if (!isValid()) {
            if (this.incoming != null) {
                this.cache = this.incoming.get();
            } else {
                if (GraphManager.isSerialComputing()) {
                    this.cache = this.computeValue();
                } else {
                    submitTask();
                    this.cache = this._computeValue();
                    submitted = false;
                }
            }
            validate();
            onValidate();
        }
        return cache;
    }

    private Callable<T> task = this::computeValue;

    private Future<T> result = null;

    private boolean submitted = false;

    protected synchronized void submitTask() {
        if (!submitted) {
            slotList.forEach(slot -> {
                if (!slot.isValid()) {
                    slot.submitTask();
                }
            });

            result = GraphManager.submitTask(task);
            submitted = true;
        }
    }

    private synchronized T _computeValue() {
        try {
            return result.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract T computeValue();
}
