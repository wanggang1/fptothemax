package org.gwgs.fpmax.test

import org.gwgs.fpmax.typeclasses.ConsoleOut

case class TestData(input: List[String], output: List[ConsoleOut], nums: List[Int]) {
  def putStrLn(line: ConsoleOut): (TestData, Unit) =
    (copy(output = line :: output), ())

  def getStrLn: (TestData, String) =
    (copy(input = input.drop(1)), input.head)

  def nextInt(upper: Int): (TestData, Int) =
    (copy(nums = nums.drop(1)), nums.head)

  def showResults = output.reverse.map(_.en).mkString("\n")
}
