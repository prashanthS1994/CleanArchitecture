package com.prashanth.contactssample.framework

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

object ContactsSampleAppViewModelFactory: ViewModelProvider.Factory {

    lateinit var application: Application

    private lateinit var interactors: Interactors

    fun inject(application: Application, interactors1: Interactors) {
        ContactsSampleAppViewModelFactory.application = application
        this.interactors = interactors1
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(ContactsSampleAppViewModel::class.java.isAssignableFrom(modelClass)) {
            return modelClass.getConstructor(Application::class.java, Interactors::class.java)
                .newInstance(
                    application,
                    interactors)
        } else {
            throw IllegalStateException("Issue with view model")
        }
    }
}
