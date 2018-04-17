#####Map#####
A map of type Map[Key,Value] is a data structure that associates keys of type Key with values of type Value.

Examples :
    val romanNumerals = Map("I" ->1 , "V" ->5 , "X" -> 10)
    val capitalOfCountry = Map("US" -> "WashingTon", "Switzerland" ->"Bern")

Map extends Iterable[(Key,Value)] , so maps support same collection operations as other iterables do:
     val countryOfCapital = capitalOfCountry map {
            case (x, y) => (y, x)
     }
In fact, the syntax key -> value is just an alternative way to write the pair (key, value).

Map is also a function , it extends the function type : Key => Value, so maps can be used 
everywhere functions can.
In particular , maps can be applied to key arugments :
    capitalOfCountry("US")

  def testMap(f: String => String): String = {
    f("US")
  }
  val k = testMap(capitalOfCountry)
  
Map will throw "No such element" exception if value not found for the key argument.
Map provide a better ways for serarch. The get method would return an Option[Value] and then 
could be evaluated or tested if it has value or empty.

 capitalOfCountry get ("US") //Option[String] = Some("WashingTon")
 val k = capitalOfCountry get ("UK") //Option[String] = None
 k.isEmpty //true 
 
//Scala recommend using pattern matching instead of the if else. 
Since Options are defined as case classes, they can ben decomposed using pattern matching:
   def showCaptial(country: String) = capitalOfCountry.get(country) match  {
        case Some(capical) => capital
        case None => "missing data "
   }
   
----Sorted and GroupBy-----
Two useful operation of SQL queries in addition to for-expressions are groupBy and orderBy 
orderBy on a collection can be expressed by sortWith and sorted.

val fruit = List("apple","pear","orange","pineapple")
fruit.sortWith(_.length < _.length)
fruit.sorted //natural sort defind on String 

groupBy is available on Scala collections. 
It partitions a collection into a map of collections according to a 
discriminator function f (the "key" generator of a map).
  fruit.groupBy(_.length) //immutable.Map[Int,List[String]] = Map(5 -> List(apple), 4 -> List(pear), 9 -> List(pineapple), 6 -> List(orange))

 
---Example--
Polynomials could be expressed with a map : x^2 -2x +5 could be expressed as Map(0 -> 5, 1 -> 2,2 -> 1).

    class  Poly(val terms: Map[Int,Int]) {
        def + (that:Poly) : Poly = ???
        }
    }
    
The key function for this "+" method of Poly is the "combineMaps" :

//this method is a kind of verbose ,
  def combineMaps(xs:Map[Int,Int],ys :Map[Int,Int]) : Map[Int,Int] = {
    val allkeys = xs.keys ++ ys.keys
    for (key <- allkeys ) yield {
      val xkey = xs.get(key)
      val ykey = ys.get(key)
      (xkey , ykey ) match {
        case (Some(xk), Some(yk)) => (key,xk + yk)
        case (Some(xk), None) => (key,xk )
        case (None, Some(yk)) => (key,yk )
      }
    }
  }.toMap
  
//this version looks better but it has mutable variables and this may not be recommended 
  def combineMaps(xs:Map[Int,Int],ys :Map[Int,Int]) : Map[Int,Int] = {
    var rmap :Map[Int,Int] = ys;
    val newMap = for (ele <- xs )  {
      val y = ys.get(ele._1)
      y match  {
        case None => rmap = rmap.+(ele)
        case Some(yvalue) =>rmap = rmap.updated(ele._1,ele._2 + yvalue)
      }
    }
    rmap 
  }

//the Mr.M's version : the "++" could concatenate 2 maps , but it replace the same entry in the first
//map if key matched. That is a slightly unexpected as we need to sum the coefficient:
//better than the above 2 version: simple and concise (every step only needed as necessary ,no more)
class  Poly(val terms: Map[Int,Int]) {
 def + (that : Poly ) = new Poly (terms ++ (other map adjust) ) //adjust the value of second map if same key found in first map 
 
 def adjust(term :(Int,Int)) : (Int,Int) = {
     val (exp ,coeff) = term 
     //below is to test whether terms contain the exp key 
     terms get exp match  {
         case Some(coeff1) => exp -> coeff + coeff1 
         case None  => exp -> coeff 
     }
 }
   
}
//the adjust could be simplified again by using the withDefaultValue.
 class  Poly(val terms0: Map[Int,Int]) {
 val terms = terms0 withDefaultValue 0       
 def adjust(term :(Int,Int)) : (Int,Int) = {
     val (exp ,coeff) = term 
     exp -> coff1 + terms(exp)
 }

---Default value --
So far, maps are partial functions (only effective to some input). Applying a map to a key value in map(key)
could lead to an exception , if the key was not stored in the map.
There is an operation withDefaultValue that turns a map into a total function:
    val cap1 = capitalOfCountry withDefaultValue "<unkonwn>"
    cap1("Andora") //"<unknonw>"
 

---variable argument---
the constructor of Polynomials looks a little longer and we have to create an intermedidate map structure.
  val mapx = new Poly(Map(1->2,2->-2,4->6))
while as Scala also support the varargs feature , so Poly could have a constructor accepting variable argument list 
of pairs (a sequence of pairs) :
  def this(bindings : (Int,Int) *) = {
    this (bindings.toMap)  
  } 

then we could new Poly by : val mapx = new Poly(1->3,4->5) 

####Excersie#######
1. the "+" operation on Poly used map concatenation with "++". Design 
another version of "+" in terms of foldLeft :
    def + (other:Poly) ={
        new Poly((other.terms foldLeft ???))(addTerm)
    }
    
    def addTerm(terms: Map[Int,Int], term : (Int,Int)) = ???

//never think of a foldLeft solution as i thought it may only work on a single value
//it did. but the inital value could be map and the output could be a map then .
//a single value does not mean a "Int" or a "Double", it shold be an object !
[Keith]:
    def + (other:Poly) ={
        new Poly((other.terms foldLeft this.terms.withDefaultValue(0) ))(addTerm)
    }
    
    def addTerm(xs:Map[Int,Int],ele : (Int,Int)) : Map[Int,Int] = 
         val (key,value) = ele 
         xs.+(key -> value + xs.get(ele))
         
        
    
2.which of the two version do you think is more efficient?
  Obviously, the above one is more efficient as it traverse the maps only once .
  
