package com.prashanth.contactssample.presentation.contactsFragment

import android.Manifest
import android.app.Application
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.GrantPermissionRule
import com.google.common.truth.Truth.assertThat
import com.prashanth.contactssample.framework.AndroidContactDataSource
import com.prashanth.contactssample.framework.Interactors
import com.prashanth.core.data.ContactRepository
import com.prashanth.core.interactors.AddContact
import com.prashanth.core.interactors.GetAllContacts
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class ContactsViewModelTest {
    private lateinit var viewModel: ContactsViewModel
    private lateinit var application: Application

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val testCoroutineRule = TestCoroutineRule()

    @get:Rule
    val permissionRuleWriteContacts: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_CONTACTS)

    @get:Rule
    val permissionRuleReadContacts: GrantPermissionRule = GrantPermissionRule.grant(Manifest.permission.READ_CONTACTS)

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext()
        val contactRepository = ContactRepository(AndroidContactDataSource(application.applicationContext))
        clearAllContacts()
        viewModel = ContactsViewModel(
            application, Interactors(
                AddContact(contactRepository),
                GetAllContacts(contactRepository)
            )
        )
    }

    @Test
    fun addOneContactAndFetchIt_returnOneContact() = testCoroutineRule.runBlockingTest{
        val addContactsUri = ContactsContract.Data.CONTENT_URI
        val rowContactId: Long = getRawContactId()
        insertContactDisplayName(addContactsUri, rowContactId, "Contact1")
        insertContactPhoneNumber(addContactsUri, rowContactId, "782685412", "work")
        viewModel.getAllContacts()

        val contactsList = viewModel.contacts.getOrAwaitValueTest()

        assertThat(contactsList.size).isEqualTo(1)
        assertThat(contactsList[0].name).isEqualTo("Contact1")
        assertThat(contactsList[0].phoneNumber).isEqualTo("782685412")
    }

    @Test
    fun addTwoContactAndFetchIt_returnTwoContacts() = testCoroutineRule.runBlockingTest{
        val addContactsUri = ContactsContract.Data.CONTENT_URI
        val rowContactId: Long = getRawContactId()
        insertContactDisplayName(addContactsUri, rowContactId, "Contact1")
        insertContactPhoneNumber(addContactsUri, rowContactId, "782685412", "work")

        val rowContactId2: Long = getRawContactId()
        insertContactDisplayName(addContactsUri, rowContactId2, "Contact2")
        insertContactPhoneNumber(addContactsUri, rowContactId2, "782685413", "work")

        viewModel.getAllContacts()

        val contactsList = viewModel.contacts.getOrAwaitValueTest()

        assertThat(contactsList.size).isEqualTo(2)
        assertThat(contactsList[0].name).isEqualTo("Contact1")
        assertThat(contactsList[0].phoneNumber).isEqualTo("782685412")

        assertThat(contactsList[1].name).isEqualTo("Contact2")
        assertThat(contactsList[1].phoneNumber).isEqualTo("782685413")
    }

    @Test
    fun noContacts_returnNull() = testCoroutineRule.runBlockingTest{
        viewModel.getAllContacts()

        val contactsList = viewModel.contacts.getOrAwaitValueTest()

        assertThat(contactsList.size).isEqualTo(0)
    }

    //Helper functions
    private fun getRawContactId(): Long {
        // Insert an empty contact.
        val contentValues = ContentValues()
        val rawContactUri: Uri? =
            application.contentResolver.insert(
                ContactsContract.RawContacts.CONTENT_URI,
                contentValues
            )
        // Get the newly created contact raw id.
        return ContentUris.parseId(rawContactUri!!)
    }

    private fun insertContactDisplayName(
        addContactsUri: Uri,
        rawContactId: Long,
        displayName: String
    ) {
        val contentValues = ContentValues()
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)

        // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
        contentValues.put(
            ContactsContract.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
        )

        // Put contact display name value.
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, displayName)
        application.contentResolver.insert(addContactsUri, contentValues)
    }

    private fun insertContactPhoneNumber(
        addContactsUri: Uri,
        rawContactId: Long,
        phoneNumber: String,
        phoneTypeStr: String
    ) {
        // Create a ContentValues object.
        val contentValues = ContentValues()

        // Each contact must has an id to avoid java.lang.IllegalArgumentException: raw_contact_id is required error.
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)

        // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
        contentValues.put(
            ContactsContract.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        )

        // Put phone number value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)

        // Calculate phone type by user selection.
        var phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME
        when {
            "home".equals(phoneTypeStr, ignoreCase = true) -> {
                phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME
            }
            "mobile".equals(phoneTypeStr, ignoreCase = true) -> {
                phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
            }
            "work".equals(phoneTypeStr, ignoreCase = true) -> {
                phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_WORK
            }
        }
        // Put phone type value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, phoneContactType)

        // Insert new contact data into phone contact list.
        application.contentResolver.insert(addContactsUri, contentValues)
    }

    private fun clearAllContacts() {
        val contentResolver: ContentResolver = application.contentResolver
        val cursor: Cursor? =
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        while (cursor?.moveToNext()!!) {
            val lookupKey: String =
                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY))
            val uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey)
            contentResolver.delete(uri, null, null)
        }
    }
}
