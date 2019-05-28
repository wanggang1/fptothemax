package org.gwgs.fpmax.typeclasses

import org.gwgs.fpmax.effects.IO
import org.gwgs.fpmax.test.{TestData, TestIO}

import scala.util.Try

object Main {
  import ConsoleOut._

  //Helpers
  def parseInt(s: String): Option[Int] = Try(s.toInt).toOption

  def finish[F[_]: Program, A](a: => A): F[A] = Program[F].finish(a)

  def putStrLn[F[_]: Console](line: ConsoleOut): F[Unit] = Console[F].putStrLn(line)

  def getStrLn[F[_]: Console]: F[String] = Console[F].getStrLn

  def nextInt[F[_]: Random](upper: Int): F[Int] = Random[F].nextInt(upper)


  def checkContinue[F[_]: Program: Console](name: String): F[Boolean] =
    for {
      _      <- putStrLn( DoYouWantToContinue(name) )
      input  <- getStrLn
      answer <- input.toLowerCase match {
                  case "y" => finish(true)
                  case "n" => finish(false)
                  case _   =>
                    putStrLn(ThatIsNotValid(name)).flatMap(_ => checkContinue(name))
                }
    } yield answer

  def printResults[F[_]: Console](input: String, num: Int, name: String): F[Unit] =
    parseInt(input).fold(
      putStrLn( YouDidNotEnterNumber )
    )(guess =>
      if (guess == num) putStrLn( YouGuessedRight(name) )
      else putStrLn( YouGuessedWrong(name, num) )
    )

  def gameLoop[F[_]: Program: Console: Random](name: String): F[Unit] =
    for {
      num   <- nextInt(5).map(_ + 1)
      _     <- putStrLn( PleaseGuess(name) )
      input <- getStrLn
      _     <- printResults(input, num, name)
      cont  <- checkContinue(name)
      _     <- if (cont) gameLoop(name) else putStrLn(Bye(name))
    } yield ()

  def main[F[_]: Program: Console: Random]: F[Unit] =
    for {
      _    <- putStrLn( WhatIsYourName )
      name <- getStrLn
      _    <- putStrLn( WelcomeToGame(name) )
      _    <- gameLoop(name)
    } yield ()


////////////////////////////// IO /////////////////////////////////

  def mainIO: IO[Unit] = main[IO]

  def main(args: Array[String]): Unit = mainIO.unsafeRun()


//////////////////////////// TestIO ///////////////////////////////
  val TestExample =
    TestData(
      input  = "John" :: "1" :: "n" :: Nil,
      output = Nil,
      nums   = 0 :: Nil
    )

  def mainTestIO: TestIO[Unit] = main[TestIO]

  /**
    *sbt console
    * >import org.gwgs.fpmax.typeclasses.Main
    * >Main.runTest
    res0: String =
    What is your name?
    Hello, John, welcome to the game!
    Dear John, please guess a number from 1 to 5:
    You guessed right, John!
    Do you want to continue (y/n), John?
    Bye!
    */
  def runTest = mainTestIO.eval(TestExample).showResults

}
