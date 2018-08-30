package org.gwgs.fpmax

package object typeclasses {

  implicit class ProgramSyntax[F[_], A](val fa: F[A]) extends AnyVal {
    def map[B](f: A => B)(implicit F: Program[F]): F[B] = F.map(fa, f)
    def flatMap[B](afb: A => F[B])(implicit F: Program[F]): F[B] = F.chain(fa, afb)
  }

}
