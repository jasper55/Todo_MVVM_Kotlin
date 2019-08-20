package com.example.android.architecture.blueprints.todoapp.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ContactBookServiceTest{

    @Test
    fun testDeleteContactFromList(){
        val contactString = "a b c"
        val contactToDelete = "a"
        val expectedString = "b c"
//        assertEquals(ContactBookService.deleteContactFromList(contactToDelete,contactString), expectedString)
    }
}