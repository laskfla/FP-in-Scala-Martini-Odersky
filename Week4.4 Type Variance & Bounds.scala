###Variance Definition#####
Variance : how subtyping relates to genericity.

Roughly speaking, a type that accepts mutations of its elements should not be 
covariant.
But immutable types can be covariant, if some conditions on methods are met.
(e.g contra-variant on arguments and covariant on return types).

Definition of variance:
Say C[T] is a parameterized type and A, B are types such that A <: B.
In general, there are three possible relationships between C[A] and C[B] :

C[A] <: C[B] then C is covariant 
C[A] >: C[B] then C is contravariant 
Neither C[A] or C[B] is a subypte of the other then C is nonvariant.

-----Note:Liskov substitution principle-----
Substitutability is a principle in object-oriented programming stating that, 
in a computer program, if S is a subtype of T, 
then objects of type T may be replaced with objects of type S 
(i.e. an object of type T may be substituted with any object of a subtype S) 
without altering any of the desirable properties of T 
(correctness, task performed, etc.).


Scala use declaration-site varince by annotating type paratmers with :
class C[+A] {...} --covariant
class C[-A] {...} --contravariant
class C[A] {...} --nonvariant


###Typing rules for Functions#####
Generally, we have the following rules for subypting between functions types:
If A2 <: A1 and B1 <:B2, then 
    A1 => B1 <: A2 => B2 
i.e argument should be contra-variant and return type is covariant.
So this leads to the following definition of Function1 trait :
        trait Function1[-T, +U] {
            def apply(x:T) : U 
        }
For example, if client expect a function of : String => AnyRef 
then, then you could pass a function like : AnyRef => String 

####Scala variance checks######
So in Scala, the compiler will check that there are no problematic combinations when 
compiling a class with variance annotations.

**Roughly**:
  > covariant type parameters can only appear in method results.
  > contravariant type paramters can only appear in method parameters.
  > nonvariant type paramter can appear anywhere.

Example, in List library:
Nil is a singleton object : case object Nil extends List[Nothing]
Without variance, we have to define each Nil[T] for any List[T].
Now, as Nothing is the bottom type and List is covariant, it type checks
to declare a singleton object Nil which is the subtype of List[T] for any type T 
(As Nil is an object, so it can only be right value, i.e., not a type)

####Bounds for type parameters#####
Considering adding a prepend method to List which prepends a given element,
yielding a new List.
A first attempt would be :
    trait  List[+T]  {
        def prepend(ele:T) : List[T] = new Cons(ele, this)
    }
While it does not work.
Although List is immutable , it does not have the issue like Array met when
using covariant.
But it is still does not work. the compiler throws error:
"Covariant type T occurs in contravariant position in type T of value ele."

The RCA:
Take this class hierarchy as an example:
          IntSet
         /      \
        /        \
       NonEmpty  Empty
val x:List[IntSet] ;
val y:List[NonEmpty] ;
x=y;
x.prepend(new Empty) 
it does not type check as the left hand expects a List[NonEmpty] but get
a List with one element actually does not conform to type NonEmpty.
Take a look at how this could happen : just due to that we did not 
follow a type tree mode, and make NonEmpty , Empty assignable.

How to fix that : to prevent this issue, we need a bound for ele:

def prepend[U >:T](ele:U ) : List[U] = new Cons(ele,this)

Now it type checks. The left hand is List[U], 
on the right side, ele is type U and "this" is type List[T].
The result type is List[U] as U is super type of T.
So it type checks.

Rules:
    >covariant type parameters may appear in lower bound of method type parameter.
    >contra-variant type parameters may appear in upper bound of method type parameter.

                A
               / \
              /   \
             B     C
            /       \
           /         \
          D           E 
         /             \
        /               \
       K                 G
   For example, 
   If type parameter of class F is covariant,
   then to prevent the case like above(D->B->A and then assign C/E to A), the type parameter of method M 
   must have the lower bound T .
   Besides that, according to the type inference rule :
   def f(xs:List[NonEmpty],x:Empty) = xs prepend x ,the result type is List[IntSet].
   The RCA is that Empty is not super type of NonEmpty,the inferer
   will try to find the smallest super type of Empty and NonEmpty.
   it is IntSet in this case. so we take Empty as IntSet.
   and then x will be super type of NonEmpty. 
   (note that when we cast Empty as IntSet, we lose the 
    customized methods/fields of Empty, no free lunch).   
   
   
   If type parameter of class F is contra-variant,
   then we use [U <: T] in type parameter.
   In this case , it actually good to declare prepend like this :
   trait List[-T] {
      def prepend(ele:T ):List[T] = new Cons(ele,this)
   }
   
   For example:
   val x:List[B] = new Cons(new B, Nil)
   val y:List[D] = x
   x.prepend(new K) //it type checks
   (for prepend, the left side is List[B] , on the right side,
    if we add element D to List[B](the underlying type),
    then the right side would be List[B] as D is subtype of B.
    So it type checks.
    But it may not be expected/flexible as the result type is determined
    by the initial type parameter of List.
    Sometimes, when we add D, we expect as List[D].
    So we could define prepend like this :
    
      trait List[-T] {
      def prepend[U<:T](ele:U ):List[U] = new Cons(ele,this)
   }
        
   
   
           
    
    







