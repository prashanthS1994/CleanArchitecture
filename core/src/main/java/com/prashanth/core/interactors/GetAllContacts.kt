package com.prashanth.core.interactors

import com.prashanth.core.data.ContactRepository

class GetAllContacts(private val contactRepository: ContactRepository) {
    suspend operator fun invoke() = contactRepository.getAllContacts()
}