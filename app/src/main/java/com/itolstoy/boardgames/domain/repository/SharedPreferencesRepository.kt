package com.itolstoy.boardgames.domain.repository

interface SharedPreferencesRepository {
    fun putUserAdminValue(value: Boolean)
    fun getUserAdminValue(): Boolean
}