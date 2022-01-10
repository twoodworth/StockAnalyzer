package me.tedwoodworth.stockAnalyzer

import yahoofinance.Stock
import java.math.BigDecimal

//stock, price, pv, fv, g, d, fgr, relative))
class StockData(
    val stock: Stock, // stock
    val price: BigDecimal, // current price
    val pv: BigDecimal, // present fair value
    val fv: BigDecimal, // future fair value
    val g: BigDecimal, // growth rate
    val d: BigDecimal, // dividend rate
    val fgr: BigDecimal, // future growth rate. (g + d)
    val relative: BigDecimal // price relative to fair value
)