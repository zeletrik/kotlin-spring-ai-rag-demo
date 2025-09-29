package eu.zeletrik.spring.ai.demo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SpringAiDemoApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
    runApplication<SpringAiDemoApplication>(*args)
}
