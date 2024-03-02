package server

import scalatags.Text.all.*
import scalatags.Text.tags2.title
import scalatags.Text.tags2.main
import scalatags.Text.tags2.section
import scalatags.Text.tags2.nav

def layout(headCnt: Seq[Modifier] = Nil, mainCnt: Seq[Modifier] = Nil) =
  doctype("html")(
    html(
      lang := "en",
      head(
        meta(
          name := "viewport",
          content := "width=device-width, initial-scale=1"
        ),
        favicons,
        script(
          src := "https://unpkg.com/htmx.org@1.9.10",
          integrity := "sha384-D1Kt99CQMDuVetoL1lrYwg5t+9QdHe7NLX/SoJYkXDFfX37iInKRy5xLSi8nO7UC",
          crossorigin := "anonymous"
        ),
        link(
          rel := "stylesheet",
          href := "https://unpkg.com/@picocss/pico@1.5.11/css/pico.min.css",
          integrity:= "sha384-bnKrovjvRzFUSqtvDhPloRir5qWWcx0KhrlfLaR4RXO9IUC+zJBuvclXv/fSdVyk",
          crossorigin := "anonymous"
        ),
        link(
            rel := "stylesheet",
            href:= "/static/resources/styles.css"
        ),
        headCnt
      ),
      body(
        navbarTmpl,
        mainCnt,
        footerTmpl
      )
    )
  )

def index() = layout(
  Seq(
    title("Home page")
  ),
  Seq(
    main(cls := "container", p("main"))
  )
)

private val footerTmpl = footer(
  div(cls := "container", p("footer"))
)

private def navbarTmpl = 
    nav( cls:="container",
        ul(
            li(
                strong("Brand")
            )
        ),
        ul(
            li(a(href:="/", "Link")),
            li(a(href:="/", "Link")),
            li(a(href:="/", role:="button", "Button"))
        )
    )

private val favicons = Seq(
    link(rel:="apple-touch-icon", attr("sizes"):= "180x180", href:="/static/resources/apple-touch-icon.png"),
    link(rel:="icon", attr("sizes") :="32x32", href:="/static/resources/favicon-32x32.png"),
    link(rel:="icon", attr("sizes"):="16x16", href:="/static/resources/favicon-16x16.png"),
)