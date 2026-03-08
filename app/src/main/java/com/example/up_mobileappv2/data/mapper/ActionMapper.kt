package com.example.up_mobileappv2.data.mapper

import com.example.up_mobileappv2.data.dto.ActionDto
import com.example.up_mobileappv2.domain.model.Action

fun ActionDto.toDomain(): Action {
    return Action(
        id = id,
        photoUrl = photo
    )
}