package com.example.t06

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_image.view.*
import java.io.ByteArrayOutputStream
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.Picasso

/**
 * A simple [Fragment] subclass.
 */
class ImageFragment : Fragment() {

    private val model: ViewModel by activityViewModels()
    private lateinit var database: DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_image, container, false)

        database = Firebase.database.reference
        view.imageTaken.setImageBitmap(model.image1)

        //var bmImg = BitmapFactory.decodeFile("/")
        //view.imageTaken.setImageBitmap(bmImg)
        //Picasso.get().load("my_images").into(view.imageTaken)


//////////////////////////////////////////////////////////////////"Android/data/com.example.package.name/files/Pictures"
/**     Used to upload photos to Firebase.
 *
        val storage = Firebase.storage
        // [START upload_create_reference]
        // Create a storage reference from our app
        val storageRef = storage.reference
        // Create a reference to "mountains.jpg"
        val mountainsRef = storageRef.child("mountains.jpg")
        // Create a reference to 'images/mountains.jpg'
        val mountainImagesRef = storageRef.child("images/mountains.jpg")
        // While the file names are the same, the references point to different files
        mountainsRef.name == mountainImagesRef.name // true
        mountainsRef.path == mountainImagesRef.path // false
        // [END upload_create_reference]


        val bmImg = BitmapFactory.decodeFile("Android/data/com.example.package.name/files/Pictures")
        view.imageTaken.setImageBitmap(bmImg)

        view.imageTaken.isDrawingCacheEnabled = true
        view.imageTaken.buildDrawingCache()
        val bitmap = (view.imageTaken.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {

        }
        */
//////////////////////////////////////////////////////////////////


        //Sends the data to firebase upon click
        view.save.setOnClickListener{
            writeNewUser(model.num.toString(), model.address, model.coordinates, model.date, model.lat, model.long)
            model.num += 1
            var map = mutableMapOf<String, firebaseSave>()
            map["1"]
            val item = info(model.image1, model.address, model.coordinates, model.date)
            model.pictureList.add(item)
            Navigation.findNavController(view).navigate(R.id.action_imageFragment_to_startFragment)
        }
        return view
    }


    //Saving my data to firebase.
    @IgnoreExtraProperties
    data class firebaseSave(
        var address: String? = "",
        var coordinates: String? = "",
        var date: String? = "",
        var lat: String? = "",
        var long: String? = ""
    )
    private fun writeNewUser(userId: String, add: String, coor: String, date: String, lat: String, long: String) {
        val user = firebaseSave(add, coor, date, lat, long)
        database.child("users").child(userId).setValue(user)
    }
}
















