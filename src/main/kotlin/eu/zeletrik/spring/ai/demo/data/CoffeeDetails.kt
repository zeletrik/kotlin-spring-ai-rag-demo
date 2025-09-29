package eu.zeletrik.spring.ai.demo.data

import java.time.LocalDate

/**
 * Represents details about a specific coffee. This data class encapsulates attributes
 * that define the origin, name, tasting notes, roast date, and roaster of the coffee.
 *
 * @property origin The geographical origin of the coffee beans.
 * @property name The name or brand of the coffee.
 * @property tasteNotes A list of descriptive notes about the flavors identified in the coffee.
 * @property roastDate The date when the coffee beans were roasted.
 * @property roaster The name of the roaster responsible for preparing the coffee.
 */
data class CoffeeDetails(
    val origin: String,
    val name: String,
    val tasteNotes: List<String>,
    val roastDate: LocalDate,
    val roaster: String,
)
