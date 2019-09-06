package com.example.android.architecture.blueprints.todoapp.login

import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.LoginFragmentBinding
import com.example.android.architecture.blueprints.todoapp.tasks.TasksFragmentDirections
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
                    user.id?.let { viewDataBinding.viewmodel?.loginUser(it) }
                }

                override fun onRegisterClicked() {
                    user.id?.let {
                        viewDataBinding.viewmodel?.openRegisterFrag(it)
                    }
                }
            }
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        user = User()
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupNavigation()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    private fun getUserId(): Int? {
        // return arguments?.let {
        //   LoginFragmentArgs.fromBundle(it).USER_ID
        //}
        return null
    }

    private fun registerLoginFragment() {

        val fm = fragmentManager
        val ft = fm!!.beginTransaction()

        fm.beginTransaction()
        val fragTwo = LoginFragment()
        val bundle = Bundle()
        //bundle.putInt("taskId", taskId)
        fragTwo.arguments = bundle
        ft.add(R.id.fragment_login, fragTwo)
        ft.commit()
    }


    private fun setupNavigation() {
        viewDataBinding.viewmodel?.openRegisterEvent?.observe(this, EventObserver {
            //navigateToRegister(it)
        })
        viewDataBinding.viewmodel?.openTaskListEvent?.observe(this, EventObserver {
            navigateToTaskActivity(it)
        })
    }

    private fun navigateToTaskActivity(userId: Int) {
        val action = LoginFragmentDirections.actionLoginFragmentToTasksFragment(userId)
        findNavController().navigate(action)
    }

    private fun navigateToAddNewTask() {
        val action = TasksFragmentDirections
                .actionTasksFragmentToAddEditTaskFragment(-1,
                        resources.getString(R.string.add_task))
        findNavController().navigate(action)
    }
}