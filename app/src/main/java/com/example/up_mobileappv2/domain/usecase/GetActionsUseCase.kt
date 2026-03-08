package com.example.up_mobileappv2.domain.usecase

import com.example.up_mobileappv2.domain.model.Action
import com.example.up_mobileappv2.domain.repository.ActionRepository
import javax.inject.Inject

class GetActionsUseCase @Inject constructor(
    private val actionRepository: ActionRepository
) {
    suspend operator fun invoke(): List<Action> {
        return actionRepository.getActions()
    }
}