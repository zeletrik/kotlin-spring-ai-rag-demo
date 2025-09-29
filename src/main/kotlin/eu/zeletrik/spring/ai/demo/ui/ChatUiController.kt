package eu.zeletrik.spring.ai.demo.ui

import eu.zeletrik.spring.ai.demo.chat.ConversationFacade
import eu.zeletrik.spring.ai.demo.data.CoffeeDetails
import kotlinx.html.ButtonType
import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.InputType
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.form
import kotlinx.html.h2
import kotlinx.html.html
import kotlinx.html.i
import kotlinx.html.input
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.stream.appendHTML
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@RestController
@RequestMapping
class ChatUiController(private val conversationFacade: ConversationFacade) {

    @PostMapping("/ingest")
    suspend fun ingestion(@RequestBody data: CoffeeDetails) = conversationFacade.ingest(data)

    @GetMapping
    suspend fun index(): ResponseEntity<String> = respondHtml {
        page {
            h2(classes = "title") { +"Spring AI Demo" }
            div {
                +"Choose a chat type from the menu"
            }
        }
    }

    @GetMapping("/chat/{type}")
    suspend fun chat(@PathVariable type: String) = respondHtml {
        val chatConversationId = UUID.randomUUID().toString()
        page {
            div(classes = "is-10-tablet is-8-desktop is-7-widescreen") {
                div(classes = "box has-background-grey-dark") {
                    div(classes = "level mb-3") {
                        div(classes = "level-left") {
                            div(classes = "level-item") {
                                span(classes = "icon has-text-primary mr-2") {
                                    i(classes = "fa-regular fa-comments") {}
                                }
                                p(classes = "title is-5 mb-0") { +"Chat [RAG: ${type.uppercase()}]" }
                            }
                        }
                        div(classes = "level-right") {
                            div(classes = "level-item") {
                                span(classes = "tag is-light is-info") { +chatConversationId }
                            }
                        }
                    }
                    div(classes = "chat-messages p-3 has-background-black-ter mb-3") {
                        attributes["id"] = "chatMessages"
                        attributes["style"] = "max-height: 60vh; overflow-y: auto; border-radius: 6px;"
                        chatCard(true, LocalDateTime.now().format(), "Hello, how can I help you today?")
                    }
                    // Input form
                    form {
                        attributes["hx-post"] = "/conversation/$chatConversationId/ask/$type"
                        attributes["hx-target"] = "#chatMessages"
                        attributes["hx-swap"] = "beforeend"
                        attributes["hx-ext"] = "json-enc"
                        attributes["hx-on::after-request"] = "this.reset()"
                        div(classes = "field has-addons") {
                            div(classes = "control is-expanded") {
                                input(type = InputType.text, name = "message", classes = "input is-medium") {
                                    placeholder = "Type your message..."
                                    attributes["autocomplete"] = "off"
                                    attributes["required"] = "true"
                                }
                            }
                            div(classes = "control") {
                                button(type = ButtonType.submit, classes = "button is-primary is-medium") {
                                    span(classes = "icon") { i(classes = "fa-regular fa-paper-plane") {} }
                                    span { +"Send" }
                                }
                            }
                        }
                        p(classes = "help is-size-7 has-text-grey mt-1") { +"Press Enter to send" }
                    }
                }
            }
        }
    }

    @PostMapping("/conversation/{chatConversationId}/ask/{type}")
    suspend fun sendMessage(
        @PathVariable chatConversationId: String,
        @PathVariable type: String,
        @RequestBody data: ChatMessage
    ): ResponseEntity<String> {
        val userTime = LocalDateTime.now().format()
        val response = conversationFacade.ask(data.message, type, chatConversationId)
        return respondHtmlContent {
            chatCard(false, userTime, data.message)
            chatCard(true, LocalDateTime.now().format(), response)
        }
    }

    private inline fun respondHtml(
        status: Int = 200,
        crossinline block: HTML.() -> Unit,
    ): ResponseEntity<String> =
        ResponseEntity
            .status(status)
            .contentType(MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8))
            .body(
                buildString {
                    append("<!DOCTYPE html>\n")
                    appendHTML().html(block = block)
                },
            )

    private inline fun respondHtmlContent(
        status: Int = 200,
        crossinline block: FlowContent.() -> Unit,
    ): ResponseEntity<String> =
        ResponseEntity
            .status(status)
            .contentType(MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8))
            .body(
                buildString {
                    appendHTML().body(block = block)
                },
            )

    private fun LocalDateTime.format(): String =
        this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

    data class ChatMessage(val message: String)
}
