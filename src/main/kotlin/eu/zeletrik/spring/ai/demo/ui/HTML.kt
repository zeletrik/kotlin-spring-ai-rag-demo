package eu.zeletrik.spring.ai.demo.ui

import eu.zeletrik.spring.ai.demo.data.RagTypes
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.ScriptCrossorigin
import kotlinx.html.a
import kotlinx.html.article
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.div
import kotlinx.html.figure
import kotlinx.html.head
import kotlinx.html.i
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.nav
import kotlinx.html.p
import kotlinx.html.script
import kotlinx.html.section
import kotlinx.html.small
import kotlinx.html.span
import kotlinx.html.strong
import kotlinx.html.title

private const val HTMX_BASE = "https://unpkg.com/htmx.org@1.9.12"
private const val BULMA_VERSION = "1.0.2"
private const val FONT_AWESOME_VERSION = "6.7.2"

@Suppress("LongMethod")
fun HTML.page(content: FlowContent.() -> Unit) {
    head {
        // HTMX
        script(src = HTMX_BASE) {}
        script(src = "$HTMX_BASE/dist/ext/json-enc.js") {}
        // Bulma
        link(rel = "stylesheet", href = "https://cdn.jsdelivr.net/npm/bulma@$BULMA_VERSION/css/bulma.min.css") {}
        // Font Awesome
        script(
            src = "https://cdnjs.cloudflare.com/ajax/libs/font-awesome/$FONT_AWESOME_VERSION/js/all.min.js",
            crossorigin = ScriptCrossorigin.anonymous,
        ) {}

        // Metadata
        title("Spring AI Demo")
        meta(charset = "UTF-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        meta(name = "htmx-config", content = "{\"requestClass\": \"is-skeleton\"}")
    }
    body(classes = "has-navbar-fixed-top") {
        nav(classes = "navbar is-fixed-top") {
            div(classes = "navbar-brand") {
                a(classes = "navbar-item", href = "/demo") {
                    p(classes = "has-text-primary is-size-4 has-text-weight-semibold is-family-monospace") {
                        +"Spring AI Demo"
                    }
                }
                a(
                    classes = "navbar-burger",
                ) {
                    attributes["role"] = "button"
                    attributes["aria-label"] = "menu"
                    attributes["aria-expanded"] = "false"
                    attributes["data-target"] = "navMenu"
                    span { attributes["aria-hidden"] = "true" }
                    span { attributes["aria-hidden"] = "true" }
                    span { attributes["aria-hidden"] = "true" }
                    span { attributes["aria-hidden"] = "true" }
                }
            }
            div(classes = "navbar-menu") {
                attributes["id"] = "navMenu"
                div(classes = "navbar-start") {}
                div(classes = "navbar-end") {
                    div(classes = "navbar-item") {
                        div(classes = "field is-grouped") {
                            p(classes = "control") {
                                a(classes = "button", href = "/chat/${RagTypes.DISABLED}") {
                                    span(classes = "icon") { i(classes = "fa-solid fa-comments") {} }
                                    span { +"Simple Chat" }
                                }
                            }
                            p(classes = "control") {
                                a(classes = "button", href = "/chat/${RagTypes.WEB_SEARCH}") {
                                    span(classes = "icon") { i(classes = "fa-solid fa-globe") {} }
                                    span { +"Web Search" }
                                }
                            }
                            p(classes = "control") {
                                a(classes = "button", href = "/chat/${RagTypes.VECTOR_STORE}") {
                                    span(classes = "icon") { i(classes = "fa-solid fa-database") {} }
                                    span { +"Vector Store" }
                                }
                            }
                            p(classes = "control") {
                                a(classes = "button", href = "/chat/${RagTypes.TOOLS}") {
                                    span(classes = "icon") { i(classes = "fa-solid fa-wrench") {} }
                                    span { +"Tools" }
                                }
                            }
                        }
                    }
                }
            }
        }
        section(classes = "hero mx-3 is-fullheight-with-navbar") {
            div(classes = "container mb-3") {
                content()
            }
        }
    }
}

fun FlowContent.chatCard(isRobot: Boolean, timestamp: String, message: String) {
    val name = if (isRobot) "Assistant" else "User"
    article(classes = "media mb-o") {
        figure(classes = "media-left") {
            span(classes = "icon has-text-primary") {
                if (isRobot) i(classes = "fa-solid fa-robot") else i(classes = "fa-regular fa-user") {}
            }
        }
        div(classes = "media-content") {
            div(classes = "content") {
                p {
                    strong { +name }
                    small(classes = "has-text-grey ml-2") { +timestamp }
                    br {}
                    span(classes = "has-text-white") {
                        +message
                    }
                }
            }
        }
    }
}
