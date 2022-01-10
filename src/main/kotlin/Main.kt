package me.tedwoodworth.stockAnalyzer

import yahoofinance.YahooFinance
import yahoofinance.histquotes.Interval
import java.io.File
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors
import kotlin.math.pow

fun main(args: Array<String>) {
    val data = analyze()
    println("Analysis Complete. Printing out stocks:\n")
    val s = "Rank,Symbol,Name,Price,PV,FV,G,D,FGR,Relative"
    println("\n$s")
    val top = data.stream().sorted(Comparator.comparingDouble<StockData?> { s -> s.relative.toDouble() }.reversed())
        .collect(Collectors.toList())
    var r = top.size
    for (t in top) {
        val s1 = r
        val s2 = t.stock.symbol
        val s3 = t.stock.name.replace(",", "")
        val s4 = "$" + t.price.toDouble()
        val s5 = "$" + t.pv.toDouble()
        val s6 = "$" + t.fv.toDouble()
        val s7 = "" + t.g.toDouble() * 100 + "%"
        val s8 = "" + t.d.toDouble() * 100 + "%"
        val s9 = "" + t.fgr.toDouble() * 100 + "%"
        val s10 = "" + t.relative.toDouble() * 100 + "%"
        println("$s1,$s2,$s3,$s4,$s5,$s6,$s7,$s8,$s9,$s10")
        r--
    }
}

fun analyze(): HashSet<StockData> {
//    val set = getDumbStockData()
//        .stream()
//        .map { a : Data -> a.ticker }
//        .collect(Collectors.toSet())
    val set = File("C:\\Users\\bloon\\IdeaProjects\\Analyzer\\src\\main\\resources\\Tickers.csv")
        .readLines()
        .stream()
        .map { a: String -> a.split(",") }
        .filter { a: List<String>? -> a?.get(1)?.equals("USA") ?: false }
        .map { a: List<String> -> a[0] }
        .collect(Collectors.toSet())
    val repeats = File("C:\\Users\\bloon\\IdeaProjects\\Analyzer\\src\\main\\resources\\repeats.csv").readLines()
    set.removeIf { a: String -> repeats.contains(a) }
    val stockData = HashSet<StockData>()
    val size = set.size
    var count = -1
    val calendar = Calendar.getInstance()
    val y = 5
    calendar.add(Calendar.YEAR, -(y * 1.5).toInt())
    for (t in set) {
        print("\n$count / $size stocks analyzed. $t currently being analyzed")
        count++
        try {
            val stock = YahooFinance.get(t) ?: continue // skip if null
            val quote = stock.quote ?: continue // skip if null
            val stats = stock.stats ?: continue // skip if null
//            when (stock.stockExchange) {
//                "NYSE", "NASDAQ", "AMEX", "NasdaqGS", "NasdaqGM", "NYSE American", "NasdaqCM", "NYSEArca" -> {}
//                else -> continue
//            }
            val price = quote.price ?: continue
            price.setScale(5)

            val feps = stats.epsEstimateCurrentYear ?: continue
            feps.setScale(5)

            val fpe = price / feps
            val d = (stock.dividend.annualYieldPercent ?: BigDecimal.ZERO) / BigDecimal.valueOf(100).setScale(5)
            val ceps = stats.eps ?: continue
            ceps.setScale(5)
            val history = stock.getHistory(calendar, Interval.MONTHLY) ?: continue
            if (history.size < 13) continue
            val old = history[0].open.setScale(5)
            val g = (BigDecimal.ONE + (price - old) / old).toDouble().pow((12.0 / (history.size - 2)))
                .toBigDecimal() - BigDecimal.ONE
            val fgr = (BigDecimal.ONE + g) * (BigDecimal.ONE + d) - BigDecimal.ONE
            val r = BigDecimal.valueOf(0.15).setScale(5)
            val fv = fpe * ceps * (BigDecimal.ONE + g).pow(y)
            if (fv <= BigDecimal.ZERO) {
                stockData.add(
                    StockData(
                        stock,
                        price,
                        BigDecimal.ZERO,
                        BigDecimal.ZERO,
                        g,
                        d,
                        fgr,
                        BigDecimal.valueOf(999999)
                    )
                )
            } else {
                val pv = fv / (BigDecimal.ONE + r).pow(y)
                val relative = (price - pv) / pv
                stockData.add(StockData(stock, price, pv, fv, g, d, fgr, relative))
            }
        } catch (_: Exception) {
        }
    }

    println("All stocks analyzed.")
    return stockData
}




