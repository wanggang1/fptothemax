package org.gwgs.fpmax.typeclasses

import cats.effect.IO

/**
  * Building program that are executed sequentially,
  * terminated with some value (aka Monad)
  */
trait Program[F[_]] {
  def finish[A](a: => A): F[A]

  def chain[A, B](fa: F[A], afb: A => F[B]): F[B]

  def map[A, B](fa: F[A], ab: A => B): F[B]
}

object Program {
  def apply[F[_]](implicit F: Program[F]): Program[F] = F

  implicit val ProgramIO = new Program[IO] {
    def finish[A](a: => A): IO[A] = IO.pure(a)

    def chain[A, B](fa: IO[A], afb: A => IO[B]): IO[B] = fa flatMap afb

    def map[A, B](fa: IO[A], ab: A => B): IO[B] = fa map ab
  }
}
