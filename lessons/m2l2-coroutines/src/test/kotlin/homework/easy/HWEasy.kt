package ru.otus.otuskotlin.coroutines.homework.easy

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

class HWEasy {

    @Test
    fun easyHw() {
        val numbers = generateNumbers()
        val toFind = 10
        val toFindOther = 1000

        val foundNumbers:List<Int>

        runBlocking {
            foundNumbers = coroutineScope {
                val num1 = async (Dispatchers.Default) { findNumberInList(toFind, numbers) }
                val num2 = async (Dispatchers.Default) { findNumberInList(toFindOther, numbers) }
                listOf(
                    num1.await(),
                    num2.await(),
                )
            }
        }


        foundNumbers.forEach {
            if (it != -1) {
                println("Your number $it found!")
            } else {
                println("Not found number $toFind || $toFindOther")
            }
        }
    }
}
