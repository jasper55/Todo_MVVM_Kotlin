/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.architecture.blueprints.todoapp.taskdetail

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.contacts.Contact
import com.example.android.architecture.blueprints.todoapp.contacts.ContactsAdapter
import com.example.android.architecture.blueprints.todoapp.contacts.ContactItemUserActionsListener
import com.example.android.architecture.blueprints.todoapp.contacts.ContactsViewModel
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.databinding.ContactlistFragBinding
import com.example.android.architecture.blueprints.todoapp.databinding.TaskdetailFragBinding
import com.example.android.architecture.blueprints.todoapp.util.*
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.ArrayList

/**
 * Main UI for the task detail screen.
 */
class TaskDetailFragment : Fragment() {
    private lateinit var viewDataBinding: TaskdetailFragBinding // is been generated because taskdetail_frag.xml
    private lateinit var contactViewDataBinding: ContactlistFragBinding // is been generated because contactlist_frag.xml


    private lateinit var viewModel: TaskDetailViewModel
    private lateinit var contactsViewModel: ContactsViewModel

    private lateinit var listAdapter: ContactsAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        requestPermission(PermissionChecker.REQUEST_CONTACTS_CODE)
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_SHORT)
        }

        setupContactList()
        setupNavigation()
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

    private fun setupNavigation() {
        viewModel.deleteTaskCommand.observe(this, EventObserver {
            val action = TaskDetailFragmentDirections
                    .actionTaskDetailFragmentToTasksFragment(DELETE_RESULT_OK)
            findNavController().navigate(action)
        })
        viewModel.editTaskCommand.observe(this, EventObserver {
            val taskId = TaskDetailFragmentArgs.fromBundle(arguments!!).TASKID
            val action = TaskDetailFragmentDirections
                    .actionTaskDetailFragmentToAddEditTaskFragment(taskId,
                            resources.getString(R.string.edit_task))
            findNavController().navigate(action)
        })
    }

    private fun setupFab() {
        activity?.findViewById<View>(R.id.fab_edit_task)?.setOnClickListener {
            viewDataBinding.viewmodel?.editTask()
        }
    }

    override fun onResume() {
        super.onResume()
        val taskId = arguments?.let {
            TaskDetailFragmentArgs.fromBundle(it).TASKID
        }
        context?.let { viewDataBinding.viewmodel?.start(taskId, it) }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val baseView = inflater.inflate(R.layout.taskdetail_frag, container, false)
        viewModel = obtainViewModel(TaskDetailViewModel::class.java)
        viewDataBinding = TaskdetailFragBinding.bind(baseView).apply {
            viewmodel = viewModel
            listener = object :

                    TaskDetailUserActionsListener {

                override fun onAddContactClicked(v: View) {
                    if (viewmodel?.contactPermissionGranted?.value!!) {
                        startPickContactIntent()
                    } else requestPermission(PermissionChecker.REQUEST_ADD_CONTACT)
                }

                override fun onTimeChanged(v: View) {
                    TimePickerFragment.showDialog(context)
                    val time = TimePickerFragment.getTime()
                    val long = DateUtil.parseTimeToLong(time)
                    viewModel?.saveTime(long, time)
                }

                override fun onDueDateChanged(v: View) {
                    DatePickerFragment.showDialog(context)
                    val date = DatePickerFragment.getDate()
                    val long = DateUtil.parseToLong(date)
                    viewModel?.saveDueDate(long, date)
                }

                override fun onFavoriteChanged(v: View) {
                    viewmodel?.setFavored((v as CheckBox).isChecked)
                }

                override fun onCompleteChanged(v: View) {
                    viewmodel?.setCompleted((v as CheckBox).isChecked)
                }
            }
        }

        val contactView = inflater.inflate(R.layout.contactlist_frag, container, false)
        contactView.findViewById<ListView>(R.id.contact_list)
        contactsViewModel = obtainViewModel(ContactsViewModel::class.java)
        contactViewDataBinding = ContactlistFragBinding.bind(contactView).apply {
            viewmodel = contactsViewModel
            listener = object :

                    ContactItemUserActionsListener {
                override fun onSendEmailClicked(contactEmail: String) {
                    viewmodel?.sendMailTo(contactEmail)
                }

                override fun onContactDeleted(task: Task, contact: Contact) {
                    viewmodel?.deleteContact(task, contact)
                }

            }
        }

        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        contactViewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        setHasOptionsMenu(true)

        return baseView // only this view is return as the other view is declared inside the xml
    }

    private fun requestPermission(code: Int) {
        requestPermissions(
                arrayOf(Manifest.permission.READ_CONTACTS),
                code)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK)
            when (requestCode) {
                ContactBookService.CALL_PICK_CONTACT -> {
                    viewDataBinding.viewmodel?.let {
                        view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_SHORT)
                    }
                    val contactIdString = viewModel?.getContactIdString()
                    val newContactId = data?.let { ContactBookService.getContactID(it, context) }

                    val contactString = ContactBookService.addContactToString(contactIdString!!, newContactId)
                    // initiate List which needs to be displayed by adapter
                    // ContactBookService.getContactListFromString(contactString)
                    viewModel?._contactIdString.value = contactString

                    contactString?.let { viewModel?.saveContactId(it) }
                }
            }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionChecker.REQUEST_ADD_CONTACT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.i("READ_CONTACTS permission granted")
                    viewDataBinding.viewmodel?._contactPermissionGranted?.value = true
                    startPickContactIntent()
                } else {
                    //else do nothing - will be call back on next launch
                    Timber.w("READ_CONTACTS permission refused")
                }
            }

            PermissionChecker.REQUEST_CONTACTS_CODE -> {
                return
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_delete -> {
                viewDataBinding.viewmodel?.deleteTask()
                true
            }
            else -> false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.taskdetail_fragment_menu, menu)
    }

    fun startPickContactIntent() {
        Intent(Intent.ACTION_PICK, Uri.parse("content://contacts")).also { pickContactIntent ->
            pickContactIntent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE // Show user only contacts w/ phone numbers
            startActivityForResult(pickContactIntent, ContactBookService.CALL_PICK_CONTACT)
        }
    }

}
