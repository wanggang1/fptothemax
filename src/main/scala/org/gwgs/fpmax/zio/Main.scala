package org.gwgs.fpmax.zio

import scalaz.zio._
import org.gwgs.fpmax.typeclasses.ConsoleOut

import scala.util.Try

object Main {

  import Console._
  import ConsoleOut._
  import Random._

  //Helpers
  def parseInt(s: String): Option[Int] = Try(s.toInt).toOption

  def finish[A](a: => A): ZIO[Any, AppError, A] =
    ZIO.effect(a).refineOrDie { case err => AppError(err.getMessage) }

  def putToConsole(text: ConsoleOut): ZIO[Console, AppError, Unit] =
    putStrLn(text).mapError(t => AppError(t.getMessage))

  def getFromConsole: ZIO[Console, AppError, String] =
    getStrLn.mapError(t => AppError(t.getMessage))

  def getInput(title: ConsoleOut): ZIO[Console, AppError, String] =
    for {
      _     <- putToConsole(title)
      input <- getFromConsole
    } yield input

  def randomNumber: ZIO[Random, AppError, Int] =
    nextInt(5).map(_ + 1).mapError(t => AppError(t.getMessage))


  def printResults(input: String, num: Int, name: String): ZIO[Console, AppError, Unit] =
    parseInt(input).fold( putToConsole( YouDidNotEnterNumber ) ) {
      guess =>
        if (guess == num) putToConsole( YouGuessedRight(name) )
        else putToConsole( YouGuessedWrong(name, num) )
    }

  def checkContinue(name: String): ZIO[Console, AppError, Boolean] =
    for {
      input  <- getInput( DoYouWantToContinue(name) )
      answer <- input.toLowerCase match {
        case "y" => finish(true)
        case "n" => finish(false)
        case _   =>
          putToConsole(ThatIsNotValid(name)).flatMap(_ => checkContinue(name))
      }
    } yield answer

  def gameLoop(name: String): ZIO[AppServices, AppError, Unit] =
    for {
      num   <- randomNumber
      input <- getInput(PleaseGuess(name))
      _     <- printResults(input, num, name)
      cont  <- checkContinue(name)
      _     <- if (cont) gameLoop(name) else putToConsole(Bye(name))
    } yield ()

  val program: ZIO[AppServices, AppError, Unit] =
    for {
      name <- getInput(WhatIsYourName)
      _    <- putToConsole(WelcomeToGame(name))
      _    <- gameLoop(name)
    } yield ()

  def main(args: Array[String]): Unit = {
    // The DefaultRuntime has environment Clock with Console with System with Random with Blocking,
    // which does not match AppServices (our own Console with Random)
//    val runtime = new DefaultRuntime {}
//    program.provide(AllServices)
//    runtime.unsafeRun(program)

    import scalaz.zio.internal.PlatformLive
    val myRuntime: Runtime[AppServices] = Runtime(AllServices, PlatformLive.Default)
    myRuntime.unsafeRun(program)
  }

}
