package net.kiral.nodegraph;

public class VectorMultiplyNode extends Node<Vector3> {
    Input<Vector3> firstInput = new Input<>(new Vector3());
    Input<Vector3> secondInput = new Input<>(new Vector3());
    Input<Float> thirdInput = new Input<>(0f);

    VectorMultiplyNode() {
        addInput(firstInput, secondInput, thirdInput);
    }

    @Override
    public Vector3 computeValue() {
        Vector3 v1 = firstInput.get();
        Vector3 v2 = secondInput.get();
        float m = thirdInput.get();

        Vector3 out = new Vector3(v1.x * v2.x * m, v1.y * v2.y * m, v1.z * v2.z * m);
        System.out.println("output computed");
        return out;
    }
}
