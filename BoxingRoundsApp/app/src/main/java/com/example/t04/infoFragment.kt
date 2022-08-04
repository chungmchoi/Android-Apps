package com.example.t04

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_info.view.*
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [infoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class infoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val model: BoxingViewModel by activityViewModels()
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_info, container, false)

    /** Realized that I shouldn't use Firebase to store data since the stability contstraint mentions
     * "switching off Wi-Fi" which means the app won't be able to retrieve exercise data from the
     * internet. I instead used SharedPreferences.
     *
        database = Firebase.database.reference
        //Write data to Firebase.
        writeNewUser("workoutData",model.arraylist2)
        //Reads data from firebase.
        val box = ArrayList<BoxingData>()
        val ref = FirebaseDatabase.getInstance().getReference("workoutData")
        ref.addValueEventListener(object: ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (boxingSnapshot in dataSnapshot.children) {

                    val box1 = boxingSnapshot.child(model.num1.toString()).getValue(BoxingData::class.java)
                    box.add(box1!!)
                    Log.d(TAG, "MyApp: 111" + dataSnapshot.value!!)
                    Log.d("MyApp 222",dataSnapshot.toString())
                    Log.d("MyApp 333",box[0].current.toString())
                    Log.d("MyApp box is: 4444",box.toString())
                    model.num1 += 1
                }
                view.recycler_view.adapter = Adapter(box)
                view.recycler_view.layoutManager = LinearLayoutManager(activity)
                view.recycler_view.setHasFixedSize(true)
            }
            override fun onCancelled(p0: DatabaseError) {
                Log.d(TAG, "MyApp: failed")
                throw p0.toException()
            }

        })
    */
        //Function used to save list of BoxingData to a key.
        fun saveArrayList(list: List<BoxingData>, key: String?) {
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val editor: SharedPreferences.Editor = prefs.edit()
            val gson = Gson()
            val json: String = gson.toJson(list)
            editor.putString(key, json)
            editor.apply()
        }
        //Used to get the list.
        fun getArrayList(key: String?): List<BoxingData> {
            val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val gson = Gson()
            val json: String = prefs.getString(key, null)
            val type: Type = object : TypeToken<List<BoxingData>>(){}.getType()
            return gson.fromJson(json, type)
        }

        //Combines past list with current list so that data is preserved.
        var finalList: List<BoxingData> = model.arraylist2 + getArrayList("1")
        saveArrayList(finalList, "1")


        //Recycler view using key
        view.recycler_view.adapter = Adapter(getArrayList("1"))
        view.recycler_view.layoutManager = LinearLayoutManager(activity)
        view.recycler_view.setHasFixedSize(true)

        //When floating action button is clicked, the recycler view is emptied.
        view.fab_btn.setOnClickListener {

            var empty = ArrayList<BoxingData>()
            model.arraylist2 = empty
            saveArrayList(empty, "1")
            view.recycler_view.adapter = Adapter(getArrayList("1"))
            view.recycler_view.layoutManager = LinearLayoutManager(activity)
            view.recycler_view.setHasFixedSize(true)
        }


        return view
    }

    /** Firebase wasn't used because of the stability wi-fi constraint.
     *
    //Saving my data to firebase.
    @IgnoreExtraProperties
    data class firebaseSave(
        var boxing: ArrayList<BoxingData>

    )
    private fun writeNewUser(userId: String, add: ArrayList<BoxingData>) {
        val user = firebaseSave(add)
        database.child(userId).setValue(user)
    }
    */

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment infoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            infoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}

