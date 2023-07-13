package dev.fastcampus.async.c4.threadcost.cpu.intensive

import kotlin.system.measureTimeMillis

fun main() {
    var sum = 0L
    measureTimeMillis {
        for(i in 0..200) {
            for (k in 0..100_000) {
                sum++
            }
        }
    }.let { println(">> sum: $sum, elapsed: $it ms") }
}