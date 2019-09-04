package com.example.android.architecture.blueprints.todoapp.contacts

import com.example.android.architecture.blueprints.todoapp.data.Task

interface ContactItemUserActionsListener {

   fun onContactDeleted(contact: Contact)
   fun onSendEmailClicked(contactEmail: String)
}
