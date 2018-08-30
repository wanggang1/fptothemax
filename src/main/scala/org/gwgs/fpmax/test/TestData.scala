package org.gwgs.fpmax.test

case class TestData(input: List[String], output: List[String], nums: List[Int]) {
  def putStrLn(line: String): (TestData, Unit) =
    (copy(output = line :: output), ())

  def getStrLn: (TestData, String) =
    (copy(input = input.drop(1)), input.head)

  def nextInt(upper: Int): (TestData, Int) =
    (copy(nums = nums.drop(1)), nums.head)

  def showResults = output.reverse.mkString("\n")
}
