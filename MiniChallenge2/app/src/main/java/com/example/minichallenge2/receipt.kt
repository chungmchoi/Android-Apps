package com.example.minichallenge2

import android.icu.text.DecimalFormat
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_receipt.view.*
import java.nio.channels.Selector

import java.math.BigDecimal
import java.math.RoundingMode
/**
 * A simple [Fragment] subclass.
 */
class receipt : Fragment() {

    //Used to get the var wallet and cost in SharedViewModel.kt and use them in the fragments.
    private lateinit var itemSelector: Selector
    private val model: SharedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //This fragment shows the order total and the balance remaining in the wallet.
        val view3 = inflater.inflate(R.layout.fragment_receipt, container, false)

        //cost and wallet is rounded to 2 decimals and placed in costr and walletr.
        val costr = BigDecimal(model.cost).setScale(2, RoundingMode.HALF_EVEN)
        val walletr = BigDecimal(model.wallet).setScale(2, RoundingMode.HALF_EVEN)

        //The rounded cost and wallet total are displayed in text view.
        view3.total.text = "Order total:" + costr
        view3.balance.text = "Balance remaining:" + walletr
        return view3
    }
}
