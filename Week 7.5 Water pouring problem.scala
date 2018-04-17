Suppose there are a few glasses without any marking but the capacity is known for each of them 
and a faucet and a sink below the faucet.
There exists an unknown amount of water in one of the glasses(say x ) and we want to figure out 
this amount. The allowed operations are :
    > full the glass with the faucet.
    > empty the glass and pour it either to the other glass or the sink.
//Actually the water pouring is to find a puring sequence to get an expected amount of water.
//Anyway, over shoes over boots ！ and this would be the "reversed" order by the classic water pouring problem.
For example , 2 glasses like below:
  glass A = 4 deciliter ,name it as MA 
  glass B = 9 deciliter ,name it as MB
and there is an unknown amount of water in glass B ( called X and take it as 6 for an example) now.
Think about the problem with the example case and then generalize the ideas.

The solution for this case would be :

    > Step1: pour the water from B to A , if A is full, empty it and pour it to the sink.
      after step1 , we get an unknown about in A and also record the passes that we empty A during 
      this operation, say PA.
      
      B : 6 -> 2 //B pour to A 
      A : 0 -> 4 
      
      A : 4 -> 0 //A pour to sink
      
      B : 2 -> 0 //B pour to A 
      A : 0 -> 2 
    
    > Step2: right now ,the problem convert to identify the smaller amount in glass A called SA.
      Then X = SA + PA * MA  , 6 = 2 + 1*4
      X = 1*4 + 2 
      
    > Step3 : the only way we could do now, it is empty A and pour it back it B.
             A is empty and B contains 2 deciliter now.
       
      A: 2 -> 0 //A pour to B  
      B: 0 -> 2
      
    > Step4 : now B has SA and A is empty, as SA is smaller than X.
      now we full A and then pour it to B until B is full. 
      So now, the amount remained in A when B is full would be the key to the problem .
           
    > Step5 : We empty B and then pour A to B until it is full and then pour B to sink. 
      Now there would be some amount in A , otherwise the problem is solved.
      Repeat until A is empty and B is full after A pours to B.
      A: 0 -> 4 //pour from sink to A  
      
      A: 4 -> 0 //A pour to B 
      B: 2 -> 6
      
      A: 0 -> 4 //pour from sink to A 
      
      A: 4 -> 1 //A pour to B 
      B: 6 -> 9 
      B: 9 ->0 //B pour to sink       
      Then we know the relation between X and the amount remained in A called RA_1 now.
        X = SA + PA * MA
      --> = (9 - (2*4 - RA_1)) + 1 * 4  
      --> =  9 - 1*4 + RA_1 
      So the answer depends on RA_1  now.  
      Repeat step 4 and 5 until we reach the case where A is empty and B is full:
      
      A: 1 -> 0 //A pour to B 
      B: 0 -> 1 
      
      A: 0 -> 4 //pour from sink to A 
      
      A: 4 -> 0 
      B: 4 -> 5
      
      A: 0 -> 4 //pour from sink to A 
      
      A: 4 -> 0 
      B: 5 -> 9 
      Then RA_1 = 9 - 2*4 and then we know : RA_1 = 1 and then we know X = 6 !  
        X = 9 - 1*4 + RA_1
      --> = 9 - 1*4 + (9 - 2*4)
      --> = 2*9 - 3*4
      --> = 6   
Now the solution would be clear for the 2 glasses case : we only have the 4 deciliter and 9 deciliter as 
a unit in the measurement and any number would be expressed as the combination of 4 and 9 : an algebraic equation.
First we need to prove that any number could be expressed as the combination of 
any two or more distinct number ,say X, Y .--To DO 

So the idea is to find the coefficient of A, B and express the unknown water amount as :
Answer = Capacity_B*Coeff_B + Capacity_A * Coeff_A.

Now let us generalize the solution :
Suppose B is not empty and A is empty at this initial state and also Capacity_B > Capacity_A.

    > Step1: pour the water from B to A  until either:
      B is empty or A is full.
      if A is full and B is empty then Capacity_A would be the answer.
      if A is full and B is not empty, pour A to sink and increase the coefficient of A(Coeff_A) by 1
      and then repeat this step.
      This step would end up with B is empty and the volume in A would be less than the capacity of A.
      Problem converted to find the amount in A now. 
      Notice the amount in A would be less than min(Capacity_A,Capacity_B).
    
    > Step2: Pour the water from A to B :
      If A is empty and B is not full //obviously ,see above.
      Full A from the faucet and then pour it to B until either 
      OR A is empty and B is full , decrease Coeff_A by 1 and increase Coeff_B by 1 and return .
      The problem is solved as we already know the answer would be 
      Capacity_B*Coeff_B + Capacity_A * Coeff_A.
      OR A is empty and B is not full, decrease the Coeff_A by 1 and repeat step 2 .
      OR A is not empty and B is full, increase Coeff_B by 1 and decrease Coeff_A by 1 ,empty B and repeat step2.
      (Notice that Capacity_B > Capacity_A, so we would measure the amount remained in B by Capacity_A,
       the opposite side operation is impossible and would get into a dead loop).
      
      
     
 ---First version ---
    //Review : 45 LOC and use var , hard to read and understand.
    
    //Uppercase for constant and avoid trouble in pattern matching.
    def waterPourProblem(cap: (Int, Int), init: (Int, Int)): Int = {
      val coeff_pair = waterPour(
      
        //with using min/max , now we do not to care which one is large and the initial water exists in 
        //which glass. The thick is to define a function only work on specific order and then pass the sorted value
        //to avoid the verbose if/else.        
        (Math.min(cap._1, cap._2), Math.max(cap._1, cap._2)),
        (Math.max(init._1, init._2), Math.min(init._1, init._2)),
        (0, 0))
      println(coeff_pair)
      Math.min(cap._1, cap._2) * coeff_pair._1 + Math.max(cap._1, cap._2) * coeff_pair._2
    }

    /*below assumption must be matched:
1. cap._1 < cap._2
2. vol._1 != 0  and vol._2 == 0
*/
    def waterPour(cap: (Int, Int), vol: (Int, Int), coeff: (Int, Int)): (Int, Int) = {
      var (coeff_a, coeff_b) = coeff
      var (vol_a, vol_b) = vol
      val (cap_a, cap_b) = cap
      //move the unknown water to B, it could exists in A or B in initial state.
      //if initial in A, then it is the next move.
      //if initial in B, then we are in the first move.
      vol_b = vol_a
      vol_a = 0
      //Thread.sleep(1000)
      while (cap_b - vol_b > cap_a) {
        println("while_loop", cap_a, cap_b,vol_a, vol_b, coeff_a, coeff_b)
        coeff_a = coeff_a - 1
        vol_b = vol_b + cap_a
      }
      if (cap_b - vol_b == cap_a) {
        coeff_a = coeff_a - 1
        coeff_b = coeff_b + 1
        (coeff_a, coeff_b)
      } else {
        vol_a = cap_a - (cap_b - vol_b)
        vol_b = 0
        coeff_a = coeff_a - 1
        coeff_b = coeff_b + 1
        waterPour(cap, (vol_a, vol_b), (coeff_a, coeff_b))
      }
    }

---Second Version--
/*The insight we get now is that : actually the water to be measured would always be in B 
  as we need to measure the amount using the smaller glass to get the point where A is empty
  and B is full. So we could remove the vol_a variable.
  
  Embed the waterPour function inside waterPourProblem to reduce the parameters passed to waterPour.
  (one problem in FP is the parameters explosion,simplify when possible).
  
  Remove the "var" variable as that is not encouraged and the above code is kind of hard to track and understand
  the algorithm in code (write the code like the problem is the philosophy of a high level language).
    
  Code format and adjust "if".
  
  What we learn from this case is that : the recursion here is a tail recursion. 
  We write the code NOT the way like before recursion : assume we get solution for k-1, and now 
  how could we process the k case. Instead, We define a "base case" where to return the result 
  and iterate (tail recursion is the same as a while loop or iteration).
  I even suspect if this case could be converted the "express k in k-1 form".
  
  Review : 20 LOC, no var and a tail recursion version.
*/
  def waterPourProblem(cap_a:Int,cap_b:Int, init:Int ): Int = {
      /*Assumption: cap._1 < cap._2
     */
      @tailrec
      def waterPour(vol: Int, coeff_a:Int,coeff_b:Int): (Int, Int) = {
        //println("call now:", cap_a,cap_b, vol, coeff_a,coeff_b)
        if (cap_b - vol == cap_a) {
           (coeff_a - 1, coeff_b + 1)
        }else if (cap_b - vol > cap_a ) {
          waterPour(vol + cap_a, coeff_a - 1 , coeff_b)
        } else {
          //skip the operation which is to move the remained water in A to B
          //vol = cap_a - (cap_b - vol_b)
          waterPour(cap_a - (cap_b - vol), coeff_a - 1, coeff_b + 1)
        }
      }
      val coeff_pair = waterPour(init,0, 0)
      println(coeff_pair)
      cap_a * coeff_pair._1 + cap_b * coeff_pair._2
    }  
   

//Go back to the "right" understanding of the Water Pouring problem.
Now let us make the generalization further to arbitrary number of glasses of arbitrary given 
capacities ,and an arbitrary target capacity in one of the glasses, i.e there exits a glass that contains 
the target size after the operations.

/*
This problem is actually a search problem. (when we can not handle the complex logic with a if/else sequence).
We model the water in the glasses as a state and after each operation, the state changed .
The operations are the :
  empty   //empty the glass 
  full    //fill the glass wit water 
  pour from one to another 
  
The states of glasses would be changed as a state machine. 
And the solutions would be a set of sequences of Moves called Paths.
As we modeled the problem as a search problem (search possible solutions in a space), then we could decide to 
use BFS/DFS. In this case , we use BFS first as it is easier to code as it is just to add a arbitrary move step from previous
path(whether it is more efficient or not depends on the 
real problem and the input.)

Also notice that the search space would be increased in a exponential of 3 if we do not remember the traversed state.
So by adding the traversed state, the solution would be an example of "dynamic programming".

Let us try it !       
*/    

//By combining OO and FP ,we make below classes(models) and the actions on classes are always FP (no side effect).     
States and Moves
        Glass: Int //0 to number -1
        States : Vector[Int](one entry per glass) //Represent the water in each glass
        Moves :
            >Empty(glass)
            >Fill(glass)
            >Pour(from,to)
Path(List[Move]) //initialized with a list of Move 
from(paths: Set[Path]): Stream[Set[Path]]//as the problem is a search problem and the search space could exploded too much 
                                         //so we use Stream as the final output and then could generate paths per request.
  
//Professor M's solution 
//problem initialized with a list of glasses' capacity.
class MyPouring(glasses:List[Int]) {

  //state is just a list of Int ,each marked the current water volume in each glass
  //indexed from 0 to number of glasses -1
  type State = List[Int]

  val initialState : State = glasses map ( _ => 0)

  trait Move {
    def change(state:State) : State
  }
  //A move is an action on a glass, so it is constructed with a glass number
  case class Empty(glass:Int) extends Move {
    //"updated" indicate a new value out of old value, not updating the existing object
    def change(state:State) :State = state.updated(glass,0)
  }
  case class Fill(glass:Int) extends Move {
    def change(state:State) : State = state.updated(glass,glasses(glass))
  }
  case class Pour(from:Int,to:Int ) extends Move {
    def change(state:State) :State = {
      //identify the amount to be subtract for source and added for target glass
      val amount = Math.min(state(from),glasses(to) - state(to))
      state.updated(from,state(from) - amount).updated(to, state(to) + amount)
    }
  }

  /**
    * Define the function in its natural scope.
    * A path constructed from a list of Move.
    * It has next  with a move to generate a new path after the Move
    * The latest move added to the head of the list.
    * T
    */
  class Path(val path:List[Move], val endState : State) {
    def next(move:Move) : Path = new Path(move :: path, move change endState)
    //An endState constructed from the initial state, we do not want to compute the endState
    //each time we call, why not remember it ?
    //def endState : State = path.foldRight(initialState)(_ change _)
    override def toString = path.reverse.mkString(" ") + "--->" +endState
  }

  val moves  = {
    val range = 0 until glasses.length
    (for {
      glassa <- range
      glassb <- range
      if (glassb != glassa)
    } yield Pour(glassa, glassb)) ++
      (for(glass<- range) yield Empty(glass)) ++
      (for(glass<- range) yield Fill(glass))
  }

  /**Finally, let us define the solution to the problem.
  We generate all the possible from the initial state.
  In this case, we do not require the end condition in the method parameter,
  then the output would be a Stream as we can not generate all the paths at once.

  Notice the recursive call of "from", the input type and ouput type may not be the same
  and we could construct the output value by recursive call.

    If we do not use th states to record the path we have traversed, then it may take too much
    time to get a result and thought it is in dead loop, while actually not....
    The paths exploded at a exponential of 6 : 6, 36,216, 1296 , 7976 ,  47876 ...
    For the case of glasses of 4,7 to get 6, it takes just 5 steps to find the first solution.
    For the case of glasses of 4,9 to get 6, it takes 8 steps, i.e. : 47876*6*6 = 1.7million
    Besides that, the action like take 3 may get answers but actually they are duplicate but just
    add some moves that has already traversed.
    */
  def from(paths:Set[Path], states: Set[State]) : Stream[Set[Path]] = {
    if(paths.isEmpty) Stream(Set())
    else {

      val more : Set[Path] = {
        for {
          path <- paths
          next <- moves map path.next
          if ! states.contains(next.endState)
        } yield next
      }
      val newStates : Set[State] = states ++ (more map(_.endState))
      paths #:: from(more,newStates)
    }
  }
  val initialPaths = Set(new Path( Nil ,initialState))
  val paths = from(initialPaths, Set(initialState))

  def solutions(target:Int) : Stream[Path] = {
    for{
      pathSet <- paths
      path <- pathSet
      if path.endState contains(target)
    }yield {path }

  }

}

/*
Some other thoughts/solutions or variants:
In a program of the complexity of the pouring program, there are many choices to be made.
Choices of representations (Models) :
    > Specific classes for moves and paths , or some encoding ?
    > OO methods or naked data structures with functions?
    > Can we have a recursive solution for this case ? //Keith added 
*/

/*
Guidelines for programming : it depends on your experience. As the old saying goes , you could learn 
a program in ten days and then you improve for ten years.
Some guidelines considered useful :
    >Modularity and name everything you can . 
     Break things up to little pieces and each one only has limited operations.
     Name each piece that makes the program much more intelligible and readable.
     
    > Put operations into natural scopes.
    For example , we put change inside a move operation because a move change things.
    
    > Keep degrees of freedom for future refinement.
    You do not need to keep everything flexible which may be over-designed. 
    Just keep the part which supposed to be refined in future.
    
    > Add parentheses if possible as the FP style use the function with parameters a lot.

*/    
