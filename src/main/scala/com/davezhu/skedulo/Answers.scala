package com.davezhu.skedulo

/**
 * @author: dzhu
 */
object Answers {

  def a1 {
    for (i <- 1 to 100) {
      print(toString(i))
    }
  }

  def toString(i: Int) = {
    val divBy4 = i % 4 == 0
    val divBy5 = i % 5 == 0
    if (divBy4 && divBy5) "HelloWorld"
    else if (divBy4) "Hello"
    else if (divBy5) "World"
    else i.toString
  }

}
