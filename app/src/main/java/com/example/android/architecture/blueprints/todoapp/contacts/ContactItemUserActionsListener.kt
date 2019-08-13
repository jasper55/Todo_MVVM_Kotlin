package com.example.android.architecture.blueprints.todoapp.contacts

interface ContactItemUserActionsListener {

   fun onContactDeleted(contact: Contact)
   fun onSendEmailClicked(contactEmail: String)
}
