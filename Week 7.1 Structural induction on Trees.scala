/*continue the program proving on Trees, a more general data structure 
FP is important as it is very close to the mathematical theories of data structures.

####Structural Induction on Trees ######
Structural induction is not limited to lists; it applies to any tree structure.

The general induction principle is the following :

To prove a property P(t) for all tress of a certain type,

> show that P(L) holds for all leaves L for a tree type .

> for each type of internal node t with subtrees s1, ...sn , show that 
    
    P(s1) /\ ... /\ P(sn) implies P(t) , i.e under the assumption that P(s1) to P(sn) satisfy 
    the predicate then P(t) holds.  
    //then we could induce the p(t) holds at its own(the element it holds) and also
    //on all subtrees of t .
    

###Example: IntSets######
Show some interesting insights about the IntSet.
Recall our definition of IntSet with the operations "contains" and "incl":
  
*/
    abstract class IntSet  {
        def incl(x:Int) : IntSet 
        def contains (x:Int) : Boolean 
    }
    
    object Empty extends IntSet {
        def incl ( x:Int) : IntSet = NonEmpty(x, Empty, Empty)
        def contains(x :Int) :Boolean = false 
    }
    
    class NonEmpty(node :Int, leftsub :IntSet, rightsub: IntSet) extends IntSet {
        def incl(x:Int) : IntSet = {
            if (x >node) NonEmpty(node, leftsub, rightsub incl x ) //Immutable!
            else if ( x <node ) NonEmpty(node, leftsub incl x , rightsub)
            else this 
        }
        
        def contains(x:Int) : IntSet = {
            if (x >node) rightsub contains x 
            else if (x <node) leftsub contains x 
            else true 
        }
    }
/* Now let us approve the IntSet implementations are correct .
The Laws of IntSet 
   What does it mean to prove the correctness of this implementation ?
   
   One way to define and show the correctness of an implementation consists of 
   proving the laws that it respects, i.e define some laws that our implementation should 
   satisfy and then show that the implementation indeed does that.
   
   In the case of IntSet, we have the following three laws :
   For any set s , and elements x and y  :
   
   Empty contains x = false 
   (s incl x) contains x = true 
   (s incl x) contains y  = s contains y if x != y 
In fact, these 3 laws define the characteristics of IntSet completely (Keith: no idea about this ...)

###Proving the laws of IntSet (L)#########

How can we prove these laws ?

--Proposition 1 : Empty contains x = false.
Proof : According to the definition of contains in Empty .

--Proposition 2 : (s incl x ) contains x = true 
Proof by structural induction on s.
    Base case : Empty 
    (Empty incl x ) contains x  
   = NonEmpty(x, Empty , Empty) contains x 
   = true // by definition of NonEmpty.contains 
    
    Induction step :
   Assume the leftsub and rightsub of tree t satisfy "(s incl x ) contains x = true "
   then we could prove that :
   (s incl x) contains x ,
   The s here could be a general NonEmpty IntSet as NonEmpty(z, l, r ).
   There are 3 case here :
   1) the z == x 
   (NonEmpty (x, l, r) incl x ) contains x 
   = NonEmpty(x, l, r) //by definition of  NonEmpty.incl 
   = true // by definition of NonEmpty.contains 
  2) the x >z 
    (NonEmpty (z, l, r ) incl x ) contains x 
   =  NonEmpty(node, l, r incl x ) contains x // by definition of NonEmpty.incl 
   =  (r incl x ) contains x // by definition of NonEmpty.contains 
   =  x //by the assumption 
  
   3) ditto for the third case where x < z 

--Proposition 3 : (s incl x) contains y  = s contains y     if x != y    
  Base case : Empty 
  (Empty incl x ) contains y 
  = NonEmpty(x, Empty , Empty) contains y 
  according to the proposition 2 , we have that the it is always false 
  on the right side :
  Empty contains y is always false as well, the proposition holds for Base Case 
  
  Induction step :
  s is defined as a general NonEmpty(z, l, r ) and assume it hold for l and r subtrees.
  
  There are 2 cases here for the contains operation as z != x 
  1) x >z 
  (s incl x ) contains y 
 = (NonEmpty(z, l, r ) incl x ) contains y 
 =  NonEmpty(z, l, r incl x ) contains y 
   for any of the three cases of y (y == z, y>z, y <z)
   we have 
   NonEmpty(z, l, r incl x ) contains y 
   if y == z, then it is true 
   if y < z, then it is l contains y 
   if y >z , then it is (r incl x ) contains y = r contains y //the assumption
   ( or we could see this is equivalent to NonEmpty(z, l, r) contains x. 
    so the prove process could be reduce both side to a common point or 
    reduce one side and then goes up to the right one to match )
   
   On the right side, for the 3 cases of y 
   we have 
   NonEmpty(z, l, r) contains y 
   if y == z, then it is true 
   if y <z , it is  l contains y 
   if y >z , it is r contains y 
   so the left and right side exactly matched for any case of y in the case of x >z 
  
  2) x <z 
    Ditto 
 So we conclude that (s incl x ) contains y =  s contains y   if x != y.
 
 
*/


//#########Exercise ########
Suppose we add a function union to IntSet :
    abstract class IntSet {
        def union (other : IntSet) : IntSet 
    }

    object Empty extends IntSet {
        def union (other:IntSet) = other 
    }
 
    class NonEmpty (x :Int, l :IntSet, r : IntSet) extends IntSet {
        def union (other:IntSet) :IntSet =(l union (r union (other))) incl x 
       
    }

To prove the correctness of union , it can be translated into the following law :
    Proposition 4 :
   (xs union ys ) contains x = xs contains x || ys contains x //xs contains or ys contains x 
Show proposition 4 by using structural induction on xs.

[Keith]: To do 

   
 
  

   
