package de.damien.frontend.data.remote.dto

import de.damien.frontend.domain.models.Comment

data class CommentDto(
    val commentId: Int,
    val text: String,
    val writer: AccountDto
)

fun CommentDto.toComment(): Comment {
    return Comment(
        commentId = commentId,
        text = text,
        writer = writer.toAccount(),
    )
}