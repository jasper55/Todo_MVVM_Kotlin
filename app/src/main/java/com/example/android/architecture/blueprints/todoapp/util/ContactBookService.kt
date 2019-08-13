package com.example.android.architecture.blueprints.todoapp.util

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract.CommonDataKinds


class ContactBookService {

    companion object  {

        fun getContactName(data: Intent, context: Context?): String {
            return getData(data,context,CommonDataKinds.Phone.DISPLAY_NAME)
        }

        fun getContactID(data: Intent, context: Context?): String {
            return getData(data,context,CommonDataKinds.Phone.CONTACT_ID)
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

        fun displayContactIds(): String{
            return ""
        }

        fun getContactNameFromDB(contactId: String, context: Context?): String? {
            var name: String? = null
            val cursor = context!!.contentResolver.query(
                    CommonDataKinds.Phone.CONTENT_URI, null,
                    CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    arrayOf<String>(contactId), null)

            while (cursor.moveToNext()) {
               name = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.DISPLAY_NAME))
            }

            cursor.close()
            return name
        }

        fun addContactToString(contactName: String, oldString: String?): String {
            if(oldString == null){
                return contactName
            }
            val newString = "$oldString $contactName"
            return newString
        }

        fun stringToList(s : String) = s.trim().splitToSequence(' ')
                .filter { it.isNotEmpty() } // or: .filter { it.isNotBlank() }
                .toList()

        fun getContactListFromString(contactString: String): List<String> {
            return stringToList(contactString)
        }

        fun deleteContactFromList(contactName: String, list: String): String {
            if (list.contains(" $contactName")) {
                 list.replace(" $contactName", "")
            }
            return list
        }

        fun getIdFromName(contactName: String, context: Context?): String {
            var id: String? = null
            val cursor = context!!.contentResolver.query(
                    CommonDataKinds.Phone.CONTENT_URI, null,
                    CommonDataKinds.Phone.DISPLAY_NAME + " = ?",
                    arrayOf<String>(contactName), null)

            while (cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.CONTACT_ID))
            }

            cursor.close()
            return id!!
        }


        val CALL_PICK_CONTACT = 1
    }

}