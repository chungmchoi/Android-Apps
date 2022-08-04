package com.example.minichallenge1

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val spinner: Spinner = findViewById<Spinner>(R.id.spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                this,
                R.array.computers,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        radioButton.setOnClickListener() {
            val myToast = Toast.makeText(applicationContext,"$300", Toast.LENGTH_SHORT)
            myToast.setGravity(Gravity.LEFT,0,-490)
            myToast.show()
        }
        radioButton2.setOnClickListener() {
            val myToast2 = Toast.makeText(applicationContext,"$500", Toast.LENGTH_SHORT)
            myToast2.setGravity(Gravity.LEFT,0,-490)
            myToast2.show()
        }
        radioButton3.setOnClickListener() {
            val myToast3 = Toast.makeText(applicationContext,"$700", Toast.LENGTH_SHORT)
            myToast3.setGravity(Gravity.LEFT,0,-490)
            myToast3.show()
        }

        class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
                // An item was selected. You can retrieve the selected item using

                if (parent.getItemAtPosition(pos).toString() == "HP") {
                    radioButton.setOnClickListener() {
                        val myToast = Toast.makeText(applicationContext,"$300", Toast.LENGTH_SHORT)
                        myToast.setGravity(Gravity.LEFT,0,-490)
                        myToast.show()
                    }
                    radioButton2.setOnClickListener() {
                        val myToast2 = Toast.makeText(applicationContext,"$500", Toast.LENGTH_SHORT)
                        myToast2.setGravity(Gravity.LEFT,0,-490)
                        myToast2.show()
                    }
                    radioButton3.setOnClickListener() {
                        val myToast3 = Toast.makeText(applicationContext,"$700", Toast.LENGTH_SHORT)
                        myToast3.setGravity(Gravity.LEFT,0,-490)
                        myToast3.show()
                    }
                }
                else    {
                    radioButton.setOnClickListener() {
                        val myToast = Toast.makeText(applicationContext,"$330", Toast.LENGTH_SHORT)
                        myToast.setGravity(Gravity.LEFT,0,-490)
                        myToast.show()
                    }
                    radioButton2.setOnClickListener() {
                        val myToast2 = Toast.makeText(applicationContext,"$550", Toast.LENGTH_SHORT)
                        myToast2.setGravity(Gravity.LEFT,0,-490)
                        myToast2.show()
                    }
                    radioButton3.setOnClickListener() {
                        val myToast3 = Toast.makeText(applicationContext,"$770", Toast.LENGTH_SHORT)
                        myToast3.setGravity(Gravity.LEFT,0,-490)
                        myToast3.show()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Another interface callback
            }
        }
    }
}
