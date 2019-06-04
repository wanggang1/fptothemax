package org.gwgs.fpmax

package object zio {

  import scalaz.zio.ZIO

  case class AppError(errMsg: String)

  type AppServices = Console with Random

  type Program[A] = ZIO[AppServices, AppError, A]

}
