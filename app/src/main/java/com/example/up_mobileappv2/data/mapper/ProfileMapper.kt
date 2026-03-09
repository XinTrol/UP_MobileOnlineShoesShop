package com.example.up_mobileappv2.data.mapper

import com.example.up_mobileappv2.data.dto.ProfileDto
import com.example.up_mobileappv2.domain.model.Profile

fun ProfileDto.toDomain(): Profile {
    return Profile(
        id = id,
        userId = userId,
        photo = photo,
        firstName = firstname,
        lastName = lastname,
        address = address,
        phone = phone
    )
}