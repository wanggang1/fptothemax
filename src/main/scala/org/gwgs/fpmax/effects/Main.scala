package org.gwgs.fpmax.effects

import org.gwgs.fpmax.models.IO

import scala.util.Try

object Main {

  import scala.io.StdIn._

  def parseInt(s: String): Option[Int] = Try(s.toInt).toOption

  def putStrLn(line: String): IO[Unit] = IO(() => println(line))

  def getStrLn: IO[String] = IO(() => readLine())

  def nextInt(upper: Int): IO[Int] = IO(() => scala.util.Random.nextInt(upper))

  def checkContinue(name: String): IO[Boolean] =
    for {
      _      <- putStrLn(s"Do you want to continue, $name?")
      input  <- getStrLn
      answer <- input.toLowerCase match {
                  case "y" => IO.point(true)
                  case "n" => IO.point(false)
                  case _   => checkContinue(name)
                }
    } yield answer

  def gameLoop(name: String): IO[Unit] =
    for {
      num   <- nextInt(5).map(_ + 1)
      _     <- putStrLn(s"Dear $name, please guess a number from 1 to 5:")
      input <- getStrLn
      _     <- parseInt(input).fold {
                putStrLn("You didn't enter a number")
              } { guess =>
                if (guess == num) putStrLn(s"You guessed right, $name!")
                else putStrLn(s"You guessed wrong, $name! The number was: $num")
              }
      cont  <- checkContinue(name)
      _     <- if (cont) gameLoop(name) else IO.point(())
    } yield ()


  def mainIO: IO[Unit] =
    for {
      _    <- putStrLn("What is your name?")
      name <- getStrLn
      _    <- putStrLn(s"Hello, $name, welcome to the game!")
      _    <- gameLoop(name)
    } yield ()


  def main(args: Array[String]): Unit = mainIO.unsafeRun()

}
