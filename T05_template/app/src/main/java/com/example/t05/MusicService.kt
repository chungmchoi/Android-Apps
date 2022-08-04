package com.example.t05

import android.app.*
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder

class MusicService: Service() {




    override fun onBind(intent: Intent?): IBinder? {
        return iBinder
    }

    var musicPlayer: MusicPlayer? = null
    private val iBinder = MyBinder()
    private val CHANNEL_ID = "Music Service Channel ID"

    companion object{
        val COMPLETE_INTENT = "complete intent"
        val MUSICNAME = "music name"}



inner class MyBinder: Binder(){
    fun getService():MusicService {

        return this@MusicService
    }

}


    override fun onCreate() {
        super.onCreate()
        musicPlayer = MusicPlayer(this)
    }



    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        createNotificationChannel()



        val pendingIntent: PendingIntent =
            Intent(this, MainActivity::class.java).let{
                    notificationIntent -> PendingIntent.getActivity(this,0,notificationIntent,0)}



        val notification: Notification =
            Notification.Builder(this, CHANNEL_ID).setContentTitle("Music").
            setContentText("Hello World!").setContentIntent(pendingIntent).build()


        startForeground(123,notification)
        return Service.START_STICKY
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(CHANNEL_ID, "Music Service",
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NotificationManager::class.java)
            manager!!.createNotificationChannel(serviceChannel)
        }
    }


    fun startMusic() {
        musicPlayer?.playMusic()
    }

    fun pauseMusic() {
        musicPlayer?.pauseMusic()
    }

    fun resumeMusic() {
        musicPlayer?.resumeMusic()
    }

    fun getPlayingStatus(): Int {
        return musicPlayer?.getMusicStatus() ?: -1
    }


    fun onUpdateMusicName(musicName: String) {

        val intent = Intent(COMPLETE_INTENT)
        intent.putExtra(MUSICNAME, musicName)
        sendBroadcast(intent)
    }

}
