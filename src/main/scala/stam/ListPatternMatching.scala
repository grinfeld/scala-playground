package stam

import scala.annotation.tailrec

object ListPatternMatching {

  def main(args: Array[String]): Unit = {
    println(filter(List(1,2,3,4,5,6,7), (p: Int) => p > 2 && p < 6))
    println(filter(List(1), (p: Int) => p == 1))
    println(filter(List(1,2,3,4,5), (p: Int) => p < 1))

    println(insertionSort(List(7,3,9,2)))

    val chars = List('a', 'b', 'a')

    println(
      chars.sorted
        .groupMapReduce(c => c)(c => (c,1))((p1,p2) => (p1._1, p1._2 + p2._2))
        .values.toList.sorted((p1:(Char, Int),p2:(Char, Int)) => p1._2 - p2._2)
    )
  }

  def filter[T](list: List[T], matchRange: T => Boolean): List[T] = {
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
