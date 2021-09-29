package com.bms.todolist.ui

import android.app.Activity
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.bms.todolist.dataSource.TaskDataSource
import com.bms.todolist.databinding.ActivityAddTaskBinding
import com.bms.todolist.extensions.format
import com.bms.todolist.extensions.text
import com.bms.todolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*


class AddTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)){
            val taskId= intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?.let {
                binding.titleTask.text = it.title
                binding.descTask.text = it.desc
                binding.dateTask.text = it.date
                binding.hourTask.text = it.hour
            }
        }

        insertListeners()
    }

    private fun insertListeners() {
        binding.dateTask.editText?.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker().build()
            datePicker.addOnPositiveButtonClickListener {
                //timezone foi necessario ser implementado pois a data estava selecionando 1 dia abaixo do selecionado
                val timeZone = TimeZone.getDefault()
                val offset = timeZone.getOffset(Date().time)* -1
                binding.dateTask.text = Date(it + offset).format()
            }
            datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
        }

        binding.hourTask.editText?.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .build()
            timePicker.addOnPositiveButtonClickListener {
                //necessario implementas 0+hora, nos horarios de 0-9
                // pois ao clicar time piker retornava 4: 5, ao inves de 04:05 por ex
                val hour = if(timePicker.hour in 0..9) "0${timePicker.hour}" else timePicker.hour
                val minute = if(timePicker.minute in 0..9) "0${timePicker.minute}" else timePicker.minute

                binding.hourTask.text = "$hour : $minute"
            }
            timePicker.show(supportFragmentManager, null)
        }

        binding.cancelTask.setOnClickListener{
            finish()
        }

        binding.createTask.setOnClickListener{
           val task = Task(
                    title = binding.titleTask.text,
                    desc = binding.descTask.text,
                    date = binding.dateTask.text,
                    hour = binding.hourTask.text,
                    id = intent.getIntExtra(TASK_ID, 0)
            )
            TaskDataSource.insertTask(task)
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    companion object{
        const val TASK_ID = "task_id"
    }

}