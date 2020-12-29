package com.prashanth.core.interactors

import com.prashanth.core.data.ContactRepository
import com.prashanth.core.domain.Contact

class AddContact(private val contactRepository: ContactRepository) {
    suspend operator fun invoke(contact: Contact) = contactRepository.addContact(contact)
}