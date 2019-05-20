package org.gwgs.fpmax.typeclasses

trait Random[F[_]] {
  def nextInt(upper: Int): F[Int]
}

object Random {
  def apply[F[_]](implicit F: Random[F]): Random[F] = F

  import cats.effect.IO
  // used in org.gwgs.fpmax.cats.Main
  implicit val RandomIO = new Random[IO] {
    def nextInt(upper: Int): IO[Int] = IO { scala.util.Random.nextInt(upper) }
  }
}
