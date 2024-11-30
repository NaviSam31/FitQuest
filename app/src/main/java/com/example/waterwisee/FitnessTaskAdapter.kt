package com.example.waterwisee

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.waterwisee.FitnessTask
import com.example.waterwisee.R
// all the viewholders will be initialized under fitnesstask adaptor
class FitnessTaskAdapter(
    private val fitnessTasks: List<FitnessTask>,
    private val deleteTask: (FitnessTask) -> Unit,
    private val startTimer: (FitnessTask, TextView, ProgressBar) -> Unit,
    private val editTask: (FitnessTask) -> Unit
) : RecyclerView.Adapter<FitnessTaskAdapter.FitnessTaskViewHolder>() {

    inner class FitnessTaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewFitnessTaskDescription)
        val taskCheckBox: CheckBox = itemView.findViewById(R.id.checkBoxFitnessTask)
        val startTimerButton: ImageView = itemView.findViewById(R.id.imageViewStartFitnessTimer)
        val timerTextView: TextView = itemView.findViewById(R.id.textViewFitnessTimer)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressBarFitnessTask)
        val editTaskButton: ImageView = itemView.findViewById(R.id.imageViewEditFitnessTask)

        fun bind(fitnessTask: FitnessTask) {
            descriptionTextView.text = fitnessTask.description
            taskCheckBox.isChecked = fitnessTask.isCompleted

            // Set initial progress
            val progress = ((fitnessTask.timeRemaining.toFloat() / fitnessTask.originalTime.toFloat()) * 100).toInt()
            progressBar.progress = progress

            // Handle task completion checkbox
            taskCheckBox.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    deleteTask(fitnessTask)
                }
            }

            // Handle timer start button click
            startTimerButton.setOnClickListener {
                startTimer(fitnessTask, timerTextView, progressBar)
            }

            // Handle edit task button click
            editTaskButton.setOnClickListener {
                editTask(fitnessTask)
            }
        }
    }
//initializing the viewholder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FitnessTaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fitness_task, parent, false)
        return FitnessTaskViewHolder(view)
    }
// the newly added tasks will bind together
    override fun onBindViewHolder(holder: FitnessTaskViewHolder, position: Int) {
        holder.bind(fitnessTasks[position])
    }

    override fun getItemCount(): Int = fitnessTasks.size
}
