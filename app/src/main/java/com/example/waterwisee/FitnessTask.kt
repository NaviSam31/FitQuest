package com.example.waterwisee

data class FitnessTask(
    var description: String,
    var originalTime: Long,  // Store the original time for the task
    var timeRemaining: Long,
    var isCompleted: Boolean = false
)



