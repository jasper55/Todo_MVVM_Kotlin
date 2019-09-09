package com.example.android.architecture.blueprints.todoapp.register


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.RegisterFragmentBinding
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel

class RegisterFragment : Fragment() {

    private lateinit var viewDataBinding: RegisterFragmentBinding

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.register_fragment, container, false)
        viewModel = obtainViewModel(RegisterViewModel::class.java)
        viewDataBinding = RegisterFragmentBinding.bind(root).apply {
            viewmodel = viewModel
            listener = object : RegisterUserActionListener {
                override fun onRegisterUserClicked() {
                    viewDataBinding.viewmodel?.createUser()
                    val userId = getUserId()
                    userId?.let { navigateToLogin(it) }
                }

                override fun onNavigateToLoginClicked() {
                    val userId = getUserId()
                    userId?.let { navigateToLogin(it) }
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
        return arguments?.let {
            RegisterFragmentArgs.fromBundle(it).USERID
        }

    }

    private fun setupNavigation() {
        viewDataBinding.viewmodel?.openLoginEvent?.observe(this, EventObserver {
            navigateToLogin(it)
        })
    }


    private fun navigateToLogin(userId: Int) {
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(userId)
        findNavController().navigate(action)
        //putUserIdToBundle()
    }

    private fun putUserIdToBundle() {

    }
}

