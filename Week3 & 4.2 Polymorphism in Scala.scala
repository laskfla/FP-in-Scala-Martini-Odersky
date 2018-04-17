---------------Polymorphism---------------
Two principal forms of polymorphism:
1.subtyping

2.generics

Method to abstract and compose types.
Type parameterization means that classes as well as methods can now have types as parameters.

Started from List , an immutable linked list and is a fundamental data structure in many functional languages.
It is constructed from two building blocks:
  Nil  : the empty list 
  Cons : a cell containing an element and the reminder of the list . eg,List(1,2,3) ,
            _____
           |c1|p1|
            /   \
           1     \ 
                ____
               |c2|p2|
                /   \
               2     \
                    ____
                   |c3|p3|
                    /   \
                   3     \
                         Nil


trait List[T] //not only Int, but Boolean, Double...
class Cons[T](val head:T,val tail :List[T]) extends List[T]
class Nil[T] extends List[T]
           
Type parameters are written in square brackets, e.g. [T]

Generic Functions:
Like classes, functions can have type parameters.
For instance, here is a function that created a list consisting of a single element.

def singleton[T](elem:T) = new Cons[T](elem,new Nil[T])

Then we can use singleton[Int](1), singleton[Boolean](true), or use singleton(1) or singleton(true) 
as long as Scala  can infer the type parameters from context.

--------Type erasure---
Type parameters do not affect evaluation in Scala.
We can assume that all type parameters and type arguments are removed before evaluating the program.
This is called type erasure(type erased at run-time).
In effect, type parameters are only for the compiler to verify that the program satisfies certain correctness
properties, but they are not relevant for the actual execution.

--------Polymorphism--------
So what we have seen is a form of polymorphism.
Polymorphism means that a function type comes "in many forms".
In programming it means that :
   1. the function can be applied to arguments of many types, (subtyping) or 
   2. the type can have instances of many types(generics).
   
We have seen two principal forms of polymorphism:
   1. subtyping : instance of a subclass can be passed to a base class .
     e.g. val l:List[Int] = new Nil[Int] 
          val l:LIST[Int] = new Cons(1,new Nil[Int])
     
   2. generics : instances of a function or class are created by type parameterization.
     
Subyping comes from OOP  first and generics comes from FP first.


------------------------------------------------------
Week4.2
In this session, we are going to see the interactions of subtyping and generics , the two principal forms
of polymorphism.

Two main areas:
    1. bounds : subject type parameters to subtype constraints.
    2. variance : defines how parameterized types behave under subtyping.
    
Type bounds:
    same issue with Java, the generic class or methods can not operate on the subtypes even 
    it is reasonable. So we need a way to eliminate this restriction .
    Bounds can help .
    def assertAllPos[S <: IntSet])(r:S) S =...
    type parameter S is defined as subtype of IntSet.
Notation:
    1. S <: T means : S is a subtype of T and 
    2. S >: T means : S is a super type of T.
    3. S >: L <: U would restrict S any type on the interval between L and U 
    

------Variance--------
Covariance
     Give NonEmpty <: IntSet 
   is 
    List[NonEmpty] <: List[IntSet] ?
If this is true, we all types for which this relationship holds covariant because their subtying relationship
varies with the type parameter.
For example, the Array in Java is covariant:
    Array[NonEmpty] <: Array[IntSet]
Use case : a function to count the elements of an Array of IntSet,then it should work on Array of NonEmpty
So in this case, what we care about is not relevant to the real type of the element.
Or like the function  sort which work on Array[Object] also works on Array[Int].
But it did cause error if we operate on the element and assign it to different type other then the type 
parameter as Java keep the Array type at runtime and will throw run-time error for this change.

###But in Scala, Array is not co-variant.
You can NOT do assignment like this 
val b :Array[IntSet] = new Array[NonEmpty]

While if there is type assignment or change of the element type, then we need to use contra-variant.

------Liskov principle------
When do we make the generic covariant or contra-variant ?
The following principle, stated by Barbara Liskov, tells us when a type can be subtype of another:
    If A <: B, then everything one can do with a value of type B ,one should also be able to do with 
    a value of A
the formal statement : 
    Let q(x) be a property provable(type sound?) about objects x of type B. Then 
    q(x) should be provable for objects y of type A where A <: B
]

     
    
