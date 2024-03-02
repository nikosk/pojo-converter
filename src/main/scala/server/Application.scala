package server

object Application extends cask.Main {

  val allRoutes = Seq(
    server.StaticRoutes()
  )

  override def debugMode = true

}
