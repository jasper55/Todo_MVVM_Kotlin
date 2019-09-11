package com.example.android.architecture.blueprints.todoapp.register


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.RegisterFragmentBinding
import com.example.android.architecture.blueprints.todoapp.login.LoginFragment
import com.example.android.architecture.blueprints.todoapp.login.LoginFragmentArgs
import com.example.android.architecture.blueprints.todoapp.tasks.TasksFragmentArgs
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel
import com.example.android.architecture.blueprints.todoapp.util.setupSnackbar
import com.google.android.material.snackbar.Snackbar

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
                    viewDataBinding.viewmodel?.registerUser()
                    //if (viewDataBinding.viewmodel?.user?.value?.uid == null) return
                    viewDataBinding.viewmodel?.user?.value?.uid?.let {
                        navigateToLogin(it)
                        //saveUserUidToBundle(it)
                    }
                }

                override fun onNavigateToLoginClicked() {
                    viewDataBinding.viewmodel?.user?.value?.uid?.let { navigateToLogin(it) }
                }
            }
        }
        return root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupNavigation()
        setupSnackbar(Snackbar.LENGTH_SHORT)
    }

    private fun getUserId(): String? {
        return arguments?.let {
            RegisterFragmentArgs.fromBundle(it).USERID
        }
    }

    private fun saveUserUidToBundle(userUid: String) {
        val fragment = RegisterFragment()
        val args = Bundle()
        args.putString("USER_ID", userUid)
        fragment.setArguments(args)
    }

    private fun setupNavigation() {
        viewDataBinding.viewmodel?.openLoginEvent?.observe(this, EventObserver {
            navigateToLogin(it)
        })
    }


    private fun navigateToLogin(userUid: String) {
        // at the same time puts userUid to Bunde
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(userUid)
        findNavController().navigate(action)
    }

    private fun setupSnackbar(length: Int, bgColor: Int = context!!.getColor(R.color.colorTextPrimary)) {
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, length, bgColor)
        }
    }
}