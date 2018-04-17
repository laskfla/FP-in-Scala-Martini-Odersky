#######Reasoning#######
How do you check if a proram is correct ? Sometimes, the operations satisfy a few laws 
which is often represented as equalities of terms.
Reasoning is the core claims of FP, namely , it is more amendable to reasoning about programs.
 

-----Referential transparency -------
(FP in Scala--Red book introduced this in the first chapter)

    Note that a proof can freely apply reduction steps as equalities to some part of a term.
    That works because pure PF does not have side effect; so that a term is equivalent to the term which it 
    reduces.
    This principle is called referential transparency.

    
-----Structure Induction------
The principle of structure induction is analogous to natural induction :
    To prove a property P(xs) for all lists xs ,
        > Show that P(Nil) holds (base case).
        > for a list xs and some elements x, show the induction step :
              if P(xs) holds, then P( x :: xs) also holds.
              


Example :
concat is associative and that it admits the empty list Nil as neutral elements to the left and to the right and
the laws of concat :
  (xs ++ ys ) ++ zs = xs ++ (ys ++ zs )
    xs ++ Nil = xs
    Nil ++ ys = ys 

Q : How can we prove properties like these  ?
A : By structural induction on Lists.
   
The implementation of concat is :
        def concat[T] (xs :List [T], ys : List[T]) : List[T] = xs match
            case Nil => ys
            case x :: x1 => x :: concat(x1,ys) 
        }    
    
It distill (extract) two defining clause of ++ 
             Nil ++ ys = ys //first clause
           (x :: x1) ++ ys = x :: (x1 ++ ys ) //the second clause.
                                             
           
 
So the base case : xs = Nil 
      left hand : (Nil ++ ys ) ++ zs  = ys  ++ zs //the first clause   
     right hand :  Nil ++ (ys ++ zs ) = ys  ++ zs 
     
Induction step:  x :: xs 
  left hand  ( (x :: xs ) ++ ys ) ++ zs = ( x :: (xs ++ ys ) ) ++ zs //the second clause
                                         =  x :: ((xs ++ ys ) ++ zs )
                                         =  x :: (xs ++ (ys ++ zs))
                                         = (x :: xs) ++ (ys ++ zs ) //the right hand
So the law of associative established.

---Exercise---
Show by induction on xs that xs ++ Nil = xs 
   base case :  xs = Nil
                xs ++ Nil = Nil ++ Nil 
                          = Nil 
                          = xs 
   induction step :
                  (x :: xs) ++ Nil
                 = x :: (xs ++ Nil)
                 = x :: xs  
    so the laws established.
    
                 
                                                 
  
   
