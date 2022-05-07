package com.example.firestorecoroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.firestorecoroutines.Models.Student
import com.example.firestorecoroutines.Objects.Constants
import com.example.firestorecoroutines.Objects.Constants.reference
import com.example.firestorecoroutines.databinding.ActivityMainBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.MetadataChanges
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.SaveBtn.setOnClickListener {
            validations()
            getData()
        }
        binding.Update.setOnClickListener {
            getData()
        }
        binding.DeleteBtn.setOnClickListener {
            deleteData()
        }

        snapshotListener()
    }

    private fun snapshotListener() {
        reference.addSnapshotListener(this, MetadataChanges.INCLUDE) { value, error ->
            error?.let {
                Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
            }

            value?.let {
                val sb = StringBuilder()
                if (it.exists()) {
                    val student = it.toObject(Student::class.java)
                    val source = if (it.metadata.hasPendingWrites()) "Local"
                    else "Server"
                    sb.append("$student\n")
                    sb.append("$source\n")
                }
                binding.tvPersons.text = sb

            }
        }
    }

    private fun getData() {
        val first = binding.firstName.text.toString()
        val last = binding.lastName.text.toString()
        val age = binding.age.text.toString()

        val hashMap = hashMapOf<String, Any>(
            "firstName" to first,
            "lastName" to last,
            "age" to age
        )
        updateData(hashMap)
    }

    private fun updateData(hashMap: HashMap<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        try {
            reference.set(hashMap, SetOptions.merge()).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Update", Toast.LENGTH_SHORT).show()

            }
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }


    private fun deleteData() = CoroutineScope(Dispatchers.IO).launch {
        try {
            reference.delete().await()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Delete", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun validations() {
        val first = binding.firstName.text.toString()
        val last = binding.lastName.text.toString()
        val age = binding.age.text.toString()

        if (first.isNotEmpty() && last.isNotEmpty() && age.isNotEmpty()) {
            val first = binding.firstName.text.toString()
            val last = binding.lastName.text.toString()
            val age = binding.age.text.toString()
            val student = Student(first, last, age)
            saveData(student)
        }
    }

    private fun saveData(student: Student) = CoroutineScope(Dispatchers.IO).launch {
        try {
            reference.set(student).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "Successfully", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_SHORT).show()
            }
        }
    }
}


