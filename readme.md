Lazy Evaluated Node Graph
==========================
This library allows apps to do heavy chained computations by using lazy evaluation.

The graph is consist of hierarchical node trees. Each node has some inputs and one output. Each
node can be connected to another node's input. When any node's output gets called, it will calculate
all parent tree all the way up and once all parent node outputs calculated, it will always return cached 
value until any input changes. When the any input gets changed, it will only update node chain on the 
invalidated nodes and its child branches.

Getting Started:
------------------
Lets say we have a Vector3 class:
```java
public class Vector3 {
    public float x;
    public float y;
    public float z;

    public Vector3(){}

    public Vector3(float x, float y, float z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "Vector3{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
```

And our application does some vec3 math calculations such as multiply. Lets take a
look at our multiply node example:
```java
public class VectorMultiplyNode extends Node<Vector3> {
    Input<Vector3> firstInput = new Input<>(new Vector3());
    Input<Vector3> secondInput = new Input<>(new Vector3());
    Input<Float> thirdInput = new Input<>(0f);

    VectorMultiplyNode(){
        addProperty(firstInput, secondInput, thirdInput);
    }

    @Override
    protected Vector3 computeValue() {
        Vector3 v1 = firstInput.get();
        Vector3 v2 = secondInput.get();
        float m = thirdInput.get();

        Vector3 out = new Vector3(v1.x * v2.x * m, v1.y * v2.y * m, v1.z * v2.z * m);
        System.out.println("output computed");
        return out;
    }
}
```

We need to extend Node class with any template class to make graph properly work. We will use the "Vector3" class as 
template in our case. By adding this we are telling node what type of output will be calculated.
```java
public class VectorMultiplyNode extends Node<Vector3> {
```

As we can see there are three different inputs named firstInput, secondInput and third input. Inputs
can have different templates. In our case will use two vector and one float input.
We will only use these for calculate output.
```java
Input<Float> firstInput = new Input<>(new Vector3());
Input<Vector3> secondInput = new Input<>(new Vector3());
Input<Float> thirdInput = new Input<>(0f);
```

These are the node's input properties that we want node always to keep track of if these properties are changed. To do that
we have to tell node that these are the properties needs to be tracked by adding this lines to constructor:
```java
VectorMultiplyNode(){
    addProperty(firstInput, secondInput, thirdInput);
}
```


Here is the place where magic happens. We can do any computation here by using input propertes.
```java
@Override
protected Vector3 computeValue() {
    Vector3 v1 = firstInput.get();
    Vector3 v2 = secondInput.get();
    float m = thirdInput.get();

    Vector3 out = new Vector3(v1.x * v2.x * m, v1.y * v2.y * m, v1.z * v2.z * m);
    System.out.println("output computed");
    return out;
}
```

Lets continue with main method and start using graph by creating a multiply node.
```java
public static void main(String[] args) {
    VectorMultiplyNode vNode = new VectorMultiplyNode();
}
```

Lets call get() function to see what happens. All nodes are invalidated when they are created. 
That means when we call get() soon after it is created it will calculate first output and cache it.
Calling get() method will make the node valid and wont calculate for next calls, 
only return cached value for new get() calls.
```java
Vector3 out = vNode.get();
```

Lets see what we got here. This will print "Vector3{x=0.0, y=0.0, z=0.0}"
```java
System.out.println(out);
```

Continue with creating our new input values.
```java
Vector3 v1 = new Vector3(1,1,1);
Vector3 v2 = new Vector3(2,2,2);
float multiplier = 2;
```

And set following values. As soon as we set any input value, this node will be invalidated again.
Our node will be invalidated as soon as we change any input and it will calculate the output again when the next time get() method is called.
```java
vNode.firstInput.set(v1);
```

We can set as many as inputs, It wont do any calculation until we call get() method.
```java
vNode.secondInput.set(v2);
vNode.thirdInput.set(multiplier);
```

Once we are done with setting inputs we can call get() method and it will calculate new output.
It will print "output computed"
```java
out = vNode.get();
```

Lets see what we got here again. This will print "Vector3{x=4.0, y=4.0, z=4.0}"
```java
System.out.println(out);
```

If we call get() again, it wont compute anything because there is no any change on the inputs, it will simply return same cached value
It wont print "output computed"
```java
out = vNode.get();
```

We can see computed output still same
```java
System.out.println(out);
```

Creating Node Chain:
-------------------
Lets start creating another node calculates length of a vector
This time we will use anonymous class instead.
```java
Node<Float> vectorLengthNode = new Node<Float>() {
    public final Input<Vector3> input = new Input<>();
    {
        addProperty(input);
    }
    @Override
    protected Float computeValue() {
        Vector3 v = input.get();
        float out = (float) Math.sqrt(v.x * v.x + v.y * v.y + v.z * v.z);
        System.out.println("vector length computed");
        return out;
    }
};
```
Now we connect this node to previous multiply node's third input.
```java
vectorLengthNode.connectTo(vNode.thirdInput);
```

or we can do reverse,
```java
vNode.thirdInput.connectFrom(vectorLengthNode);
```

Getting connected also invalidates node. So calling get() method will trigger all invalidated nodes
But when we try to evaluate this line we'll get "java.lang.NullPointerException"
Because in the vectorLengthNode we didn't initialize input input and returns null and causes this error
```java
out = vNode.get();
```

So we set initial value to input input
Since it's anonymous class we can reach its properties by getProperty(int i) method
```java
vectorLengthNode.getProperty(0).set(new Vector3(1,2,3));
```

It will work this time, and calculate new output
```java
out = vNode.get();
```

We'll see new value is Vector3{x=7.483315, y=7.483315, z=7.483315}
```java
System.out.println(out);
```