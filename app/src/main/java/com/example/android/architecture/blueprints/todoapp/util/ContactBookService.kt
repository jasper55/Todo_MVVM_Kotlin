package com.example.android.architecture.blueprints.todoapp.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import androidx.core.app.ActivityCompat.startActivityForResult
import android.provider.ContactsContract.CommonDataKinds
import androidx.room.util.CursorUtil.getColumnIndex
import android.R.id




class ContactBookService {

    companion object  {

        fun getContactName(data: Intent, context: Context?): String {
            return getData(data,context,ContactsContract.Contacts.DISPLAY_NAME)
        }

        fun getContactID(data: Intent, context: Context?): String {
            return getData(data,context,ContactsContract.Contacts.NAME_RAW_CONTACT_ID)
        }
        
        fun getNameFromId(id: String): String{
            val name = id
            return name
        }

        private fun getData(data: Intent, context: Context?, type: String): String{
            var item = ""
            val contactData = data.data
            val c = context!!.contentResolver.query(contactData, null, null, null, null)
            if (c.moveToFirst()) {
                item = c.getString(c.getColumnIndex(type))
                // TODO Fetch other Contact details as you want to use
            }
            return item
        }

        fun getContactNameFromDB(contactId: String, context: Context?): String? {
            val names = ArrayList<String>()
            val cursor = context!!.contentResolver.query(
                    CommonDataKinds.Phone.CONTENT_URI, null,
                    CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf<String>(contactId), null)

            while (cursor.moveToNext()) {
                names.add(cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME)))
            }

            cursor.close()
            return names.toString()
        }

        fun addContactToList(){
            
        }

        val CALL_PICK_CONTACT = 1
    }

}