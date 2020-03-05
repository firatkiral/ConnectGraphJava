package net.kiral.nodegraph;

public class VectorMultiplyNode extends Node<Vector3> {
    Socket<Vector3> firstSocket = new Socket<>(new Vector3());
    Socket<Vector3> secondSocket = new Socket<>(new Vector3());
    Socket<Float> thirdSocket = new Socket<>(0f);

    VectorMultiplyNode() {
        addInput(firstSocket, secondSocket, thirdSocket);
    }

    @Override
    public Vector3 computeValue() {
        Vector3 v1 = firstSocket.get();
        Vector3 v2 = secondSocket.get();
        float m = thirdSocket.get();

        Vector3 out = new Vector3(v1.x * v2.x * m, v1.y * v2.y * m, v1.z * v2.z * m);
        System.out.println("output computed");
        return out;
    }
}
