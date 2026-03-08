package com.example.up_mobileappv2.data.mapper

import com.example.up_mobileappv2.data.dto.auth.UserDto
import com.example.up_mobileappv2.domain.model.User

fun UserDto.toDomain(): User {
    return User(
        id = id,
        email = email
    )
}