package server

import scalatags.Text.all.*
import scalatags.Text.tags2.title
import scalatags.Text.tags2.main
import scalatags.Text.tags2.section
import scalatags.Text.tags2.nav
import java.time.LocalDate

def layout(headCnt: Seq[Modifier] = Nil, mainCnt: Seq[Modifier] = Nil) =
  doctype("html")(
    html(
      lang := "en",
      head(
        meta(charset := "utf-8"),
        meta(
          name := "viewport",
          content := "width=device-width, initial-scale=1"
        ),
        meta(name:="description", content:="A tool to create Converters for POJOs with Lombok builders"),
        favicons,
        script(
          src := "https://unpkg.com/htmx.org@1.9.10",
          integrity := "sha384-D1Kt99CQMDuVetoL1lrYwg5t+9QdHe7NLX/SoJYkXDFfX37iInKRy5xLSi8nO7UC",
          crossorigin := "anonymous"
        ),
        link(
          rel := "stylesheet",
          href := "https://unpkg.com/@picocss/pico@1.5.11/css/pico.min.css",
          integrity := "sha384-bnKrovjvRzFUSqtvDhPloRir5qWWcx0KhrlfLaR4RXO9IUC+zJBuvclXv/fSdVyk",
          crossorigin := "anonymous"
        ),
        link(
          rel := "stylesheet",
          href := "/static/resources/styles.css"
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
    main(
      cls := "container",
      form(
        attr("hx-post") := "/submit",
        attr("hx-target") := "#results",
        div(
          cls := "grid",
          div(
            label(
              "From class",
              textarea(name := "from", required := true)
            )
          ),
          div(
            label(
              "To class",
              textarea(name := "to", required := true)
            )
          )
        ),
        button(`type` := "submit", "Convert")
      ),
      div(
        id := "results"
      )
    )
  )
)

private val footerTmpl = footer(
  div(cls := "container", small(s"©${LocalDate.now().getYear()}"))
)

private def navbarTmpl =
  nav(
    cls := "container",
    ul(
      li(
        strong("DTO Converter")
      )
    )
  )

private val favicons = Seq(
  link(
    rel := "apple-touch-icon",
    attr("sizes") := "180x180",
    href := "/static/resources/apple-touch-icon.png"
  ),
  link(
    rel := "icon",
    attr("sizes") := "32x32",
    href := "/static/resources/favicon-32x32.png"
  ),
  link(
    rel := "icon",
    attr("sizes") := "16x16",
    href := "/static/resources/favicon-16x16.png"
  )
)
