package com.example.thoughtbattle.data.services.models

import java.io.Serializable

data class WikimediaImageInfoResponse(
    val query: ImageInfoQuery
) {
    data class ImageInfoQuery(
        val pages: Map<String, ImageInfoPage>
    ):Serializable

    data class ImageInfoPage(
        val title: String,
        val imageinfo: List<ImageInfo>?
    ): Serializable

    data class ImageInfo(
        val url: String,
        val descriptionurl: String,
        val descriptionshorturl: String
    ):Serializable
}