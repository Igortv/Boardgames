package com.itolstoy.boardgames.presentation.arch

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineExceptionHandler

//abstract class BaseViewModel<VS: ViewState> : ViewModel(),
//    com.itolstoy.boardgames.presentation.arch.ViewModel<VS> {
//    final override val viewStateLiveData: MutableLiveData<VS> by lazy {
//        val liveData = OnActiveLiveData(initialState)
//        return@lazy liveData
//    }
//
//    private inner class OnActiveLiveData<VS : ViewState>(private val initialState: VS) : MutableLiveData<VS>() {
//        private var isActiveHasFired = false
//        override fun onActive() {
//            super.onActive()
//            if (!isActiveHasFired) {
//                isActiveHasFired = true
//                postValue(initialState)
//            }
//
//        }
//    }
//}
abstract class BaseViewModel : ViewModel() {
    fun handleException(action: (Throwable)->Unit): CoroutineExceptionHandler {
        return CoroutineExceptionHandler { _, exception ->
            action(exception)
        }
    }
}