package com.example.android.architecture.blueprints.todoapp.tasks

/**
 * Used with the filter spinner in the tasks list.
 */
enum class TasksFilterType {
    /**
     * Do not filter tasks.
     */
    ALL_TASKS,

    /**
     * Filters only the active (not completed yet) tasks.
     */
    ACTIVE_TASKS,

    /**
     * Filters only the completed tasks.
     */
    COMPLETED_TASKS,

    /**
     * Filters only the favored tasks.
     */
    FAVORITE_TASKS,

    SORT;

    /**
     * sorts the tasks (uid, name, expiry).
     */
    enum class SORT_BY{
        DUE_DATE, NAME, ID
    }
}
