package com.crossmint.megaverse.util

import com.crossmint.megaverse.pattern.XPatternGenerator

/**
 * Utility class for visualizing megaverse patterns.
 */
object PatternVisualizer {
    
    /**
     * Visualizes the X-pattern that will be created.
     * @param gridSize The size of the grid
     * @return A string representation of the grid with POLYanets marked
     */
    fun visualizeXPattern(gridSize: Int): String {
        val patternGenerator = XPatternGenerator()
        val polyanets = patternGenerator.generatePattern(gridSize)
        
        val positions = polyanets.map { it.position }.toSet()
        
        val sb = StringBuilder()
        sb.appendLine("X-Pattern Visualization (${gridSize}x${gridSize} grid):")
        sb.appendLine("=".repeat(gridSize * 3 + 2))
        
        for (row in 0 until gridSize) {
            sb.append("|")
            for (col in 0 until gridSize) {
                val position = com.crossmint.megaverse.domain.Position(row, col)
                if (positions.contains(position)) {
                    sb.append(" 🪐")
                } else {
                    sb.append(" · ")
                }
            }
            sb.appendLine("|")
        }
        
        sb.appendLine("=".repeat(gridSize * 3 + 2))
        sb.appendLine("Legend: 🪐 = POLYanet, · = Empty space")
        sb.appendLine("Total POLYanets: ${polyanets.size}")
        
        return sb.toString()
    }
    
    /**
     * Visualizes the Phase 1 X-pattern specifically.
     */
    fun visualizePhase1Pattern(): String = visualizeXPattern(11)
}
