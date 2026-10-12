package com.tf.clasificacioncutter.data.model.cutterdata

// Convert Database Entity -> Domain Model (For UI/Business Logic)
fun CutterDataEntity.toDomain(): CutterData {
    return CutterData(
        id = this.id,
        name = this.name,
        code = this.code,
        authorName = this.authorName,
        isbn = this.isbn,
        bookName = this.bookName,
        ddcs = this.ddcs,
        ddcSelected = this.ddcSelected,
        needsReview = this.needsReview,
        dontSeparateName = this.dontSeparateName,
        ignoreArticles = this.ignoreArticles,
        useFormatMac = this.useFormatMac,
        timestamp = this.timestamp
    ).apply {
        this.authorSurname = this@toDomain.authorSurname
    }
}

// Convert Domain Model -> Database Entity (For Room Persistence)
fun CutterData.toEntity(): CutterDataEntity {
    return CutterDataEntity(
        id = this.id,
        name = this.name,
        code = this.code,
        authorName = this.authorName,
        authorSurname = this.authorSurname,
        isbn = this.isbn,
        bookName = this.bookName,
        ddcs = this.ddcs,
        ddcSelected = this.ddcSelected,
        needsReview = this.needsReview,
        dontSeparateName = this.dontSeparateName,
        ignoreArticles = this.ignoreArticles,
        useFormatMac = this.useFormatMac,
        timestamp = this.timestamp
    )
}