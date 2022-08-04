package com.example.burger_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Find the ViewById of all the food items, calculate button, and EditText number input.
        val whiteBun = findViewById<CheckBox>(R.id.WhiteBun)
        val wheatBun = findViewById<CheckBox>(R.id.WheatBun)
        val beef = findViewById<CheckBox>(R.id.Beef)
        val chicken = findViewById<CheckBox>(R.id.GrilledChicken)
        val turkey = findViewById<CheckBox>(R.id.Turkey)
        val salmon = findViewById<CheckBox>(R.id.Salmon)
        val veggie = findViewById<CheckBox>(R.id.Veggie)
        val mushroom = findViewById<CheckBox>(R.id.Mushroom)
        val lettuce = findViewById<CheckBox>(R.id.Lettuce)
        val tomato = findViewById<CheckBox>(R.id.Tomato)
        val pickle = findViewById<CheckBox>(R.id.Pickles)
        val mayo = findViewById<CheckBox>(R.id.Mayo)
        val mustard = findViewById<CheckBox>(R.id.Mustard)
        val showButton = findViewById<Button>(R.id.calculate)
        val editText = findViewById<EditText>(R.id.editText)

        //Variables used in this program to calculate cost and calories of burger.
        var total = 0.0
        var calories = 0
        var total2 = 0.0
        var calories2 = 0
        var num = 1
        var toppings = 0

        /**
         * When 'White Bun' is checked, the cost and calories of the white bun is multiplied by
         * the number of burgers "num" and added to "total" and "calories".
         * The current total cost and calories is then outputted on the GUI.
         * "total2" and "calories2" is used get the original cost+calories of a single white bun
         * so that it can be used to multiply when number of burgers is less than the previous
         * number of burgers.
         * If 'White Bun' is unchecked, then the process above is repeated except total cost
         * and calories is subtracted.
         * If White Bun is checked, then wheatBun is automatically unchecked.
         */
        WhiteBun.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (whiteBun.isChecked) {
                total += (num * 1.0)
                calories += (num * 140)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 += 1.0
                calories2 += 140
                wheatBun.isChecked = false
            }
            else {
                total -= (num * 1.0)
                calories -= (num * 140)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 1.0
                calories2 -= 140
            }
        }

        /**
         * This block is similiar to the 'White Bun' portion of the code above except this time
         * its 'Wheat Bun'. The blocks are repeated for all food items (beef, tomato, etc).
         * I didn't want to potentially ruin my code by attempting to create a function that
         * would make these repeat blocks unecessary. I'm still unfamiliar with kotlin functions.
         */
        WheatBun.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (wheatBun.isChecked) {
                total += (num * 1.0)
                calories += (num * 100)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 += 1.0
                calories2 += 100
                whiteBun.isChecked = false
            }
            else {
                total -= (num * 1.0)
                calories -= (num * 100)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 1.0
                calories2 -= 100
            }
        }

        //Beef
        Beef.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (beef.isChecked) {
                total += (num * 5.5)
                calories += (num * 240)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 += 5.5
                calories2 += 240
                chicken.isChecked = false
                turkey.isChecked = false
                salmon.isChecked = false
                veggie.isChecked = false
            }
            else {
                total -= (num * 5.5)
                calories -= (num * 240)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 5.5
                calories2 -= 240
            }
        }

        //Chicken
        GrilledChicken.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (chicken.isChecked) {
                total += (num * 5.0)
                calories += (num * 180)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 += 5.0
                calories2 += 180
                beef.isChecked = false
                turkey.isChecked = false
                salmon.isChecked = false
                veggie.isChecked = false
            }
            else {
                total -= (num * 5.0)
                calories -= (num * 180)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 5.0
                calories2 -= 180
            }
        }

        //Turkey
        Turkey.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (turkey.isChecked) {
                total += (num * 5.0)
                calories += (num * 190)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 += 5.0
                calories2 += 190
                beef.isChecked = false
                chicken.isChecked = false
                salmon.isChecked = false
                veggie.isChecked = false
            }
            else {
                total -= (num * 5.0)
                calories -= (num * 190)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 5.0
                calories2 -= 190
            }
        }

        //Salmon
        Salmon.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (salmon.isChecked) {
                total += (num * 7.5)
                calories += (num * 95)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 += 7.5
                calories2 += 95
                beef.isChecked = false
                chicken.isChecked = false
                turkey.isChecked = false
                veggie.isChecked = false
            }
            else {
                total -= (num * 7.5)
                calories -= (num * 95)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 7.5
                calories2 -= 95
            }
        }

        //Veggie
        Veggie.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (veggie.isChecked) {
                total += (num * 4.5)
                calories += (num * 80)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 += 4.5
                calories2 += 80
                beef.isChecked = false
                chicken.isChecked = false
                turkey.isChecked = false
                salmon.isChecked = false
            }
            else {
                total -= (num * 4.5)
                calories -= (num * 80)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 4.5
                calories2 -= 80
            }
        }

        /**
         * Mushroom.
         * Mushroom and the other 5 toppings require additional code so that only 3 toppings
         * are allowed instead of 1.
         * I used var "condiments" to count the number of toppings and prevent additional
         * toppings from being added if there are already 3.
         */
        Mushroom.setOnCheckedChangeListener{ calculate, isChecked ->
            if (mushroom.isChecked) {
                toppings += 1

                if (toppings < 4) {
                    total += (num * 1.0)
                    calories += (num * 60)
                    textView2.text = total.toString()
                    textView3.text = calories.toString()
                    total2 += 1.0
                    calories2 += 60
                }
                else {
                    mushroom.isChecked = false
                    toppings -= 1
                }

            }
            else {
                total -= (num * 1.0)
                calories -= (num * 60)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 1.0
                calories2 -= 60
                toppings -= 1
            }
        }

        //Lettuce
        Lettuce.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (lettuce.isChecked) {
                toppings += 1

                if (toppings < 4) {
                total += (num * 0.3)
                calories += (num * 20)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 += 0.3
                calories2 += 20
                }
                 else {
                    lettuce.isChecked = false
                    toppings -= 1
                }
            }
            else {
                total -= (num * 0.3)
                calories -= (num * 20)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 0.3
                calories2 -= 20
                toppings -= 1
            }
        }

        //Tomato
        Tomato.setOnCheckedChangeListener{ buttonView, isChecked ->

            if (tomato.isChecked) {
                toppings += 1

                if (toppings < 4) {
                    total += (num * 0.3)
                    calories += (num * 20)
                    textView2.text = total.toString()
                    textView3.text = calories.toString()
                    total2 += 0.3
                    calories2 += 20
            }
                else {
                    tomato.isChecked = false
                    toppings -= 1
                }
            }
            else {
                total -= (num * 0.3)
                calories -= (num * 20)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 0.3
                calories2 -= 20
                toppings -= 1
            }
        }

        //Pickles
        Pickles.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (pickle.isChecked) {
                toppings += 1
                if (toppings < 4) {
                    total += (num * 0.5)
                    calories += (num * 30)
                    textView2.text = total.toString()
                    textView3.text = calories.toString()
                    total2 += 0.5
                    calories2 += 30
            }
            else {
                    pickle.isChecked = false
                    toppings -= 1
                }
            }
            else {
                total -= (num * 0.5)
                calories -= (num * 30)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 0.5
                calories2 -= 30
                toppings -= 1
            }
        }

        //Mayo
        Mayo.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (mayo.isChecked) {
                toppings += 1
                    if (toppings < 4) {
                    total += (num * 0.0)
                    calories += (num * 100)
                    textView2.text = total.toString()
                    textView3.text = calories.toString()
                    total2 += 0.0
                    calories2 += 100
                }
                else {
                    mayo.isChecked = false
                        toppings -= 1
                }
            }
            else {
                total -= (num * 0.0)
                calories -= (num * 100)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 0.0
                calories2 -= 100
                toppings -= 1
            }
        }

        //Mustard
        Mustard.setOnCheckedChangeListener{ buttonView, isChecked ->
            if (mustard.isChecked) {
                toppings += 1
                if (toppings < 4) {
                    total += (num * 0.0)
                    calories += (num * 60)
                    textView2.text = total.toString()
                    textView3.text = calories.toString()
                    total2 += 0.0
                    calories2 += 60

                }
                else {
                    mustard.isChecked = false
                    toppings -= 1
                }
            }
            else {
                total -= (num * 0.0)
                calories -= (num * 60)
                textView2.text = total.toString()
                textView3.text = calories.toString()
                total2 -= 0.0
                calories2 -= 60
                toppings -= 1
            }
        }

        /**
         * Gets the EditText number inputted by the user and places it into "num".
         * "num" is the number of burgers made. "total2" and "calories2" is multiplied by
         * "num" to get the total cost and total calories of all burgers.
         */
        showButton.setOnClickListener {

            num = editText.getText().toString().toInt()
            total = total2 * num
            calories = calories2 * num
            textView2.text = total.toString();
            textView3.text = calories.toString();
        }
        //Prints the starting total and starting calories which is 0.
        textView2.text = total.toString();
        textView3.text = calories.toString()
    }
}
