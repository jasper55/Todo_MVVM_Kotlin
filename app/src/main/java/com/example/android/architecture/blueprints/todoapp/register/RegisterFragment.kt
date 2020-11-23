package com.example.android.architecture.blueprints.todoapp.register


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.databinding.RegisterFragmentBinding
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel
import com.example.android.architecture.blueprints.todoapp.util.onTextChanged
import com.example.android.architecture.blueprints.todoapp.util.setupDismissableSnackbar
import com.example.android.architecture.blueprints.todoapp.util.setupSnackbar
import com.example.android.architecture.blueprints.todoapp.util.vibratePhone
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
        setupDismissableSnackbar()
        listenForPasswordFieldChanges()
    }

    private fun getUserId(): String? {
        return arguments?.let {
            RegisterFragmentArgs.fromBundle(it).USERID
        }
    }

    private fun listenForPasswordFieldChanges() {
        viewDataBinding.inputPassword.onTextChanged {
            if (!isInteger(it)) {
                hideKeyboard(context!!)
                vibratePhone()
                viewDataBinding.inputPassword.text.dropLast(1)
                viewDataBinding.errorPrompt.text = "Only numbers are allowed for your password"
                viewDataBinding.errorPrompt.visibility = View.VISIBLE
                viewDataBinding.errorPrompt.startAnimation(AnimationUtils.loadAnimation(context,R.anim.shake))
            } else {
                viewDataBinding.errorPrompt.visibility = View.GONE
            }
        }
    }

    private fun isInteger(str: String?) = str?.toIntOrNull()?.let { true } ?: false

    private fun hideKeyboard(context: Context) {
        val view: View = this.view!!.findFocus()
        val imm: InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun saveUserUidToBundle(userUid: String) {
        val fragment = RegisterFragment()
        val args = Bundle()
        args.putString("USER_ID", userUid)
        fragment.setArguments(args)
    }

    private fun setupNavigation() {
        viewDataBinding.viewmodel?.openLoginEvent?.observe(this, EventObserver {
            if(isPasswordLength6()) {
                navigateToLogin(it)
            } else {
                viewDataBinding.errorPrompt.visibility = View.VISIBLE
                viewDataBinding.errorPrompt.text = "Your password must consist of 6 numbers"
                return@EventObserver
            }

        })
    }

    private fun isPasswordLength6(): Boolean {
        return viewDataBinding.inputPassword.length() == 6
    }


    private fun navigateToLogin(userUid: String) {
        // at the same time puts userUid to Bunde
        val action = RegisterFragmentDirections.actionRegisterFragmentToLoginFragment(userUid)
        findNavController().navigate(action)
    }

    private fun setupSnackbar(length: Int, bgColor: Int = context!!.getColor(R.color.colorSnackbar)) {
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, length, bgColor)
        }
    }

    private fun setupDismissableSnackbar(length: Int = Snackbar.LENGTH_LONG) {
        viewDataBinding.viewmodel?.let {
            view?.setupDismissableSnackbar(this,it.errorMessageEvent, length)
        }
    }
}