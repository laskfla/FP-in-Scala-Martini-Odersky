Immutable collections enable the expression of algorithm in a very high
level and concise way that, in addition , has a high chance of being correct :).

	 Seq
	/   \
 List   Array
 
Two important differences between Lists and Arrays in Scala :
	> Lists are immutable, while Arrays are mutable.
	> Lists are recursive, while Arrays are flat (elements are equally accessible and visible).

The List type:
	> Lists are homogeneous : the elements of a list must all have the same type.
	> A list is constructed by List() (the empty List or Nil) or U :: List[T] ("::" pronounced cons)
In Scala , operators ends with ":" are right associate, 
So A :: B :: C == A :: ( B :: C )
Besides,operators ending in ":" are also different in the way they are seen as
method calls of the right-hand operand :  the right operand is the target object (or receiver) .

So below expressions are equivalent.
    val x = 1 :: 2 :: Nil 
    val x = Nil.::(2).::(1)	
	

在FP中，为了referential transparency, object 都是 immutable的。
而且基于lamda演算的很多算法都是recursive的。
把data structure也定义成 recursive 的,和 algorithm isomorphic(同构),
这样的话算法的每一步的operation都可以是数据结构的部分对应起来，易于理解，操作简单。
参照"Why recursive data structure" 中对isomorphic的解释：
"Isomorphic means, fundamentally, “having the same shape.” 
Obviously, a quadtree doesn’t look anything like the code 
in rotateQuadTree or multirec. So how can a quadtree “look like” 
an algorithm? The answer is that the quadtree’s data structure 
looks very much like the way rotateQuadTree behaves at run time.
More precisely, the elements of the quadtree and the 
relationships between them can be put into a 
one-to-one correspondance with the call graph 
of rotateQuadTree when acting on that quadtree."
更进一步，作者阐述，"If the interface between algorithm and DS is so complicated,
The answer can often be found by imagining a data structure that looks like 
the algorithm’s basic form"


Take scala.collection.immutable.List as an example :
All operations on lists can be expressed in terms of the following
three operations :
     > head   the first elements of the list 
     > tail   the list composed of all the elements except the first
     > isEmpty  "true" if the list is empty,"false" otherwise.
这里其实可以看到和 recursive algo 的对应关系：
     > head + tail 是对list的的递归分解
     > isEmpty 是base case和boundary
Perfect matched .

e.g. Insertion Sort :

  def isort(xs :List[Int]) : List[Int] = {
     xs match  {
       case List() => List()
       case y :: ys => insertSort(y,isort(ys))
     }
  }

  def insertSort(y:Int, ys:List[Int]) : List[Int] ={
    ys match {
      case List() => List(y)
      case z :: zs => {
        if(y<=z) y::ys
        else z :: insertSort(y,zs)
      }
    }
  }
  



	
	
