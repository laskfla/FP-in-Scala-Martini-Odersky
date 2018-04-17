########Queries with For#######
//the insight from Sir.M
For expression is very similar to the common operations of query language for database.
For has "if" for filter and nested form "for" expression is similar to the join in database.
(The collection returned could be then reduced/aggregated ,deduped/distinct , etc)

For example: Suppose that we have a database books, represented as a list of books .
    case class Book(title : String, authors : List[String])
 
 //the argument could be passed in  "parameter name = value" form  
val books  : List[Book] = List(
    Book(title  = "Structure and Interpretation of Computer Programs",
         author = List("Abelson , Harald","Sussman, Gerald J.")),
         
    Book(title  = "Introduction to Functional Programming",
         author = List("Bird, Richard","Wadler, Phil")),
           
    Book(title  = "Effective Java",
         author = List("Bloch, Joshua")),

    Book(title  = "Java puzzlers",
         author = List("Bloch, Joshua","Gafter, Neal")),

    Book(title  = "Programming in Scala",
         author = List("Odersky, Martin","Spoon, Lex","Venners, Bill"))
    )         
    
  val x = for (book <- books ;author <- book.authors if author.startsWith("Bird") )
    yield book.title //yield just follow the for expression, like the linux if command 

  val y = for( book <- books if book.title.indexOf("Program") >= 0 )
    yield book.title 

Find the names of authors who have written at least two books :
    //equivalent to a group by case,
     (for {
       b1 <- books
       b2 <- books
       if b1.title < b2.title  //otherwise may get duplicate data ,but it will not work for user with 
                                //three books, so we need to use a distinct on List 
       a1 <- b1.authors         //or to choose a Set for the books .
       a2 <- b2.authors
       if a1 == a2
     } yield a1) distinct 
     

########Translations of for loops ############
The syntax of for is closely related to the high-order functions : map, flatMap and filter.
First of all, all these functions can all be defined in terms of for :
    def mapFunc[T,U](xs:List[T],f: T => U) :List[U] =  
      for (x <- xs) yield f(x)
      
    def flatMap[T,U](xs:List[T],f: T => Iterable[U]) : List[U] = 
      for ( x <- xs; y <- f(x) ) yield y  //nested for expression

    def filter[T](xs : List[T], p : T => Boolean) : List[T] = 
       for (x <- xs if p(x)) yield x 
       
In reality, it goes the other way. Scala compiler expresses the for-expression in terms of 
map, flatMap and a lazy variant of filter.
//the filter in for expression is lazy . 
    > A simple for-expression is translated to map :
        for (x <- e1) yield e2 
       e1 map ( x => e2)
       
    > A nested for-expression :
        for (x <- e1 ; y <- e2; s ) yield  e3  is translated to 
        //s is sequence of generators and filters 
         e1 flatMap ( x => (for ( y <- e2; s ) yield e3)) 
         
    >  A for-expression 
       for (x <- e1 if f; s ) yield e2 
       where f is a filter and s is a (potentially) sequences of generators and filters is translated to :
        for (x <- e1.withFilter(x => f ); s )  
     //withFilter is a variant of filter that does not produce an immediate list , but instead 
     //filters the following map and flatMap function application.
     //then the translation continues and finally could be translated to case 2 .
     
Example : 
    for  {
        i <- 1 until n 
        j <- 1 until i 
        if (isPrime(x + y ))        
    } yield (x, y)
    
    is translated to :
    
     (1 until n ) flatMap( i => 
         (1 until i ).withFilter( j => isPrime(x + y ))
            .map ( i => (i, j)))
     is exactly what we do in the first attempt.but the for loop is more concise and easy to understand.
     
--Generalization of for --
Interestingly, the translation of for is not limited to lists or sequences, or even collections.
It is based solely on the presence of the methods map, flatMap and witFilter.
This lets you use the for syntax for your own type as well --you must define map,
flatMap and withFilter for these types.

---for and databases---
As long as the client interface to the database defines the methods map, flatMap and withFilter, we can
use the for syntax for querying the database.
This is the basis of Scala data base connection frameworks ScalaQuery and Slick.
Similar ideas underly Miscrsoft's LINQ(Language Integrated Query).


    
    
