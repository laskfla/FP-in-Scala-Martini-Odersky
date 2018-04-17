/*##Delayed evaluation####

((100 to 1000) filter isPrime)(1) //take the second prime number between 100 and 1000.
it will calculate the full prime list between 100 and 1000, while we only use the first 2 of them.
which is much bad in performance.

However, we can make the short-code efficient y using a trick :
    Avoid computing the tail of a sequence until it is needed for the 
    evaluation result (which might be never)

The idea is implemented in a new class, the Stream.
Streams are similar to lists, but their tail is evaluated only on demand.

#####Defining Stream ####3
Streams are defined from a constant Stream.empty and a constructor Stream.cons.
*/
//For instance.
  val x = Stream.cons (1, Stream.cons(2,Stream.empty))

//They can also be defined like the other collections by using the object Stream as a factory  
  Stream(1,2,3)
  
//The toStream method on a collection will turn the collection into a Stream:
  (1 to 1000).toStream // Stream(1,?) , the Stream structure is similar to List, but the tail is not evaluated.

//#####Stream ranges#######
Let us try to write a function that return (lo until hi).toStream directly :
    def streamRange(lo:Int, hi:Int) : Stream[Int] = 
        if (lo >= hi) Stream.empty
        else Stream.cons(lo,streamRange(lo+1,hi))
    
    def listRange(lo:Int,hi:Int) : List[Int] =
        if (lo>=hi) Nil 
        else lo :: listRange(lo+1,hi)
        
It will not create the whole Stream range as the listRange may do, and it will create a structure like this :
         ----       
        /   \
      1      ?   //the head is 1 and the tail part is not evaluated yet .
      
//####Methods on Stream#####
It basically follow the same operator as List does , except for the "cons" operator .
x :: xs always produces a list, never a stream.
There is however an alternative operator #:: which produces a stream.
     x #:: xs == Stream.cons(x, xs )
#:: can be used in expressions as well as patterns.

//#####Implementation of Stream######
The implementation is quite similar to List.
Concrete implementation of streams are defined in the Stream companion object. Here is the 
first draft.

    object Stream  {       
        //cons return a Stream[T] object with the tl object encapsulated in it .
        //tl or tail must not be evaluated in the cons body, otherwise it would not a "lazy" 
        
        def cons[T](hd:T,tl: => Stream[T]) = new Stream[T] {
            def isEmpty = false 
            def head = hd  //even using "def",while the value is evaluated in initialization.
            def tail = tl  //notice the "def" keyword, not "val" .
                           //if "val",even tl is pass by name, then it would be evaluated when assigned.
                                                          
        }
          
        val empty = new Stream[Nothing] {
            def isEmpty = true 
            def head = throw new NoSuchElementException("empty.head")
            def tail = throw new NoSuchElementException("empty.tail")
        }
    }
    
The first time the tl is dereferenced when called on tail method. 


//#####Lazy evaluation#######
Roughly Laziness means do things as late as possible and never to it twice.
Stream is good, but if we evaluate the tail multiple times, the correponding 
stream will be recomputed each time .

This problem can be avoided by storing the result of the first evaluation of tail and 
re-using the stored result instead of recomputing tail.

This optimization is sound, since in a purely FP, and expression produces the same result 
each time it is evaluated. (while if there is side effect, or a not purely FP, then 
the result may be unexpected)

We call this schema "Lazy evaluation"(as opposed to by-name evaluation in the case where 
everything is recomputed and strict evaluation for normal parameters and "val" definitions.)

//###Lazy evaluation in Scala###
Lazy evaluation is so attractive that Haskell is a FP that use lazy evaluation by default.
Why Scala does not do that ?
while there are two problems about the lazy evaluation, it is quite unpredictable in when the 
computation happens and how much space it would take. In a pure FP, it does not matter when 
the computation happens. But once you add mutable side effects like IO or mutable structure, then 
it matters.
 
Scala uses strict evaluation by default, but allows lazy evaluation of value definition with the 
lazy val form :
    lazy val x = exp 
    
/*[Keith] comment 
The call-by-name is actually implemented by pass an object reference to the function  .
So, when the parameter is references, it is just calling the method of the object 
with the method body defined just the same as its definition.

The lazy evaluation solve the "multiple evaluation" problem when the call-by-name argument is 
evaluated multiple times when called/referenced multiple times.
It actually store the value the first time it is referenced, similar to the java singleton pattern(lazy 
initialization mode) 
*/

//using a lazy value for tail , the Stream.cons can be implemented more efficiently :
  def cons[T](hd:T, tl : => Stream[T]) = new Stream[T] {
    def head = hd 
    lazy val tail = tl 
    ...
  }
/*[Keith Comment]: that is the reason why spark is fast in transformation and it is lazy evaluation.
  Also, the RDD calculation is only triggered by an "action", so the result is purely depends on the 
  the action , so the RDD can not be reused. If configured, the intermediate RDD could be cached.
  As Professor M recorded, lazy evaluation and eliminated the intermediate data objects, but it also 
  own cons : it is unpredictable about when the calculation happens (in spark it is fine as it is only 
  triggered by an "action") and can not determine how much space it would take in each call .  
*/

###Seeing it in Action###

//Let's observe the execution trace of the expression using the substitution model :
//    (streamRange(1000,10000) filter isPrime ) apply (1)
//and we could assume the filter and apply is defined as below :
    def filter[T]( f: T =>Boolean) : Stream[T] = {
        this match {
            case Stream.emtpy => Stream.empty 
            case x #:: xs if f(x) => x #:: xs filter f 
            case _ => this.tail filter f 
        }
    }

    def apply (x:Int)  = if (x==0) head else this.tail.apply(x-1)

    def streamRange(lo:Int, hi:Int) : Stream[Int] = 
        if (lo >= hi) Stream.empty
        else Stream.cons(lo,streamRange(lo + 1,hi)) //recursive structure 

 
//let us reducing the expression 
    streamRange(1000,10000).filter(isPrime).apply(1)
    
    --> if ( 1000 >= 10000) empty 
        else cons(1000,streamRange(1000 + 1, 10000) ) 
        .filter(isPrime).apply(1)
     
    -->cons(1000,streamRange(1000+1,10000)).filter(isPrime).apply(1)
      //abbreviate the cons(1000,streamRange(1000+1,10000) as C1 and so on 
    -->C1.filter(isPrime).apply(1)
    
    -->(if(C1.isEmpty) C1   //expanding filter 
        else if ((isPrime(C1.head)) cons(C1.head, C1.tail.filter(isPrime))
        else C1.tail.filter(isPrime))
        .apply(1)
    
    --> C1.tail.filter(isPrime).apply(1) //evaluating if 
    
    --> streamRange(1001,10000).filter(isPrime).apply(1)
    
    the evaluation continues like this until :
    
    --> streamRange(1009,10000).filter(isPrime).apply(1)
    
    --> cons(1009,streamRange(1000+1,10000)).filter(isPrime).apply(1)
    
    --> C9.filter(isPrime).apply(1)
     
    --> ((isPrime(C9.head)) cons(C9.head, C9.tail.filter(isPrime))).apply(1)
    /*the filter pushed to tail and now we get another stream without a filter operation.
      so now, we could proceed to evaluate the apply function.
      as we need to take the second element, so we proceed to process the apply function 
      on the tail Stream : C9.tail.filter(isPrime).apply(0)
      So now it is clear how the lazy evaluation helps eliminate the intermediate result :
      the final function call (should be an "action") will trigger the whole evaluation chain.
      each element is send to the next function whenever the current function call is done.
      (filter returned when it met the first prime number and send to next function for evaluation.
       when apply does not meet, it will trigger the following evaluation to start on Stream.tail.
       So if there is another function on the output of apply, see f, then if f does not return,
       it will trigger the whole evaluation chain to repeat.     
       That is same logic for Stream in Java8 and should be same for RDD in spark as well. 
       So the reason why streamRange is more efficient than listRange is that : it is emitting 
       element per the final "action" 's requirement, no more.
              
       )
    */
    -->  cons(C9.head, C9.tail.filter(isPrime))).apply(1) 
    --> C9.tail.filter(isPrime).apply(0)
    ...
    -->cons(C13.head, C13.tail.filter(isPrime))).apply(0)
    -->C13.head  //1013 

So it is convinced that streamRange did never beyond the second prime number !

/*Keith Comment 
I just came across the reason why it is not allowed to insert & select from the same table in spark.
The reason is that the RDD from the same location of HDFS is just immutable.
And to support fault-tolerant, the underlying source of RDD should not be changed in RDD.
As we may have multiple stages and the RDD may be cached or re-computed due to node failure ,
It is required that the underlying data of RDD should not be changed.
While for hive, the reason is that it does not use cache anyway as each step is actually a 
map-reduce job and no way to cache the data.
So it is safe that we could insert into the same table as it is a atomic operation and goes well 
with hive. (even we did the same thing in spark, but the RDD of the select query may be reused ).

Advantage of RDD is also the disadvantage (no free lunch !). 
The immutability of RDD could not support the parameter update in ML on the fly ,
and need to reset the whole thing and re-run the process with different parameter and 
can not adjust the parameter values during the computation.  
Check the Angel project of Tencent for more details.

*/
#######7.4 Continue with laziness######
One nice aspect of laziness, is that is makes it easy to deal with infinite amout.


For instance, the stream of all integers :
/*notice the recursive function call of "from" here in which it does not define a base case and 
  it does not toward to the base case in each iteration. 
  This is because, from(n+1) is a call-by-name of cons function and it will not be evaluated 
  when passed to cons . In cons body, the from(n+1) is assigned to a "def" tail and it will NOT 
  trigger the evaluation of from as well.
  While, whenever tail is referenced within an expression, it may be evaluated once and 
  we get a head which is call-by-value and another Stream with a call-by-name tail object.
  
  Learn the way to construct an infinite stream.
*/
   
    def from(n:Int) : Stream[Int] = n #:: from(n+1)
    
    //the stream of all natural numbers 
    val nats = from(0)
    
    //the stream of all multiplier of 4 
    val nats = from(0) map (_*4)
    
    val maps = (nats take 10).asList //will trigger the evaluation .
  

/*The Sieve of Eratosthenes 
  An ancient technique to calculate prime numbers.
  
  > Start with all integers from 2, the first prime number 
  
  > Eliminate all multipliers of 2 
  
  > The first element of the resulting list is 3, a prime number 
  
  >Eliminate all multipliers of 3 
  
  > Iterate forever. At each step, the first number in the list is a 
    prime number and we eliminate all its multipliers.
    
  So now let us define the sieve method :
*/

//it is also a "recursive" but lazy , so it is fine 
//so actually we could say the infinite stream is actually a lazy recursive function call.

     def sieve(n:Int) : Stream[Int] = {
      //println(n)
      if(isPrime(n)) n #:: (sieve(n+1) filter (_ % n != 0))
      else sieve(n+1).filter (_ % n != 0)
    }

    println(sieve(2).take(100).toList)
    
  
/*The square roots 
Our previous algorithm for square roots always used a isGoodEnough test to tell when to 
terminate the iteration.

With streams we can now express the concept of converging sequence without having to worrying about
when to terminate it :
*/
    def sqrtStream(x:Double) : Stream[Double] = {
        def improve(guess:Double) = (guess + x/guess)/2
        lazy val guesses : Stream[Double] = 1 #:: (guesses map improve)
        guesses 
    }
    sqrtStream(2).take(3).toList 
    
/*[Keith Comment] : the key point to understand this "lazy" recursion is remember that 
  "lazy" means only evaluate when accessed . So when the expression :
    lazy val guesses : Stream[Double] = 1 #:: (guesses map improve) 
   is evaluated, the "guesses" on the right side is actually not checked , just take it as 
   a black box (the question mark ?). So it is type checked on both side and no error.
   (BTW,I struggled at this point for a few hours :) )   
   The "lazy val" means : the "val" is for hold a value and "lazy" means initialize as lazy as possible. 
   
    The recursion call here works as below :
    Take C is shorthand for : (guesses map improve).
    
    sqrtStream(2).take(4).toList  
   -->  guesses.take(4).toList 
   -->  guesses.head :: guesses.tail.take(3)
   -->  (1 #:: C).head :: guesses.tail.take(3)
   -->   1  :: C.take(3)
   -->   1  :: ((1 #:: C) map improve).head :: ((1 #:: C) map improve ).tail.take(2) //evaluating C 
   -->   1  :: (improve(1) #:: C map improve).head :: (C map improve ) take (2) //evaluating map 
   -->   1  :: improve(1)   :: (C map improve ) take (2)
   -->   1  :: improve(1)   :: ((1 #:: C) map improve ) take (2)
   -->   1  :: improve(1)   :: (improve(1) #:: (C map improve)) take(1)
   -->   1  :: improve(1)   :: improve(improve(1)) :: ( C map improve) take (1)
   -->   1  :: improve(1)   :: improve(improve(1)) :: (((1 #:: C) map improve) map improve) take(1) //evaluating C 
   ... 
   -->   1  :: improve(1)   :: improve(improve(1)) :: improve(improve(improve(1))) 

  While if above is true, I would concern about the performance. When I add a "println" in improve,
  I did not see the multiple evaluation on the same value of 1 .
  And I go to SO for help to find out how it is implemented in Scala.
  When I typed the question , SO gives me a few similar question. I did find one exactly the one I am raising.
  and fortunately, Will Ness, the FP gunu gave a more detailed answer and I realized I miss one important 
  characteristics of Lazy val :  memorization.(Notice the similar question is about Haskell and in Haskell by default 
  every val is a lazy val, but it still applied to Scala.)
  
      sqrtStream(2).take(4).toList      
   -->  guesses.take(4).toList 
   -->  (1 #:: C).head :: guesses.tail.take(3)
   -->   1  :: C.take(3) 
   -->   1  :: ((1 #:: C) map improve).head :: ((1 #:: C) map improve ).tail.take(2) // evaluating C 
          //tail is evaluated as tail_1 = guesses map improve = C 
          #*then evaluated as    tail_1 = improve(1) #:: tail_1 map improve*#
          //let us name it as    tail_1 = head_1     #:: tail_2  
          //head_1 is a value and tail_2 is uninitialized at this moment.
          
   -->   1  :: head_1 ::  tail_2 take (2) // evaluating map 
   -->   1  :: head_1 :: (tail_1 map improve) take (2) //evaluating tail_2 
   -->   1  :: head_1 :: ((head_1 #:: tail_2) map improve) take (2) //tail_1 is a value now 
   
   -->   1  :: head_1 :: (improve(head_1) #:: (tail_2 map improve)) take(2)
          #*so the tail_2 is evaluated as improve(head_1) #:: tail_2 map improve *#
          //let us name it as    tail_2 = head_2          #:: tail_3 
          //and we see the head_1 is reused as it is a value 
   -->   1  :: head_1 :: head_2 :: tail_3 take (1)
   
   -->   1  :: head_1 :: head_2 :: (tail_2 map improve) take(1) //evaluating tail_3 
   -->   1  :: head_1 :: head_2 :: (head_2 #:: tail_3) map improve) take(1) //tail_2 is a value now 
   ... 
   -->   1  :: head_1 :: head_2 :: improve(head_2)  
   
   So we see that when Scala evaluate this infinite stream with "lazy val", the lazy val will be 
   evaluated once and then memorized. The recursion on lazy val would like above case.
   The recursion MUST be transferred to tail element and then continue in each tail element evaluation.
   
   As we see here, the recursion on lazy val actually happened as the normal recursive as we start on 
   base case and then proceed to the final problem. But it writes on the reverse side .
   Recursion usually means we write code from f(n) to f(1)(base case) , while with lazy val ,
   We could write like f(1) to f(n) which is more intuitive. 
*/  
        
