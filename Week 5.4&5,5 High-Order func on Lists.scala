#######Recurring patterns on List we have seen so far ############
    > transform each element and get another list.
    
    > retrieving a list of all elements satisfying a criterion. (like take, drop)
    
    > combining the elements of a list using an operator. (like sum)
    
In this session, we are going to abstract those patterns and write general functions for those patterns.
 
The first pattern could be generalized using map function.
   abstract class List[T] {
    def map[U](f:T => U) : List[U] = this match {
        case Nil => this
        case y :: ys => f(y) :: ys.map(f) 
        }
   }

The second pattern :
    def filter(f:T => Boolean ) : List[T] = this match {
        case Nil => this
        case y :: ys => if(f(y)) y :: ys.filter(f) 
                        else ys.filter(f)
   }
   
Other variations what extract sublists based on predicate:
    xs filterNot p 
    xs partition p 
    xs takeWhile p 
    xs dropWhile p 
    xs span p //same as (xs takeWhile p,xs dropWhile p ) but 
              //computed in a single traversal of the list xs.
   
########Exercise1########
Write a function pack that packs consecutive duplicates
of list elements into sublists. For instance:
        pack(List("a","a","a","b","c","c","a")) 
should give 
       List(List("a","a","a"),List("b"),List("c","c","c"),List("a"))

//first attempt :
//think of the solution like iterative programming.
//iterative the list from first element and compare with the consecutive element, 
//if match , then compose them together as a list , otherwise make the first element as new list.

  def pack[String](xs:List[String]) : List[List[String]] = xs match  {
    case Nil => List()
    case x :: x1 => x1 match  {
      case Nil => List(List(x))
      //while when we see we have to check the x1 structure before making decisions , then we should know we 
      //have to do the same thing for ys. So this algorithm does not work.
      //Actually, what you need to implement is the span function.
      case y :: ys => if (x == y) List(List(x),List(y))++ pack(ys)
      else List(List(x),List(y)) ++ pack(ys)
    }
  }
  pack(List("a","a","a","b") //List(List("a","a"),List("a"),List("b"))
   

  
//second attempt : 
//think of the solution with Functional programming:
//in recursion, 1) assume the pack function already exists and works well.
//2)in each step, we will only take care of the last element of the list .
//write the logic between the head element with the pack(tail)( thinking recursively, do not try to control the whole process flow!).
//recursion is the induction step in mathematics actually, perfectly matched. (base case  + recursive call according to relation between the 
//current problem and sub-problem. Also making progress to base base)
//List is processed actually from right to left, while imperative programing process list from left to right.
  def pack[T](xs:List[T]) : List[List[T]] = xs match  {
    case Nil => List()
    //this piece of code resemble the span function. While the library use a mutable ListBuffer[T] (could append element)
    //for better performance.
    case x :: x1 => if(pack(x1).isEmpty) List(List(x)) //calling pack(x1).Empty would make it return only after have traversed the last element.
    else { pack(x1).head match  {
      case Nil => List(List(x))
      case y :: ys => if (x == y ) List(x :: pack(x1).head) ++ pack(x1).tail
      else List(x) :: pack(x1)
    }
    }
  }
    
//Third attempt
//The problem here is to extract sub-lists from the list while the element equals to the expected value.
//We can still process the list from left to right and will be much concise and readable.
//So, after you get the point of recursion, we need to think higher and learn to use those library functions to make your code 
//concise and faster.


def pack[T](xs:List[T]) : List[List[T]] = xs match  {
    case Nil => List()
    case x :: ys =>
        val (first , rest ) = xs span (y => y == x )
        first :: pack(rest)
   }

########Exercise2########
Using pack, write a encode function called run-time length encoding which is to encode consecutive duplicate data with a single element plus the duplicate
number.

//most work of encode has been done with pack, after that we need to iterate the list and for each list
//and map that to a pair of (T, Int). Obviously, we could use the map function.
    def encode(xs:List[T]) : List[(T,Int)] = 
      //for empty list case of pack, map function would return empty as well, so it is good to go with below code
       pack(xs).map (y => (y.head, y.length))
  

######Reduce & Fold combinators #######
The third recurring pattern is to combine the elements of list with an operator, that is called reduce or fold.
//as we defined in the before sessions for reduce :(this is actually the foldRight as it need a zero for empty)
  def reduce[T,U](xs:List[T])(f:(T,U) => U )(zero:U) : U = xs match  {
    case Nil => zero
    case x :: x1 => f(x, reduce(x1)(f)(zero))
  }

 reduceLeft   //reduce from left hand of a list and throw error if called on empty List.
 sum could be expressed as  : def sum(xs:List[Int]) = xs reduceLeft(0::xs)(_ + _) //much compact and readable
 product could be expressed as  : def product(xs:List[Int]) = xs reduceLeft(1::xs)(_ * _) //much compact and readable
 
 foldLeft //a more general form or reduceLeft which takes an accumulator , z , as an additional parameter, which is returned when
          //foldLeft is called on an empty list. reduceLeft = tail.foldLeft(head) (f)         
  
 (List(e1,e2,...en) foldLeft z)(op) = ((...(z op e1)op)....) op en.
 
 Similarly , 
 foldRight   :  (List(e1,e2,...en) foldRight z)(op) = e1 op (op(....(en op z ))) //z is on right hand
 reduceRight :  (List(e1,e2,...en) foldRight z)(op) = e1 op (op(....(en-1 op en ))) 
 
 
  abstract class List {
   def foldLeft[U](z: U)(op : (U,T) => U) : U = this match {
        case Nil => z 
        case x :: xs => xs.foldLeft(op(z,x))(op)
   }
  }
  
-------Difference between foldLeft vs foldRight-----------
For operations that are associative and commutative, foldLeft and foldRight are equivalent .
Sometimes, only one of the two operations is appropriate. like the concat operation, which is foldRight and can not be replaced with 
foldLeft 

  def concat(xs:List[T],ys:List[T]) : List[T] = {
   ( xs foldRight ys ) ( _ :: _ ) //what a concise definition using combinator style !
   //this can not be replaced by foldLeft as you will get type error (you are doing ys :: T and :: does not defined on T)
  }

  
