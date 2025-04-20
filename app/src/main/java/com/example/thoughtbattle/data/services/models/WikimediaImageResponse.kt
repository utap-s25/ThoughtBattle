package com.example.thoughtbattle.data.services.models
import java.io.Serializable

data class WikimediaImageResponse(
    val query: Query
) {
    data class Query(
        val pages: Map<String, Page>
    ): Serializable

    data class Page(
        val pageid: Long,
        val ns: Int,
        val title: String,
        val images: List<Image>?
    ): Serializable

    data class Image(
        val ns: Int,
        val title: String
    ): Serializable
}
