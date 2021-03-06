package stam

import java.util.NoSuchElementException

import scala.annotation.tailrec
import scala.math.Ordering

object ListPatternMatching {

  trait MyPairOrdering extends Ordering[(Char, Int)] {
    def compare(p1: (Char, Int), p2: (Char, Int)): Int =
      Ordering.Int.compare(p1._2, p2._2)
  }

  implicit object ListPatternMatching extends MyPairOrdering

  def main(args: Array[String]): Unit = {

    println(filterByRange(List(1,2,3,4,5,6,7), (p: Int) => p > 2 && p < 6))
    println(filterByRange(List(1), (p: Int) => p == 1))
    println(filterByRange(List(1,2,3,4,5), (p: Int) => p < 1))

    println(filterByRangeOther(List(1,2,3,4,5,6,7))(p => p > 2 && p < 6))
    println(filterByRangeOther(List(1))(p => p == 1))
    println(filterByRangeOther(List(1,2,3,4,5))(p => p < 1))

    println(insertionSort(List(7,3,9,2)))

    val chars = List('a', 'b', 'a')

    println(
      chars.sorted
        .groupMapReduce(c => c)(c => (c,1))((p1,p2) => (p1._1, p1._2 + p2._2))
        .values.toList.sorted // calls MyPairOrdering -> this is extended implicitly with MyPairOrdering
    )

    println(flattern(List(List(1,1), 2, List(3, List(5,8)))))

    println(insertionSort(List(10,4,14,1,5)))

    println(concat(List(1,2,3), List(4)))

    println(isPrime(2))
    println(isPrime(3))
    println(isPrime(8))
    println(isPrime(11))
    println(isPrime(14))
    println(isPrime(23))

    println(sumOfSomething(5))
    println(sumOfSomethingElse(5))

    println(new Poly(1 -> 2, 3 -> 4) + new Poly(0 -> 3, 1 -> 2))

    var mnem = Map('2' -> "ABC", '3' -> "DEF", '4' -> "GHI", '5' -> "JKL", '6' -> "MNO", '7' -> "PQRS", '8' -> "TUV", '9' -> "WYZ")

    def charCode(): Map[Char, Char] = {
      for ((digit, str) <- mnem; ltr <- str) yield ltr -> digit
    }

    def wordCode(word: String): String = {
      word.toUpperCase map charCode()
      // same as above word.toUpperCase.map(c => charCode()(c))
    }
    println(wordCode("Java"))
  }



  class Poly(val terms0: Map[Int, Double]) {
    def this(bindings: (Int, Double)*) = this(bindings.toMap)
    val terms: Map[Int, Double] = terms0 withDefaultValue(0.0)
    def + (other: Poly): Poly = {
      new Poly(terms ++ other.terms.map(adjust))
    }

    def adjust(term: (Int, Double)) : (Int, Double) = {
      val (exp, coeff) = term
      exp -> (coeff + terms(exp))
    }

    override def toString: String = {
      (for ((exp, coeff) <- terms.toList.sorted.reverse) yield coeff + "x^" + exp) mkString " + "
    }
  }

  def sumOfSomething(n: Int): Seq[(Int, Int)] = {
    (1 until n).flatMap(i =>
      (1 until i) map (j => (i, j))).filter(p => isPrime(p._1 + p._2))
  }

  def sumOfSomethingElse(n: Int): Seq[(Int, Int)] = {
    for {
      i <- 1 until n
      j <- 1 until i
      if isPrime(i + j)
    } yield (i,j)
  }

  def isPrime(n :Int): Boolean = {
    if (n == 1 || n == 2 || n == 3) true
    else (n/2-1 until n).forall(x => n % x != 0)
  }

  def concat[T](xs: List[T], ys: List[T]): List[T] =
    (xs foldRight ys) (_ :: _)

  def removeAt[T](xs : List[T], n: Int) : List[T] = {
    if (n < 0 || n >= xs.length) throw new NoSuchElementException
    xs match {
      case head :: _ if n==0 => List(head)
      case _ :: tail => removeAt(tail, n -1) ::: tail.tail
    }
  }

  def flattern(xs :  List[Any]): List[Any] =  {
    xs match {
      case List() => List()
      case y :: xs => y match {
          case l:List[Any] => flattern(l) ::: flattern(xs)
          case any => List(any) ::: flattern(xs)
        }
    }
  }

  def filterByRangeOther[T](list: List[T])(matchRange: T => Boolean): List[T] = {
    val from = list.indexWhere(matchRange)
    val res = list.dropWhile(t => !matchRange(t)).takeWhile(matchRange)
    if (res == Nil) Nil
    else if (from <= 1) res
    else list(from-1) :: res
  }

  def filterByRange[T](list: List[T], matchRange: T => Boolean): List[T] = {
    @tailrec
    def loop (res: List[T], working: List[T]): List[T] = working match {
      // case -> a.k.k.a instance of
      case Nil =>  res
      // case when prev not matches and current does
      case prev :: current :: tail if !matchRange(prev) && matchRange(current) => loop(current :: prev :: res, tail)
      // case if only current match
      case current :: tail if matchRange(current) => loop(current :: res, tail)
      // since we search for sequence -> we can stop for first not match and res is not empty
      case :: (_, _) if res.nonEmpty => res
      // rest of cases
      case :: (_, tail) => loop(res, tail)
    }

    loop (Nil, list).reverse
  }

  def insertionSort(list: List[Int]) : List[Int] = {

    def insert(x: Int, xs: List[Int]): List[Int] = xs match {
      case Nil => List(x)
      case y :: _ if x <= y => x :: xs
      case y :: tail if x > y => y :: insert(x, tail)
    }

    list match  {
      case Nil => Nil
      case head :: tail => insert(head, insertionSort(tail))
    }
  }
}
