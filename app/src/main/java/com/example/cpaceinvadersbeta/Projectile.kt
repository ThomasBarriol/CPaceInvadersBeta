package com.example.cpaceinvadersbeta

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import java.util.ArrayList

class Projectile(x: Float, y: Float, diametre: Float, lvl: Float) : Balle(x, y, diametre, lvl) {

    var projOnScreen = true
    override var dy: Float = -1.0f

    override fun move(lesAsteroides: ArrayList<Asteroide>, lesBalles: ArrayList<Projectile>,lvl: Float) {
        super.move(lesAsteroides, lesBalles,lvl)
        for (a in lesAsteroides){
            a.gereBalle(this, a,lvl)
        }
    }

    override fun draw(canvas: Canvas?) {
        paint.color = Color.RED
        super.draw(canvas)
    }
}





