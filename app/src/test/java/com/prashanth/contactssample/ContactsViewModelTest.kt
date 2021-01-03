package com.prashanth.contactssample

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.prashanth.contactssample.framework.ContactsSampleApplication
import com.prashanth.contactssample.framework.Interactors
import com.prashanth.contactssample.presentation.contactsFragment.ContactsViewModel
import com.prashanth.core.domain.Contact
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals


class ContactsViewModelTest {
    @get:Rule
    val liveDataRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @RelaxedMockK
    private lateinit var interactors: Interactors

    private val application = ContactsSampleApplication()

    private val contactsViewModel: ContactsViewModel by lazy {
        ContactsViewModel(application, interactors)
    }

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        //every { GlobalScope.launch { interactors.getAllContacts() } } returns contactsList
//        coEvery  { interactors.getAllContacts()  } returns contactsList
    }

    @Test
    fun getOneContact() {
        //Given
        val contactsList: List<Contact> = listOf(
            Contact("Contact1", "123")
        )
        coEvery { interactors.getAllContacts() } returns contactsList

        contactsViewModel.getAllContacts()

        contactsViewModel.contacts.observeForever {
            assertEquals("Contact1", it[0].name)
            assertEquals("123", it[0].phoneNumber)
        }
    }

    @Test
    fun getTwoContactsNew() {
            val contactsList: List<Contact> = listOf(
                Contact("Contact1", "123")
                , Contact("Contact2", "456")
            )
            coEvery { interactors.getAllContacts() } returns contactsList

            contactsViewModel.getAllContacts()

            contactsViewModel.contacts.observeForever {
                assertEquals("Contact1", it[0].name)
                assertEquals("123", it[0].phoneNumber)

                assertEquals("Contact2", it[1].name)
                assertEquals("456", it[1].phoneNumber)
            }
    }

    @Test
    fun getEmptyContactList() {
        val contactsList: List<Contact> = listOf()
        coEvery { interactors.getAllContacts() } returns contactsList

        contactsViewModel.getAllContacts()

        contactsViewModel.contacts.observeForever {
            assertEquals(0, it.size)
        }
    }

    @Test
    fun testAddContact() {
        val contact = Contact("Contact3", "789")

        val contactsList: List<Contact> = listOf(
            contact
        )
        coEvery { interactors.getAllContacts() } returns contactsList

        contactsViewModel.addContact(contact)

        contactsViewModel.contacts.observeForever {
            assertEquals("Contact3", it[0].name)
            assertEquals("789", it[0].phoneNumber)
        }
    }

    @Test
    fun testAddContact_emptyContact() {
        val contact = Contact("", "")

        val contactsList: List<Contact> = listOf(
            contact
        )
        coEvery { interactors.getAllContacts() } returns contactsList

        contactsViewModel.addContact(contact)

        contactsViewModel.contacts.observeForever {
            assertEquals("", it[0].name)
            assertEquals("", it[0].phoneNumber)
        }
    }
}
