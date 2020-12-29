package com.prashanth.core.data

import com.prashanth.core.domain.Contact

interface ContactDataSource {
    suspend fun add(contact: Contact)
    suspend fun getAll(): List<Contact>
}