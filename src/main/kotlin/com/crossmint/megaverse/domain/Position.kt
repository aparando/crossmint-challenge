package com.crossmint.megaverse.domain

/**
 * Represents a position in the 2D megaverse grid.
 * @param row The row coordinate (0-indexed)
 * @param column The column coordinate (0-indexed)
 */
data class Position(
    val row: Int,
    val column: Int
) {
    init {
        require(row >= 0) { "Row must be non-negative, got $row" }
        require(column >= 0) { "Column must be non-negative, got $column" }
    }

    override fun toString(): String = "($row, $column)"
}
