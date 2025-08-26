package com.crossmint.megaverse.pattern

import com.crossmint.megaverse.api.GoalMapResponse
import com.crossmint.megaverse.domain.Cometh
import com.crossmint.megaverse.domain.ComethDirection
import com.crossmint.megaverse.domain.Polyanet
import com.crossmint.megaverse.domain.Position
import com.crossmint.megaverse.domain.Soloon
import com.crossmint.megaverse.domain.SoloonColor

/**
 * Dynamic pattern generator that reads from the goal map API.
 * This automatically adapts to whatever pattern the API returns.
 */
class GoalMapDrivenPatternGenerator {

    fun generateFromGoalMap(goalMap: GoalMapResponse): GoalMapDrivenMegaverse {
        if (goalMap.error != null || goalMap.goal == null) {
            throw IllegalArgumentException("Invalid goal map: ${goalMap.error}")
        }

        val polyanets = mutableListOf<Polyanet>()
        val soloons = mutableListOf<Soloon>()
        val comeths = mutableListOf<Cometh>()
        val emptySpaces = mutableListOf<Position>()

        goalMap.goal.forEachIndexed { rowIndex, row ->
            row.forEachIndexed { colIndex, cell ->
                val position = Position(rowIndex, colIndex)

                when (cell) {
                    "POLYANET" -> polyanets.add(Polyanet(position))
                    "WHITE_SOLOON" -> soloons.add(Soloon(position, SoloonColor.WHITE))
                    "BLUE_SOLOON" -> soloons.add(Soloon(position, SoloonColor.BLUE))
                    "RED_SOLOON" -> soloons.add(Soloon(position, SoloonColor.RED))
                    "PURPLE_SOLOON" -> soloons.add(Soloon(position, SoloonColor.PURPLE))
                    "RIGHT_COMETH" -> comeths.add(Cometh(position, ComethDirection.RIGHT))
                    "LEFT_COMETH" -> comeths.add(Cometh(position, ComethDirection.LEFT))
                    "UP_COMETH" -> comeths.add(Cometh(position, ComethDirection.UP))
                    "DOWN_COMETH" -> comeths.add(Cometh(position, ComethDirection.DOWN))
                    "SPACE" -> {
                        emptySpaces.add(position)
                    }

                    else -> {
                        // Unknown cell type - log it but continue
                        println("Warning: Unknown cell type '$cell' at position ($rowIndex, $colIndex)")
                        emptySpaces.add(position)
                    }
                }
            }
        }

        return GoalMapDrivenMegaverse(
            polyanets = polyanets,
            soloons = soloons,
            comeths = comeths,
            emptySpaces = emptySpaces,
            goalMap = goalMap.goal
        )
    }

    /**
     * Analyzes the goal map and provides statistics.
     */
    fun analyzeGoalMap(goalMap: GoalMapResponse): GoalMapAnalysis {
        if (goalMap.error != null || goalMap.goal == null) {
            return GoalMapAnalysis(error = goalMap.error)
        }

        var polyanetCount = 0
        var soloonCount = 0
        var comethCount = 0
        var spaceCount = 0
        var unknownCount = 0

        goalMap.goal.forEach { row ->
            row.forEach { cell ->
                when (cell) {
                    "POLYANET" -> polyanetCount++
                    "WHITE_SOLOON", "BLUE_SOLOON", "RED_SOLOON", "PURPLE_SOLOON" -> soloonCount++
                    "RIGHT_COMETH", "LEFT_COMETH", "UP_COMETH", "DOWN_COMETH" -> comethCount++
                    "SPACE" -> spaceCount++
                    else -> unknownCount++
                }
            }
        }

        return GoalMapAnalysis(
            gridSize = goalMap.goal.size,
            polyanetCount = polyanetCount,
            soloonCount = soloonCount,
            comethCount = comethCount,
            spaceCount = spaceCount,
            unknownCount = unknownCount,
            totalObjects = polyanetCount + soloonCount + comethCount,
            totalPositions = polyanetCount + soloonCount + comethCount + spaceCount
        )
    }
}

/**
 * Represents a megaverse generated from the goal map.
 */
data class GoalMapDrivenMegaverse(
    val polyanets: List<Polyanet>,
    val soloons: List<Soloon>,
    val comeths: List<Cometh>,
    val emptySpaces: List<Position>,
    val goalMap: List<List<String>>
) {
    val totalObjects: Int
        get() = polyanets.size + soloons.size + comeths.size

    val totalPositions: Int
        get() = totalObjects + emptySpaces.size
}

/**
 * Analysis of the goal map structure.
 */
data class GoalMapAnalysis(
    val gridSize: Int = 0,
    val polyanetCount: Int = 0,
    val soloonCount: Int = 0,
    val comethCount: Int = 0,
    val spaceCount: Int = 0,
    val unknownCount: Int = 0,
    val totalObjects: Int = 0,
    val totalPositions: Int = 0,
    val error: String? = null
) {
    val isValid: Boolean
        get() = error == null && gridSize > 0
}
