package com.itolstoy.boardgames.presentation.arch

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

//abstract class BaseAppCompatActivity<VS: ViewState, VM: BaseViewModel<VS>> : AppCompatActivity() {
//    protected val viewModel by lazy {
//        return@lazy createViewModel()
//    }
//
//    protected abstract fun createViewModel(): VM
//
//    protected fun initViewStateLiveData() {
//        viewModel.viewStateLiveData.observe(this, Observer {
//            viewStateHandler(it).invoke()
//        })
//    }
//
//    protected abstract fun viewStateHandler(viewState: VS): () -> Unit
//}