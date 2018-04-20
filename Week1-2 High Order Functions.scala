-----------naive version of diffent "sums" ------------
def sumInts(a:Int, b:Int) :Int = {
    if(a>b) 0 else a + sumInts(a + 1, b) 
}

sumInts(1,10)

def cube(x:Int) :Int = x * x * x

def sumCube (a:Int, b:Int ) :Int = {
    if(a>b) 0 else cube(a) + sumCube(a + 1 , b)
} 
sumCube(1,10)

def fact(x:Int) :Int = {
    if(x==0) 1 else x * fact(x-1)
}
def sumFact(a:Int, b:Int) : Int = {
    if(a>b) 0 else fact(a) + sumFact(a + 1, b)
}
sumFact(1,10)

*Now, let us try to optimize the above codes:*
-----------first attempt---------------
We see the common part of these code, only difference in the function to process each int .
so make that function as a parameter.

def sum(f:(Int) => Int, a:Int, b:Int ):Int = {
    if(a>b) 0 else f(a) +  sum(f,a + 1, b)  
}

def sumInts(a:Int, b:Int) = sum(x => x, a:Int, b:Int)
def sumCube(a:Int, b:Int) = sum(x => x * x * x, a:Int, b:Int)
def sumFact(a:Int, b:Int) = sum(fact, a:Int, b:Int)


---------second attempt----------------
Now, we see the duplicate parameter of a, b on the both side, can we make it less verbose ?
In the above attempt, we use high-order function which take another function as input. 
In this case, not only take another function as input, but also return a new function as value.

def sum(f:(Int) => Int) : (Int, Int) => Int = {
  def sumF(a:Int, b:Int) :Int = {
    if(a>b) 0 else f(a) + sumF(a + 1,b)
  }
  sumF
}
def sumInts = sum(x => x)
def sumCube = sum(x => x * x * x)
def sumFact = sum(fact)

sumInts(1,10) 
sumCube(1,10)
sumFact(1,10)

--------third attempt---------------
those defined function sumInt, sumCube, sumFact is still a little verbose, can we make it shorter ?

sumInts(1,10) is equivalent to sum(x => x)(1,10)
sumCube(1,10) is equivalent to sum(x => x * x * x)(1,10)
sumFact(1,10) is equivalent to sum(x => fact(x))(1,10)

As function application is associated to the left.

--------Fourth attempt----
There is special syntax for those functions that return functions. For example,
the above sum can be rewritten as(i.e. another syntax sugar) :
def sum(f:(Int) => Int)(a:Int, b:Int): Int = {
  if(a>b) 0 else f(a) + sum(f)(a + 1, b)
}
sum(x => x)(1,10)
sum(x => x * x * x)(1,10)
sum(x => fact(x))(1,10)

In this way, the function sum in the original code with 3 parameters has been "currying" to a 
high-order function with this form.
The benefit is that the parameters can be applied in different context , i.e. partially applied.
sum(f:Int=>Int) application will return a function : (Int, Int) => Int
For example :
val f = sum(x => x * x) _ //the underscore is to make this is a partially applied function 
f(1,10) //385

 

----Concepts--
1. The type A => B is the type of a function that takes an argument of type A and return a result of type B.
So, Int => Int is the type of a function that map integers to integers.

2. In general, a definition of function with multiple parameter lists :
 def f(arg1)(arg2)...(argn) = E  
 is equivalent to 
 def f(arg1)(arg2)...(argn-1) = {def g(argn) = E;g} where g is fresh identifier. 
 (actually is : def f(arg1)(arg2)...(argn-1) : (argn)=>E  = {def g(argn)} = E;g} 
 This is exactly we did when define the model for sumF, we drop the outer argument list and define a new 
 sum function which return sumF to handle the outer argument.
 
 Or for short , we can just write an anonymous function : 
 def f(arg1)(arg2)...(argn-1) = (argn) =>E 
 
 By repeating the process n times 
   def f(arg1)(arg2)...(argn) = E 
 is shown to be equivalent to 
   def f = (arg1 =>(arg2 =>...(argn =>E)...))
This style of definition and function application is called currying.
The function type associated to the right .
For example , 
  def sum(f:Int=>Int)(a:Int, b:Int) :Int =...
The type of sum is (Int =>Int) =>((Int,Int) =>Int) in anonymous form.

---Exercise---
1. write a product function to calculate the product between a and b ?
2. write factorial in terms of product 
3. write a more general function, which generalize both sum and product ?

Answers :
1. 
def product(a:Int, b:Int) : Int = {
    if(a>b) 1 else a * product(a + 1, b)
}
2.
def factorial(a:Int) = product(1,a);

def sum(f:(Int) => Int)(a:Int, b:Int): Int = {
  if(a>b) 0 else f(a) + sum(f)(a + 1, b)
}
3.
The parameters we need : 
1)the function f to map a to f(a)
2)the function combine to combine the f(a) and the rest of the values.
3)the parameter a, b 
4)and the default value if a>b 

So it is kind of map+combine 
--version1--
def mapReduce(f:(Int) => Int, combine:(Int, Int) => Int,zero:Int)(a:Int,b:Int) : Int = {
    if(a>b) zero else combine(f(a),mapReduce(f,combine, zero)(a + 1,b)) 
}

mapReduce(x => x,(a,b) => a + b,0)(1,10) //sum
mapReduce(x => x,(a,b) => a * b,1)(1,10)  //product 

--version2 with fully curring---
def mapReduce(f:(Int) =>Int)(combine:(Int,Int) => Int) (a:Int, b:Int)(zero:Int) :Int ={
    if(a>b)zero else combine(f(a),mapReduce(f)(combine)(a+1,b)(zero))
} 
--version2.1 with parameters in different places--
def mapReduce(f:(Int) =>Int)(a:Int, b:Int)(zero:Int)(combine:(Int,Int) => Int) :Int ={
    if(a>b)zero else combine(f(a),mapReduce(f)(a+1,b)(zero)(combine))
} 
//only the recursive function application call defines recursive function
//the combine case in above is not recursive as the second one is actually a parameter.
mapReduce(x => x)(1,10)(0)((a,b) => a + b)

Is actually equivalent to :

def mapReduce(f:(Int) =>Int) : (Int,Int) => ((Int) => (((Int,Int) => Int) => Int)) = {
    def mapReduceF1(a:Int,b:Int) : (Int) => (((Int,Int) => Int) => Int) = {
        def mapReduceF2(zero:Int) : ((Int,Int) => Int) => Int = {
            def mapReduceF3(combine:(Int,Int) => Int) : Int = {
                if(a>b) zero else combine(f(a),mapReduce(f)(a + 1, b)(zero)(combine))
            }
            mapReduceF3
        }
        mapReduceF2
    } 
    mapReduceF1
}
mapReduce(x => x)(1,10)(0)((a,b) => a + b) //sum
mapReduce(x => x)(1,5)(1)((a,b) => a * b)  //product 
mapReduce(x => x * x * x )(1,10)(0)((a,b) => a + b) //sum of cube

The return type can be omitted except the outermost one as it is a recursive call.
But need to add underscore to differentiate the function application and partially applied function.

def mapReduce(f:(Int) =>Int) : (Int,Int) => ((Int) => (((Int,Int) => Int) => Int)) = {
    def mapReduceF1(a:Int, b:Int)  = {
        def mapReduceF2(zero:Int)  = {
            def mapReduceF3(combine:(Int,Int) => Int) = {
                //where partial function does not change the fact that the function is only
                //applied/evaluated when the whole parameter list is ready.
                if(a>b) zero else combine(f(a),mapReduce(f)(a + 1, b)(zero)(combine))
            }
            mapReduceF3 _ 
        }
        mapReduceF2 _ 
    } 
    mapReduceF1 _ 
}
mapReduce(x => x)(1,10)(0)((a,b) => a + b) //sum
mapReduce(x => x)(1,5)(1)((a,b) => a * b)  //product 
mapReduce(x => x * x * x )(1,10)(0)((a,b) => a + b) //sum of cube


---Further improvement ---
change the recursive function to non-recursive one ,that is 
to make it as a tail recursion

e.g:
def sum(f:Int => Int)(a:Int,b:Int) : Int = {
    if(a>b) 0 else f(a) + sum(f)(a + 1 , b)
}
changed this to tail-recursion :
--version1--
def sum(f:Int => Int)(a:Int,b:Int) : Int = {
    def loop(x:Int,acc:Int) :Int = {
        if(x > b) acc else loop(x + 1,f(x) + acc) 
    }
    loop(a,0)
}

--version2 with @tailrec and currying---
import scala.annotation.tailrec
@tailrec
def sum(f:Int => Int)(a:Int,b:Int)(acc:Int) : Int = {
        if(a > b) acc else sum(f)(a+1,b)(acc+f(a))

}
/*
https://stackoverflow.com/questions/3114142/what-is-the-scala-annotation-to-ensure-a-tail-recursive-function-is-optimized

The Scala compiler will automatically optimize any truly tail-recursive method. 
If you annotate a method that you believe is tail-recursive with the @tailrec annotation, 
then the compiler will warn you if the method is actually not tail-recursive. 
This makes the @tailrec annotation a good idea, both to ensure that a method is currently optimizable 
and that it remains optimizable as it is modified.
Note that Scala does not consider a method to be tail-recursive if it can be overridden. 
Thus the method must either be private, final, on an object (as opposed to a class or trait), 
or inside another method to be optimized.
*/
for the mapReduce one : 

def mapReduce(f:Int => Int,combine: (Int,Int) => Int, zero:Int)(a:Int,b:Int) :Int = {
    def loop(x:Int,acc:Int) :Int = {
        if(x > b) acc else loop(x + 1, combine(f(x),acc))
    }
    loop(a,zero)
}
