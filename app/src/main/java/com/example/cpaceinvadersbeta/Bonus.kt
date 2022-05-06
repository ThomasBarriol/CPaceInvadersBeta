package com.example.cpaceinvadersbeta

import android.content.res.Resources
import android.graphics.*

class Bonus(x: Float, y: Float, diametre: Float, lvl: Float, resources: Resources, var type: Int) : Balle(x, y, diametre, lvl)  {
    override var dy: Float = 10.0f
    var bonusOnScreen = true



    override fun draw(canvas: Canvas?) {
        if(type == 0){
            paint.color = Color.BLUE
        }
        else if(type==1){
            paint.color = Color.MAGENTA

        }

        super.draw(canvas)



    }
    fun gereBonus(c: Canon,bns:Bonus): Boolean {
        return RectF.intersects(c.r, bns.r)

    }

}