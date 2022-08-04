package com.example.t05

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.json.JSONObject

class UploadWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {


    companion object   {
        const val TAG = "PP05_TAG"
        const val USERNAME = "CS3714"
        const val INITIALIZE_STATUS = "initialization status"
        const val MUSIC_PLAYING = "music playing"
    }

    override fun doWork(): Result {
        val json = JSONObject()
        json.put("username", inputData.getString("username"))
        json.put("date", inputData.getString("date"))
        json.put("event", inputData.getString("event"))
        //Log.d("doWork is username:", inputData.getString("username").toString())     //testing if it works
        //Log.d("doWork is event:", inputData.getString("event").toString())           //testing if it works
        return uploadLog(json, MainActivity.URL)
    }

    //Utilizes TrackerRetrofitService.
    fun uploadLog(json: JSONObject, url: String): Result {
        var call = TrackerRetrofitService.create(url).postLog(json)
        call.execute()        //use when sending data to site
        return Result.success()
    }
}