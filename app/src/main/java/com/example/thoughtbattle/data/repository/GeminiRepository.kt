package com.example.thoughtbattle.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.vertexai.vertexAI


class GeminiRepository {
    val model = Firebase.vertexAI.generativeModel("gemini-2.0-flash")

    suspend fun generateDebateSideInfo(topic: String, side: String): String {
        val prompt = """
             Generate a 100-word objective summary description for $side in a debate about $topic.
           Just give general overview information 
        """.trimIndent()

        return try {
            val response = model.generateContent(prompt)
            Log.d("GeminiRepository", "Response text: ${response.text}")
            response.text ?: throw Exception("Empty response from AI")
        } catch (e: Exception) {
            "Could not generate content: ${e.message}"
        }
    }

suspend fun generateCorrelationInfo(topic: String, sideA: String, sideB: String): String {
val prompt = """
    Generate a 100-word objective summary description for a debate about $topic in the context of the sides $sideA and $sideB.
    Please just give general information such as any context needed to understand the debate.
""".trimIndent()
    try {
        val response = model.generateContent(prompt)
        return response.text ?: throw Exception("Empty response from AI")
    } catch (e: Exception) {
        return "Could not generate content: ${e.message}"
    }
}

}