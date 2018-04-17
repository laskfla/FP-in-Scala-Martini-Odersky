######Paris and Tuples#####
Pair is a shorthand of Tuple2 
//case class auto add apply/unapply , it can be seen as a simply way to combine the class and companion object . 
//conventionally, there is single constructor in case class (that is the name "case" come from).
//You did could define multiple constructors, 
//while,PM (pattern matching) won't work in that way.

	case class Tuple2[+T1,+T2] (_1: T1,_2: T2) {
		override def toString = "("+_1+","+_2+")"
		 def swap: Tuple2[T2,T1] = Tuple2(_2, _1)
   }	
 
There could be 3 elements, 4 elements , etc . used in pattern matching.


Pairs or Tuple make the pattern matching on more than 1 variables simpler .
Take the mergeSort as an example :
//类似方法的套路都是先定义一个接受input的函数，再定义problem solving中的private function。

With single pattern matching :
     def mergeSort(xs:List[Int], ys:List[Int]) :List[Int] = {
       xs match {
         case List() => ys
         case x :: x1 =>
           {
             ys match {
               case List() =>List(x)
               case y::y1 => {
                 if (x<y) x :: mergeSort(x1,ys)
                 else y :: mergeSort(xs,y1)
               }
             }
           }
       }
     }
//pattern matching; whenever you see boilerplate code.
//there is a chance to make it simple and clean 
With Pair or Tuple : clearly, it is much compact and clean.
        def merge(xs:List[T],ys:List[T]) :List[T]  ={
          (xs,ys) match {
            case (Nil,ys) => ys
            case (xs,Nil) => xs
            case (x::xl, y::yl) =>
              if(x<y) x :: merge(xl,ys)
              else {
                y :: merge(xs,yl)
              }
          }
        }
 
#####Implicit######
/*When use it, you should follow the convention and best practices.
  each constructor has its own field and its sole purpose is to make 
  program simple or short .
  implicit is to make mergeSort generalized without passing the 
  compare functions each time (copy the reference each time). 
*/
    def msort[T](xs:List[T])(lt:(T,T) => Boolean ) :List[T]  = {
      val n = xs.length/2
      if( n == 0) xs
      else {
        def merge(xs:List[T],ys:List[T]) :List[T]  ={
          (xs,ys) match {
            case (Nil,ys) => ys
            case (xs,Nil) => xs
            case (x::xl, y::yl) =>
              if(lt(x,y)) x :: merge(xl,ys)
              else {
                y :: merge(xs,yl)
              }
          }
        }
        val (fst,snd) = xs splitAt n
        merge(msort(fst)(lt),msort(snd)(lt))
      }
    }
/*
  Above lt funtion used 4 times,but only the first 2 are useful.
  if we have many calls to msort, then you have to copy it each time.
  
  Scala has the Ordering library for common types.
  By making this lt parameter implicit, you do not need to 
  fill in the lt every time and the compiler will search for the
  proper values.
*/    
    def msort[T](xs:List[T])(implicit Ordering[T]) :List[T]  = {
      val n = xs.length/2
      if( n == 0) xs
      else {
        def merge(xs:List[T],ys:List[T]) :List[T]  ={
          (xs,ys) match {
            case (Nil,ys) => ys
            case (xs,Nil) => xs
            case (x::xl, y::yl) =>
              if(lt(x,y)) x :: merge(xl,ys)
              else {
                y :: merge(xs,yl)
              }
          }
        }
        val (fst,snd) = xs splitAt n
        merge(msort(fst),msort(snd))
      }
    }
    val xs = List(1,-2,5,4,9,7)
    msort(xs) //

---Rules for implicit paramters--
Say, a function takes an implicit parameter of type T .
The complier will search an implicit definition that :
    > is marked implicit
    > has as type compatible with type T 
    > is visible at the point of the function call, 
      or is defined in a companion object associated with T.
If there is a single(most specific) definition, it will be 
taken as the actual argument for the implicit parameter.

e.g. 
1. def f (implicit x:Int) {
    }
    implicit val y :Int =20
    f  //would be equivalent to f(y) 
    
2. class  C 
   object C {
   implicit  val x: C = new C
  }
   def f (implicit x:C )
   f //is equivalent to f(x)
 
    
More details :
https://docs.scala-lang.org/tutorials/FAQ/finding-implicits.html
http://jsuereth.com/scala/2011/02/18/2011-implicits-without-tax.html

      
    
