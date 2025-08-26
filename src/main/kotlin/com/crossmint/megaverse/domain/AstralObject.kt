package com.crossmint.megaverse.domain

/**
 * Base interface for all astral objects in the megaverse.
 */
interface AstralObject {
    val position: Position
    val type: AstralObjectType
}

/**
 * Enum representing the different types of astral objects.
 */
enum class AstralObjectType {
    POLYANET,
    SOLOON,
    COMETH
}

/**
 * Represents a POLYanet (planet) in the megaverse.
 */
data class Polyanet(
    override val position: Position
) : AstralObject {
    override val type: AstralObjectType = AstralObjectType.POLYANET
}

/**
 * Represents a SOLoon (moon) in the megaverse.
 */
data class Soloon(
    override val position: Position,
    val color: SoloonColor
) : AstralObject {
    override val type: AstralObjectType = AstralObjectType.SOLOON
}

/**
 * Represents a COMeth (comet) in the megaverse.
 */
data class Cometh(
    override val position: Position,
    val direction: ComethDirection
) : AstralObject {
    override val type: AstralObjectType = AstralObjectType.COMETH
}

/**
 * Valid colors for SOLoons.
 */
enum class SoloonColor(val value: String) {
    BLUE("blue"),
    RED("red"),
    PURPLE("purple"),
    WHITE("white")
}

/**
 * Valid directions for COMeths.
 */
enum class ComethDirection(val value: String) {
    UP("up"),
    DOWN("down"),
    RIGHT("right"),
    LEFT("left")
}
