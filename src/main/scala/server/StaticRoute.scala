package server

import scalatags.Text.all._
import server.service.ConverterService
import scala.util.Failure
import scala.util.Try
import scala.util.Success

case class StaticRoutes(prefix: String = "")(implicit
    cc: castor.Context,
    log: cask.Logger
) extends cask.Routes {


  @cask.staticResources(s"${prefix}/static/resources/")
  def staticResources() = { "static" }

  @cask.staticFiles(s"${prefix}/static/files/")
  def staticFiles() = { "./static" }

  @cask.get("/")
  def home() = {
    server.index()
  }

  @cask.postForm("/submit")
  def submit(from: cask.FormValue, to: cask.FormValue) = {
    Try(ConverterService.convert(from.value, to.value)) match {
      case Success(res) => pre(code(res))
      case Failure(e) => pre(e.getMessage)
    }
  }

  initialize()
}
