package com.example.android.architecture.blueprints.todoapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat.startActivityForResult


class IntentCallService {

    companion object  {

        fun getContactName(data: Intent, context: Context?): String {
            var name = ""
            val contactData = data.data
            val c = context!!.contentResolver.query(contactData, null, null, null, null)
            if (c.moveToFirst()) {
                name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                // TODO Fetch other Contact details as you want to use
            }
            return name
        }

        fun getContactID(data: Intent, context: Context?): String {
            var name = ""
            val contactData = data.data
            val c = context!!.contentResolver.query(contactData, null, null, null, null)
            if (c.moveToFirst()) {
                name = c.getString(c.getColumnIndex(ContactsContract.Contacts.NAME_RAW_CONTACT_ID))
                // TODO Fetch other Contact details as you want to use
            }
            return name
        }

        fun startPickContactIntent(activity: Activity) {
            //val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            //startActivityForResult(activity,pickContactIntent, CALL_PICK_CONTACT,null)
            Intent(Intent.ACTION_PICK, Uri.parse("content://contacts")).also { pickContactIntent ->
                pickContactIntent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE // Show user only contacts w/ phone numbers
                //startActivityForResult(pickContactIntent, CALL_PICK_CONTACT)
            }
        }

        val CALL_PICK_CONTACT = 1
    }


}