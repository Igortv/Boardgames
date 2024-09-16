package com.itolstoy.boardgames.domain.usecase.preferences

import com.itolstoy.boardgames.domain.repository.AuthenticationRepository
import com.itolstoy.boardgames.domain.repository.SharedPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveAdminValueUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    operator fun invoke(isAdmin: Boolean) {
        authenticationRepository.putUserAdminValue(isAdmin)
    }
}