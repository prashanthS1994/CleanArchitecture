package com.prashanth.contactssample.presentation.contactsFragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prashanth.contactssample.R
import com.prashanth.core.domain.Contact
import kotlinx.android.synthetic.main.item_contact_display.view.*

class ContactsAdapter(
    private val contacts: MutableList<Contact> = mutableListOf(),
    private val itemClickListener: (Contact) -> Unit
) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    val TAG = "ContactsAdapter"
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contactName: TextView = view.contactName
        val phoneNumber: TextView = view.contactPhoneNumber
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_contact_display, parent, false)
        )
    }

    override fun getItemCount() = contacts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.run {
        holder.contactName.text = contacts[position].name
        holder.phoneNumber.text = contacts[position].phoneNumber
        holder.itemView.setOnClickListener { itemClickListener.invoke(contacts[position]) }
    }

    fun update(newContacts: List<Contact>) {
        contacts.clear()
        contacts.addAll(newContacts)
        Log.d(TAG, "Size of list: ${newContacts.size}")
        notifyDataSetChanged()
    }
}