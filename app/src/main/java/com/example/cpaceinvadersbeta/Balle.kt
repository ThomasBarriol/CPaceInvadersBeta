package com.example.cpaceinvadersbeta

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import androidx.core.graphics.component2
import java.util.*


abstract class Balle(x : Float, y : Float, var diametre : Float,lvl : Float){


    val random = Random()
    val paint = Paint()
    var r = RectF(x,y,x+diametre, y+diametre)

    abstract var dy : Float

    open fun draw(canvas: Canvas?){
        canvas?.drawOval(r, paint)
    }

    open fun move(lesAsteroides: ArrayList<Asteroide>, lesBalles: ArrayList<Projectile>, lvl: Float){
        r.offset(0f, 40.0f*dy)
    }

}






