package com.example.minichallenge2

import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_order.*
import kotlinx.android.synthetic.main.fragment_order.applem
import kotlinx.android.synthetic.main.fragment_order.applep
import kotlinx.android.synthetic.main.fragment_order.funds
import kotlinx.android.synthetic.main.fragment_order.orangem
import kotlinx.android.synthetic.main.fragment_order.orangep
import kotlinx.android.synthetic.main.fragment_order.purchase
import kotlinx.android.synthetic.main.fragment_order.qapple
import kotlinx.android.synthetic.main.fragment_order.qorange
import kotlinx.android.synthetic.main.fragment_order.view.*
import java.nio.channels.Selector
import java.math.BigDecimal
import java.math.RoundingMode
/**
 * A simple [Fragment] subclass.
 */
class order : Fragment() {

    //Used to get the var wallet and cost in SharedViewModel.kt and use them in the fragments.
    private lateinit var itemSelector: Selector
    private val model: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order, container, false)

        //variables for number of apples, oranges, and current wallet rounded to 2 decimals.
        var apples = 0
        var oranges = 0
        val walletr = BigDecimal(model.wallet).setScale(2, RoundingMode.HALF_EVEN)
        view.funds.text = walletr.toString()

        /**
         * applep is the "+" button. When it is pressed, 1 apple is added.
         * applem is the "-" button. When it is pressed, 1 apple is subtracted.
         * qapple is the number of apples currently shown on the app screen.
         */
        view.applep.setOnClickListener{
            apples += 1
            view.qapple.text = apples.toString()
        }
        view.applem.setOnClickListener{
            if (apples > 0) {   //Cannot have less than 0 apples.
                apples -= 1
                view.qapple.text = apples.toString()
            }
        }
        //Same as the apple button code above except this code block represents oranges.
        view.orangep.setOnClickListener{
            oranges += 1
            view.qorange.text = oranges.toString()
        }
        view.orangem.setOnClickListener{
            if (oranges > 0) {  //Cannot have less than 0 oranges.
                oranges -= 1
                view.qorange.text = oranges.toString()
            }
        }

        //When the "purchase" button is clicked, goes to the next fragment.
        view.purchase.setOnClickListener{
            //Calculates the current total cost based on number of apples and oranges selected.
            model.cost = (apples * 2.49) + (oranges * 4.99)
            Navigation.findNavController(view).navigate(R.id.action_order_to_confirm2)
        }

        return view
    }

}
