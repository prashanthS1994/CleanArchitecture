package com.prashanth.contactssample.framework

import android.app.Application
import androidx.lifecycle.AndroidViewModel

open class ContactsSampleAppViewModel(application: Application, protected val interactors: Interactors) :
    AndroidViewModel(application) {

    protected val application: ContactsSampleApplication = getApplication()

}