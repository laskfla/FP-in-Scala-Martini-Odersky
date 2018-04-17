/*##Convert telephone numbers to sentences###*/
//Task : 
//Phone keys have mnemonics assigned to them.
val mnemonics = Map (
 '2' ->"ABC",'3' ->'DEF','4'->"GHI",'5'->"JKL",'6' ->"MNO",
 '7' -> "PQRS",'8' ->"TUV",'9'->"WXYZ")
 
val sentences = Set("Scala is fun","We are here","See you later")
 
/*
Assume you are given a dictionary words as a list of words.
Design a method such that :
    translate(phoneNumber)
produces all phrases of words that can serve as mnemonics for the phone number.

Example : The digit number : 7225247386 should have the mnemonic, "Scala is fun" as one element of 
the set solution phrases.
Why  ? because number 7 has "S" associated and number "2" has both the "a" and "c" associated and so on .

***Background***
This example was taken from:
    Lutz Prechelt : An Empirical Comparison of Seven Programming languages. IEEE(2000).
Tested with Tcl, Python, Perl, Rexx, Java, C++, C 
Code size medians :
     > 100 loc for scripting languages
     > 200-300 loc for the others.
Let us see if we could resolve this task in Scala in a more concise way.
*/

/*--first attempt after 3 hours thinking and coding ---
 My recursive version solution :
 Choose recursive version as the prototype as it is easier to code and shorter.
 Since the digits may not be too long , so the recursive version is actually works well.
 Then we need to consider below for recursion :
 > What is input and output type ?
   As we assume there is already a solution return Set[String] for a input digit String,
   and we only need to care about the current case .
   The input type is String and output type is Set[String].
   But actually as it need to record at which position we should check the matched sentences 
   for the current digit. So we need a Map[String,Int] to record the expected match position.   
   
 > What is the base case ?
   There is only single digit in the string and we need to match with all the sentences.
     
*/
  val zeros = List.fill(sentences.size)(1)
  val sentencesMap : Map[String,Int] = (sentences zip zeros).toMap
  def translate(phoneNumber: String) : Set[String] = {
    val phoneLen = phoneNumber.length
    //filter out spaces(punctuations actually)  
    val candidates = sentencesMap.filter {case (x,y) =>x.filter(_ !=' ').length == phoneLen }

    def trans(digits : String) : Map[String,Int] = {
       if (digits.length == 1) {
         var baseMap:Map[String,Int] = Map()
         for (x <- mnemonics(digits.head) ) {
           baseMap = baseMap ++ candidates.filter(_._1.last.toUpper == x)
//           println("x in base case is :" + x)
//           println("baseMap is " + baseMap)
         }
         baseMap
       } else  {
         val xs : Map[String,Int]  = trans(digits.tail)
         var itMap : Map[String,Int] = Map()
         for( echar <- mnemonics(digits.head)) {
           itMap = itMap ++ xs.filter { case (x,y) => {
            // println("it case :" + echar)
            // println("itMap : " + itMap)
             val str = x.filter(_ != ' ').toUpperCase
             str.charAt(str.length -1 - y) == echar //y is index from right side 
           }
           }.map { case (m,n) =>(m,n+1) }
         }
         itMap
       }
     }
    trans(phoneNumber).keys.toSet
}

//Second attempt (iterative optimization ^-^)
/*the first attempt is not good when it checks if one of the mnemoic character is matched with 
//the sentences as a specific position as its complexity is m x n where m is the mnemonics length 
//and n is the Map size .
//can we avoid this just as did in Week 6.6 ? 
  Actually , for such operations, it could be replaced by a fold operation.
  Loc dropped from 27 to 23 but it looks better than the first one.
  And it did not use any variable.
  
  A few learnings :
  > Map.get return a Option and Option has contains method and some other collection method as well,
    you may wrongly use  contains method as if on the element of the Option , like the String case 
    So you could use Map function instead and with a default value if necessary.
    
    
*/

    val zeros = List.fill(sentences.size)(0)
    val sentencesMap: Map[String, Int] = (sentences zip zeros).toMap

    def translate(phoneNumber: String): Set[String] = {
      val candidates = sentencesMap.filter { case (x, y) => x.filter(_ != ' ').length == phoneNumber.length }

      def mnemonicsCheck(ch: Char, str: String, postIndex: Int): Boolean = {
        mnemonics(ch).contains(str.charAt(str.length - 1 - postIndex).toUpper)
      }

      def addItem(xm: Map[String, Int], ele: (String, Int), digitNum: Char): Map[String, Int] = {
        ele match {
          case (k, v) if mnemonicsCheck(digitNum, k.filter(_ != ' '), v) => xm + (k -> (v + 1))
          case _ => xm
        }
      }

      def trans(digits: String): Map[String, Int] = {
        if (digits.length == 1) {
          candidates.foldLeft(Map[String, Int]())((map, entry) => addItem(map, entry, digits.head))
        } else {
          val xs: Map[String, Int] = trans(digits.tail)
          xs.foldLeft(Map[String, Int]())((map, entry) => addItem(map, entry, digits.head))
        }
      }
      trans(phoneNumber).keys.toSet
    }
    
   println( translate("7225247386"))
   println( translate("932734373") )
   
//Professor M's version 
/* let us think the other side : when we map a string to sentences or words,
how about we map the sentences or words to a digit string and then compare the 2 strings.
if match, then we get one mnemonic for the digit string .
Besides that , save the digitnum to each words in a Map instead of re-calculating each time
is more efficient than my version :). //a programmer's insight for the problem.

Leaned from the code  :
    > "str.toUpperCase map charCode" is more efficient and shorter than 
      "str map (x =>charCode(x.toUpper))"
      
    > while as we check the whole dictionary for each digit sequence (or each query) , 
      we could save the result of mapping from words to digit numbers and then 
      we could simply check the dictionary in a constant time.
      
    > Interface oriented(more general) instead of Object oriented programming methodology .
      So the wordsForNum is defined as Map[String,Seq[String]] instead of Map[String,List[String]]
      
    > The most import part is that there is no recursion at all.  
    
*/

/*But actually I did not understand the problem here.
The dictionary is for word and then we may match any substring of the input digit number 
and then we got a composed "phrases" or "sentences".
But even for the simplified version as above, Professor M's solution is better .
So the real solution for this is as below 
*/

//Scala worksheet version 
object Test {
  val mnemonics = Map(
    '2' -> "ABC",
    '3' -> "DEF",
    '4' -> "GHI",
    '5' -> "JKL",
    '6' -> "MNO",
    '7' -> "PQRS",
    '8' -> "TUV",
    '9' -> "WXYZ")

  val in = Source.fromURL("https://lamp.epfl.ch/files/content/sites/lamp/files/teaching/progfun/linuxwords.txt")
  val words = in.getLines.toList.filter( str => str forall (_.isLetter)  )

  //Invert the mnemonics to give a map from chars 'A' -'Z' to '2'-'9'
  val charCode:Map[Char,Char] =
    for {
      ele <- mnemonics
      ch <- ele._2
    } yield (ch,ele._1)

  //Maps a word to the digit string it can represents, e.g "Java" ->"5282"

  def wordCode(word:String ) : String = word.toUpperCase map charCode

   val wordsForNum : Map [String, Seq[String]] = {
     words groupBy wordCode withDefaultValue(Seq())
   }

  def encode(number:String) : Set[List[String]] = {
    if(number.isEmpty) Set(List())
    else
      (for {
        split <- 1 to number.length
        word <- wordsForNum(number take split )
        rest <- encode(number drop split )
      } yield word :: rest
        ).toSet
  }
  //format not good
  def translate(number:String) : Set[String] ={
    val words = encode(number)
    words map ( _ mkString(" "))
  }
  translate("7225247386")
}
