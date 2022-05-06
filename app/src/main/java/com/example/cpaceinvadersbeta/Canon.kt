package com.example.cpaceinvadersbeta

import android.content.res.Resources
import android.graphics.*
import android.os.Build

class Canon(view: CanonView, resources: Resources, var shield : Boolean) {
    private val canonPaint = Paint()
    private val hublotPaint = Paint()
    private val bouclierPaint = Paint()


    val r = RectF(view.screenWidth+510   ,view.screenHeight+1550,view.screenWidth+580 , view.screenHeight+1700)
    private val cepeee = BitmapFactory.decodeResource(resources, R.drawable.cepeee)


    fun draw(canvas: Canvas?) {
        canonPaint.color = Color.LTGRAY
        hublotPaint.color = Color.BLUE

        if(shield){
            bouclierPaint.color = Color.BLUE
            canvas?.drawCircle(r.centerX(),r.centerY(),90f,bouclierPaint)
        }

        canvas?.drawOval(r, canonPaint) //fuselage du vaisseau
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas?.drawOval(r.left +20, r.top +20, r.right -20, r.top +70, hublotPaint)//hublot du vaisseau
            canvas?.drawArc(r.left -10,r.top + 100,r.right + 10,r.bottom+40,0f,
                    -180f,true,canonPaint)//ailerons du vaisseau
            canvas?.drawBitmap(cepeee,Rect(0,0,1000,1000),Rect((r.left +10).toInt(),
                    (r.top +80).toInt(), (r.right ).toInt(), (r.top +150).toInt()), hublotPaint)//logo cp

        }
    }
}

