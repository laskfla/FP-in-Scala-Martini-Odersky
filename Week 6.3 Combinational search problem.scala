#######Set collection ########

A set is written analogously to a sequence :
    val fruit = Set("apple","banana","pear")
    val s = ( 1 to 6 ).toSet 
 
The difference between Seq and Set 
  1. Sets are unordered 
  2. Sets do not have duplicate elements.
  3. The fundamental operation on sets in contains :
     s contains 5 //true 
     
---The N-Queens problem---    
The eight queens problem is to place eight queens on a chessboard so that no queens is threatened by another.
In other words, there can't be two queens in the same row, column or diagonal.

Think it recursively as well !

Once we have placed the k -1 queens , one must place the kth queen in a column where it is not "in check"
with any other queen on the board.

Take the 4-queens as an example :
      o x o o 
      o o o x 
      x o o o 
      o o x o 
 
Here is the algorithm with recursion.
Remember the rule of recursive algorithm :
1. pretend this algorithm named algo already exists and works.
2. provide base case 
3. write the process logic for k given the algo(k-1)  works and the return results.

For the n-queens problem:
//Martini Odersky 这老师讲解问题真是很简练，这些都行都要靠自己揣摩出来。

/*assume the previous k-1 queens has been put properly in the check board and 
  the element in the returned list mark the row number. and the column number
  started from 0 to list.length - 1.
  the key algo for isSafe is that :
  
  1)none of the two places has the same row number (as we enumerated from col index,
  so col index is always different)
  2)for diagonal , think of the line of the two elements that in the diagonal position.
  the characteristic is that the math.abs(cola -colb ) = match.abs(rowa - rowb).

some key points about the program:
  1. since in list, the "::" (prepend) operation is constant time
     and usually preferred, then in the isSafe part, 
     when we add a column index for the queenList, the col number should be 
     indexed from list.length-1 to 0 by -1 (the 3rd column's sol is place in the 
     head of the list)

  2. when the place is iterated, it always started from 0 to the constant number n (the 
     n-queens). so in that case, we need to remember the initial parameter.
     that is why we have 2 parameters in the queen function.
     A better approach is to define another inner placeQueen recursive function that 
     only has one parameter and it could always access the n value.
   
  3. a variant of the solution is to put the solution in the end of the list ,
     using the ":+" method of a list .
     it may looks like more natural then the first one, but may cost more resources.     
*/

  def queens(n:Int ,k:Int) : Set[List[Int]] = {
    if(n==0) Set(List())
    else {
      for {
        queenList <- queens(n-1,k)//queens(n-1) is not a sub-problem of queens(n)
        place <- (0 until k) //not 0 to n , but 0 to k, the original parameter
        if (isSafe(queenList,place))
      }yield place :: queenList //queenList :+ place
    }
  }
  def isSafe(queenList:List[Int],rowplace:Int):Boolean = {
    val listLen = queenList.length
    val pairs = (listLen-1 to 0 by -1) zip queenList
    //println(pairs +":"+rowplace)
    pairs forall {
      case (col, row) => row != rowplace &&
        math.abs(listLen-col) != math.abs(rowplace-row)
    }

  }


  def queensTail(n:Int ,k:Int) : Set[List[Int]] = {
    if(n==0) Set(List())
    else {
      for {
        queenList <- queensTail(n-1,k)//queens(n-1) is not a sub-problem of queens(n)
        place <- (0 until k) //not 0 to n , but 0 to k, the original parameter
        if (isSafeTail(queenList,place))
      }yield queenList :+ place
    }
  }
  def isSafeTail(queenList:List[Int],rowplace:Int):Boolean = {
    val listLen = queenList.length
    val pairs = (0 until listLen) zip queenList
    //println(pairs +":"+rowplace)
    pairs forall {
      case (col, row) => row != rowplace &&
        math.abs(listLen-col) != math.abs(rowplace-row)
    }

  }

}

---to show the output, we could use below function---
//这里也有小坑，学习M大的讲解知识，不实践跟没学一样。。。

//first attempt : 
    def show (set :Set[List[Int]]): Unit = {
     val x  = for {
          list <- set
          ele <- list.reverse //notice the index is reversed
      } yield Vector.fill(list.length)("*").updated(ele,"X").mkString(" ")
      println(x.mkString("\n"))
    }
//这样做有两个问题： 1） 返回的x是Set[String],而Set会去重，所以最后只会输出一个，debug了半天。。。

//Second attempt: convert Set to List , will it would be fine  ?
    def show (set :List[List[Int]]): Unit = {
     val x  = for {
          list <- set
          ele <- list.reverse //notice the index is reversed
      } yield Vector.fill(list.length)("*").updated(ele,"X").mkString(" ")
      println(x.mkString("\n"))
    }
//这样输出有个小问题是，各个解都会连在一起，所以想分开各个解，但是由于这里返回X，已经不能区分各个解了。
//这样在输出的时候就得每隔n个行就得再输出一个换行，显然这个方式有点complex了。

//看看M老师的方法： 就是综合上面的解存在的问题： 1) first attempt 的问题，所以使用map 2）second attempt的问题map也可以解决

    def show (list : List[Int]) :Unit = {
        val x =for (x <- list) yield Vector.fill(list.length)("*").updated(ele,"X").mkString(" ")
        println(x)
        println("\n")
        }
    queens(4) map show 
    
