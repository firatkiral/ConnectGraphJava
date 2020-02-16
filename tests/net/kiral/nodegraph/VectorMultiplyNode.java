package net.kiral.nodegraph;

public class VectorMultiplyNode extends Node<Vector3> {
    Property<Vector3> firstInput = new Property<>(new Vector3());
    Property<Vector3> secondInput = new Property<>(new Vector3());

    VectorMultiplyNode(){
        addProperty(firstInput, secondInput);
    }

    @Override
    protected Vector3 computeValue() {
        Vector3 v1 = firstInput.get();
        Vector3 v2 = secondInput.get();

        Vector3 out = new Vector3(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
        System.out.println("output computed");
        return out;
    }
}
