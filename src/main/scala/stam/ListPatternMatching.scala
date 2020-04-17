package stam

import java.util.NoSuchElementException

import scala.annotation.tailrec

object ListPatternMatching {

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
        .values.toList.sorted((p1:(Char, Int),p2:(Char, Int)) => p1._2 - p2._2)
    )

    println(flattern(List(List(1,1), 2, List(3, List(5,8)))))

    println(insertionSort(List(10,4,14,1,5)))

    println(concat(List(1,2,3), List(4)))
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
