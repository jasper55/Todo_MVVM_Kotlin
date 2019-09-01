/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.architecture.blueprints.todoapp.tasks

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
import com.example.android.architecture.blueprints.todoapp.util.obtainViewModel
import com.example.android.architecture.blueprints.todoapp.util.setupSnackbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber
import java.util.ArrayList

/**
 * Display a grid of [Task]s. User can choose to view all, active or completed tasks.
 */
class TasksFragment : Fragment() {

    private lateinit var viewDataBinding: TasksFragBinding
    private lateinit var listAdapter: TasksAdapter

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
                else -> false
            }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val activity = activity as AppCompatActivity
        // Set the lifecycle owner to the lifecycle of the view
        val viewmodel = viewDataBinding.viewmodel
        viewDataBinding.lifecycleOwner = this.viewLifecycleOwner
        setupSnackbar(Snackbar.LENGTH_SHORT)
        setupListAdapter()
        setupRefreshLayout()
        setupNavigation()
        setupFab()
        viewmodel?.loadTasks(true)
//        viewmodel?.saveDataIfInternetAvailable(activity)
        viewmodel?.loadDataFromFBIfAvailable(activity)
    }

    private fun setupNavigation() {
        viewDataBinding.viewmodel?.openTaskEvent?.observe(this, EventObserver {
            openTaskDetails(it)
        })
        viewDataBinding.viewmodel?.newTaskEvent?.observe(this, EventObserver {
            navigateToAddNewTask()
        })
    }

    private fun setupSnackbar(length: Int, bgColor: Int = context!!.getColor(R.color.colorTextPrimary)) {
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
