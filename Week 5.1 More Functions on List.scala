#######More functions on List#####

 Sublists and accessors:
    val x = List(1,2,3,4,5,6) 
    x.last : 6 //the last element in a LIst, exception if List is empty
    x.init : List(1,2,3,4,5) //A list consisting all elements except the last one 
    x take 2 : List(1,2)//Taking the first n elements 
    x drop 2 : List(3,4,5,6) //The rest of the collection after taking n elements
    x(4)   : 5 //the element indexed at 4
    
  Creating new Lists :
    xs ++ ys //concate lists 
    xs.reverse // lists in reversed order 
    xs updated (n,x) //same list as xs, except at index n where it contains x 
    ys ::: xs //prepend ys to xs ,same as xs.:::(ys)
    
  Finding elemtns :
    xs indexOf x //the index of the first element that equal to x 
    xs contains x //same as xs indexOf x >0 
  
  
-----Implementations --------
Define them as external functions
 //notice the scala match style 

  def last[T](xs:List[T]) : T = xs match  {
      case List() => throw new Error("empty.last")
      case x :: Nil => x
      
      //这里y :: ys case实际包含x::Nil 的，while ,for single element case ,
      //while as pattern match in sequence, so single element match the x :: Nil.
      
      case y :: ys  => last(ys)
    }
  
  def init[T](xs:List[T]) : List[T] = xs match  {
     case List() => throw new Error("empty.init")
     case List(x) => List()
     case y1 :: ys => y1 :: init(ys)
   }
  
  // ++ depends on the elements of xs 
  def ++[T](xs: List[T],ys:List[T]) : List[T] = xs match {
       case List() => ys
       case z :: zs => z :: ++(zs, ys)
     }
 
 //no append in List, so have to use list concatenation 
 //notice the scala match style
 //the complexity here would be quadratic. 
    def reverse[T](xs:List[T]) : List[T] = xs match {
      case List() => List()
      case y :: ys =>reverse(ys) ++ List(y)
    }
    
  def removeAt[T](n:Int, xs:List[T]) :List[T] = {
    if(n >xs.length) xs else {
      xs match {
        case List() => List()
        case y :: ys => if(n==0) ys else y :: removeAt(n-1,ys)
      }
    }
  }
  or use the take/drop : 
   def removeAt[T](n:Int,xs:List[T]):List[T] = (xs take n) ::: (xs drop n+1)

   
##########Tuples and Pairs############
