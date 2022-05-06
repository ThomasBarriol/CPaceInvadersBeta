package com.example.cpaceinvadersbeta

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.media.AudioManager
import android.media.SoundPool
import android.view.View

class Asteroide(x: Float, y: Float, diametre: Float, resources: Resources, lvl: Float) : Balle(x, y, diametre,lvl) {

    var asteroideOnScreen = true
    override var dy: Float = (((0.17 + (( random.nextInt(10))/10).toDouble())*80/(diametre))+0.1*(lvl)).toFloat()
    private var color = Color.DKGRAY// Brown Color.argb(255, 139,69,19)
    private val cs = BitmapFactory.decodeResource(resources, R.drawable.cs)


    fun gereBalle(b:Projectile, p:Asteroide,lvl : Float){
        //repère les collision entre les projectiles et les asteroides
        if(RectF.intersects(p.r,b.r)){
            b.projOnScreen = false
            if(p.diametre/80f<=1){
                p.asteroideOnScreen = false

            }
            else{
                p.diametre = p.diametre - 80f
                r.right = r.right-80f
                r.bottom = r.bottom -80f
                r.offset(40f,40f)
                dy = ((0.17 + (( random.nextInt(10))/10).toDouble())*80/(diametre)+0.1*(lvl)).toFloat()
            }
        }
    }

    override fun draw(canvas: Canvas?) {
        paint.color = color
        super.draw(canvas)

        canvas?.drawBitmap(cs, Rect(0, -100, 650, 450), Rect((r.left+0.1*diametre).toInt(),
                (r.top+0.1*diametre).toInt(), (r.right+0.2*diametre).toInt(), (r.bottom-0.1*diametre).toInt()), paint)
    }


}




