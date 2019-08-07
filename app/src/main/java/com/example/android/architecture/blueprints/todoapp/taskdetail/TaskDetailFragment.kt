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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.TaskdetailFragBinding
import com.example.android.architecture.blueprints.todoapp.util.*
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

/**
 * Main UI for the task detail screen.
 */
class TaskDetailFragment : Fragment() {
    private lateinit var viewDataBinding: TaskdetailFragBinding

    private lateinit var viewModel: TaskDetailViewModel

    private var contactsPermissionGranted = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_SHORT)
        }

        setupNavigation()
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
        viewDataBinding.viewmodel?.start(taskId)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.taskdetail_frag, container, false)
        viewModel = obtainViewModel(TaskDetailViewModel::class.java)
        viewDataBinding = TaskdetailFragBinding.bind(view).apply {
            viewmodel = viewModel
            listener = object :

                    TaskDetailUserActionsListener {

                override fun onAddContactClicked(v: View) {
                    if (contactsPermissionGranted) {
                        startPickContactIntent()
                        } else requestPermissions(
                            arrayOf(Manifest.permission.READ_CONTACTS),
                            PermissionChecker.REQUEST_CONTACTS_CODE)
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

        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        setHasOptionsMenu(true)
        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            IntentCallService.CALL_PICK_CONTACT -> {
                viewDataBinding.viewmodel?.let {
                    view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_SHORT)
                }
                viewModel?._contactName.value = data?.let {
                    IntentCallService.getContactName(it, context)
                }!!
            }
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionChecker.REQUEST_CONTACTS_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Timber.i("READ_CONTACTS permission granted")
                    contactsPermissionGranted = true
                    startPickContactIntent()
                } else {
                    //else do nothing - will be call back on next launch
                    Timber.w("READ_CONTACTS permission refused")
                }
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

    fun startPickContactIntent(){
        Intent(Intent.ACTION_PICK, Uri.parse("content://contacts")).also { pickContactIntent ->
            pickContactIntent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE // Show user only contacts w/ phone numbers
            startActivityForResult(pickContactIntent, IntentCallService.CALL_PICK_CONTACT)
        }
    }

}
