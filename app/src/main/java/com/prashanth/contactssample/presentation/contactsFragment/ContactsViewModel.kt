package com.prashanth.contactssample.presentation.contactsFragment

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.prashanth.contactssample.framework.ContactsSampleAppViewModel
import com.prashanth.contactssample.framework.Interactors
import com.prashanth.core.domain.Contact
import kotlinx.coroutines.*

class ContactsViewModel(application:Application, interactors:Interactors):
    ContactsSampleAppViewModel(application, interactors) {

    private val _contacts: MutableLiveData<List<Contact>> = MutableLiveData()
    val contacts: LiveData<List<Contact>>
        get() = _contacts

    fun getAllContacts() {
        runBlocking { _contacts.postValue(interactors.getAllContacts()) }
    }

    fun addContact(contact: Contact) {
        runBlocking {
                interactors.addContact(contact)
            getAllContacts()
        }
    }
}
