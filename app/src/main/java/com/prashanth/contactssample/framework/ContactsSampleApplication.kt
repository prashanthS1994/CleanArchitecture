package com.prashanth.contactssample.framework

import android.app.Application
import android.util.Log
import com.prashanth.core.data.ContactRepository
import com.prashanth.core.interactors.AddContact
import com.prashanth.core.interactors.GetAllContacts

class ContactsSampleApplication: Application() {
    val TAG = "ContactsSampleApp"
    override fun onCreate() {
        super.onCreate()

        val contactRepository = ContactRepository(AndroidContactDataSource(this))

        Log.d(TAG, "Setting the application!")
        ContactsSampleAppViewModelFactory.inject(this,
            Interactors(
                AddContact(contactRepository),
                GetAllContacts(contactRepository)))
    }
}