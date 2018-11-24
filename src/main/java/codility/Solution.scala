package codility

import scala.collection.JavaConverters._

// you can write to stdout for debugging purposes, e.g.
// println("this is a debug message")

object Solution {
  def solution(a: Array[Int]): Int = {
    // write your code in Scala 2.12
    var min = a(0)
    for( x <- 1 until a.length ){
      if (x<min) {
        min = x
      }
    }
    min-1
  }
}