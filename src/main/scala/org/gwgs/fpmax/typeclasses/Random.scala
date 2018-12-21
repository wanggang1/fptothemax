package org.gwgs.fpmax.typeclasses

import cats.effect.IO

trait Random[F[_]] {
  def nextInt(upper: Int): F[Int]
}

object Random {
  def apply[F[_]](implicit F: Random[F]): Random[F] = F

  implicit val RandomIO = new Random[IO] {
    def nextInt(upper: Int): IO[Int] = IO { scala.util.Random.nextInt(upper) }
  }
}
