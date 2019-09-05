package com.example.android.architecture.blueprints.todoapp.login

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
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel


class LoginFragment : Fragment() {

    private lateinit var viewDataBinding: LoginFragmentBinding

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.login_fragment, container, false)
        viewModel = obtainViewModel(LoginViewModel::class.java)
        viewDataBinding = LoginFragmentBinding.bind(root).apply {
            this.viewmodel = viewModel
        }
         //Set the lifecycle owner to the lifecycle of the view
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        return root
    //return null
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val viewmodel = viewDataBinding.viewmodel
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
    }

    private fun getUserId(): Int? {
        // return arguments?.let {
         //   LoginFragmentArgs.fromBundle(it).USERID
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

    private fun navigateToAddNewTask() {
        val action = TasksFragmentDirections
                .actionTasksFragmentToAddEditTaskFragment(-1,
                        resources.getString(R.string.add_task))
        findNavController().navigate(action)
    }

    private fun setupNavigation() {
        viewDataBinding.viewmodel?.goToRegisterEvent?.observe(this, EventObserver {
            navigateToRegister(it)
        })
        viewDataBinding.viewmodel?.goToTasksActivity?.observe(this, EventObserver {
            navigateToAddNewTask()
        })
    }

    private fun navigateToRegister(userId : Int) {
        val action = TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(userId)
        findNavController().navigate(action)
    }

}