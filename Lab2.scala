package jsy.student

import jsy.lab2.Lab2Like

object Lab2 extends jsy.util.JsyApplication with Lab2Like {
  import jsy.lab2.Parser
  import jsy.lab2.ast._

  /*
   * CSCI 3155: Lab 2
   * <Lei Teng>
   * 
   * Partner: <Zijun Xu>
   * Collaborators: <Jonathan Young>
   */

  /*
   * Fill in the appropriate portions above by replacing things delimited
   * by '<'... '>'.
   * 
   * Replace the '???' expression with  your code in each function.
   * 
   * Do not make other modifications to this template, such as
   * - adding "extends App" or "extends Application" to your Lab object,
   * - adding a "main" method, and
   * - leaving any failing asserts.
   * 
   * Your lab will not be graded if it does not compile.
   * 
   * This template compiles without error. Before you submit comment out any
   * code that does not compile or causes a failing assert. Simply put in a
   * '???' as needed to get something  that compiles without error. The '???'
   * is a Scala expression that throws the exception scala.NotImplementedError.
   *
   */

  /* We represent a variable environment as a map from a string of the
   * variable name to the value to which it is bound.
   * 
   * You may use the following provided helper functions to manipulate
   * environments, which are just thin wrappers around the Map type
   * in the Scala standard library.  You can use the Scala standard
   * library directly, but these are the only interfaces that you
   * need.
   */



  /* Some useful Scala methods for working with Scala values include:
   * - Double.NaN
   * - s.toDouble (for s: String)
   * - n.isNaN (for n: Double)
   * - n.isWhole (for n: Double)
   * - s (for n: Double)
   * - s format n (for s: String [a format string like for printf], n: Double)
   *
   * You can catch an exception in Scala using:
   * try ... catch { case ... => ... }
   */

  def toNumber(v: Expr): Double = {
    require(isValue(v))
    (v: @unchecked) match {
      case N(n) => n
      case B(n) => if (n) 1 else 0
      case S(s) =>
        if(s.matches("[ ]+")) 0.0 else try s.toDouble catch {case _:Throwable => Double.NaN}
      case Undefined => Double.NaN
      case _ => throw new UnsupportedOperationException
    }
  }

  def toBoolean(v: Expr): Boolean = {
    require(isValue(v))
    (v: @unchecked) match {
      case B(b) => b
      case N(0) => false
      case N(n) => if (n.isNaN()) false else true
      case S("") => false
      case S(_) => true
      case Undefined => false
      case _ => throw new UnsupportedOperationException
    }
  }

  def toStr(v: Expr): String = {
    require(isValue(v))
    (v: @unchecked) match {
      case S(s) => s
      case N(n) => if(n.isWhole()) "%.0f" format n else n.toString
      case B(b) => if(b) "true" else "false"
      case Undefined => "undefined"
      case _ => throw new UnsupportedOperationException
    }
  }

  def eval(env: Env, e: Expr): Expr = {
    e match {
      /* Base Cases */
      case B(_) | S(_) | N(_) => e
      case Var(x) => lookup(env, x)
      case Undefined => Undefined


      /* Inductive Cases */
      case Print(e1) => println(pretty(eval(env, e1))); Undefined
      case Unary(Neg, e1) => N(0 - toNumber(eval(env, e1)))
      case Unary(Not, e1) => B(!toBoolean(eval(env, e1)))
      case Binary(bop,e1,e2) => bop match{
        case Plus => {
          val v1 = eval(env, e1)
          val v2 = eval(env, e2)
          (v1,v2) match {
            case (S(_),S(_)) => S(toStr(v1)+toStr(v2))
            case (S(_),(_)) | ((_),S(_)) => S(toStr(eval(env,v1)) + toStr(eval(env, v2)))
            case ((_),(_)) => N(toNumber(eval(env,v1)) + toNumber(eval(env,v2)))
          }
        }
        case Minus => {
          val v1 = eval(env, e1)
          val v2 = eval(env, e2)
          N(toNumber(eval(env,v1))-toNumber(eval(env,v2)))
        }
        case Times => {
          val v1 = eval(env, e1)
          val v2 = eval(env, e2)
          N(toNumber(eval(env,v1))*toNumber(eval(env,v2)))
        }
        case Div => {
          val v1 = eval(env, e1)
          val v2 = eval(env, e2)
          N(toNumber(eval(env,v1)) / toNumber(eval(env,v2)))
        }
        case Eq => {
          val v1 = eval(env, e1)
          val v2 = eval(env, e2)
          B(toNumber(eval(env,v1)) == toNumber(eval(env,v2)))
        }
        case Ne => {
          val v1 = eval(env, e1)
          val v2 = eval(env, e2)
          B(toNumber(eval(env,v1)) != toNumber(eval(env,v2)))
        }
        case Lt => {
          val v1 = eval(env, e1)
          val v2 = eval(env, e2)
          (v1,v2) match {
            case (S(_),S(_)) => B(toStr(eval(env,v1)) < toStr(eval(env, v2)))
            case (B(_), B(_)) => B(toBoolean(v1) < toBoolean(v2))
            case ((_),(_)) => B(toNumber(eval(env,v1)) < toNumber(eval(env,v2)))
          }
        }
        case Le => {
          val v1 = eval(env, e1)
          val v2 = eval(env, e2)
          (v1,v2) match {
            case (S(_),S(_)) => B(toStr(eval(env,v1)) <= toStr(eval(env, v2)))
            case (B(_), B(_)) => B(toBoolean(v1) <= toBoolean(v2))
            case ((_),(_)) => B(toNumber(eval(env,v1)) <= toNumber(eval(env,v2)))
          }
        }
        case Gt => {
          val v1 = eval(env, e1)
          val v2 = eval(env, e2)
          (v1,v2) match {
            case (S(_),S(_)) => B(toStr(eval(env,v1)) > toStr(eval(env, v2)))
            case (B(_), B(_)) => B(toBoolean(v1) > toBoolean(v2))
            case ((_),(_)) => B(toNumber(eval(env,v1)) > toNumber(eval(env,v2)))
          }
        }
        case Ge => {
          val v1 = eval(env, e1)
          val v2 = eval(env, e2)
          (v1,v2) match {
            case (S(_),S(_)) => B(toStr(eval(env,v1)) >= toStr(eval(env, v2)))
            case (B(_), B(_)) => B(toBoolean(v1) >= toBoolean(v2))
            case ((_),(_)) => B(toNumber(eval(env,v1)) >= toNumber(eval(env,v2)))
          }
        }
        case And => {
          val v1 = eval(env, e1)
          val v2 = eval(env, e2)
          if (toBoolean(eval(env,v1)) == false) eval(env,v1)
          else eval(env,v2)
        }
        case Or => {
          val v1 = eval(env, e1)
          val v2 = eval(env, e2)
          if (toBoolean(eval(env,v1)) == true) eval(env,v1)
          else eval(env,v2)
        }
        case Seq => {
          eval(env,e1)
          return eval(env,e2)
        }
        case _ => throw new UnsupportedOperationException
    }
      case If(e1,e2,e3) => {
        if (toBoolean(eval(env,e1)) == true) eval(env,e2) else eval(env,e3)
      }
      case ConstDecl(x:String, e1:Expr, e2:Expr) => {

        val v:Expr = eval(env,e1)
        eval(extend(env,x,v),e2)
      }
      case _ => throw new UnsupportedOperationException
    }
  }



  /* Interface to run your interpreter from the command-line.  You can ignore what's below. */
  def processFile(file: java.io.File) {
    if (debug) { println("Parsing ...") }

    val expr = Parser.parseFile(file)

    if (debug) {
      println("\nExpression AST:\n  " + expr)
      println("------------------------------------------------------------")
    }

    if (debug) { println("Evaluating ...") }

    val v = eval(expr)

     println(pretty(v))
  }

}
