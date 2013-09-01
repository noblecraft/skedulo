package com.davezhu.skedulo

import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

/**
 * @author: dzhu
 */
class AnswersTest extends FlatSpec with ShouldMatchers{

  it should "(a1) return correct string for number" in {
    Answers.toString(1) should be("1")
    Answers.toString(4) should be("Hello")
    Answers.toString(5) should be("World")
    Answers.toString(20) should be("HelloWorld")
    Answers.toString(21) should be("21")
  }

}
