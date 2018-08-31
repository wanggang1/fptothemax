package org.gwgs.fpmax.test

import org.gwgs.fpmax.typeclasses.{Console, ConsoleOut, Program, Random}

/**
  * State Monad, with state type as TestData
  */
case class TestIO[A](run: TestData => (TestData, A)) { self =>
  def map[B](ab: A => B): TestIO[B] =
    TestIO(t => self.run(t) match { case (t, a) => (t, ab(a)) })

  def flatMap[B](afb: A => TestIO[B]): TestIO[B] =
    TestIO(t => self.run(t) match { case (t, a) => afb(a).run(t) })

  def eval(t: TestData): TestData = run(t)._1
}

object TestIO {
  def point[A](a: => A): TestIO[A] = TestIO(t => (t, a))

  implicit val ProgramTestIO = new Program[TestIO] {
    def finish[A](a: => A): TestIO[A] = TestIO.point(a)

    def chain[A, B](fa: TestIO[A], afb: A => TestIO[B]): TestIO[B] = fa.flatMap(afb)

    def map[A, B](fa: TestIO[A], ab: A => B): TestIO[B] = fa.map(ab)
  }

  implicit val ConsoleTestIO = new Console[TestIO] {
    def putStrLn(line: ConsoleOut): TestIO[Unit] = TestIO(t => t.putStrLn(line))
    def getStrLn: TestIO[String] = TestIO(t => t.getStrLn)
  }

  implicit val RandomTestIO = new Random[TestIO] {
    def nextInt(upper: Int): TestIO[Int] = TestIO(t => t.nextInt(upper))
  }
}
