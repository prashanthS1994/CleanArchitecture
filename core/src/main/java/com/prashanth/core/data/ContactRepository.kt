package com.prashanth.core.data

import com.prashanth.core.domain.Contact

class ContactRepository(private val dataSource: ContactDataSource) {
    suspend fun addContact(contact: Contact) = dataSource.add(contact)
    suspend fun getAllContacts() = dataSource.getAll()
}