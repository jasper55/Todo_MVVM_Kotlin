package com.example.android.architecture.blueprints.todoapp.contacts

import android.app.Application
import androidx.lifecycle.*
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.util.ContactBookService
import kotlinx.coroutines.launch

class ContactsViewModel(
        private val tasksRepository: TasksLocalDataSource,
        application: Application
) : AndroidViewModel(application) {

    private val _items = MutableLiveData<List<Contact>>().apply { value = emptyList() }
    val items: LiveData<List<Contact>> = _items

    private val _items2 = MutableLiveData<List<Contact>>().apply { value = emptyList() }
    val items2: LiveData<List<Contact>> = _items2

    fun deleteContact(task: Task, contact: Contact) = viewModelScope.launch {

        // remove contact from itemList
        val itemList = items.value
        val newList = itemList?.minus(listOf(contact))
//       __ itemList?.remove(contact)
//        itemList?.removeAt(contact)
        _items.value = newList

        // remove contactID from contactIsString
        val newListString = contact.contactId?.let { ContactBookService.deleteContactFromList(it, task.contactIdString) }

        // updateContactIdString
        newListString?.let {
            tasksRepository.saveContactId(task, newListString)
//            showSnackbarMessage(R.string.task_marked_complete)
        }
    }

    fun sendMailTo(contactEmail: String) {

    }
}