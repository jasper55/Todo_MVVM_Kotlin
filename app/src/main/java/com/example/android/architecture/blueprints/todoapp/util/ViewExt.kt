package com.example.android.architecture.blueprints.todoapp.util

/**
 * Extension functions for View and subclasses of View.
 */

import android.view.View
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.android.architecture.blueprints.todoapp.Event
import com.example.android.architecture.blueprints.todoapp.R
import com.example.android.architecture.blueprints.todoapp.ScrollChildSwipeRefreshLayout
import com.example.android.architecture.blueprints.todoapp.tasks.TasksViewModel
import com.google.android.material.snackbar.Snackbar

/**
 * Transforms static java function Snackbar.make() to an extension function on View.
 */

val FORE_EVER = 1000000000

fun View.showSnackbar(snackbarText: String, timeLength: Int, color: Int = context!!.getColor(R.color.colorAccent)) {
    Snackbar.make(this, snackbarText, timeLength).run {
        addCallback(object : Snackbar.Callback() {
            override fun onShown(sb: Snackbar?) {
                EspressoIdlingResource.increment()
            }

            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                EspressoIdlingResource.decrement()
            }

        }).view.setBackgroundColor(color)
        show()
    }
}

/**
 * Triggers a snackbar message when the value contained by snackbarTaskMessageLiveEvent is modified.
 */
fun View.setupSnackbar(
        lifecycleOwner: LifecycleOwner,
        snackbarEvent: LiveData<Event<Int>>,
        timeLength: Int,
        bgColor: Int
) {

    snackbarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            showSnackbar(context.getString(it), timeLength, bgColor)
        }
    })
}

fun View.setupSnackbarMessage(
        lifecycleOwner: LifecycleOwner,
        snackbarEvent: LiveData<Event<String>>,
        timeLength: Int,
        bgColor: Int
) {

    snackbarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            showSnackbar(it, timeLength, bgColor)
        }
    })
}

fun View.setupDismissableSnackbar(
        lifecycleOwner: LifecycleOwner,
        snackbarEvent: LiveData<Event<String>>,
        timeLength: Int = Snackbar.LENGTH_INDEFINITE,
        color: Int = context!!.getColor(R.color.error)) {

    snackbarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {

            Snackbar.make(this, it, timeLength).run {
                addCallback(object : Snackbar.Callback() {

                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        EspressoIdlingResource.decrement()
                    }

                }).view.setBackgroundColor(color)
                setAction("DISMISS", object : View.OnClickListener {
                    override fun onClick(v: View?) {
                        dismiss()
                    }
                })
                setActionTextColor(context!!.getColor(R.color.colorWhite))
                duration  = 1000000000
                show()
            }
        }
    })
}


fun View.setupSnackbar(
        lifecycleOwner: LifecycleOwner,
        snackbarEvent: LiveData<Event<Int>>,
        timeLength: Int = Snackbar.LENGTH_SHORT
) {

    snackbarEvent.observe(lifecycleOwner, Observer { event ->
        event.getContentIfNotHandled()?.let {
            showSnackbar(context.getString(it), timeLength)
        }
    })
}

/**
 * Reloads the data when the pull-to-refresh is triggered.
 *
 * Creates the `android:onRefresh` for a [SwipeRefreshLayout].
 */
@BindingAdapter("android:onRefresh")
fun ScrollChildSwipeRefreshLayout.setSwipeRefreshLayoutOnRefreshListener(
        viewModel: TasksViewModel) {
    setOnRefreshListener { viewModel.refresh() }
}

