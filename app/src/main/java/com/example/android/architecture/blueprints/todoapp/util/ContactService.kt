package com.example.android.architecture.blueprints.todoapp.util

import android.app.Activity
import android.content.ClipData
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.fragment.app.FragmentActivity
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity


public class ContactService {

    companion object  {

        fun getName(data: Intent, context: Context): String {
            var name = ""
            val contactData = data.data
            val c = context.contentResolver.query(contactData, null, null, null, null)
            if (c.moveToFirst()) {
                name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
                // TODO Fetch other Contact details as you want to use
            }
            return name
        }

        fun pickContact(context: Context?) {
            val pickContactIntent = Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)
            startActivityForResult(context as Activity,pickContactIntent, CALL_PICK_CONTACT,null)
        }

        val CALL_PICK_CONTACT = 1
    }


}