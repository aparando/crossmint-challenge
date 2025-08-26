package com.crossmint.megaverse

import com.crossmint.megaverse.api.CrossmintApiClientImpl
import com.crossmint.megaverse.pattern.XPatternGenerator
import com.crossmint.megaverse.service.MegaverseService
import com.crossmint.megaverse.util.PatternVisualizer
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

/**
 * Main application for Phase 1 of the Crossmint Megaverse Challenge.
 * Creates a megaverse with POLYanets forming an X-pattern on an 11x11 grid.
 */
class MegaverseApplication(
    private val candidateId: String,
    private val dryRun: Boolean = false
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    
    private val apiClient: CrossmintApiClientImpl = CrossmintApiClientImpl(candidateId = candidateId)
    private val megaverseService: MegaverseService = MegaverseService(apiClient)
    private val patternGenerator: XPatternGenerator = XPatternGenerator()
    
    /**
     * Executes Phase 1 of the challenge: creating an X-pattern of POLYanets.
     */
    fun executePhase1(): MegaverseExecutionResult = runBlocking {
        logger.info("🚀 Starting Phase 1: Creating X-pattern of POLYanets")
        
        if (dryRun) {
            logger.info("🔍 DRY RUN MODE: No actual API calls will be made")
        }
        
        try {
            // Retrieve and validate goal map
            logger.info("📋 Retrieving goal map...")
            val goalMap = megaverseService.getGoalMap()
            
            if (goalMap.error != null) {
                logger.warn("⚠️ Could not retrieve goal map: ${goalMap.error}")
            } else {
                logger.info("✅ Goal map retrieved successfully")
                if (goalMap.goal != null) {
                    logger.info("📐 Goal map dimensions: ${goalMap.goal.size}x${goalMap.goal.firstOrNull()?.size}")
                }
            }
            
            // Create the X-pattern
            logger.info("🎯 Creating X-pattern of POLYanets...")
            val result = if (dryRun) {
                simulatePhase1Result()
            } else {
                megaverseService.createMegaverse(patternGenerator, 11)
            }
            
            // Log the results
            logResults(result)
            
            MegaverseExecutionResult(
                phase = "Phase 1",
                success = result.isFullySuccessful,
                message = if (result.isFullySuccessful) {
                    "✅ Successfully created X-pattern with ${result.successCount} POLYanets"
                } else {
                    "⚠️ Created X-pattern with ${result.successCount} POLYanets, but ${result.failureCount} failed"
                },
                details = result
            )
            
        } catch (e: Exception) {
            logger.error("❌ Error during Phase 1 execution", e)
            MegaverseExecutionResult(
                phase = "Phase 1",
                success = false,
                message = "❌ Error: ${e.message}",
                details = null
            )
        }
    }
    
    private fun simulatePhase1Result(): com.crossmint.megaverse.service.MegaverseCreationResult {
        val polyanets = patternGenerator.generatePhase1Pattern()
        logger.info("🔍 DRY RUN: Would create ${polyanets.size} POLYanets")
        
        return com.crossmint.megaverse.service.MegaverseCreationResult(
            totalObjects = polyanets.size,
            successCount = polyanets.size,
            failureCount = 0,
            results = polyanets.map { polyanet ->
                com.crossmint.megaverse.service.ObjectCreationResult(
                    position = polyanet.position,
                    success = true
                )
            }
        )
    }
    
    private fun logResults(result: com.crossmint.megaverse.service.MegaverseCreationResult) {
        logger.info("📊 === Phase 1 Results ===")
        logger.info("📊 Total objects: ${result.totalObjects}")
        logger.info("✅ Successful creations: ${result.successCount}")
        logger.info("❌ Failed creations: ${result.failureCount}")
        
        if (result.failureCount > 0) {
            logger.warn("⚠️ Failed positions:")
            result.results.filter { !it.success }.forEach { failed ->
                logger.warn("  ${failed.position}: ${failed.error}")
            }
        }
        
        if (result.isFullySuccessful) {
            logger.info("🎉 Phase 1 completed successfully! The X-pattern has been created.")
        } else {
            logger.warn("⚠️ Phase 1 completed with some failures. Check the logs above for details.")
        }
    }
}

/**
 * Result of executing a megaverse phase.
 */
data class MegaverseExecutionResult(
    val phase: String,
    val success: Boolean,
    val message: String,
    val details: com.crossmint.megaverse.service.MegaverseCreationResult?
)

/**
 * Main function to run Phase 1 of the challenge.
 */
fun main(args: Array<String>) {
    val candidateId = "d8d2b013-aa8f-44b4-8bde-f8261dab23b8"
    
    // Check if we should run in dry-run mode
    val dryRun = args.isNotEmpty() && args[0] == "--dry-run"
    
    println("🚀 Welcome to the Crossmint Megaverse Challenge!")
    println("Candidate ID: $candidateId")
    println("Phase 1: Creating X-pattern of POLYanets")
    if (dryRun) {
        println("🔍 DRY RUN MODE: No actual API calls will be made")
    }
    println("=".repeat(50))
    
    // Show the pattern that will be created
    println("\n📐 Pattern Preview:")
    println(PatternVisualizer.visualizePhase1Pattern())
    
    val application = MegaverseApplication(candidateId, dryRun)
    val result = application.executePhase1()
    
    println("\n" + "=".repeat(50))
    println("📊 Execution Results:")
    println("Phase: ${result.phase}")
    println("Status: ${if (result.success) "✅ SUCCESS" else "❌ FAILED"}")
    println("Message: ${result.message}")
    
    if (result.details != null) {
        println("\nDetailed Results:")
        println("  Total objects: ${result.details.totalObjects}")
        println("  Successful: ${result.details.successCount}")
        println("  Failed: ${result.details.failureCount}")
    }
    
    if (dryRun) {
        println("\n🔍 This was a dry run. To execute the actual challenge, run without --dry-run")
    } else {
        println("\n🎯 Challenge completed! Check the logs above for detailed information.")
    }
}
