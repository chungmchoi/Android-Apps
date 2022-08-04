package com.example.minichallenge2

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_confirm.view.*
import java.nio.channels.Selector

/**
 * A simple [Fragment] subclass.
 */
class confirm : Fragment() {

    //Used to get the var wallet and cost in SharedViewModel.kt and use them in the fragments.
    private lateinit var itemSelector: Selector
    private val model: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //This fragment confirms whether the user wants to purchase the fruits or not.
        val view2 = inflater.inflate(R.layout.fragment_confirm, container, false)
        view2.confirmtext.text = "This purchase will cost you $" + model.cost + ". Proceed?"

        /*
        * When purchase2 button is clicked and wallet funds is sufficient, the fruits are purchased
        * and cost is subtracted from the wallet. If funds are insufficient, then a toast comment
        * stating "insufficient funds" appears.
        */
        view2.purchase2.setOnClickListener{
            if (model.wallet >= model.cost) {
                model.wallet -= model.cost
                Navigation.findNavController(view2).navigate(R.id.action_confirm_to_receipt22)
            }
            else    {
                val myToast = Toast.makeText(context,"Insufficient funds.",Toast.LENGTH_SHORT)
                myToast.setGravity(Gravity.CENTER_VERTICAL,0,700)
                myToast.show()
            }
        }
        return view2
    }

}
