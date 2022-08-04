package com.example.t05
import android.media.AudioAttributes
import android.media.MediaPlayer
import java.io.IOException

class MusicPlayer(private val musicService: MusicService): MediaPlayer.OnCompletionListener {

    // I took the mp3's from Shuo Niu: https://mathcs.clarku.edu/~shniu/index.html
    val MUSICPATH = arrayOf("http://people.cs.vt.edu/~esakia/mario.mp3", "http://people.cs.vt.edu/~esakia/tetris.mp3")
    val MUSICNAME = arrayOf("Super Mario", "Tetris")

    private lateinit var player: MediaPlayer
    private var currentPosition = 0
    private var musicIndex = 0
    private var musicStatus = 0 //0: before starts 1: playing 2: paused

    fun getMusicStatus(): Int {
        return musicStatus
    }

    fun getMusicName(): String {
        return MUSICNAME[musicIndex]
    }

    fun playMusic() {
        player = MediaPlayer()
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())
        try {
            player.setDataSource(MUSICPATH[musicIndex])
            player.prepare()
            player.setOnCompletionListener(this)
            player.start()
            musicService.onUpdateMusicName(getMusicName())
        } catch (ex: IOException) {
            ex.printStackTrace()
        }

        musicStatus = 1
    }

    fun pauseMusic() {
        if (player.isPlaying()) {
            player.pause()
            currentPosition = player.getCurrentPosition()
            musicStatus = 2
        }
    }

    fun resumeMusic() {
        player.seekTo(currentPosition)
        player.start()
        musicStatus = 1
    }


    override fun onCompletion(mp: MediaPlayer?) {
        musicIndex = (musicIndex + 1) % 2
        player.release()
        playMusic()
    }
}