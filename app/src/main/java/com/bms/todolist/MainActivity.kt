package com.bms.todolist

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.bms.todolist.dataSource.TaskDataSource
import com.bms.todolist.databinding.ActivityAddTaskBinding
import com.bms.todolist.databinding.ActivityMainBinding
import com.bms.todolist.ui.AddTaskActivity
import com.bms.todolist.ui.TaskListAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy {TaskListAdapter() } //lazy - espera ser chamado o atributo para ser iniciado o adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvTask.adapter = adapter

        updateList()

        insertListeners()
        updateList()

    }

    private fun insertListeners() {
        binding.fabButton.setOnClickListener {
            startActivityForResult(Intent(this, AddTaskActivity::class.java), CREATE_NEW_TASK)
        }

        adapter.listenerEdit = {
            val intent = Intent(this, AddTaskActivity::class.java)
            intent.putExtra(AddTaskActivity.TASK_ID, it.id)
            startActivityForResult(intent, CREATE_NEW_TASK)
        }
        adapter.listenerDelete = {
            TaskDataSource.deleteTask(it)
            updateList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CREATE_NEW_TASK && resultCode == Activity.RESULT_OK)
            updateList()
    }

    private fun updateList(){
        var list = TaskDataSource.getList()
        if(list.isEmpty()){
            binding.include.emptyState.visibility= View.VISIBLE
        }else{
            binding.include.emptyState.visibility= View.GONE
        }

        adapter.submitList(list)
    }

    companion object{
        private const val  CREATE_NEW_TASK = 1000
    }
}