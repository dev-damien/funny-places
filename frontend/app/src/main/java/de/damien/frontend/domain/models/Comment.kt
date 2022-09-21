package de.damien.frontend.domain.models

data class Comment(
    val commentId: Int,
    val text: String,
    val writer: Account
)