package net.kiral.nodegraph;

class NodeTest {
    public static void main(String[] args) {

        /**
         * Simple computation
         */
        VectorMultiplyNode vNode = new VectorMultiplyNode();

        Vector3 v1 = new Vector3(1,1,1);
        Vector3 v2 = new Vector3(2,2,2);

        vNode.firstInput.set(v1);
        vNode.secondInput.set(v2);

        //if we call get() method it will calculate output
        //it will print "output computed"
        Vector3 out = vNode.get();

        //we can see computed output
        //it will print "Vector3{x=2.0, y=2.0, z=2.0}"
        System.out.println(out);

        //if we call get() again, it wont compute anything
        //because there is no any change on the inputs, it will simply return same cached value
        //it wont print "output computed"
        out = vNode.get();

        //we can see computed output still same
        System.out.println(out);

        //lets change one of first input
        //as soon as we set new input node will be invalidated
        //but wont compute anything until get() method get called (here is the lazy part)
        //benefit of lazy evaluation is if we have lots of inputs and heavy computation,
        //it will prevent from doing heavy computation each time we assign desired inputs
        //and it will wait until we call get() method
        vNode.firstInput.set(new Vector3(3,3,3));

        //lets change second input
        vNode.secondInput.set(new Vector3(4,4,4));

        //now it will compute output again and will print "output computed"
        out = vNode.get();

        //check if its computed
        //it will print "Vector3{x=12.0, y=12.0, z=12.0}"
        System.out.println(out);

        /**
         * Creating node chain
         */

    }
}