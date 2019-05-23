package org.gwgs.fpmax

package object zio {

  import scalaz.zio.ZIO

  type AppError = Exception

  type AppServices = Console with Random

  type Program[A] = ZIO[AppServices, AppError, A]

}
