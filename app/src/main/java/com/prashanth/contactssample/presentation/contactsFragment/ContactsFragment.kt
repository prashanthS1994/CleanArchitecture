package com.prashanth.contactssample.presentation.contactsFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.prashanth.contactssample.R
import com.prashanth.contactssample.framework.ContactsSampleAppViewModelFactory
import com.prashanth.core.domain.Contact
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment: Fragment() {
    companion object {
        var count = 0
    }

    private lateinit var viewModel: ContactsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = ContactsAdapter {}

        contactsRecyclerView.adapter = adapter

        viewModel = ViewModelProvider(this, ContactsSampleAppViewModelFactory)
            .get(ContactsViewModel::class.java)
        viewModel.contacts.observe(viewLifecycleOwner,  { adapter.update(it) })
        viewModel.getAllContacts()

        fab.setOnClickListener {
            val contact = Contact("contact${++count}", "12584${count}")
            viewModel.addContact(contact)
        }
    }
}
