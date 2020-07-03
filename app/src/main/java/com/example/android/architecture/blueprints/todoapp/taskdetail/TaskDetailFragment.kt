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
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.contacts.*
import com.example.android.architecture.blueprints.todoapp.databinding.TaskdetailFragBinding
import com.example.android.architecture.blueprints.todoapp.util.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import timber.log.Timber


/**
 * Main UI for the task detail screen.
 */
class TaskDetailFragment : Fragment() {
    private lateinit var viewDataBinding: TaskdetailFragBinding // is been generated because taskdetail_frag.xml

    private lateinit var viewModel: TaskDetailViewModel

    private lateinit var timePicker: TimePickerFragment
    private lateinit var datePicker: DatePickerFragment

    private var taskId: Int? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupFab()
        requestPermission(PermissionChecker.REQUEST_CONTACTS_CODE)
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, Snackbar.LENGTH_SHORT)
        }
        taskId = TaskDetailFragmentArgs.fromBundle(arguments!!).TASKID
        setupNavigation()
        setupDataPickers()
    }

    private fun setupDataPickers() {
        timePicker = TimePickerFragment()
        datePicker = DatePickerFragment()
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
        context?.let {
            viewDataBinding.viewmodel?.start(taskId, it)
        }
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
                    timePicker.showDialog(context)
                    val time = timePicker.getTime()
                    val long = DateUtil.parseTimeToLong(time)
                    viewModel?.saveTime(long, time)
                }

                override fun onDueDateChanged(v: View) {
                    datePicker.showDialog(context)
                    val date = datePicker.getDate()
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

        taskId = TaskDetailFragmentArgs.fromBundle(arguments!!).TASKID

        addContactsFragmentToView(taskId!!)

        viewDataBinding.setLifecycleOwner(this.viewLifecycleOwner)
        setHasOptionsMenu(true)
        return baseView // only this view is return as the other view is declared inside the xml
    }

    private fun addContactsFragmentToView(taskId: Int) {

        val fm = fragmentManager
        val ft = fm!!.beginTransaction()

        fm.beginTransaction()
        val fragTwo = ContactsFragment()
        val bundle = Bundle()
        bundle.putInt("taskId", taskId)
        fragTwo.arguments = bundle
        ft.add(R.id.contact_list, fragTwo)
        ft.commit()
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
                        it.viewModelScope.launch {
                            val contactIdString = it.getContactIdString()
                            val newContactId = data?.let { ContactBookService.getContactID(it, context) }
                            val contactString = ContactBookService.addContactToString(contactIdString!!, newContactId)
                            // initiate List which needs to be displayed by adapter
                            // ContactBookService.getContactListFromString(contactString)
                            it._contactIdString.value = contactString
                            contactString?.let { viewModel?.saveContactId(it) }
                        }
                    }
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
            R.id.menu_delete_db_task -> {
                viewDataBinding.viewmodel?.showDeleteDialog(context!!)
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