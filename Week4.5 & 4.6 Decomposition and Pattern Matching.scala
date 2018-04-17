######Decomposition#######

When compose tree-like structures, how do we use the class hierarchy and define access methods for element access.

For example, consider a arithmetic expression (for simplicity, only consider number and sum).
then 
Expr ::= Number | Sum
Number ::= [0-9]+
Sum ::= Expr '+' Expr
 
At first attemp, we define a Expr trait and 2 sub-classes : Number and Sum 

trait Expr  {
    //the first 2 are classification methods and the other 3 are access methods
	def isNumber : Boolean
	def isSum : Boolean
	def numValue : Int  //if it is number, then access the value 
	def leftOp : Expr  //if it is sum, then leftOp is the left operand
	def rightOp : Expr 
}

class Number(n:Int ) extends Expr  {
  def isNumber = true
  def isSum = false 
  def numValue = n 
  def leftOp = throw new Error ("number.leftOp")
  def rightOp = throw new Error ("number.rightOp")
}

class Sum(left:Expr, right:Expr ) extends Expr {
  def isNumber = false
  def isSum = true 
  def numValue :Int = throw new Error("sum.numValue")
  def leftOp = left
  def rightOp = right 
}

###Evaluation of Expressions####
def eval (e :Expr ) :Int =  {
	if (e.isNumber) e.numValue
	else if(e.isSum) eval (e.leftOp) + eval (e.rightOp)
	else throw new Error("Unknown expression " + e) //a prudent clause in case someone add a new subclass of Expr 
}

It may looks good.
While it become tedious and error-prone if we add new subclasses  or new method.
we have change all existing sub-classes !
For example, if we add 2 new expressions : Prod and Variable.
then the total methods count would be 5x8 = 40 (add 2 new classification and 1 access method for Variable)
minus the previous method count : 15, so we need to add another 25 methods.
That is quadratic of the total classes (trait + 4 sub-classes)!

For sub-classes ,the solutions may be :
0) use run time type check and type cast.
    def isInstanceOf[T] : Boolean  //java : x instanceOf T 
    def asInstanceOf[T] : T  //java: (T)x  
   But that is not type safe and not encouraged in scala as it most use for interoperability with Java.
   While, its pros are : no need for classification methods and add access methods only for 
   classes where the value is defined.
 
1)  OO decomposition
if what you want to do is to evaluate the expression, then we could simply define eval in 
   trait and subclasses 
   trait Expr  {
	def eval : Int 
   }   
   class Number(n:Int) extends Expr {
	def eval = n
   }
   class Sum(e1:Expr,e2:Expr) extends Expr {
	def eval = e1.eval + e2.eval 
   }
   While, it still not flexible regarding adding new methods like product/display. then all sub-classes nedd 
   to be changed.
   The limitation is that when we want to simplify the expression instead of evaluating it,
   like a*b + a*c -> a*(b+c).This is a non-local simplification (only concerning the sub-class itself,no others involved).
   And we still need to check the expression types and this expression can not be encapsulated 
   in the method of a single object. So you are back to the first attempt: need to add test 
   and access methods for all the different subclasses.
   
    
2) Default implementation in trait.
   add new method default implementation in trait (java8 interface) and add customized implementation in new sub-classes
   It has the same limitation of solution (1).

3) Pattern matching

####Pattern matching######
The above task is to find a general and convenient way to access objects in a extensible
class hierarchy.

Observation of the task : 
the sole purpose of test(class classification) and accessor functions is to 
reverse the construction process :
	> Which subclass was used  (classification) ?
	> What were the arguments of the constructor (to get the values)?
This situation is so common in many functional languages, Scala included, 
so let us automate it this process with : **Pattern Matching**

Here is the changes we need to do when using pattern matching for sub-classes :
	> declare the sub-classes with "case" modifier, in this case, scala compiler will
	  auto-generate a companion object (apply :factory method) and some methods like unapply , etc 

Pattern matching is a generalization of switch from C/Java to class hierarchies.
It is expressed in Scala using the keyword match.
Example :
	 def eval (e: Expr) : Int = e match  {
		case Number(n) => n 
		case Sum(e1,e2) => eval (e1) + eval (e2)
     }
Syntax Rules :
	> match is followed by a sequence of case, pat => expr.
	> Each case associate an expression expr with a pattern pat.
	> A MatchError exception is throw if no pattern matches the value of the selector 

Forms of patterns (Semantic restrictions):
    > constructors , e.g. Number, Sum 
	
	> variables, e.g. n, e1,e2  which must always begin with a lowercase letter.
	  and can only appear once in a pattern. 
	  
	> wildcard patterns _  to match anything.
	  it could appear multiple times in a pattern, but that means different things.
	  
	> constants ,e.g. 1, true or constant variables like PI/N. 
	  which must begin with a uppercase except null, numbers like 1, 2 , true, false 
Those forms of patterns could be composed to construct more complicated cases.
like case Sum(e1,e2) to match a Sum constructor with e1, e2 as the argument (Expr).
The references to the pattern variables are replaced by the corresponding parts in the 
selector.

What do Patterns match ? (Semantic of pattern match)
	> A constructor pattern C(p1,p2...pn) matches all the values 
    of type C (or its subtypes) that have been constructed with 
    arguments matching the patterns p1,...pn 
    
    > A variable pattern x matches any value, and binds the name of the 
      variable to this value.
          
    > A constant pattern c matches values that are equal to C (equls or ==)
And the real pattern matching process is a recursive call .
e.g. eval ( Sum(Number(1), Number(2)) )
first, it match Sum(e1,e2) and e1 binds to Number(1), e2 to Number(2)
second, Number(1) matches Number(n) and n binds to 1 ,ditto for Number(2)
such process could go further and eventually terminates.
Also, it is possbile to defin eval just inside the Expr trait:
    trait Expr {
        def eval:Int = this match  {
            case Number(n) =>n 
            case Sum(e1,e2) => eval(e1) + eval(e2)
        }
    }

Which one is preferred(the above and the OO one) depends on the the future 
extensibility and the possible extension path of your system.
If you add sub-classes more often then the OO one is preferred
as you only need to change the new sub-classes.

If you add new methods more frequently and the class hierarchy kept stable,
then the above one is preferred as for new methods you only need to add that 
in the trait body once.
(这种traint内部pattern 去match实际还没有定义出来的subclass实际上说明了
method 和class的分离性。method依附object/class的主要目的是polymorphism。
method/function本身是可以独立存在的（C语言），所以这种定义method的方式perfectly fine
)

--Exercise----
Add case classes Var for variable x and Prod for products x * y as 
discusses previously.
Change your show function so that it also deals products.
Pay attention you get operator precedence right but use as few 
parentheses as possible.

Example :
    Sum ( Prod(2,Var("x")), Var("y")) 
should print "2 * x + y ".
    Prod( Sum(2,Var("x")), Var("y")) 
should print "(2 + x) * y "

	
	

