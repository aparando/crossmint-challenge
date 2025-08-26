package com.crossmint.megaverse.service

import com.crossmint.megaverse.api.CrossmintApiClient
import com.crossmint.megaverse.domain.AstralObjectType
import com.crossmint.megaverse.domain.Cometh
import com.crossmint.megaverse.domain.Polyanet
import com.crossmint.megaverse.domain.Position
import com.crossmint.megaverse.domain.Soloon
import com.crossmint.megaverse.pattern.PatternGenerator
import kotlinx.coroutines.delay
import org.slf4j.LoggerFactory

/**
 * Service for building and managing the megaverse.
 */
class MegaverseService(
    private val apiClient: CrossmintApiClient,
    private val retryDelayMs: Long = 1000,
    private val maxRetries: Int = 3
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    /**
     * Creates a megaverse based on the provided pattern generator.
     * @param patternGenerator The generator that creates the pattern of astral objects
     * @param gridSize The size of the grid
     * @return Result of the megaverse creation process
     */
    suspend fun createMegaverse(
        patternGenerator: PatternGenerator,
        gridSize: Int
    ): MegaverseCreationResult {
        logger.info("Starting megaverse creation with grid size: $gridSize")

        val astralObjects = patternGenerator.generatePattern(gridSize)
        logger.info("Generated ${astralObjects.size} astral objects")

        val results = mutableListOf<ObjectCreationResult>()
        var successCount = 0
        var failureCount = 0

        for (astralObject in astralObjects) {
            // Since we're only generating Polyanets in Phase 1, we can safely cast
            val result = createPolyanetWithRetry(astralObject)

            results.add(result)
            if (result.success) {
                successCount++
                logger.debug(
                    "Successfully created {} at {}",
                    astralObject.type,
                    astralObject.position
                )
            } else {
                failureCount++
                logger.warn("Failed to create ${astralObject.type} at ${astralObject.position}: ${result.error}")
            }

            // Add a small delay to avoid overwhelming the API
            delay(100)
        }

        logger.info("Megaverse creation completed. Success: $successCount, Failures: $failureCount")

        return MegaverseCreationResult(
            totalObjects = astralObjects.size,
            successCount = successCount,
            failureCount = failureCount,
            results = results
        )
    }

    /**
     * Creates a POLYanet with retry logic for resilience.
     */
    suspend fun createPolyanetWithRetry(polyanet: Polyanet): ObjectCreationResult {
        var lastError: String? = null

        for (attempt in 1..maxRetries) {
            try {
                val response = apiClient.createPolyanet(polyanet)

                if (response.success) {
                    return ObjectCreationResult(
                        position = polyanet.position,
                        success = true
                    )
                } else {
                    lastError = response.error ?: "Unknown error"
                    logger.warn("Attempt $attempt failed for ${polyanet.position}: $lastError")

                    // If it's a rate limit error, wait longer
                    if (lastError.contains("429") || lastError.contains("Too Many Requests")) {
                        val rateLimitDelay = 2000L * attempt // 2s, 4s, 6s
                        logger.info("Rate limited, waiting ${rateLimitDelay}ms before retry...")
                        delay(rateLimitDelay)
                        continue
                    }
                }
            } catch (e: Exception) {
                lastError = "Exception: ${e.message}"
                logger.warn("Attempt $attempt failed with exception for ${polyanet.position}: $lastError")
            }

            if (attempt < maxRetries) {
                delay(retryDelayMs * attempt) // Exponential backoff
            }
        }

        return ObjectCreationResult(
            position = polyanet.position,
            success = false,
            error = "Failed after $maxRetries attempts. Last error: $lastError"
        )
    }

    /**
     * Creates a SOLoon with retry logic for resilience.
     */
    suspend fun createSoloonWithRetry(soloon: Soloon): ObjectCreationResult {
        var lastError: String? = null

        for (attempt in 1..maxRetries) {
            try {
                val response = apiClient.createSoloon(soloon)

                if (response.success) {
                    return ObjectCreationResult(
                        position = soloon.position,
                        success = true
                    )
                } else {
                    lastError = response.error ?: "Unknown error"
                    logger.warn("Attempt $attempt failed for ${soloon.position}: $lastError")

                    // If it's a rate limit error, wait longer
                    if (lastError.contains("429") || lastError.contains("Too Many Requests")) {
                        val rateLimitDelay = 2000L * attempt // 2s, 4s, 6s
                        delay(rateLimitDelay)
                        continue
                    }
                }
            } catch (e: Exception) {
                lastError = "Exception: ${e.message}"
                logger.warn("Attempt $attempt failed with exception for ${soloon.position}: $lastError")
            }

            if (attempt < maxRetries) {
                delay(retryDelayMs * attempt) // Exponential backoff
            }
        }

        return ObjectCreationResult(
            position = soloon.position,
            success = false,
            error = "Failed after $maxRetries attempts. Last error: $lastError"
        )
    }

    /**
     * Creates a ComETH with retry logic for resilience.
     */
    suspend fun createComethWithRetry(cometh: Cometh): ObjectCreationResult {
        var lastError: String? = null

        for (attempt in 1..maxRetries) {
            try {
                val response = apiClient.createCometh(cometh)

                if (response.success) {
                    return ObjectCreationResult(
                        position = cometh.position,
                        success = true
                    )
                } else {
                    lastError = response.error ?: "Unknown error"
                    logger.warn("Attempt $attempt failed for ${cometh.position}: $lastError")

                    // If it's a rate limit error, wait longer
                    if (lastError.contains("429") == true || lastError.contains("Too Many Requests") == true) {
                        val rateLimitDelay = 2000L * attempt // 2s, 4s, 6s
                        logger.info("Rate limited, waiting ${rateLimitDelay}ms before retry...")
                        delay(rateLimitDelay)
                        continue
                    }
                }
            } catch (e: Exception) {
                lastError = "Exception: ${e.message}"
                logger.warn("Attempt $attempt failed with exception for ${cometh.position}: $lastError")
            }

            if (attempt < maxRetries) {
                delay(retryDelayMs * attempt) // Exponential backoff
            }
        }

        return ObjectCreationResult(
            position = cometh.position,
            success = false,
            error = "Failed after $maxRetries attempts. Last error: $lastError"
        )
    }

    /**
     * Retrieves the goal map to verify the current state.
     */
    suspend fun getGoalMap() = apiClient.getGoalMap()
}

/**
 * Result of creating a single astral object.
 */
data class ObjectCreationResult(
    val position: Position,
    val success: Boolean,
    val error: String? = null
)

/**
 * Overall result of the megaverse creation process.
 */
data class MegaverseCreationResult(
    val totalObjects: Int,
    val successCount: Int,
    val failureCount: Int,
    val results: List<ObjectCreationResult>
) {
    val isFullySuccessful: Boolean
        get() = failureCount == 0
}

/**
 * Result of creating a single astral object in Phase 2.
 */
data class Phase2ObjectCreationResult(
    val position: Position,
    val type: AstralObjectType,
    val success: Boolean,
    val error: String? = null
)

/**
 * Overall result of the Phase 2 megaverse creation process.
 */
data class Phase2MegaverseCreationResult(
    val totalObjects: Int,
    val polyanetCount: Int,
    val soloonCount: Int,
    val comethCount: Int,
    val successCount: Int,
    val failureCount: Int,
    val results: List<Phase2ObjectCreationResult>
) {
}
