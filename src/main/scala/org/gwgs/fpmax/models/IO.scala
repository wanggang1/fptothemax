package org.gwgs.fpmax.models

/**
  *
  * TODO: use scalaz or zio
  */
case class IO[A](unsafeRun: () => A) { self =>
  def map[B](f: A => B): IO[B] = IO(() => f(self.unsafeRun()))

  def flatMap[B](f: A => IO[B]): IO[B] = IO(() => f(self.unsafeRun()).unsafeRun())
}

object IO {
  def point[A](a: => A): IO[A] = IO(() => a)
}