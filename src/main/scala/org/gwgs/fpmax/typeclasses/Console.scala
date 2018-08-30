package org.gwgs.fpmax.typeclasses

/**
  * a Class that has ability to read from and write to Console
  */
trait Console[F[_]] {
  def putStrLn(line: String): F[Unit]
  def getStrLn: F[String]
}

object Console {
  def apply[F[_]](implicit F: Console[F]): Console[F] = F
}
