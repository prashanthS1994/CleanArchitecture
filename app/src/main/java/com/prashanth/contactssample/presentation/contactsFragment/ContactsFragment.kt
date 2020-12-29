package com.prashanth.contactssample.presentation.contactsFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.prashanth.contactssample.R
import com.prashanth.contactssample.framework.ContactsSampleAppViewModelFactory
import kotlinx.android.synthetic.main.fragment_contacts.*

class ContactsFragment: Fragment() {
    companion object {
        fun newInstance() = ContactsFragment()
    }

    private lateinit var viewModel: ContactsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val adapter = ContactsAdapter() {
            //mainActivityDelegate.openDocument(it)
        }
        contactsRecyclerView.adapter = adapter

        viewModel = ViewModelProviders.of(this, ContactsSampleAppViewModelFactory)
            .get(ContactsViewModel::class.java)
        viewModel.contacts.observe(this, Observer { adapter.update(it) })
        viewModel.getAllContacts()

        fab.setOnClickListener {
        //TODO: Add implementation to add contact
        }
    }
}