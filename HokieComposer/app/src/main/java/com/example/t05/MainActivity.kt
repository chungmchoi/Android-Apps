package com.example.t05

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity(){

    companion object   {
        /**
         * I went to www.requestbin.com and clicked on the blue button that says
         * "Create Request Bin". Then I copied the endpoint link on the top right and pasted it
         * onto my const val ROUTE. Then run the app and click buttons. You can test this program
         * by going to requestbin, getting an endpoint, and pasting it on ROUTE.
         */
        const val URL = "http://requestbin.fullcontact.com/"
        const val ROUTE = "https://a9611e38303125b291c69960ca19c932.m.pipedream.net"
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}


