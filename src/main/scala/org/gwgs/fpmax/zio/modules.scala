package org.gwgs.fpmax.zio

import org.gwgs.fpmax.typeclasses.ConsoleOut

import scalaz.zio._

object Console {

  trait Service {
    def putStrLn(line: ConsoleOut): Task[Unit]
    def getStrLn: Task[String]
  }

  //helpers
  def putStrLn(line: ConsoleOut): ZIO[Console, Throwable, Unit] =
    ZIO.accessM(_.console.putStrLn(line))

  def getStrLn: ZIO[Console, Throwable, String] =
    ZIO.accessM(_.console.getStrLn)
}

trait Console {
  def console: Console.Service
}

trait ConsoleLive extends Console {
  import scala.io.StdIn._

  lazy val console: Console.Service = new Console.Service {
    override def putStrLn(line: ConsoleOut): Task[Unit] =
      Task(println(line.en))
    override def getStrLn: Task[String] =
      Task(readLine())
  }
}


object Random {

  trait Service {
    def nextInt(upper: Int): Task[Int]
  }

  //helper
  def nextInt(upper: Int): ZIO[Random, Throwable, Int] =
    ZIO.accessM(_.random.nextInt(upper))
}

trait Random {
  def random: Random.Service
}

trait RandomLive extends Random {

  import scala.util.{Random => ScalaRandom}

  lazy val random: Random.Service = new Random.Service {
    override def nextInt(upper: Int): Task[Int] =
      Task.succeed(ScalaRandom.nextInt(upper))
  }

}

// environment for the whole program
object AllServices extends ConsoleLive with RandomLive
