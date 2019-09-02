package com.example.android.architecture.blueprints.todoapp.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.ContactlistFragBinding
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel
import timber.log.Timber
import java.util.ArrayList


class ContactsFragment : Fragment() {
    private lateinit var contactViewDataBinding: ContactlistFragBinding // is been generated because contactlist_frag.xml

    private lateinit var contactsViewModel: ContactsViewModel

    private lateinit var listAdapter: ContactsAdapter
    private var taskId: Int = -1

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        getTaskIdFromBundle()
        setupContactListAdapter()
        contactViewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        contactViewDataBinding.viewmodel?.loadContacts(taskId, context)
    }

    override fun onResume() {
        super.onResume()
        getTaskIdFromBundle()
        setupContactListAdapter()
        contactViewDataBinding.viewmodel?.loadContacts(taskId, context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val contactView = inflater.inflate(R.layout.contactlist_frag, container, false)
        contactsViewModel = obtainViewModel(ContactsViewModel::class.java)
        contactViewDataBinding = ContactlistFragBinding.bind(contactView).apply {

            viewmodel = contactsViewModel
            listener = object :

                    ContactItemUserActionsListener {
                override fun onSendEmailClicked(contactEmail: String) {
                    viewmodel?.sendMailTo(contactEmail)
                }

                override fun onContactDeleted(contact: Contact) {
                    viewmodel?.deleteContact(contact)
                }

            }
        }
        return contactView
    }

    private fun setupContactListAdapter() {

        val viewModel = contactViewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = ContactsAdapter(ArrayList(0), viewModel)
            contactViewDataBinding.contactListView.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun getTaskIdFromBundle() {
        val bundle = this.arguments
        if (bundle != null) {
            taskId = bundle.getInt("taskId") }
    }
}