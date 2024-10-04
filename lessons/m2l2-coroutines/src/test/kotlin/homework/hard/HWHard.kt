package ru.otus.otuskotlin.coroutines.homework.hard

import kotlinx.coroutines.*
import ru.otus.otuskotlin.coroutines.homework.hard.dto.Dictionary
import java.io.File
import kotlin.test.Test

class HWHard {
    @Test
    fun hardHw() {
        val dictionaryApi = DictionaryApi()
        val words = FileReader.readFile().split(" ", "\n").toSet()

        val dictionaries = findWords(dictionaryApi, words, Locale.EN)

        dictionaries.filterNotNull().map { dictionary ->
            print("For word ${dictionary.word} i found examples: ")
            println(
                dictionary.meanings
                    .mapNotNull { definition ->
                        val r = definition.definitions
                            .mapNotNull { it.example.takeIf { it?.isNotBlank() == true } }
                            .takeIf { it.isNotEmpty() }
                        r
                    }
                    .takeIf { it.isNotEmpty() }
            )
        }
    }

    private fun findWords(
        dictionaryApi: DictionaryApi,
        words: Set<String>,
        @Suppress("SameParameterValue") locale: Locale
    ): List<Dictionary?> {
        // make some suspensions and async
        val result:List<Dictionary?>

        runBlocking {
            val deferredResults = words.map { word ->
                async (Dispatchers.IO) {
                    dictionaryApi.findWord(locale, word)
                }
            }

            result = deferredResults.awaitAll()
        }

        return result
    }


    object FileReader {
        fun readFile(): String =
            File(
                this::class.java.classLoader.getResource("words.txt")?.toURI()
                    ?: throw RuntimeException("Can't read file")
            ).readText()
    }
}
