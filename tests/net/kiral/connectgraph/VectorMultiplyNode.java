package net.kiral.connectgraph;

public class VectorMultiplyNode extends Node<Vector3> {
    Slot<Vector3> firstSlot = new Slot<>(new Vector3());
    Slot<Vector3> secondSlot = new Slot<>(new Vector3());
    Slot<Float> thirdSlot = new Slot<>(0f);

    VectorMultiplyNode() {
        addSocket(firstSlot, secondSlot, thirdSlot);
    }

    @Override
    public Vector3 computeValue() {
        Vector3 v1 = firstSlot.get();
        Vector3 v2 = secondSlot.get();
        float m = thirdSlot.get();

        Vector3 out = new Vector3(v1.x * v2.x * m, v1.y * v2.y * m, v1.z * v2.z * m);
        System.out.println("output computed");
        return out;
    }
}
