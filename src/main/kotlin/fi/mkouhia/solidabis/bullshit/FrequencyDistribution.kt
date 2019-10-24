package fi.mkouhia.solidabis.bullshit

import java.io.File
import java.io.FileNotFoundException

open class FrequencyDistribution<T>(val frequency: Map<T, Double>) {

    companion object {
        fun <T> fromCountFile(file: File, func: (String) -> T): FrequencyDistribution<T> {
            val counts: Map<T, Int> = file.useLines { line ->
                line
                    .filter { it.isNotBlank() }
                    .map {
                        val parts = it.split(",")
                        val key: T = func(parts[0].trim())
                        val value: Int = parts[1].toInt()
                        key to value
                    }
                    .toMap()
            }

            val totalCount = counts.values.sum()
            val expectedFrequency = counts.mapValues { it.value.toDouble() / totalCount }
            return FrequencyDistribution<T>(expectedFrequency)
        }

        private fun getResourceFile(fileName: String): File {
            val pathName: String =
                this::class.java.classLoader.getResource(fileName)?.path ?: throw FileNotFoundException(fileName)
            return File(pathName)
        }

        val FinnishSyllables: FrequencyDistribution<String> =
            fromCountFile(getResourceFile("FinnishSyllableTypes.csv")) { it }
        val FinnishCharacters: FrequencyDistribution<Char> =
            fromCountFile(getResourceFile("FinnishMonograms.csv")) { it[0].toLowerCase() }

    }
}
