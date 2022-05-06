package com.example.cpaceinvadersbeta

import android.content.Intent
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main2.*

class MainActivity2 : AppCompatActivity(), View.OnClickListener {
    lateinit var menuView: MenuView

    private var soundPool: SoundPool? = null
    private val soundId = 1

    override fun onPause() {
        super.onPause()
        menuView.pause()
    }

    override fun onResume() {
        super.onResume()
        menuView.resume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        diffic.setOnClickListener(this)
        start.setOnClickListener(this)
        soundEffect.setOnClickListener(this)
        cred.setOnClickListener(this)
        rules.setOnClickListener(this)

        menuView = findViewById<MenuView>(R.id.vMenu)
        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool!!.load(baseContext, R.raw.bruh, 1)
    }

    fun playSound(view: View){
        soundPool?.play(soundId,0.5f,0.5f,0,0,1f)
    }

    override fun onClick(v: View?) {

        when (v?.id) {
            R.id.start ->{
                var truc = menuView.startDiff
                var chose = menuView.sound
                var bidule = menuView.music
                playSound(menuView)

                val monIntent : Intent =  Intent(this@MainActivity2,MainActivity::class.java)
                monIntent.putExtra("com.example.cpaceinvadersbeta.difff",truc)
                monIntent.putExtra("com.example.cpaceinvadersbeta.sound",chose)
                monIntent.putExtra("com.example.cpaceinvadersbeta.music",bidule)

                startActivity(monIntent)
            }
            R.id.diffic ->{
                menuView.difficulti()
            }
            R.id.soundEffect ->{
                menuView.soundSet()
            }
            R.id.cred ->{
                menuView.credit()
            }
            R.id.rules ->{
                menuView.gameRules()
            }
        }
    }
}