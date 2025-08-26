package com.crossmint.megaverse.api

import com.crossmint.megaverse.domain.Cometh
import com.crossmint.megaverse.domain.Polyanet
import com.crossmint.megaverse.domain.Soloon
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Client for interacting with the Crossmint Megaverse API.
 */
interface CrossmintApiClient {
    /**
     * Creates a POLYanet at the specified position.
     */
    suspend fun createPolyanet(polyanet: Polyanet): ApiResponse

    /**
     * Deletes a POLYanet at the specified position.
     */
    suspend fun deletePolyanet(position: com.crossmint.megaverse.domain.Position): ApiResponse

    /**
     * Creates a SOLoon at the specified position.
     */
    suspend fun createSoloon(soloon: Soloon): ApiResponse

    /**
     * Deletes a SOLoon at the specified position.
     */
    suspend fun deleteSoloon(position: com.crossmint.megaverse.domain.Position): ApiResponse

    /**
     * Creates a COMeth at the specified position.
     */
    suspend fun createCometh(cometh: Cometh): ApiResponse

    /**
     * Deletes a COMeth at the specified position.
     */
    suspend fun deleteCometh(position: com.crossmint.megaverse.domain.Position): ApiResponse

    /**
     * Retrieves the goal map for the current challenge phase.
     */
    suspend fun getGoalMap(): GoalMapResponse
}

/**
 * Response from the Crossmint API.
 */
data class ApiResponse(
    val success: Boolean,
    val message: String? = null,
    val error: String? = null
)

/**
 * Response containing the goal map.
 */
data class GoalMapResponse(
    val goal: List<List<String>>? = null,
    val error: String? = null
)

/**
 * Implementation of the Crossmint API client using OkHttp.
 */
class CrossmintApiClientImpl(
    private val baseUrl: String = "https://challenge.crossmint.io",
    private val candidateId: String,
    private val httpClient: OkHttpClient = OkHttpClient(),
    private val objectMapper: ObjectMapper = ObjectMapper()
) : CrossmintApiClient {

    override suspend fun createPolyanet(polyanet: Polyanet): ApiResponse {
        val requestBody = FormBody.Builder()
            .add("candidateId", candidateId)
            .add("row", polyanet.position.row.toString())
            .add("column", polyanet.position.column.toString())
            .build()

        return executeRequest("/api/polyanets", requestBody)
    }

    override suspend fun deletePolyanet(position: com.crossmint.megaverse.domain.Position): ApiResponse {
        val requestBody = FormBody.Builder()
            .add("candidateId", candidateId)
            .add("row", position.row.toString())
            .add("column", position.column.toString())
            .build()

        return executeDeleteRequest("/api/polyanets", requestBody)
    }

    override suspend fun createSoloon(soloon: Soloon): ApiResponse {
        val requestBody = FormBody.Builder()
            .add("candidateId", candidateId)
            .add("row", soloon.position.row.toString())
            .add("column", soloon.position.column.toString())
            .add("color", soloon.color.value)
            .build()

        return executeRequest("/api/soloons", requestBody)
    }

    override suspend fun deleteSoloon(position: com.crossmint.megaverse.domain.Position): ApiResponse {
        val requestBody = FormBody.Builder()
            .add("candidateId", candidateId)
            .add("row", position.row.toString())
            .add("column", position.column.toString())
            .build()

        return executeDeleteRequest("/api/soloons", requestBody)
    }

    override suspend fun createCometh(cometh: Cometh): ApiResponse {
        val requestBody = FormBody.Builder()
            .add("candidateId", candidateId)
            .add("row", cometh.position.row.toString())
            .add("column", cometh.position.column.toString())
            .add("direction", cometh.direction.value)
            .build()

        return executeRequest("/api/comeths", requestBody)
    }

    override suspend fun deleteCometh(position: com.crossmint.megaverse.domain.Position): ApiResponse {
        val requestBody = FormBody.Builder()
            .add("candidateId", candidateId)
            .add("row", position.row.toString())
            .add("column", position.column.toString())
            .build()

        return executeDeleteRequest("/api/comeths", requestBody)
    }

    override suspend fun getGoalMap(): GoalMapResponse {
        val request = Request.Builder()
            .url("$baseUrl/api/map/$candidateId/goal")
            .get()
            .build()

        return try {
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string()
                if (responseBody != null) {
                    try {
                        val goalMap = objectMapper.readValue(responseBody, Map::class.java)
                        val goalArray = goalMap["goal"] as? List<*>
                        if (goalArray != null) {
                            val goal = goalArray.map { row ->
                                (row as List<*>).map { it.toString() }
                            }
                            GoalMapResponse(goal = goal)
                        } else {
                            GoalMapResponse(error = "Goal map structure is invalid")
                        }
                    } catch (e: Exception) {
                        GoalMapResponse(error = "Failed to parse goal map: ${e.message}")
                    }
                } else {
                    GoalMapResponse(error = "Empty response body")
                }
            } else {
                GoalMapResponse(error = "HTTP ${response.code}: ${response.message}")
            }
        } catch (e: IOException) {
            GoalMapResponse(error = "Network error: ${e.message}")
        }
    }

    private fun executeRequest(endpoint: String, requestBody: RequestBody): ApiResponse {
        val request = Request.Builder()
            .url("$baseUrl$endpoint")
            .post(requestBody)
            .build()

        return executeRequestInternal(request)
    }

    private fun executeDeleteRequest(
        endpoint: String,
        requestBody: RequestBody
    ): ApiResponse {
        val request = Request.Builder()
            .url("$baseUrl$endpoint")
            .delete(requestBody)
            .build()

        return executeRequestInternal(request)
    }

    private fun executeRequestInternal(request: Request): ApiResponse {
        return try {
            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                ApiResponse(success = true, message = "Success")
            } else {
                val errorBody = response.body?.string() ?: "Unknown error"
                ApiResponse(success = false, error = "HTTP ${response.code}: $errorBody")
            }
        } catch (e: IOException) {
            ApiResponse(success = false, error = "Network error: ${e.message}")
        }
    }
}
