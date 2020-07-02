package com.example.android.architecture.blueprints.todoapp.tasks

import android.content.Context
import android.net.ConnectivityManager
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.data.Result.Success
import com.example.android.architecture.blueprints.todoapp.data.Task
import com.example.android.architecture.blueprints.todoapp.data.source.TasksDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.local.TasksLocalDataSource
import com.example.android.architecture.blueprints.todoapp.data.source.remote.FirebaseDatabaseHelper
import com.example.android.architecture.blueprints.todoapp.util.ADD_EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.util.DELETE_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.util.EDIT_RESULT_OK
import com.example.android.architecture.blueprints.todoapp.util.EspressoIdlingResource
import com.example.android.architecture.blueprints.todoapp.data.source.remote.FirebaseCallback
import timber.log.Timber
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import android.content.Context.ACTIVITY_SERVICE
import android.app.ActivityManager
import android.os.Build
import android.app.AlarmManager
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*
import java.io.File


/**
 * Exposes the data to be used in the task list screen.
 *
 *
 * [BaseObservable] implements a listener registration mechanism which is notified when a
 * property changes. This is done by assigning a [Bindable] annotation to the property's
 * getter method.
 */
class TasksViewModel(
    private val tasksRepository: TasksLocalDataSource,
    private val application: Application
) : ViewModel() {

    val _items = MutableLiveData<List<Task>>().apply { value = emptyList() }
    val items: LiveData<List<Task>> = _items

    private val _dataLoading = MutableLiveData<Boolean>()
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _isInternetAvailable = MutableLiveData<Boolean>()
    val isInternetAvailable: LiveData<Boolean> = _isInternetAvailable

    private val _userLoggedIn = MutableLiveData<Boolean>()
    val userLoggedIn: LiveData<Boolean> = _userLoggedIn

    private val _currentFilteringLabel = MutableLiveData<Int>()
    val currentFilteringLabel: LiveData<Int> = _currentFilteringLabel

    private val _noTasksLabel = MutableLiveData<Int>()
    val noTasksLabel: LiveData<Int> = _noTasksLabel

    private val _noTaskIconRes = MutableLiveData<Int>()
    val noTaskIconRes: LiveData<Int> = _noTaskIconRes

    private val _tasksAddViewVisible = MutableLiveData<Boolean>()
    val tasksAddViewVisible: LiveData<Boolean> = _tasksAddViewVisible

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarMessage: LiveData<Event<Int>> = _snackbarText

    private val _errorMessageEvent = MutableLiveData<Event<String>>()
    val errorMessageEvent: LiveData<Event<String>> = _errorMessageEvent

    private var _currentFiltering = TasksFilterType.ACTIVE_TASKS

    private var _currentSorting = TasksFilterType.SORT_BY.DUE_DATE

    // Not used at the moment
    private val isDataLoadingError = MutableLiveData<Boolean>()

    private val _openTaskEvent = MutableLiveData<Event<Int>>()
    val openTaskEvent: LiveData<Event<Int>> = _openTaskEvent

    private val _newTaskEvent = MutableLiveData<Event<Unit>>()
    val newTaskEvent: LiveData<Event<Unit>> = _newTaskEvent

    private val _LoginEvent = MutableLiveData<Event<Boolean>>()
    val LoginEvent: LiveData<Event<Boolean>> = _LoginEvent

    // This LiveData depends on another so we can use a transformation.
    val empty: LiveData<Boolean> = Transformations.map(_items) {
        it.isEmpty()
    }

    private var firebaseHelper: FirebaseDatabaseHelper
    private var userAuth: FirebaseAuth


    init {
        // Set initial state
        setFiltering(TasksFilterType.ACTIVE_TASKS)
        firebaseHelper = FirebaseDatabaseHelper()
        userAuth = FirebaseAuth.getInstance()
    }

    /**
     * Sets the current task filtering type.
     *
     * @param requestType Can be [TasksFilterType.ALL_TASKS],
     * [TasksFilterType.COMPLETED_TASKS], or
     * [TasksFilterType.ACTIVE_TASKS]
     */

    fun setFiltering(requestType: TasksFilterType) {
        _currentFiltering = requestType
        _currentSorting

        // Depending on the filter type, set the filtering label, icon drawables, etc.
        when (requestType) {
            TasksFilterType.ALL_TASKS -> {
                setFilter(R.string.label_all, R.string.no_tasks_all,
                    R.drawable.logo_no_fill, true)
            }
            TasksFilterType.ACTIVE_TASKS -> {
                setFilter(R.string.label_active, R.string.no_tasks_active,
                    R.drawable.ic_check_circle_96dp, false)
            }
            TasksFilterType.COMPLETED_TASKS -> {
                setFilter(R.string.label_completed, R.string.no_tasks_completed,
                    R.drawable.ic_verified_user_96dp, false)
            }
            TasksFilterType.FAVORITE_TASKS -> {
                setFilter(R.string.label_favorite, R.string.no_favorite_tasks,
                    R.drawable.ic_verified_user_96dp, true)
            }
            TasksFilterType.SORT -> {
                when (_currentSorting) {
                    TasksFilterType.SORT_BY.DUE_DATE -> {
                        setFilter(R.string.label_sorted_date, R.string.sorted_tasks,
                            R.drawable.ic_verified_user_96dp, true)
                    }
                    TasksFilterType.SORT_BY.NAME -> {
                        setFilter(R.string.label_sorted_name, R.string.sorted_tasks,
                            R.drawable.ic_verified_user_96dp, true)
                    }
                    TasksFilterType.SORT_BY.ID -> {
                        setFilter(R.string.label_sorted_id, R.string.sorted_tasks,
                            R.drawable.ic_verified_user_96dp, true)
                    }
                }
            }
        }
    }

    private fun setFilter(@StringRes filteringLabelString: Int, @StringRes noTasksLabelString: Int,
                          @DrawableRes noTaskIconDrawable: Int, tasksAddVisible: Boolean) {
        _currentFilteringLabel.value = filteringLabelString
        _noTasksLabel.value = noTasksLabelString
        _noTaskIconRes.value = noTaskIconDrawable
        _tasksAddViewVisible.value = tasksAddVisible
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            tasksRepository.clearCompletedTasks()
            _snackbarText.value = Event(R.string.completed_tasks_cleared)
            loadTasks(false)
        }
    }

    fun showNoInternetConnection() {
        showErrorMessage(application.getString(R.string.no_internet_connection))
    }

    fun completeTask(task: Task, completed: Boolean) = viewModelScope.launch {
        if (completed) {
            tasksRepository.completeTask(task)
            showSnackbarMessage(R.string.task_marked_complete)
        } else {
            tasksRepository.activateTask(task)
            showSnackbarMessage(R.string.task_marked_active)
        }
    }

    fun favorTask(task: Task, favorite: Boolean) = viewModelScope.launch {
        if (favorite) {
            tasksRepository.favorTask(task)
            showSnackbarMessage(R.string.task_marked_favorite)
        } else {
            tasksRepository.unfavorTask(task)
            showSnackbarMessage(R.string.task_marked_unfavored)
        }
    }

    /**
     * Called by the Data Binding library and the FAB's click listener.
     */
    fun addNewTask() {
        _newTaskEvent.value = Event(Unit)
    }

    /**
     * Called by the [TasksAdapter].
     */
    internal fun openTask(taskId: Int) {
        _openTaskEvent.value = Event(taskId)
    }

    fun showEditResultMessage(result: Int) {
        when (result) {
            EDIT_RESULT_OK -> _snackbarText.setValue(
                Event(R.string.successfully_saved_task_message)
            )
            ADD_EDIT_RESULT_OK -> _snackbarText.setValue(
                Event(R.string.successfully_added_task_message)
            )
            DELETE_RESULT_OK -> _snackbarText.setValue(
                Event(R.string.successfully_deleted_task_message)
            )
        }

    }

    private fun showSnackbarMessage(message: Int) {
        _snackbarText.value = Event(message)
    }

    private fun showErrorMessage(message: String) {
        _errorMessageEvent.value = Event(message)
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the [TasksDataSource]
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    fun loadTasks(forceUpdate: Boolean) {

        _dataLoading.value = true

        EspressoIdlingResource.increment() // Set app as busy.

        viewModelScope.launch {
            val tasksResult = tasksRepository.getTasks()

            if (tasksResult is Success) {
                val tasks = tasksResult.data
                val tasksToShow = ArrayList<Task>()
                var sortedList = tasks

                if (tasksToShow.isNullOrEmpty()) {
                    _snackbarText.value = Event(R.string.local_db_empty)
                    loadDataFromFirebaseDB()
                }

                else {

                    // We filter the tasks based on the requestType
                    when (_currentFiltering) {
                        TasksFilterType.SORT -> {
                            when (_currentSorting) {
                                TasksFilterType.SORT_BY.DUE_DATE -> {
                                    _currentSorting = TasksFilterType.SORT_BY.NAME
                                    sortedList = sortByDate(tasks)
                                }
                                TasksFilterType.SORT_BY.NAME -> {
                                    _currentSorting = TasksFilterType.SORT_BY.ID
                                    sortedList = sortByName(tasks)
                                }
                                TasksFilterType.SORT_BY.ID -> {
                                    _currentSorting = TasksFilterType.SORT_BY.DUE_DATE
                                    sortedList = sortByID(tasks)
                                }
                            }
                            for (task in sortedList) {
                                tasksToShow.add(task)
                            }
                        }
                    }
                    for (task in tasks) {
                        when (_currentFiltering) {
                            TasksFilterType.ALL_TASKS -> tasksToShow.add(task)
                            TasksFilterType.ACTIVE_TASKS -> if (task.isActive) {
                                tasksToShow.add(task)
                            }
                            TasksFilterType.COMPLETED_TASKS -> if (task.isCompleted) {
                                tasksToShow.add(task)
                            }
                            TasksFilterType.FAVORITE_TASKS -> if (task.isFavorite) {
                                tasksToShow.add(task)
                            }
                        }
                    }
                    isDataLoadingError.value = false
                    _items.value = ArrayList(tasksToShow)
                    Timber.i("Tasks loaded from local db")
                    saveDataToFirebase(isInternetAvailable.value!!,true)
                }
            } else {
                isDataLoadingError.value = true
                _items.value = emptyList()
                _snackbarText.value = Event(R.string.local_db_empty)
            }

            _dataLoading.value = false
            EspressoIdlingResource.decrement() // Set app as idle.
        }

    }

    fun saveDataToFirebase(isConnected: Boolean, isSyncingProccess: Boolean) = viewModelScope.launch {
        if (!isConnected) {
            showNoInternetConnection()
            return@launch
        }
        if (items.value == emptyList<Task>() && isConnected) {
            showErrorMessage(application.getString(R.string.local_db_empty))
            return@launch
        }


        firebaseHelper.deleteAllTasks()
        firebaseHelper.saveToDatabase(items?.value!!)
        if (!isSyncingProccess) {
        _snackbarText.value = Event(R.string.tasks_saved_to_remote_db)
        }
        else {
            _snackbarText.value = Event(R.string.remote_tasks_successfully_synced)
        }
//            EspressoIdlingResource.decrement() // Set app as idle.
    }


    fun checkNetworkConnection(activity: AppCompatActivity) {
        _isInternetAvailable.value =
            (activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager)
                .activeNetworkInfo?.isConnected == true
        if (!isInternetAvailable.value!!) {
            showNoInternetConnection()
        }
    }

    private fun getTaskListFromFirebaseAndStoreToLocalDB(connected: Boolean) {
        viewModelScope.launch {
            if (connected) {
                EspressoIdlingResource.increment() // Set app as busy.
                val firebaseCallback = object : FirebaseCallback {
                    override fun onCallback(todoList: List<Task>) {

                        if (items.value == emptyList<Task>()) {
                            showErrorMessage(application.getString(R.string.remote_db_empty))
                        } else {
                            _items.value = todoList
                            _snackbarText.value = Event(R.string.tasks_retrieved_from_remote_db)
                            viewModelScope.launch {
                                items.value?.forEach {
                                    tasksRepository.saveTask(it)
                                }
                            }
                        }
                    }
                }
                firebaseHelper.readTasks(firebaseCallback)
                EspressoIdlingResource.decrement() // Set app as idle.

            } else {
                showNoInternetConnection()
            }

        }
    }

    fun synchronizeDbs() {
        loadTasks(false)
    }

    fun loadDataFromFirebaseDB() {
//        if (items.value == emptyList<Task>()) {
        getTaskListFromFirebaseAndStoreToLocalDB(isInternetAvailable.value!!)
//        }
    }

    fun deleteAllTasksFromLocalDB() {
        viewModelScope.launch {
            tasksRepository.deleteAllTasks()
            _snackbarText.value = Event(R.string.all_local_tasks_deleted)
            loadTasks(false)
        }
    }

    fun deleteAllTasksFromRemoteDB() {
        viewModelScope.launch {
            firebaseHelper.deleteAllTasks()
            _snackbarText.value = Event(R.string.all_remote_tasks_deleted)
        }
    }

    fun clearAppData2(activity: AppCompatActivity) {

        val deleteData: Boolean
        try {
            // clearing app data
            if (Build.VERSION_CODES.KITKAT <= Build.VERSION.SDK_INT) {
                deleteData = (activity.getSystemService(ACTIVITY_SERVICE) as ActivityManager).clearApplicationUserData() // note: it has a return value!
                if (deleteData) restartApp()
            } else {
                val packageName = getApplicationContext<Context>().packageName
                val runtime = Runtime.getRuntime()
                runtime.exec("pm clear $packageName")
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearAppData(activity: AppCompatActivity) {
        val cache = activity.cacheDir
        val appDir = File(cache.getParent())
        if (appDir.exists()) {
            val children = appDir.list();
            for (s in children) {
                if (!s.equals("lib")) {
                    deleteDir(File(appDir, s))
                    Log.i("TAG", "File /data/data/APP_PACKAGE/" + s + " DELETED")
                }
            }
        }
        deleteAllTasksFromLocalDB()
        deleteAllTasksFromRemoteDB()
        //restartApp()
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory()) {
            val children = dir.list()
            for (i in children) {
                val success = deleteDir(File(dir, i));
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
    }

    fun navigateToLoginFrag() {
        _LoginEvent.value = Event(true)
    }

    fun logout() {
        viewModelScope.launch {
            userAuth.signOut()
            if (userAuth.currentUser == null) {
                _userLoggedIn.value = false
                showSnackbarMessage(R.string.logout_successful)
            }
        }
    }


    private fun restartApp() {
        val intent = Intent(getApplicationContext<Context>(), TasksActivity::class.java)
        val mPendingIntentId = 1
        val mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT)
        val mgr = getApplicationContext<Context>().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent)
        System.exit(0)
    }

    fun setLoginStatus() {
        if (userAuth.currentUser == null) {
            _userLoggedIn.value = false
        }
        _userLoggedIn.value = true
    }

    fun checkUserStatus() {
        if (userAuth.currentUser == null) {
            _userLoggedIn.value = false
            showErrorMessage(application.getString(R.string.synchronization_failed))
        }
    }

    private fun sortByDate(tasks: List<Task>): List<Task> {
        val sortedList = tasks.sortedBy { it.dueDate }
        return sortedList
    }

    private fun sortByName(tasks: List<Task>): List<Task> {
        val sortedList = tasks.sortedBy { it.title }
        return sortedList
    }

    private fun sortByID(tasks: List<Task>): List<Task> {
        val sortedList = tasks.sortedBy { it.id }
        return sortedList
    }
}
