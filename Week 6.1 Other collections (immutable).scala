#####Other sequences(immutable)########
Element access in List is linear to the element position in the list,access to the first element is much
faster than access to the middle or end of a list.

The Scala library also defines an alternative sequence implementation, Vector.
This one has more evenly balanced access patterns than List.
Essentially, it is represented as a shallow trees.

    > elements up to 32 elements is essentially stored in an array.
          
    > elements more than 32 make the tress span to 32 small sub-trees and each subtree has 32 elements.
      the tree will span so on .
      32 elements in root node, then 32 subtrees each has 32 elements , and then 32 * 32 subtrees with each has 32 elements.
      So we get elements count as 32(2^5), 32*32(2^10) , 32*32*32(2^15),...
1)Element access time is log32(N),
 
2)and the bulk operator on vector run faster and in chunk of 32 array elements which may in single cache line.
  e.g map, filter, etc
  
3)while if your algorithm matches well with the recursive data structure (get head + tail) of list, and size is not large.
  list is a better option.
  
The support operations is the same as list, except the "::"  (is used for pattern matching of list and is a constructor of new list)
instead of x :: xs. there is 
      x +: xs   Create a new vector with leading element x, followed by all elements of xs.
      
      xs +: x   Create a new vector with trailing element x, preceded by all elements of xs.
(Note that the ":" always point to the sequence)
      
                     
Arrays and Strings support the same operation as Seq and can implicitly be converted to sequences where needed.
(They can not be subclasses of Seq because they come from Java)              
                                   Iterable
                                /       |     \          
                          Seq         Set    Map
                       /  |   \    
      (String|Array)List Vector Range 
                    
           val xs = Array(1,2,3)
            xs map ( x => x*2) filter (x >3)
            
            
----------Range----------
Range represents a sequence of evenly spaced integers.
Three operators :
    to (include), until (exclusive), by (to determine step value)
    
    val r : Range = 1 to 10 by 3 // 1 4 7 10
    val s : Range = 1 to 5 //1,2,3,4,5
    annoymous ranges : 1 to 10 by  3   , 6 to 1 by -2 , etc 
Range a represents as single object with three fields : lower bound,
upper bound and step value .
   
-----Other operations on Seq ----
xs exists p  //true if there is elements in xs that make p holds
xs forall p  //true if p(x) holds for all elements of xs.
xs zip ys    // a sequence of pairs drawn from corresponding elements of sequences of xs and ys .
  e.g. List(1,2,3) zip "abcd" // List( (1,'a') ,(2,'b'),(3,'c')) 
xs flatMap f //applied a collection -valued function f to all elements of xs and concatenates the results.
             //flatMap is the combination of map + flatten (fold operator)
xs.sum //the sum of elements for numeric collection 
xs.product //the product ....
xs.max/min //the max or min in the collection, the elements much be comparable .


---Pattern matching for pairs ---
Generally , the function value 
    { case p1 => e1 , ..pn => en }
is equivalent to :
    x => x match { case p1 =>e1 ...pn => en }    
But it is much shorter and readable .

###Exercise###
define a isPrime to test if n is a prime number (do not consider the performance)

def isPrime(n:Int) = (2 until n) forall ( x => n % x != 0 )

for the number 1 to n, for each value x, find the prime numbers of the combinations of 
 the number 1 to x + x.

  def findPrime(n:Int) : Seq[(Int,Int)] = {
    ((1 to 10) flatMap (x => (1 to x) map (y => (y, x)))) filter { case (m, n) => isPrime(m + n) }
  }

######6.2 For expressions ####
High order functions such as map, flatMap or filter provide powerful constructs for manipulating lists.
But sometimes the level of abstraction required by these functions make the program difficult to understand.
In this case, Scala's for expression notation can help.

Example :
     case class Person(name:String, age:Int )
     
 To obtain the names of persons over 20 years old, you can write :
    for (p <- persons if p.age >20) yield p.name //readable and easy to understand
  is equivalent to :
    persons filter (p => p.age >20) map ( p => p.name)
 
 The for expression is similar to loops in imperative languages, except that it builds(yield) a list of the results of all the iterations.
 
 Syntax of for :
     for (s) yield e  //or f { s } yield s , then s could be in multiple lines without requiring semicolons.
  
 where s is a sequence of generators and filters  and e is an expression whose value is returned by an iteration.
 
  > A generator is of the form p <- e, where p is a pattern and e an expression whose value is a collection.
  > A filter is of the form if f where f is a boolean expression for each iteration value.
  > The sequence must start with a generator.
  > If there are several generators in the sequence, the last generators vary faster than the first.

 
 Let us rewrite the findPrime with for expression:
     for {
      i <- (1 until n )
      j <- (2 until i )
      if isPrime( x + y )
     } yield (x , y )
  
#####Exercise #######
Write a version of scalarProduct that makes use of a for :
     def scalarProduct(xs :List[Int],ys: List[Int]) : Int = {
        for {
            ( x ,y ) <- xs zip ys  
            
        } yield x * y
     }.sum 

 
    
    
   
