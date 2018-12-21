package org.gwgs.fpmax.typeclasses

import cats.effect.IO

/**
  * a Class that has ability to read from and write to Console
  */
trait Console[F[_]] {
  def putStrLn(line: ConsoleOut): F[Unit]
  def getStrLn: F[String]
}

object Console {
  def apply[F[_]](implicit F: Console[F]): Console[F] = F

  implicit val ConsoleIO = new Console[IO] {
    import scala.io.StdIn._

    def putStrLn(line: ConsoleOut): IO[Unit] = IO { println(line.en) }

    def getStrLn: IO[String] = IO { readLine() }
  }
}


/**
  * Use ConsoleOut to structure the output message,
  * so it becomes more testable, easy internationalization, etc
  */
sealed trait ConsoleOut {
  def en: String
}

object ConsoleOut {
  case class YouGuessedRight(name: String) extends ConsoleOut {
    def en = s"You guessed right, $name!"
  }
  case class YouGuessedWrong(name: String, num: Int) extends ConsoleOut {
    def en = s"You guessed wrong, $name! The number was: $num"
  }
  case class DoYouWantToContinue(name: String) extends ConsoleOut {
    def en = s"Do you want to continue (y/n), $name?"
  }
  case class PleaseGuess(name: String) extends ConsoleOut {
    def en = s"Dear $name, please guess a number from 1 to 5:"
  }
  case class ThatIsNotValid(name: String) extends ConsoleOut {
    def en = s"That is not a valid selection, $name"
  }
  case object WhatIsYourName extends ConsoleOut {
    def en = "What is your name?"
  }
  case class WelcomeToGame(name: String) extends ConsoleOut {
    def en = s"Hello, $name, welcome to the game!"
  }
  case object YouDidNotEnterNumber extends ConsoleOut {
    def en = "You did not enter a number!"
  }
}