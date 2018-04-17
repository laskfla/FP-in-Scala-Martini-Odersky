Function type are Class type:

Function type means class :
 (Int) => Int is a function type with single argument and return value is Int.
is actually equivalent to this in Scala :
trait Function1[Int,Int] { //trait Function1 with Int as the type parameter
    def apply(x:Int):Int
}

Function value means the object :
 val f = (x:Int) = x * x  is equivalent to :
 val f = {
   class AnnoFunc extends Function1[Int,Int] {
    def apply(x:Int) : Int = x * x 
   }
   new AnnoFunc
 }
 the shorter form is just like Java : 
 val f = new Function1[Int,Int] {
    def apply(x:Int) = x * x 
 }
 
Function call, such as f(a,b) where f is a value of some class type, is expanded to :
 f.apply(a,b)

Note : Method is not function value otherwise it would be a infinite expansion.
e.g apply in the above definition if expanded to class, then it will be not terminated.
Method definition :
    def f(x:Int): Boolean  = ...
is not itself a function value.

But if f is used in a place where a Function type is expected,it is converted automatically to the function 
value(we define type in the parameter, and you give the value of this type when call the method):
 (x:Int) => f(x)//i.e. input parameter is Int and return value is f(x) as defined in method body
or expanded to : 
 new Function1[Int,Boolean] = {
    def apply(x:Int)  = f(x)
 }
 
for example :
    def fc(anno:(Int) => Int,x:Int) :Int =  {
        anno(x)
    } 
could be called with fc(f,100), in this case, f will be converted to a function value.
The conversion of name f (method name) to anonymous function called eta-expansion in lambda calculus.


--------------Exercise--------------------
define an 
object List{
    ....
}
with 3 functions in it so that users can create list of length 0-2 using the syntax
List() //the empty List
List(1) //the list with single element 1 
List(2,3)//the list with elements 2 and 3 

Answer : take List(1) as an example
def apply[T](x:T): List[T] = new Cons(x,new Nil)


 
#######2018-02-04#######
----------Week4.3 Primitive type as class ---
Boolean 
这个地方的做法和之前看lambda的思路完全一致，如果没有那个基础和当时的思考的话，很难理解这段
视频。在lambda演算中，一切都是函数,数字也会被表示成函数。这其实是更高层次的抽象。
人们定义number是为了表示value，做memory的，但目的是为了operation的。

Lambda中，Boolean就是被表示成了选择函数,所有其他在Boolean上的OP都可以被这个函数表示
Let us see how to define that in Scala:

abstract class Boolean {
    //the if(condition)then expression1 else exression2 is abstracted as 
    //the ifThenElse function called on a boolean value .
    //The only function for ALL boolean operations.
    
    //if the boolean value (object) is true, then t, else e returned.
    //That is different behavior according to the true/false object.
    //So it needs to be abstract/undefined in the Boolean class.and then 
    //define function body in the 2 different sub-class(singleton object)
  
    //As the boolean evaluation is short-circuit, so use call by name . 
    def ifThenElse[T](t: =>T, e: =>T) : T 
    
    def && (x: Boolean) : Boolean = ifThenElse(x,false) 
    def || (x: Boolean) : Boolean = ifThenElse(true,x)
    def unary_! : Boolean = ifThenElse(false, true) 
    def == (x:Boolean) : Boolean = ifThenElse(x,x.unary_!)
    def != (x:Boolean) : Boolean = ifThenElse(x.unary_!,x)
    
    //Exercise, define < operator to compare Boolean 
    //and assume false<true
    def < (x:Boolean) : Boolean = ifThenElse(false,x)
}
  
Now,we define true/false :

object true extends Boolean {
    def ifThenElse[T](t: =>T, e: =>T) :T = t
}
object false extends Boolean  {
    def ifThenElse[T](t: =>T, e: =>T) : T = e 
}


----Int(Natural Number)-----------
According to Peano number,Natural Number is composed by zero and a successor class.
Let us define a simple version with only support positive integer and zero.

abstract class Nat {
  def isZero: Boolean
  def predecessor : Nat
  def successor : Nat
  def + (x:Nat) : Nat
  def - (x:Nat) : Nat
}
object Zero extends Nat  {
  def isZero = true
  def predecessor = throw new Error("zero predecessor")
  def successor = new Succ(Zero)
  def + (that : Nat) : Nat = that
  def - (that : Nat) : Nat = if (that.isZero) Zero else throw new Error("zero substraction :" + that )
  override def toString() = "0"

  //overload the comparison operator
    def <(that : Nat) : Boolean = {
    if(that.isZero) false
    else if(this.isZero) true
    else this.predecessor<(that.predecessor)
    
}

//the parameter of a class will be a field in the java object.
//so the recursive call of +/- method will access the field named n 
class Succ(n : Nat) extends Nat {
  def isZero = false
  def predecessor = n
  def successor = new Succ(this)
  //succ(n) + m = succ(n+m), this is a recursive call
  def + (that : Nat) : Nat = {
    println(this + " add " + that)
    new Succ(this.predecessor + that)
  }
  //succ(n) - m = n - m.predecessor, a recursive call as well
  def - (that : Nat) : Nat = if(that.isZero) this else n - that.predecessor

  def product(that:Nat) : Nat = {
    if(that.isZero) Zero
    else {
      this + this.product(that.predecessor)
    }
  }

  override def toString(): String = {
      //we can use "|" or "[" as the indicator, use numbers here 
      //just for readability 
      String.valueOf(1+ new Integer(predecessor.toString))
  }
}


object HelloWorld {
  def main(args: Array[String]) :Unit = {
    val n1 = new Succ(Zero);
    val n2 = n1.successor
    val n3 = n2.successor
    val n4 = n3.successor
    println(n4+n1) //5 ,

  }
}
