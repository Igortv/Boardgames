package com.itolstoy.boardgames.presentation.arch

import androidx.lifecycle.MutableLiveData

interface ViewModel<VS: ViewState> {
    val initialState: VS
    val viewStateLiveData: MutableLiveData<VS>
}