package com.example.cpaceinvadersbeta

import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Thread.sleep


class MainActivity() : AppCompatActivity(), View.OnClickListener, View.OnTouchListener {
    lateinit var canonView: CanonView
    var sound = false //true si bruit de tire
    var size : Float = 30f //diamètre des projectiles



    private var soundPool: SoundPool? = null
    private val soundId = 1



    override fun onPause() {
        super.onPause()
        canonView.pause()
    }

    fun playSound(view: View){
        soundPool?.play(soundId,0.1f,0.1f,0,0,1f)
    }


    override fun onResume() {
        super.onResume()
        canonView.resume()
    }


    override fun onStop() {
        super.onStop()
        if (canonView.mMediaPlayer != null) {
            canonView.mMediaPlayer!!.release()
            canonView.mMediaPlayer = null
        }
    }

    override fun onClick(v: View?){
        when(v?.id){

            R.id.shoot ->{
                if(canonView.lesBalles.size<9){//si il existe moins de 9 projectiles
                    canonView.lesBalles.add(Projectile((canonView.canon.r.centerX() -15).toFloat(), canonView.canon.r.centerY().toFloat(), size,canonView.difficulty))
                    canonView.shots++
                    canonView.hits++
                    if(sound){//si les bruits de tire sont souhaités, le bruit se produit
                        playSound(canonView)
                    }
                    sleep(10)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        left.setOnTouchListener(this)
        right.setOnTouchListener(this)
        shoot.setOnClickListener(this)

        //on recupère les information de l'autre activity
        val posit = intent.getIntExtra("com.example.cpaceinvadersbeta.difff", 0)
        val soundOn = intent.getBooleanExtra("com.example.cpaceinvadersbeta.sound", false)
        val musicOn = intent.getIntExtra("com.example.cpaceinvadersbeta.music", 0)
        canonView = findViewById<CanonView>(R.id.vMain)

        //active ou desactive la musique lors de la partie
        if(musicOn==0){
            canonView.musicCheck=false
        }
        else{
            canonView.musicCheck = true
        }
        canonView.startDiff = posit
        canonView.difficulty = posit.toFloat()

        //sélectionne la musique choisi dans l'écran d'accueil
        sound = soundOn
        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool!!.load(baseContext, R.raw.laser2, 1)
        if(musicOn!=0) {
            if (canonView.mMediaPlayer == null) {
                if(musicOn==1){
                    canonView.mMediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.music)
                }
                else if(musicOn==2){
                    canonView.mMediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.starman)
                }
                else if(musicOn==3){
                    canonView.mMediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.rocketman)
                }
                else if(musicOn==4){
                    canonView.mMediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.doom)
                }

                canonView.mMediaPlayer!!.setVolume(1f,1f)

                canonView.mMediaPlayer!!.isLooping = true
                canonView.mMediaPlayer!!.start()
            } else canonView.mMediaPlayer!!.start()
        }

    }

    override fun onTouch(v: View?,e: MotionEvent): Boolean {
        //déplace le vaisseau(canon) lorsque l'utilisateur appuie sur les boutons correspondants
            val action = e.action

                if (action == MotionEvent.ACTION_DOWN) {
                    when(v?.id){
                        R.id.left -> {
                            canonView.canon.r.offset(-100f, 0f)
                        }
                        R.id.right -> {
                            canonView.canon.r.offset(100f, 0f)
                        }
                    }

                }

            return v?.onTouchEvent(e) ?: true
    }

}
