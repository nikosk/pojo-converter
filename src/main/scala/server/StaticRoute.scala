package server

case class StaticRoutes(prefix: String = "")(implicit cc: castor.Context, log: cask.Logger)
    extends cask.Routes {

  @cask.staticResources(s"${prefix}/static/resources/")
  def staticResources() = { "static" }

  @cask.staticFiles(s"${prefix}/static/files/")
  def staticFiles() = { "./static" }

  @cask.get("/")
  def home() = {
    server.index()
  }

  initialize()
}
