package com.example.cpaceinvadersbeta

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.*
import android.media.MediaPlayer
import android.os.Bundle
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.graphics.component2
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import java.util.*
import kotlin.math.roundToInt
import kotlin.system.exitProcess

class CanonView @JvmOverloads constructor (context: Context, attributes: AttributeSet? = null, defStyleAttr: Int = 0): SurfaceView(context, attributes,defStyleAttr), SurfaceHolder.Callback, Runnable {
    lateinit var canvas: Canvas
    val backgroundPaint = Paint()
    val textPaint = Paint()
    var gameOver : Boolean = false
    var hearts = 3
    var hits = 0f
    var shots = 0f
    var bnsType = 0
    var checkBns= 0.0





    var difficulty = 0f
    var startDiff = difficulty.toInt()

    val random = Random()

    var lesBalles   = ArrayList<Projectile>()
    var lesAsteroides = ArrayList<Asteroide>()
    var leBonus = ArrayList<Bonus>()
    
    var screenWidth = 0f
    var screenHeight = 0f
    var drawing = false
    lateinit var thread: Thread

    val activity = context as FragmentActivity
    var totalElapsedTime = 0.0


    val canon = Canon(this, resources,false)

    var mMediaPlayer: MediaPlayer? = null
    var musicCheck = false

    val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
    var highScore = sharedPref.getFloat(startDiff.toString(), 0f)



    init {
        backgroundPaint.color = Color.argb(255,25,25,60)
        textPaint.textSize = screenWidth/20
        textPaint.color = Color.RED

    }

    fun pause() {
        drawing = false
        thread.join()
    }

    fun resume() {
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        highScore = sharedPref.getFloat(startDiff.toString(), 0f)
        drawing = true
        thread = Thread(this)
        thread.start()
    }

    fun stopSong() {
        //met la musique en pause jusqu'à ce que la fonction playSound() est appelée
        if (mMediaPlayer != null) {
            mMediaPlayer!!.pause()

        }
    }

    fun playSong() {
        //lance la lecture de la musique
        if(musicCheck) {
                mMediaPlayer!!.start()
        }
    }

    fun updatePositions(elapsedTimeMS: Double) {
        //déplace les éléments, détecte la fin de partie



        if(hearts<=0){
            stopSong()
            drawing = false
            gameOver = true
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
            highScore = sharedPref.getFloat(startDiff.toString(), 0f)
            val score = hits
            if(score>highScore){
                with (sharedPref.edit()) {
                    putFloat(startDiff.toString(), score)
                    apply()
                }
                highScore = sharedPref.getFloat(startDiff.toString(), 0f)
            }

            showGameOverDialog(R.string.lose)
        }
        else{
            try {
                for (b in lesBalles) {
                    b.move(lesAsteroides, lesBalles, difficulty)
                    if (b.r.component2() < 0f) {
                        b.projOnScreen = false
                        hits--
                    }
                }

                for (a in lesAsteroides) {

                    a.move(lesAsteroides, lesBalles, difficulty)

                    if (a.r.component2() > screenHeight) {
                        a.asteroideOnScreen = false
                        hearts--
                    }

                    if (RectF.intersects(a.r, canon.r)) {
                        if (canon.shield) {
                            canon.shield = false
                            a.asteroideOnScreen = false
                        } else {
                            hearts = 0
                        }


                    }

                }
                for (bns in leBonus) {
                    bns.r.offset(0.0F, bns.dy.toFloat())
                }

                if (leBonus.size == 0) {
                    if (checkBns > 17) {
                        if (bnsType == 0 && !canon.shield) {
                            leBonus.add(
                                Bonus(
                                    random.nextInt((screenWidth - 70).toInt()).toFloat(),
                                    (random.nextInt(20)).toFloat(), 70f, difficulty, resources, 0
                                )
                            )
                        } else if (bnsType == 1) {
                            leBonus.add(
                                Bonus(
                                    random.nextInt((screenWidth - 70).toInt()).toFloat(),
                                    (random.nextInt(20)).toFloat(), 70f, difficulty, resources, 1
                                )
                            )
                        }

                        checkBns = 0.0
                    } else checkBns += elapsedTimeMS / 1000.0
                }
                else {
                    for (bns in leBonus) {
                        if (bns.gereBonus(canon, bns)) {
                            bns.bonusOnScreen = false
                            if (bnsType == 1) {
                                hearts++
                                if (!canon.shield) {
                                    bnsType = 0
                                }
                            } else if (bnsType == 0) {
                                canon.shield = true
                                bnsType = 1
                            }
                            leBonus.clear()
                        } else if (bns.r.top > screenHeight) {
                            bns.bonusOnScreen = false
                            if (bnsType == 0) {
                                bnsType = 1
                            } else if (bnsType == 1) {
                                if (!canon.shield) {
                                    bnsType = 0
                                }
                            }
                            leBonus.clear()
                        }
                    }
                }

            }catch (e : ConcurrentModificationException){}
            try{
                collectGarb()
            }catch (e: ConcurrentModificationException) {

            }catch (e : ArrayIndexOutOfBoundsException){

            }
        }






    }
    fun collectGarb(){
        //supprime les objets inutilisés de leur liste

        var lengthBall: Int = lesBalles.size
        var lengthAsteroide: Int = lesAsteroides.size

        var sellaBsel = lesBalles.reversed()
        var sedioretsAsel = lesAsteroides.reversed()

        if (!lesBalles.isEmpty()) {
            var ind0: Int = 0

            for (i in sellaBsel) {

                if (!i.projOnScreen) {
                    lesBalles.removeAt(lengthBall - 1 - ind0)
                }

                ind0++
            }
        }


        if (!lesAsteroides.isEmpty()) {
            var ind1: Int = 0

            for (i in sedioretsAsel) {

                if (!i.asteroideOnScreen) {
                    lesAsteroides.removeAt(lengthAsteroide - 1 - ind1)
                    hits++
                }

                ind1++
            }
        }
    }

    fun showGameOverDialog(messageId: Int) {
        //affiche le score à la fin de la partie
        class GameResult: DialogFragment() {
            override fun onCreateDialog(bundle: Bundle?): Dialog {
                val builder = AlertDialog.Builder(getActivity())
                builder.setTitle(resources.getString(messageId))
                builder.setMessage("Astéroïdes détruits : ${hits.toInt()} \nPrécision :" +
                        " ${((hits/shots)*100).roundToInt()}%")
                builder.setNegativeButton("difficulté",DialogInterface.OnClickListener{_,_->difficulties()})
                builder.setPositiveButton("menu", DialogInterface.OnClickListener { _, _-> exitProcess(-1) })
                builder.setNeutralButton("Recommencer", DialogInterface.OnClickListener { _, _->newGame()})

                return builder.create()
            }
        }
        activity.runOnUiThread(
            Runnable {
                val ft = activity.supportFragmentManager.beginTransaction()
                val prev = activity.supportFragmentManager.findFragmentByTag("dialog")

                if (prev != null) {
                    ft.remove(prev)
                }

                ft.addToBackStack(null)
                val gameResult = GameResult()
                gameResult.setCancelable(false)
                gameResult.show(ft,"dialog")
            }

        )
    }

    fun difficulties(){
        //permet de changer le niveau de difficulté sans devoir retourner dans le menu
        class Difficult: DialogFragment() {
            lateinit var dialog:AlertDialog
            override fun onCreateDialog(bundle: Bundle?): Dialog {
                val builder = AlertDialog.Builder(getActivity())
                val arrayDiff = arrayOf("Fossile","Bleu","Fier Poil","Commitard","Vieux C*n")
                builder.setTitle("Niveaux de difficulté")
                builder.setSingleChoiceItems(arrayDiff,startDiff,DialogInterface.OnClickListener{dialog, which ->
                    startDiff = which})
                builder.setPositiveButton("ok") {dialog, which ->
                    dialog.dismiss()
                }
                difficulty = startDiff.toFloat()

                return builder.create()
                dialog.show()
            }
        }
        activity.runOnUiThread(
                Runnable {
                    val ft = activity.supportFragmentManager.beginTransaction()
                    val prev = activity.supportFragmentManager.findFragmentByTag("dialog")

                    if (prev != null) {
                        ft.remove(prev)
                    }

                    ft.addToBackStack(null)
                    val diff = Difficult()
                    diff.setCancelable(false)
                    diff.show(ft,"dialog")
                })
    }

    fun newGame() {
        //remet certains paramètres à leur valeur initaile et relance une partie
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        highScore = sharedPref.getFloat(startDiff.toString(), 0f)
        difficulty = startDiff.toFloat()


        hits = 0f
        hearts=3
        shots=0f
        lesAsteroides.clear()
        lesBalles.clear()
        totalElapsedTime = 0.0
        drawing = true

        if (gameOver) {
            gameOver = false
            playSong()
            thread = Thread(this)
            thread.start()
        }

    }

    override fun onSizeChanged(w:Int, h:Int, oldw:Int, oldh:Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        screenWidth = w.toFloat()
        screenHeight = h.toFloat()


        textPaint.setTextSize(w/20f)
        textPaint.isAntiAlias = true
    }

    override fun run() {
        //fonction réalisée en boucle tant que le thread associé est actif
        var previousFrameTime = System.currentTimeMillis()
        var check = 0.0





        while (drawing) {
            val currentTime = System.currentTimeMillis()
            val elapsedTimeMS: Double = (currentTime - previousFrameTime).toDouble()
            totalElapsedTime += elapsedTimeMS / 1000.0 //en seconde
            val interval = 1.2

            updatePositions(elapsedTimeMS)


            if (check > interval && lesAsteroides.size < 8) {
                var taille = 80f*(1+random.nextInt(4))
                lesAsteroides.add(Asteroide(random.nextInt((screenWidth - taille).toInt()).toFloat(),
                        (random.nextInt(20)).toFloat(), taille, resources, difficulty))
                difficulty = (difficulty + elapsedTimeMS/1500).toFloat()
                check = 0.0
            } else check += elapsedTimeMS / 1000.0

            try{
                draw()
            }catch (e : ConcurrentModificationException){

            }catch (e : IllegalStateException){
                holder.unlockCanvasAndPost(canvas)
            }

            previousFrameTime = currentTime
        }
    }

    fun draw() {
        //dessine tout les éléments sur la surface de jeu
        if (holder.surface.isValid) {
            canvas = holder.lockCanvas()
            canvas.drawRect(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), backgroundPaint)
            canvas.drawText("${hearts} vie(s)",30f,50f, textPaint)
            canvas.drawText("Score : ${hits.toInt()} (Best : ${highScore.toInt()} ) ",30f,100f, textPaint)
            //canvas .drawText("${lesBalles.size} ${lesAsteroides.size} ${leBonus.size}",30f,150f,textPaint)   Permet de se rendre compte de la suppression des objets des listes
            //canvas .drawText("${difficulty} ${startDiff} ",30f,200f,textPaint)  Permet de se rendre compte de l'augmentation de la difficulté
            for (b in lesBalles){
                if(b.projOnScreen){
                    b.draw(canvas)
                }
            }

            for (p in lesAsteroides) {
                if (p.asteroideOnScreen) {
                    p.draw(canvas)
                }
            }
            for (bns in leBonus){
                if(bns.bonusOnScreen){
                    bns.draw(canvas)
                }
            }
            canon.draw(canvas)

            holder.unlockCanvasAndPost(canvas)
        }
    }


    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {}
    override fun surfaceCreated(holder: SurfaceHolder) {}
    override fun surfaceDestroyed(holder: SurfaceHolder) {}
}

