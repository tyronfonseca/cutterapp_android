package com.tf.clasificacioncutter.data.model.openlibrary

import com.google.gson.annotations.SerializedName

data class OpenLibraryResponse(
    @SerializedName("docs") val docs: List<BookDoc>
)

data class BookDoc(
    @SerializedName("title") val title: String?,
    @SerializedName("author_name") val authorName: List<String>?,
    @SerializedName("ddc") val ddc: List<String>?
)
