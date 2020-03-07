package net.kiral.nodegraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public abstract class Node<T> extends Socket<T> implements Iterable<Socket> {
    private final List<Socket> socketList = new ArrayList<>();

    public final void bind(Socket... sockets) {
        boolean markInvalid = false;
        for (Socket socket : sockets) {
            Objects.requireNonNull(socket, "socket");
            if (socket.addListener(invalidationListener)) {
                markInvalid = true;
            }
        }
        if (markInvalid) {
            invalidate();
        }
    }

    public final void unbind(Socket... sockets) {
        boolean markInvalid = false;
        for (Socket socket : sockets) {
            Objects.requireNonNull(socket, "socket");
            if (socket.removeListener(invalidationListener)) {
                markInvalid = true;
            }
        }

        if (markInvalid) {
            invalidate();
        }
    }

    protected final void addSocket(Socket... sockets) {
        for (Socket socket : sockets) {
            Objects.requireNonNull(socket, "socket");
            socketList.add(socket);
            bind(socket);
        }
    }

    public final Socket getSocket(int i) {
        return this.socketList.get(i);
    }

    @Override
    public Iterator<Socket> iterator() {
        return socketList.iterator();
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
            socketList.forEach(socket -> {
                if (!socket.isValid()) {
                    socket.submitTask();
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
