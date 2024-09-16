package com.itolstoy.boardgames.data.repository

import android.content.SharedPreferences
import com.itolstoy.boardgames.domain.repository.SharedPreferencesRepository
import javax.inject.Inject

class SharedPreferencesRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SharedPreferencesRepository{
    companion object {
        const val IS_ADMIN_PREF = "is_admin"
    }

    override fun putUserAdminValue(value: Boolean) {
        sharedPreferences.edit().putBoolean(IS_ADMIN_PREF, value).apply()
    }

    override fun getUserAdminValue(): Boolean {
        return sharedPreferences.getBoolean(IS_ADMIN_PREF, false)
    }
}