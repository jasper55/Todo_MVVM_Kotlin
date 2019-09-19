package com.example.android.architecture.blueprints.todoapp.contacts

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.data.Result
import com.example.android.architecture.blueprints.todoapp.R
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
        // Espresso does not work well with coroutines yet. See
        // https://github.com/Kotlin/kotlinx.coroutines/issues/982
        EspressoIdlingResource.increment() // Set app as busy.

        viewModelScope.launch {
            val taskResult = taskId?.let { tasksRepository.getTask(it) }
            if (taskResult is Result.Success) {

                val contactIdString = taskResult.data.contactIdString
                val contactList = ContactBookService.getContactArrayListFromDB(taskId, contactIdString, context!!)
                if (contactIdString == ""){
                    isDataLoadingError.value = false
                    _items.value = emptyList()
                    showErrorMessage(getApplication<Application>().getString(R.string.loading_tasks_error))
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

    fun sendMailTo(contactEmail: String) {

    }
}