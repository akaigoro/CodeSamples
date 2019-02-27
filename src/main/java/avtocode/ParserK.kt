package avtocode

import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.util.*


private val COMMA_DELIMITER = ","

@Throws(IOException::class)
fun main(a: Array<String>) {
    val start = System.currentTimeMillis()
    var lineCount = 0
    val fileName = "../sourceFiles/subset-20190226-structure-20170202.csv"
    //        String fileName = "../sourceFiles/data-20190226-structure-20170202.csv";
    BufferedReader(FileReader(fileName)).use { br ->
        var lineNumber = 0
        while (true) {
            val line = br.readLine()
            if (line == null) {
                break
            }
            lineNumber++
            var fullname = line.substring(0, line.indexOf(COMMA_DELIMITER))
            fullname = trimQ(fullname)
            parse(fullname)
            lineCount++
        }
    }
    val end = System.currentTimeMillis()
    println("lineCount=" + lineCount + ", firsts=" + firsts.size)
    println("time=" + (end - start) + " ms")

    val countersC = firsts.values
    val counters = countersC.toTypedArray()
    Arrays.sort(counters) { o1: Counter, o2: Counter -> if (o1.counter > o2.counter) -1 else if (o1.counter < o2.counter) 1 else 0 }
    var important = 0
    for (cnt in counters) {
        if (cnt.counter == 1) {
            break
        }
        important++
        println(cnt.word + " " + cnt.counter)
    }
    println("imortant=$important")
    println("ooos=$ooos")
}

private fun trimQ(fullname: String): String {
    val length = fullname.length
    var first = 0
    var last = length - 1
    while (last > first) {
        if (fullname[first] != '"' || fullname[last] != '"') {
            break
        }
        first++
        last--
    }
    return if (first == 0) {
        fullname
    } else fullname.substring(first, last)
}

internal var firsts = HashMap<String, Counter>()

internal fun parse(fullname: String) {
    val words = fullname.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    firstsAdd(words[0])
}

internal var ooos = 0

private fun firstsAdd(word: String) {
    if ("\"\"ООО" == word) {
        ooos++
    }
    var counter: Counter? = firsts[word]
    if (counter == null) {
        counter = Counter(word)
        firsts[word] = counter
    }
    counter.counter++
}

internal class Counter(val word: String) {
    var counter = 0
}
