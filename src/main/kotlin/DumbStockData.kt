package me.tedwoodworth.stockAnalyzer

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.net.URL

@Serializable
class Data(
    val ticker: String,
    val name: String,
    val is_etf: Boolean?,
    val exchange: String
)

fun getDumbStockData(): List<Data> {
    val response = URL("https://dumbstockapi.com/stock?exchanges=AMEX,NASDAQ,NYSE")
        .readText()

    return Json.decodeFromString(response)
}