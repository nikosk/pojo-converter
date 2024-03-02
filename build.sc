import mill._
import mill.scalalib._
import mill.scalajslib._
import mill.scalajslib.api._

object root extends RootModule with SbtModule {

  def scalaVersion = "3.3.1"

  override def ivyDeps = Agg(
    ivy"com.lihaoyi::cask:0.9.2",
    ivy"com.lihaoyi::scalatags::0.12.0",
    ivy"com.github.javaparser:javaparser-symbol-solver-core:3.25.8",
    ivy"com.squareup:javawriter:2.5.1"
  )

  override def mainClass: T[Option[String]] = Some("server.Application")

  object test extends SbtModuleTests with TestModule.ScalaTest {
    def ivyDeps = Agg(ivy"org.scalatest::scalatest:3.2.18")
  }
}
