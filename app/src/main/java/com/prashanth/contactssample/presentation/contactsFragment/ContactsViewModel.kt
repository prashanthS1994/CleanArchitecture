package com.prashanth.contactssample.presentation.contactsFragment;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.prashanth.contactssample.framework.ContactsSampleAppViewModel

import com.prashanth.contactssample.framework.Interactors;
import com.prashanth.core.domain.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ContactsViewModel(application:Application, interactors:Interactors):
    ContactsSampleAppViewModel(application, interactors) {

    val contacts: MutableLiveData<List<Contact>> = MutableLiveData()

    fun getAllContacts() {
        GlobalScope.launch { contacts.postValue(interactors.getAllContacts()) }
    }

    fun addContact(contact: Contact) {
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                interactors.addContact(contact)
            }

            getAllContacts()
        }
    }
}
