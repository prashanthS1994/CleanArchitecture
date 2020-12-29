package com.prashanth.contactssample.framework

import com.prashanth.core.interactors.AddContact
import com.prashanth.core.interactors.GetAllContacts

data class Interactors(val addContact: AddContact, val getAllContacts: GetAllContacts)
