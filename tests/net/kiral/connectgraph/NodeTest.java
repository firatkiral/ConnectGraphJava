package net.kiral.connectgraph;

class NodeTest {
    public static void main(String[] args) {

        /**
         * Simple computation
         */
        VectorMultiplyNode multiplyNode = new VectorMultiplyNode();

        //lets call get() function to see what happens.
        //All nodes are invalidated when they are created. That means when we call get() soon after it is created
        //it will calculate first output and cache it.
        //and it will print "output computed"
        //Calling get() method will make node valid and wont calculate for next calls, only return cached value
        Vector3 out = multiplyNode.get();

        //Lets see what we got here
        //it will print "Vector3{x=0.0, y=0.0, z=0.0}"
        System.out.println(out);

        //Create our input values here
        Vector3 v1 = new Vector3(1, 1, 1);
        Vector3 v2 = new Vector3(2, 2, 2);
        float multiplier = 2;

        //and set these values
        //as soon as we set any input value, this node will be invalidated again.
        multiplyNode.firstSlot.set(v1);
        //now our node is invalidated and it will calculate the output again when the next time get() method is called.
        //so we can set as many as inputs without doing unnecessary calculation.
        multiplyNode.secondSlot.set(v2);
        multiplyNode.thirdSlot.set(new Float(multiplier));

        //Once we are done with setting inputs we can call get() method and it will calculate new output
        //it will print "output computed"
        out = multiplyNode.get();

        //we can see computed output
        //it will print "Vector3{x=4.0, y=4.0, z=4.0}"
        System.out.println(out);

        //if we call get() again, it wont compute anything
        //because there is no any change on the inputs, it will simply return same cached value
        //it wont print "output computed"
        out = multiplyNode.get();

        //we can see computed output still same
        System.out.println(out);

        /**
         * Creating node chain
         */
        Node<Float> vectorLengthNode = new Node<Float>() {
            public final Slot<Vector3> input = new Slot<>(new Vector3());
            {
                addSlot(input);
            }

            @Override
            public Float computeValue() {
                Vector3 v = input.get();
                Float out = new Float((float) Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z));
                System.out.println("vector length computed");
                return out;
            }
        };

        //Now we connect this node to previous multiply node's third input.
        vectorLengthNode.connectTo(multiplyNode.thirdSlot);

        //or we can do reverse
        multiplyNode.thirdSlot.connectFrom(vectorLengthNode);

        //Getting connected also invalidates node. So calling get() method will trigger all invalidated nodes
        //But when we try to evaluate this line we'll get "java.lang.NullPointerException"
        //Because in the vectorLengthNode we didn't initialize input property and returns null and causes this error
        //out = multiplyNode.get();

        //So we set initial value to input property
        //Since it's anonymous class we can reach its properties by getProperty(int i) method
        vectorLengthNode.getSlot(0).set(new Vector3(1, 2, 3));

        //It will work this time, and calculate new output
        out = multiplyNode.get();

        //We'll see new value is Vector3{x=7.483315, y=7.483315, z=7.483315}
        System.out.println(out);
    }
}