package org.gwgs.fpmax.cats

import cats.Monad
import cats.implicits.{toFlatMapOps, toFunctorOps}
import cats.effect.IO
import org.gwgs.fpmax.typeclasses.{Console, ConsoleOut, Random}

import scala.util.Try

/**
  * Use Monad Instead of typeclasses.Program
  *
  * https://medium.com/@agaro1121/free-monad-vs-tagless-final-623f92313eac
  * https://softwaremill.com/free-tagless-compared-how-not-to-commit-to-monad-too-early/
  */
object Main {

  import ConsoleOut._

  //Helpers
  def parseInt(s: String): Option[Int] = Try(s.toInt).toOption

  def finish[F[_]: Monad, A](a: => A): F[A] = Monad[F].point(a)

  def putStrLn[F[_]: Console](line: ConsoleOut): F[Unit] = Console[F].putStrLn(line)

  def getStrLn[F[_]: Console]: F[String] = Console[F].getStrLn

  def nextInt[F[_]: Random](upper: Int): F[Int] = Random[F].nextInt(upper)


  def checkContinue[F[_]: Monad: Console](name: String): F[Boolean] =
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

  def gameLoop[F[_]: Monad: Console: Random](name: String): F[Unit] =
    for {
      num   <- nextInt(5).map(_ + 1)
      _     <- putStrLn( PleaseGuess(name) )
      input <- getStrLn
      _     <- printResults(input, num, name)
      cont  <- checkContinue(name)
      _     <- if (cont) gameLoop(name) else finish(())
    } yield ()

  def main[F[_]: Monad: Console: Random]: F[Unit] =
    for {
      _    <- putStrLn( WhatIsYourName )
      name <- getStrLn
      _    <- putStrLn( WelcomeToGame(name) )
      _    <- gameLoop(name)
    } yield ()


////////////////////////////// IO /////////////////////////////////

  def mainIO: IO[Unit] = main[IO]

  def main(args: Array[String]): Unit = mainIO.unsafeRunSync

//////////////////////////// TestIO ///////////////////////////////
  import org.gwgs.fpmax.test.{TestData, TestIO}
  //TODO: fix TestIO with cats.Monad

}
