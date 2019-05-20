package org.gwgs.fpmax.effects


case class IO[A](unsafeRun: () => A) { self =>
  def map[B](f: A => B): IO[B] = IO(() => f(self.unsafeRun()))

  def flatMap[B](f: A => IO[B]): IO[B] = IO(() => f(self.unsafeRun()).unsafeRun())
}

object IO {
  def point[A](a: => A): IO[A] = IO(() => a)

  import org.gwgs.fpmax.typeclasses.{Console, ConsoleOut, Program, Random}

  // used in org.gwgs.fpmax.typeclasses.Main
  implicit val ProgramIO = new Program[IO] {
    def finish[A](a: => A): IO[A] = IO.point(a)

    def chain[A, B](fa: IO[A], afb: A => IO[B]): IO[B] = fa flatMap afb

    def map[A, B](fa: IO[A], ab: A => B): IO[B] = fa map ab
  }

  // used in org.gwgs.fpmax.typeclasses.Main
  implicit val ConsoleIO = new Console[IO] {
    import scala.io.StdIn._

    def putStrLn(line: ConsoleOut): IO[Unit] = IO(() => println(line.en))

    def getStrLn: IO[String] = IO(() => readLine())
  }

  // used in org.gwgs.fpmax.typeclasses.Main
  implicit val RandomIO = new Random[IO] {
    def nextInt(upper: Int): IO[Int] = IO(() => scala.util.Random.nextInt(upper))
  }

}