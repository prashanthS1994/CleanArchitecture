package com.prashanth.contactssample.framework

import android.app.Application
import com.prashanth.core.data.ContactRepository
import com.prashanth.core.interactors.AddContact
import com.prashanth.core.interactors.GetAllContacts

class ContactsSampleApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        val contactRepository = ContactRepository(AndroidContactDataSource(this))

        ContactsSampleAppViewModelFactory.inject(this,
            Interactors(
                AddContact(contactRepository),
                GetAllContacts(contactRepository)))
    }
}
