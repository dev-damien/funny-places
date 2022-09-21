package de.damien.frontend.data.remote.dto

import de.damien.frontend.domain.models.Account

data class AccountDto(
    val name: String
)

fun AccountDto.toAccount(): Account{
    return Account(
        name = name
    )
}