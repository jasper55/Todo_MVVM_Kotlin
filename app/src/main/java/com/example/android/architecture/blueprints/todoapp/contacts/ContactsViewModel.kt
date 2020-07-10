package com.example.android.architecture.blueprints.todoapp.contacts

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.util.ContactBookService
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import kotlinx.coroutines.launch


class ContactsViewModel(
        private val tasksRepository: TasksLocalDataSource,
        application: Application
) : AndroidViewModel(application) {

    private val _items = MutableLiveData<List<Contact>>().apply { value = emptyList() }
    val items: LiveData<List<Contact>> = _items

    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    private val _title = MutableLiveData<String>()
    private val _description = MutableLiveData<String>()


//    private val taskId?: String by inject()

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarText

    private val _errorMessageEvent = MutableLiveData<Event<String>>()
    val errorMessageEvent: LiveData<Event<String>> = _errorMessageEvent

    // Not used at the moment
    private val isDataLoadingError = MutableLiveData<Boolean>()

    fun deleteContact(contact: Contact) {

        // remove contact from itemList (for the view)
        val itemList = items.value
        val newList = itemList?.minus(listOf(contact))
        _items.value = newList
        val taskId = contact.taskId

        deleteContactFromDB(contact,taskId)
    }

    fun deleteContactFromDB(contact: Contact,taskId: Int?) = viewModelScope.launch {
        val taskResult = taskId?.let {
            tasksRepository.getTask(it)
        }
        if (taskResult is Result.Success) {

            _title.value = taskResult.data.title
            _description.value = taskResult.data.description
            val task = taskResult.data
            val contactIdString = task.contactIdString
            val newListString = contact.contactId?.let { ContactBookService.deleteContactFromList(it, contactIdString) }

            // updateContactIdString
            newListString?.let {
                tasksRepository.saveContactId(task, newListString)
            showSnackbarMessage(R.string.task_marked_complete)
            }
        }
    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun showErrorMessage(message: String) {
        _errorMessageEvent.value = Event(message)
    }

    fun loadContacts(taskId: Int?, context: Context?) {

        _dataLoading.value = true
        EspressoIdlingResource.increment() // Set app as busy.

        viewModelScope.launch {
            val taskResult = taskId?.let { tasksRepository.getTask(it) }
            if (taskResult is Result.Success) {

                val contactIdString = taskResult.data.contactIdString
                val contactList = ContactBookService.getContactArrayListFromDB(taskId, contactIdString, context!!)
                if (contactIdString == ""){
                    isDataLoadingError.value = false
                    _items.value = emptyList()
                } else {
                isDataLoadingError.value = false
                _items.value = ArrayList(contactList) }
            } else {
                isDataLoadingError.value = false
                _items.value = emptyList()
                showErrorMessage(getApplication<Application>().getString(R.string.loading_tasks_error))
            }

            EspressoIdlingResource.decrement() // Set app as idle.
            _dataLoading.value = false
        }
    }

    fun sendMailTo(contactEmail: String,context: Context) {
        showErrorMessage(context.getString(R.string.loading_tasks_error))

//        val emailIntent = Intent(Intent.ACTION_SEND)
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>(contactEmail))
//        emailIntent.type = "message/rfc822"


//        getApplication<Application>().baseContext.startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"))

        /*ACTION_SEND action to launch an email client installed on your Android device.*/
        val mIntent = Intent(Intent.ACTION_SEND)
        /*To send an email you need to specify mailto: as URI using setData() method
        and data type will be to text/plain using setType() method*/
        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"
        // put recipient email in intent
        /* recipient is put as array because you may wanna send email to multiple emails
           so enter comma(,) separated emails, it will be stored in array*/
        mIntent.putExtra(Intent.EXTRA_EMAIL, contactEmail)
        val subject = _title.value
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        val message = _description.value
        mIntent.putExtra(Intent.EXTRA_TEXT, message)


        try {
            //start email intent
            context.startActivity(Intent.createChooser(mIntent, "Choose an Email Client..."))
        }
        catch (e: Exception){
            //if any thing goes wrong for example no email client application or any exception
            //get and show exception message
            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
        }

    }

    fun callPhoneNumber(phoneNumber: String,context: Context) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phoneNumber, null))
        context.startActivity(intent)
    }
}