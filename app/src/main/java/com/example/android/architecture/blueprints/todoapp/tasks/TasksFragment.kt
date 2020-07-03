package com.example.android.architecture.blueprints.todoapp.tasks

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.architecture.blueprints.todoapp.EventObserver
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.databinding.TasksFragBinding
import com.example.android.architecture.blueprints.todoapp.register.StartActivity
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel
import com.example.android.architecture.blueprints.todoapp.util.setupDismissableSnackbar
import com.example.android.architecture.blueprints.todoapp.util.setupSnackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.ArrayList

/**
 * Display a grid of [Task]s. User can choose to view all, active or completed tasks.
 */
class TasksFragment : Fragment() {

    private lateinit var viewDataBinding: TasksFragBinding
    private lateinit var listAdapter: TasksAdapter
    private lateinit var act: AppCompatActivity

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewDataBinding = TasksFragBinding.inflate(inflater, container, false).apply {
            viewmodel = obtainViewModel(TasksViewModel::class.java)
        }
        setHasOptionsMenu(true)
        return viewDataBinding.root
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.menu_clear -> {
                    viewDataBinding.viewmodel?.clearCompletedTasks()
                    true
                }
                R.id.menu_filter -> {
                    showFilteringPopUpMenu()
                    true
                }
                R.id.menu_refresh -> {
                    viewDataBinding.viewmodel?.loadTasks(true)
                    true
                }
                R.id.menu_save_to_remote -> {
                    viewDataBinding.viewmodel?.saveDataToFirebase(false)
                    true
                }
                R.id.menu_save_to_local -> {
                    viewDataBinding.viewmodel?.getTaskListFromFirebaseAndStoreToLocalDB()
                    true
                }
                R.id.menu_synchronize -> {
                    viewDataBinding.viewmodel?.syncDatabases(act)
//                    viewDataBinding.viewmodel?.checkNetworkConnection(act)
//                    viewDataBinding.viewmodel?.checkUserStatus()
//                    if (viewDataBinding.viewmodel?.isInternetAvailable?.value!! && viewDataBinding.viewmodel?.userLoggedIn?.value!!) {
//                        viewDataBinding.viewmodel?.saveDataToFirebase()
//                    }
                    true
                }
                R.id.menu_delete_db_tasks -> {
                    viewDataBinding.viewmodel?.deleteAllTasksFromLocalDB()
                    true
                }
                R.id.menu_delete_remote_tasks -> {
                    viewDataBinding.viewmodel?.deleteAllTasksFromRemoteDB()
                    true
                }
                R.id.menu_clear_app_data -> {
                    viewDataBinding.viewmodel?.clearAppData(act)
                    true
                }
                R.id.menu_log_in -> {
                    viewDataBinding.viewmodel?.navigateToLoginFrag()
                    true
                }
                R.id.menu_log_out -> {
                    viewDataBinding.viewmodel?.logout()
                    true
                }

                else -> false
            }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        act = activity as AppCompatActivity
        // Set the lifecycle owner to the lifecycle of the view
        val viewmodel = viewDataBinding.viewmodel
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar(Snackbar.LENGTH_SHORT)
        setupDismissableSnackbar()
        setupListAdapter()
        setupRefreshLayout()
        setupNavigation()
        setupFab()
        viewmodel?.checkNetworkConnection(act)
        //runBlockingScope(viewmodel)
//        viewmodel?.loadTasks(false)
        viewmodel?.syncDatabases(act)
        viewmodel?.setLoginStatus()
    }

    override fun onResume() {
        super.onResume()
        viewDataBinding.viewmodel?.checkNetworkConnection(act)
    }

    private fun runBlockingScope(viewmodel: TasksViewModel?) {
        val ioDispatcher: CoroutineDispatcher = Dispatchers.Main

        runBlocking {
            withContext(ioDispatcher) {
                coroutineScope {
                    launch { viewmodel?.loadTasks(false) }
                    launch { viewmodel?.saveDataToFirebase(false) }
                }
            }
        }
    }

    private fun setupNavigation() {
        viewDataBinding.viewmodel?.openTaskEvent?.observe(this, EventObserver {
            openTaskDetails(it)
        })
        viewDataBinding.viewmodel?.newTaskEvent?.observe(this, EventObserver {
            navigateToAddNewTask()
        })
        viewDataBinding.viewmodel?.LoginEvent?.observe(this, EventObserver {
            navigateToLoginFrag()
        })
    }

    private fun setupSnackbar(length: Int, bgColor: Int = context!!.getColor(R.color.colorSnackbar)) {
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage, length, bgColor)
        }
        arguments?.let {
            val message = TasksFragmentArgs.fromBundle(it).userMessage
            viewDataBinding.viewmodel?.showEditResultMessage(message)
        }
    }

    private fun setupSnackbar() {
        viewDataBinding.viewmodel?.let {
            view?.setupSnackbar(this, it.snackbarMessage)
        }
        arguments?.let {
            val message = TasksFragmentArgs.fromBundle(it).userMessage
            viewDataBinding.viewmodel?.showEditResultMessage(message)
        }
    }

    private fun setupDismissableSnackbar(length: Int = Snackbar.LENGTH_LONG) {
        viewDataBinding.viewmodel?.let {
            view?.setupDismissableSnackbar(this, it.errorMessageEvent, length)
        }
    }

    private fun showFilteringPopUpMenu() {
        val view = activity?.findViewById<View>(R.id.menu_filter) ?: return
        PopupMenu(requireContext(), view).run {
            menuInflater.inflate(R.menu.filter_tasks, menu)

            setOnMenuItemClickListener {
                viewDataBinding.viewmodel?.run {
                    setFiltering(
                            when (it.itemId) {
                                R.id.active -> TasksFilterType.ACTIVE_TASKS
                                R.id.completed -> TasksFilterType.COMPLETED_TASKS
                                R.id.favorite -> TasksFilterType.FAVORITE_TASKS
                                R.id.sort -> TasksFilterType.SORT
                                else -> TasksFilterType.ALL_TASKS
                            }
                    )
                    loadTasks(false)
                }
                true
            }
            show()
        }
    }

    private fun setupFab() {
        activity?.findViewById<FloatingActionButton>(R.id.fab_add_task)?.let {
            it.setOnClickListener {
                navigateToAddNewTask()
            }
        }
    }

    private fun navigateToAddNewTask() {
        val action = TasksFragmentDirections
                .actionTasksFragmentToAddEditTaskFragment(-1,
                        resources.getString(R.string.add_task))
        findNavController().navigate(action)
    }

    private fun navigateToLoginFrag() {
        val intent = Intent(context, StartActivity::class.java)
        startActivity(intent)
    }


    private fun openTaskDetails(taskId: Int) {
        val action = TasksFragmentDirections.actionTasksFragmentToTaskDetailFragment(taskId)
        findNavController().navigate(action)
    }

    private fun setupListAdapter() {
        val viewModel = viewDataBinding.viewmodel
        if (viewModel != null) {
            listAdapter = TasksAdapter(ArrayList(0), viewModel)
            viewDataBinding.tasksList.adapter = listAdapter
        } else {
            Timber.w("ViewModel not initialized when attempting to set up adapter.")
        }
    }

    private fun setupRefreshLayout() {
        viewDataBinding.refreshLayout.run {
            setColorSchemeColors(
                    ContextCompat.getColor(requireActivity(), R.color.colorPrimary),
                    ContextCompat.getColor(requireActivity(), R.color.colorAccent),
                    ContextCompat.getColor(requireActivity(), R.color.colorPrimaryDark)
            )
            // Set the scrolling view in the custom SwipeRefreshLayout.
            scrollUpChild = viewDataBinding.tasksList
        }
    }
}
