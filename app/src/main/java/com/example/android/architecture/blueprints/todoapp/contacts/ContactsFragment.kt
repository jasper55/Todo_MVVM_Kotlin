package com.example.android.architecture.blueprints.todoapp.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.databinding.ContactlistFragBinding
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel
import timber.log.Timber
import java.util.ArrayList


class ContactsFragment: Fragment() {
    private lateinit var contactViewDataBinding: ContactlistFragBinding // is been generated because contactlist_frag.xml

    private lateinit var contactsViewModel: ContactsViewModel

    private lateinit var listAdapter: ContactsAdapter
    private lateinit var taskId: String

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val bundle = this.arguments
        if (bundle != null) {
            taskId = bundle.getString("taskId") }
//        viewDataBinding.viewmodel?.let {
//            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_SHORT)
//        }
//        val task = viewModel.task.value!!
        contactViewDataBinding.viewmodel?.loadContacts(taskId,context)
        setupContactList()
    }


    override fun onResume() {
        super.onResume()
//        val taskId = arguments?.let {
//            TaskDetailFragmentArgs.fromBundle(it).TASKID
//        }
        context?.let {
            contactViewDataBinding.viewmodel?.loadContacts(taskId, it)
        }
        setupContactList()
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

    private fun setupContactList() {

        val viewModel = contactViewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = ContactsAdapter(ArrayList(0), viewModel)
            contactViewDataBinding.contactListView.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    fun getTaskId(): String{
        return this.taskId
    }

}