package com.example.android.architecture.blueprints.todoapp.contacts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.databinding.DataBindingUtil
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.databinding.ContactItemBinding

class ContactsAdapter(
        private var contacts: List<Contact>,
        private val viewModel: ContactsViewModel
) : BaseAdapter() {

    fun replaceData(contacts: List<Contact>) {
        setList(contacts)
    }

    override fun getItem(position: Int) = contacts[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = contacts.size

    override fun getView(position: Int, view: View?, viewGroup: ViewGroup): View {
        val binding: ContactItemBinding
        binding = if (view == null) {
            // Inflate
            val inflater = LayoutInflater.from(viewGroup.context)

            // Create the binding
            ContactItemBinding.inflate(inflater, viewGroup, false)
        } else {
            // Recycling view
            DataBindingUtil.getBinding(view) ?: throw IllegalStateException()
        }

        val userActionsListener = object : ContactItemUserActionsListener {
            override fun onContactDeleted(task: Task, contact: Contact) {
                viewModel.deleteContact(task, contact)
            }

            override fun onSendEmailClicked(contactEmail: String) {
                viewModel.sendMailTo(contactEmail)
            }
        }

        with(binding) {
            contact = contacts[position]
            listener = userActionsListener
            executePendingBindings()

            return binding.root
        }

    }

    private fun setList(contacts: List<Contact>) {
        this.contacts = contacts
        notifyDataSetChanged()
    }
}