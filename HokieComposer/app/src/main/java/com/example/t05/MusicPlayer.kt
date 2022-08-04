package com.example.t05
import android.content.res.AssetFileDescriptor
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import kotlinx.coroutines.*
import java.io.IOException

class MusicPlayer(private val musicService: MusicService): MediaPlayer.OnCompletionListener {

    // I took the mp3's from Shuo Niu: https://mathcs.clarku.edu/~shniu/index.html
    val MUSICPATH = arrayOf(R.raw.gotechgo, R.raw.sandman, R.raw.triumph, R.raw.clapping, R.raw.cheering, R.raw.lestgohokies)
    val MUSICNAME = arrayOf("Go Tech Go", "Enter Sandman Intro", "Tech Triumph", "clapping", "cheering", "lets go hokies")
    private lateinit var player: MediaPlayer
    private var currentPosition = 0

    //Values used for sound mixing.
    var musicIndex = 0
    var effectIndex1 = 0
    var effectIndex2 = 0
    var effectIndex3 = 0
    var seek1 = 0
    var seek2 = 0
    var seek3 = 0
    var resume = true
    private var musicStatus = 0 //0: before starts 1: playing 2: paused
    private lateinit var ioScope: CoroutineScope

    //Soundpool variables.
    private var soundpool: SoundPool
    private var sound: Array<Int> = arrayOf(0, 0, 0)
    private var effects: Array<Int> = arrayOf(0, 0, 0)
    private var loaded = false

    init {
        var audioAttributes: AudioAttributes = AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MOVIE).build()

        soundpool = SoundPool.Builder().setMaxStreams(6).setAudioAttributes(audioAttributes).build()
        soundpool.setOnLoadCompleteListener {
            soundPool, sampleId, status-> loaded = true
        }
        //Loading audio into soundpool.
        sound[0] = soundpool.load(musicService.applicationContext,R.raw.gotechgo,1)
        sound[1] = soundpool.load(musicService.applicationContext,R.raw.sandman,1)
        sound[2] = soundpool.load(musicService.applicationContext,R.raw.triumph,1)
        effects[0] = soundpool.load(musicService.applicationContext,R.raw.clapping,1)
        effects[1] = soundpool.load(musicService.applicationContext,R.raw.cheering,1)
        effects[2] = soundpool.load(musicService.applicationContext,R.raw.lestgohokies,1)

        ioScope = CoroutineScope(Dispatchers.IO)
    }

    //Function used to change current music.
    fun changeMusic(num: Int)   {
        musicIndex = num
    }
    //3 functions to change the current sound effect.
    fun changeEffect1(num1: Int)   {
        effectIndex1 = num1
    }
    fun changeEffect2(num2: Int)   {
        effectIndex2 = num2
    }
    fun changeEffect3(num3: Int)   {
        effectIndex3 = num3
    }
    //3 functions to get the seek bar position.
    fun seeker1(numa: Int)   {
        seek1 = numa
    }
    fun seeker2(numb: Int)   {
        seek2 = numb
    }
    fun seeker3(numc: Int)   {
        seek3 = numc
    }

    fun getMusicStatus(): Int {
        return musicStatus
    }

    fun getMusicName(): String {
        return MUSICNAME[musicIndex]
    }

    //Uses coroutine to add sound effects with a delay based on the seek bar position.
    suspend fun delayEffect1() {

        delay(seek1.toLong() * 400)
        if (resume == true) {
            soundpool.play(effects[effectIndex1], 1.0F, 1F, 1, 0, 1F)
        }
    }
    suspend fun delayEffect2() {

        delay(seek2.toLong() * 400)

        if (resume == true) {
                soundpool.play(effects[effectIndex2], 1.0F, 1F, 1, 0, 1F)
            }
    }
    suspend fun delayEffect3() {

        delay(seek3.toLong() * 400)

        if (resume == true) {
            soundpool.play(effects[effectIndex3], 1.0F, 1F, 1, 0, 1F)
        }
    }

    fun playMusic() {

        player = MediaPlayer()
        player.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build())

        val assetFileDescriptor: AssetFileDescriptor = musicService.resources.openRawResourceFd(MUSICPATH[musicIndex])
        try {
            player.setDataSource(assetFileDescriptor)
            player.prepare()
            player.setOnCompletionListener(this)
            soundpool.autoPause()
            player.start()

            //Launches sound effects when the music is played.
            ioScope.launch {
                    delayEffect1()
                    delayEffect2()
                    delayEffect3()
            }

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
            soundpool.autoPause()
            resume = false
        }
    }

    fun resumeMusic() {
        player.seekTo(currentPosition)
        player.start()
        musicStatus = 1
        soundpool.autoResume()
        resume = true
    }

    //Restart function added for this app.
    fun restartMusic()  {
        player.release()
        playMusic()
        resume = true
    }

    override fun onCompletion(mp: MediaPlayer?) {
        player.release()
        playMusic()
    }
}