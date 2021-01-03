package com.prashanth.contactssample.framework

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import com.prashanth.contactssample.framework.ContactsSampleAppViewModelFactory.application
import com.prashanth.core.data.ContactDataSource
import com.prashanth.core.domain.Contact

class AndroidContactDataSource(private val context: Context): ContactDataSource {
    override suspend fun add(contact: Contact) {
        val addContactsUri = ContactsContract.Data.CONTENT_URI
        val rowContactId: Long = getRawContactId()
        insertContactDisplayName(addContactsUri, rowContactId, contact.name)
        insertContactPhoneNumber(addContactsUri, rowContactId, contact.phoneNumber, "work")
    }

    override suspend fun getAll(): List<Contact> {
        val contactList = ArrayList<Contact>()

        val contentResolver: ContentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        if (cursor?.count ?: 0 > 0) {
            while (cursor != null && cursor.moveToNext()) {
                val id = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name = cursor.getString(
                    cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                if (cursor.getInt(
                        cursor.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER
                        )
                    ) > 0
                ) {
                    val pCur: Cursor? = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    while (pCur?.moveToNext()!!) {
                        val phoneNo = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                        val contact = Contact(name, phoneNo)
                        contactList.add(contact)
                    }
                    pCur.close()
                }
            }
        }
        cursor?.close()
        return contactList
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
        cursor.close()
    }
}
