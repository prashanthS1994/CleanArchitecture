package com.prashanth.contactssample.framework

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import com.prashanth.core.data.ContactDataSource
import com.prashanth.core.domain.Contact


class AndroidContactDataSource(val context: Context): ContactDataSource {
    val TAG = "ContactDataSource"
    override suspend fun add(contact: Contact) {

    }

    override suspend fun getAll(): List<Contact> {
        val contactList = ArrayList<Contact>()

        val contentResolver: ContentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(
            ContactsContract.Contacts.CONTENT_URI,
            null, null, null, null
        )
        ///////////////////////
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
                        Log.i(TAG, "Name: $name")
                        Log.i(TAG, "Phone Number: $phoneNo")
                        val contact = Contact(name, phoneNo)
                        contactList.add(contact)
                    }
                    pCur.close()
                }
            }
        }
        cursor?.close()
        Log.d(TAG, "Size of list: ${contactList.size}")
        ///////////////////////
        return contactList
    }
}