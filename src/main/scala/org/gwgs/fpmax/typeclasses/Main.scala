package org.gwgs.fpmax.typeclasses

import org.gwgs.fpmax.effects.IO

import scala.util.Try

object Main {

  def parseInt(s: String): Option[Int] = Try(s.toInt).toOption

  def finish[F[_]: Program, A](a: => A): F[A] = Program[F].finish(a)

  def putStrLn[F[_]: Console](line: String): F[Unit] = Console[F].putStrLn(line)

  def getStrLn[F[_]: Console]: F[String] = Console[F].getStrLn

  def nextInt[F[_]: Random](upper: Int): F[Int] = Random[F].nextInt(upper)


  def checkContinue[F[_]: Program: Console](name: String): F[Boolean] =
    for {
      _      <- putStrLn(s"Do you want to continue, $name?")
      input  <- getStrLn
      answer <- input.toLowerCase match {
                  case "y" => finish(true)
                  case "n" => finish(false)
                  case _   => checkContinue(name)
                }
    } yield answer

  def printResults[F[_]: Console](input: String, num: Int, name: String): F[Unit] =
    parseInt(input).fold(
      putStrLn("You did not enter a number")
    )(guess =>
      if (guess == num) putStrLn("You guessed right, " + name + "!")
      else putStrLn("You guessed wrong, " + name + "! The number was: " + num)
    )

  def gameLoop[F[_]: Program: Console: Random](name: String): F[Unit] =
    for {
      num   <- nextInt(5).map(_ + 1)
      _     <- putStrLn(s"Dear $name, please guess a number from 1 to 5:")
      input <- getStrLn
      _     <- printResults(input, num, name)
      cont  <- checkContinue(name)
      _     <- if (cont) gameLoop(name) else finish(())
    } yield ()

  def main[F[_]: Program: Console: Random]: F[Unit] =
    for {
      _    <- putStrLn("What is your name?")
      name <- getStrLn
      _    <- putStrLn(s"Hello, $name, welcome to the game!")
      _    <- gameLoop(name)
    } yield ()


  def mainIO: IO[Unit] = main[IO]

  def main(args: Array[String]): Unit = mainIO.unsafeRun()

}
