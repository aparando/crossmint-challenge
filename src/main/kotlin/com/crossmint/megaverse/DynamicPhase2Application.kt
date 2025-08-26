package com.crossmint.megaverse

import com.crossmint.megaverse.api.CrossmintApiClientImpl
import com.crossmint.megaverse.pattern.GoalMapDrivenPatternGenerator
import com.crossmint.megaverse.service.MegaverseService
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

/**
 * Dynamic Phase 2 application that automatically reads the goal map
 * and creates whatever pattern the API tells us.
 *
 */
class DynamicPhase2Application(
    private val candidateId: String,
    private val dryRun: Boolean = false
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    private val apiClient: CrossmintApiClientImpl = CrossmintApiClientImpl(candidateId = candidateId)
    private val megaverseService: MegaverseService = MegaverseService(apiClient)
    private val patternGenerator: GoalMapDrivenPatternGenerator = GoalMapDrivenPatternGenerator()

    fun executePhase2(): DynamicPhase2ExecutionResult = runBlocking {
        logger.info("🚀 Starting Dynamic Phase 2: Goal-Map-Driven Megaverse Creation")
        logger.info("This solution automatically adapts to whatever pattern the API returns!")

        if (dryRun) {
            logger.info("🔍 DRY RUN MODE: No actual API calls will be made")
        }

        try {
            // Step 1: Automatically retrieve the goal map from the API
            logger.info("Step 1: Retrieving goal map from /api/map/$candidateId/goal...")
            val goalMap = megaverseService.getGoalMap()

            if (goalMap.error != null) {
                logger.error("❌ Failed to retrieve goal map: ${goalMap.error}")
                return@runBlocking DynamicPhase2ExecutionResult(
                    success = false,
                    message = "Failed to retrieve goal map: ${goalMap.error}",
                    details = null
                )
            }

            if (goalMap.goal == null) {
                logger.error("❌ Goal map is null")
                return@runBlocking DynamicPhase2ExecutionResult(
                    success = false,
                    message = "Goal map is null",
                    details = null
                )
            }

            logger.info("✅ Goal map retrieved successfully!")
            logger.info("📐 Grid dimensions: ${goalMap.goal.size}x${goalMap.goal.firstOrNull()?.size}")

            // Step 2: Analyze the goal map automatically
            logger.info("🔍 Step 2: Analyzing goal map structure...")
            val analysis = patternGenerator.analyzeGoalMap(goalMap)
            
            if (!analysis.isValid) {
                logger.error("❌ Goal map analysis failed: ${analysis.error}")
                return@runBlocking DynamicPhase2ExecutionResult(
                    success = false,
                    message = "Goal map analysis failed: ${analysis.error}",
                    details = null
                )
            }

            logger.info("📊 Goal Map Analysis:")
            logger.info("  📐 Grid size: ${analysis.gridSize}x${analysis.gridSize}")
            logger.info("  🪐 POLYanets: ${analysis.polyanetCount}")
            logger.info("  🌙 SOLoons: ${analysis.soloonCount}")
            logger.info("  ☄️  ComETHs: ${analysis.comethCount}")
            logger.info("  ·  Empty spaces: ${analysis.spaceCount}")
            logger.info("  📊 Total objects to create: ${analysis.totalObjects}")
            logger.info("  🌌 Total grid positions: ${analysis.totalPositions}")

            // Step 3: Generate the pattern dynamically from the goal map
            logger.info("🎯 Step 3: Generating pattern dynamically from goal map...")
            val megaverse = patternGenerator.generateFromGoalMap(goalMap)
            
            logger.info("✅ Pattern generated dynamically!")
            logger.info("  🪐 POLYanets: ${megaverse.polyanets.size}")
            logger.info("  🌙 SOLoons: ${megaverse.soloons.size}")
            logger.info("  ☄️  ComETHs: ${megaverse.comeths.size}")
            logger.info("  📊 Total objects: ${megaverse.totalObjects}")
            logger.info("  🌌 Empty spaces: ${megaverse.emptySpaces.size}")
            logger.info("  🌌 Total grid positions: ${megaverse.totalPositions}")

            // Step 4: Create the megaverse automatically
            if (dryRun) {
                logger.info("🔍 DRY RUN: Would create ${megaverse.totalObjects} objects based on goal map")
                return@runBlocking simulateDynamicPhase2Result(megaverse)
            } else {
                logger.info("🚀 Step 4: Creating megaverse automatically based on goal map...")
                val result = createMegaverseFromGoalMap(megaverse)
                
                logger.info("🎉 Dynamic megaverse creation completed!")
                logger.info("  🪐 POLYanets: ${result.polyanetCount}/${megaverse.polyanets.size}")
                logger.info("  🌙 SOLoons: ${result.soloonCount}/${megaverse.soloons.size}")
                logger.info("  ☄️  ComETHs: ${result.comethCount}/${megaverse.comeths.size}")
                logger.info("  📊 Total Success: ${result.successCount}/${megaverse.totalObjects}")
                logger.info("  ❌ Total Failures: ${result.failureCount}")

                return@runBlocking DynamicPhase2ExecutionResult(
                    success = result.failureCount == 0,
                    message = if (result.failureCount == 0) {
                        "✅ Successfully created megaverse with ${result.successCount} objects based on goal map!"
                    } else {
                        "⚠️ Created megaverse with ${result.successCount} objects, but ${result.failureCount} failed"
                    },
                    details = result
                )
            }

        } catch (e: Exception) {
            logger.error("❌ Error during dynamic Phase 2 execution", e)
            return@runBlocking DynamicPhase2ExecutionResult(
                success = false,
                message = "Error: ${e.message}",
                details = null
            )
        }
    }

    /**
     * Creates the megaverse based on the goal map.
     */
    private suspend fun createMegaverseFromGoalMap(megaverse: com.crossmint.megaverse.pattern.GoalMapDrivenMegaverse): com.crossmint.megaverse.service.Phase2MegaverseCreationResult {
        val results = mutableListOf<com.crossmint.megaverse.service.Phase2ObjectCreationResult>()
        
        var polyanetSuccess = 0
        var soloonSuccess = 0
        var comethSuccess = 0
        var totalFailures = 0

        // Create POLYanets
        logger.info("🪐 Creating ${megaverse.polyanets.size} POLYanets from goal map...")
        megaverse.polyanets.forEach { polyanet ->
            val result = megaverseService.createPolyanetWithRetry(polyanet)
            results.add(com.crossmint.megaverse.service.Phase2ObjectCreationResult(
                position = polyanet.position,
                type = com.crossmint.megaverse.domain.AstralObjectType.POLYANET,
                success = result.success,
                error = result.error
            ))
            
            if (result.success) {
                polyanetSuccess++
                logger.debug("✅ Created POLYanet at ${polyanet.position}")
            } else {
                totalFailures++
                logger.warn("❌ Failed to create POLYanet at ${polyanet.position}: ${result.error}")
            }
            
            // Rate limiting to avoid 429 errors
            kotlinx.coroutines.delay(1000)
        }

        // Create SOLoons
        logger.info("🌙 Creating ${megaverse.soloons.size} SOLoons from goal map...")
        megaverse.soloons.forEach { soloon ->
            val result = megaverseService.createSoloonWithRetry(soloon)
            results.add(com.crossmint.megaverse.service.Phase2ObjectCreationResult(
                position = soloon.position,
                type = com.crossmint.megaverse.domain.AstralObjectType.SOLOON,
                success = result.success,
                error = result.error
            ))
            
            if (result.success) {
                soloonSuccess++
                logger.debug("✅ Created ${soloon.color} SOLoon at ${soloon.position}")
            } else {
                totalFailures++
                logger.warn("❌ Failed to create SOLoon at ${soloon.position}: ${result.error}")
            }
            
            // Rate limiting to avoid 429 errors
            kotlinx.coroutines.delay(1000)
        }

        // Create ComETHs
        logger.info("☄️ Creating ${megaverse.comeths.size} ComETHs from goal map...")
        megaverse.comeths.forEach { cometh ->
            val result = megaverseService.createComethWithRetry(cometh)
            results.add(com.crossmint.megaverse.service.Phase2ObjectCreationResult(
                position = cometh.position,
                type = com.crossmint.megaverse.domain.AstralObjectType.COMETH,
                success = result.success,
                error = result.error
            ))
            
            if (result.success) {
                comethSuccess++
                logger.debug("✅ Created ${cometh.direction} ComETH at ${cometh.position}")
            } else {
                totalFailures++
                logger.warn("❌ Failed to create ComETH at ${cometh.position}: ${result.error}")
            }
            
            // Rate limiting to avoid 429 errors
            kotlinx.coroutines.delay(1000)
        }

        return com.crossmint.megaverse.service.Phase2MegaverseCreationResult(
            totalObjects = megaverse.totalObjects,
            polyanetCount = megaverse.polyanets.size,
            soloonCount = megaverse.soloons.size,
            comethCount = megaverse.comeths.size,
            successCount = polyanetSuccess + soloonSuccess + comethSuccess,
            failureCount = totalFailures,
            results = results
        )
    }

    private fun simulateDynamicPhase2Result(megaverse: com.crossmint.megaverse.pattern.GoalMapDrivenMegaverse): DynamicPhase2ExecutionResult {
        logger.info("🔍 DRY RUN: Would create megaverse based on goal map:")
        logger.info("  🪐 POLYanets: ${megaverse.polyanets.size}")
        logger.info("  🌙 SOLoons: ${megaverse.soloons.size}")
        logger.info("  ☄️  ComETHs: ${megaverse.comeths.size}")
        logger.info("  📊 Total: ${megaverse.totalObjects} objects")

        return DynamicPhase2ExecutionResult(
            success = true,
            message = "DRY RUN: Would create ${megaverse.totalObjects} objects based on goal map",
            details = null
        )
    }
}

data class DynamicPhase2ExecutionResult(
    val success: Boolean,
    val message: String,
    val details: com.crossmint.megaverse.service.Phase2MegaverseCreationResult?
)

fun main(args: Array<String>) {
    val candidateId = "d8d2b013-aa8f-44b4-8bde-f8261dab23b8"

    // Check if we should run in dry-run mode
    val dryRun = args.isNotEmpty() && args[0] == "--dry-run"

    println("🚀 Welcome to Dynamic Phase 2 of the Crossmint Megaverse Challenge!")
    println("Candidate ID: $candidateId")
    println("Phase 2: Goal-Map-Driven Megaverse Creation")
    println("=".repeat(60))
    println("🎯 KEY FEATURE: This solution automatically reads the goal map")
    println("🎯 KEY FEATURE: No hardcoded patterns - everything is dynamic!")
    println("🎯 KEY FEATURE: Adapts to any changes in the API automatically!")
    println("=".repeat(60))

    if (dryRun) {
        println("🔍 DRY RUN MODE: No actual API calls will be made")
    }

    val application = DynamicPhase2Application(candidateId, dryRun)
    val result = application.executePhase2()

    println("\n" + "=".repeat(60))
    println("📊 Dynamic Phase 2 Execution Results:")
    println("Status: ${if (result.success) "✅ SUCCESS" else "❌ FAILED"}")
    println("Message: ${result.message}")

    if (result.details != null) {
        println("\nDetailed Results:")
        println("  Total objects: ${result.details.totalObjects}")
        println("  POLYanets: ${result.details.polyanetCount}")
        println("  SOLoons: ${result.details.soloonCount}")
        println("  ComETHs: ${result.details.comethCount}")
        println("  Successful: ${result.details.successCount}")
        println("  Failed: ${result.details.failureCount}")
    }

    if (dryRun) {
        println("\n🔍 This was a dry run. To execute Phase 2, run without --dry-run")
    } else {
        println("\n🎯 Dynamic Phase 2 completed! The solution automatically adapted to the goal map!")
    }
}
