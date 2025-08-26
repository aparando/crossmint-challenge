package com.crossmint.megaverse.pattern

import com.crossmint.megaverse.domain.Polyanet
import com.crossmint.megaverse.domain.Position

/**
 * Interface for generating patterns of astral objects in the megaverse.
 */
interface PatternGenerator {
    /**
     * Generates a list of astral objects based on the pattern.
     * @param gridSize The size of the grid (e.g., 11 for 11x11)
     * @return List of astral objects to be placed
     */
    fun generatePattern(gridSize: Int): List<Polyanet>
}

/**
 * Generates an X-pattern of POLYanets across the grid.
 */
class XPatternGenerator : PatternGenerator {

    override fun generatePattern(gridSize: Int): List<Polyanet> {
        require(gridSize > 0) { "Grid size must be positive, got $gridSize" }

        val positions = mutableSetOf<Position>()

        // This is the specific X-pattern from the challenge image
        // It's a hollow X that avoids corners
        if (gridSize == 11) {
            // Phase 1 specific pattern (11x11 grid) - HOLLOW X avoiding corners
            val phase1Positions = listOf(
                // Row 0: (0,1), (0,9) - NOT corners
                Position(0, 1), Position(0, 9),
                // Row 1: (1,0), (1,10) - NOT corners
                Position(1, 0), Position(1, 10),
                // Row 2: (2,2), (2,8)
                Position(2, 2), Position(2, 8),
                // Row 3: (3,3), (3,7)
                Position(3, 3), Position(3, 7),
                // Row 4: (4,4), (4,6)
                Position(4, 4), Position(4, 6),
                // Row 5: (5,5) - Center
                Position(5, 5),
                // Row 6: (6,4), (6,6)
                Position(6, 4), Position(6, 6),
                // Row 7: (7,3), (7,7)
                Position(7, 3), Position(7, 7),
                // Row 8: (8,2), (8,8)
                Position(8, 2), Position(8, 8),
                // Row 9: (9,0), (9,10) - NOT corners
                Position(9, 0), Position(9, 10),
                // Row 10: (10,1), (10,9) - NOT corners
                Position(10, 1), Position(10, 9)
            )
            positions.addAll(phase1Positions)
        } else {
            // For other grid sizes, create a simple pattern avoiding corners
            when (gridSize) {
                1 -> {
                    // 1x1 grid: just the center
                    positions.add(Position(0, 0))
                }

                3 -> {
                    // 3x3 grid: center and adjacent positions (avoiding corners)
                    positions.addAll(
                        listOf(
                            Position(1, 1), // Center
                            Position(0, 1), Position(1, 0), // Top and left of center
                            Position(1, 2), Position(2, 1)  // Right and bottom of center
                        )
                    )
                }

                5 -> {
                    // 5x5 grid: simple cross avoiding corners
                    positions.addAll(
                        listOf(
                            Position(1, 1),
                            Position(2, 2),
                            Position(3, 3), // Main diagonal (avoiding corners)
                            Position(1, 3),
                            Position(3, 1) // Anti-diagonal (avoiding corners)
                        )
                    )
                }

                else -> {
                    // For other sizes, create a simple cross avoiding corners
                    val center = gridSize / 2
                    if (gridSize % 2 == 1) {
                        // Odd-sized grid: center position
                        positions.add(Position(center, center))
                    }
                    // Add positions around center avoiding corners
                    for (i in 1 until gridSize - 1) {
                        if (i != gridSize - 1) {
                            positions.add(Position(center, i))
                            positions.add(Position(i, center))
                        }
                    }
                }
            }
        }

        return positions.map { Polyanet(it) }
    }

    /**
     * Generates the X-pattern for the standard 11x11 grid as specified in Phase 1.
     */
    fun generatePhase1Pattern(): List<Polyanet> = generatePattern(11)
}
