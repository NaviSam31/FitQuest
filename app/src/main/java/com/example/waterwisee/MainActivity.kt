package com.example.waterwisee


import android.app.*
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fitnessTaskAdapter: FitnessTaskAdapter
    private lateinit var fitnessTaskList: MutableList<FitnessTask>
    private lateinit var refreshButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createNotificationChannel()

        // Initialize views   as a list view the title , description , time , edit button
        recyclerView = findViewById(R.id.recyclerViewFitnessTasks)
        val fabAddTask: FloatingActionButton = findViewById(R.id.fabAddFitnessTask)
        refreshButton = findViewById(R.id.buttonRefresh)

        // Initialize fitness task list and adapter
        fitnessTaskList = loadTasksFromSharedPreferences()

        fitnessTaskAdapter = FitnessTaskAdapter(
            fitnessTaskList,
            { task -> deleteTask(task) },
            { task, timerTextView, progressBar -> startTimer(task, timerTextView, progressBar) },
            { task -> showEditTaskDialog(task) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = fitnessTaskAdapter

        fabAddTask.setOnClickListener { showAddTaskDialog() }

        refreshButton.setOnClickListener { refreshTasks() }
    }
// the dialog when you add tasks
    private fun showAddTaskDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_fitness_task, null)
        val taskNameInput = dialogView.findViewById<EditText>(R.id.editTextFitnessTaskName)
        val taskTimeInput = dialogView.findViewById<EditText>(R.id.editTextFitnessTaskTime)

        AlertDialog.Builder(this)
            .setTitle("Add Fitness Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val taskName = taskNameInput.text.toString()
                val taskTime = taskTimeInput.text.toString().toLongOrNull() ?: 0L
                if (taskName.isNotEmpty()) {
                    addTask(FitnessTask(taskName, taskTime * 60000, taskTime * 60000))
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun addTask(fitnessTask: FitnessTask) {
        fitnessTaskList.add(fitnessTask)
        saveTasksToSharedPreferences()
        fitnessTaskAdapter.notifyDataSetChanged()
    }

    private fun deleteTask(fitnessTask: FitnessTask) {
        fitnessTaskList.remove(fitnessTask)
        saveTasksToSharedPreferences()
        fitnessTaskAdapter.notifyDataSetChanged()
    }

    private fun refreshTasks() {
        Toast.makeText(this, "Tasks refreshed!", Toast.LENGTH_SHORT).show()
    }

    private fun saveTasksToSharedPreferences() {
        val sharedPreferences = getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("fitnessTaskList", fitnessTaskList.map { "${it.description}|${it.originalTime}|${it.timeRemaining}|${it.isCompleted}" }.toSet())
        editor.apply()
    }

    private fun loadTasksFromSharedPreferences(): MutableList<FitnessTask> {
        val sharedPreferences = getSharedPreferences("fitness_prefs", Context.MODE_PRIVATE)
        val taskSet = sharedPreferences.getStringSet("fitnessTaskList", emptySet()) ?: emptySet()
        val taskList = mutableListOf<FitnessTask>()

        taskSet.forEach { taskString ->
            val taskData = taskString.split("|")
            if (taskData.size == 4) {
                val description = taskData[0]
                val originalTime = taskData[1].toLongOrNull() ?: 0L
                val timeRemaining = taskData[2].toLongOrNull() ?: 0L
                val isCompleted = taskData[3].toBoolean()
                taskList.add(FitnessTask(description, originalTime, timeRemaining, isCompleted))
            } else {
                // Log or handle incorrect data format
            }
        }
        return taskList
    }

    private fun startTimer(fitnessTask: FitnessTask, timerTextView: TextView, progressBar: ProgressBar) {
        val timer = object : CountDownTimer(fitnessTask.timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                fitnessTask.timeRemaining = millisUntilFinished
                val minutesRemaining = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                val secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                timerTextView.text = String.format("%02d:%02d", minutesRemaining, secondsRemaining)

                // Update progress
                val progress = ((millisUntilFinished.toFloat() / fitnessTask.originalTime.toFloat()) * 100).toInt()
                progressBar.progress = progress
            }

            override fun onFinish() {
                notifyTaskCompletion(fitnessTask)
                Toast.makeText(this@MainActivity, "Fitness task ${fitnessTask.description} completed!", Toast.LENGTH_SHORT).show()

                // Set progress to 100% on completion
                progressBar.progress = 100
            }
        }
        timer.start()
    }

    private fun notifyTaskCompletion(fitnessTask: FitnessTask) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notification = NotificationCompat.Builder(this, "fitness_reminder_channel")
            .setContentTitle("Fitness Task Completed")
            .setContentText("Task: ${fitnessTask.description} is completed!")
            .setSmallIcon(R.drawable.ic_task_done)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        notificationManager.notify(1, notification)
    }

    private fun showEditTaskDialog(fitnessTask: FitnessTask) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_fitness_task, null)
        val taskNameInput = dialogView.findViewById<EditText>(R.id.editTextFitnessTaskName)
        val taskTimeInput = dialogView.findViewById<EditText>(R.id.editTextFitnessTaskTime)

        taskNameInput.setText(fitnessTask.description)
        taskTimeInput.setText((fitnessTask.timeRemaining / 60000).toString()) // Convert milliseconds to minutes

        AlertDialog.Builder(this)
            .setTitle("Edit Fitness Task")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val newTaskName = taskNameInput.text.toString()
                val newTaskTime = taskTimeInput.text.toString().toLongOrNull() ?: 0L

                if (newTaskName.isNotEmpty()) {
                    fitnessTask.description = newTaskName
                    fitnessTask.timeRemaining = newTaskTime * 60000
                    fitnessTask.originalTime = newTaskTime * 60000

                    fitnessTaskAdapter.notifyDataSetChanged()
                    saveTasksToSharedPreferences()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Fitness Task Reminder"
            val descriptionText = "Channel for fitness task reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("fitness_reminder_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
