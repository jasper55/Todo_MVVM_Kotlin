package com.example.android.architecture.blueprints.todoapp.login

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.LoginFragmentBinding
import com.example.android.architecture.blueprints.todoapp.tasks.TasksActivity
import com.example.android.architecture.blueprints.todoapp.userrepository.User
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel


class LoginFragment : Fragment() {

    private lateinit var viewDataBinding: LoginFragmentBinding

    private lateinit var viewModel: LoginViewModel

    private lateinit var user: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.login_fragment, container, false)
        viewModel = obtainViewModel(LoginViewModel::class.java)
        viewDataBinding = LoginFragmentBinding.bind(root).apply {
            viewmodel = viewModel
            listener = object : UserActionsNavigationListener{

                override fun onLoginClicked() {
                    //user.uid?.let { viewDataBinding.viewmodel?.loginUser(it) }
                }

                override fun onRegisterClicked() {
                        viewDataBinding.viewmodel?.openRegisterFrag()
                }
            }
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupNavigation()
    }

    private fun getUserId(): Int? {
        // return arguments?.let {
        //   LoginFragmentArgs.fromBundle(it).USER_ID
        //}
        return null
    }

    private fun setupNavigation() {
        viewDataBinding.viewmodel?.openRegisterEvent?.observe(this, EventObserver {
            navigateToRegisterFrag()
        })
        viewDataBinding.viewmodel?.openTaskListEvent?.observe(this, EventObserver {
            navigateToTaskActivity(it)
        })
    }

    private fun navigateToTaskActivity(userUid: String) {
        val action = LoginFragmentDirections.actionLoginFragmentToTasksFragment()
        val intent = Intent(context,TasksActivity::class.java)
        intent.putExtra("USER_ID",userUid)
        startActivity(intent)
        findNavController().navigate(action)
    }

    private fun navigateToRegisterFrag() {
        val action = LoginFragmentDirections.actionLoginFragmentToRegisterFragment(null.toString())
        findNavController().navigate(action)
    }
}