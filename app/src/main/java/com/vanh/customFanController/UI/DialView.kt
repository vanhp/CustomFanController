package com.vanh.customFanController.UI

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.vanh.customFanController.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private enum class FanSpeed(val label:Int){
    OFF(R.string.fan_off),
    LOW(R.string.fan_low),
    MEDIUM(R.string.fan_medium),
    HIGH(R.string.fan_high);

    fun next() = when(this){
        OFF -> LOW
        LOW -> MEDIUM
        MEDIUM -> HIGH
        HIGH -> OFF
    }

}
private const val RADIUS_OFFSET_LABEL = 30
private const val RADIUS_OFFSET_INDICATOR = -35

// tell compiler to generate overloads for this function that substitute default parameter values.
class DialView @JvmOverloads constructor(context:Context, attrs: AttributeSet? = null,defStyleAttr:Int = 0)
                                                : View(context, attrs,defStyleAttr) {
    private var radius = 0.0f
    private var fanSpeed = FanSpeed.OFF
    private var pointPosition = PointF( 0.0f, 0.0f)

    private var fanSpeedLowColor = 0
    private var fanSpeedMediumColor = 0
    private var fanSpeedHighColor = 0
init {
    isClickable = true
    context.withStyledAttributes(attrs,R.styleable.DialView){
        fanSpeedLowColor = getColor(R.styleable.DialView_fanColor1,0)
        fanSpeedMediumColor = getColor(R.styleable.DialView_fanColor2,0)
        fanSpeedHighColor = getColor(R.styleable.DialView_fanColor3,0)
    }

}

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create("",Typeface.BOLD)
    }

    override fun onSizeChanged(width: Int, height: Int, oldwidth: Int, oldheight: Int) {
        super.onSizeChanged(width, height, oldwidth, oldheight)

        radius = (min(width,height) / 2.0 * 0.8).toFloat()

    }
    private fun PointF.computeXYForSpeed(pos:FanSpeed,radius: Float){
        val startAngle = Math.PI * (9/8.0) //in radian
        val angle = startAngle + pos.ordinal * (Math.PI/4)
        x = (radius * cos(angle)).toFloat() + width /2
        y = (radius * sin(angle)).toFloat() + height /2

    }
    // override this for custom view instead of onClick
    // leave the onclick for some tweaking or let user handle them
    override fun performClick(): Boolean {
        //must happen first, which enables accessibility events as well as calls onClickListener().
        if (super.performClick()) return true

        fanSpeed = fanSpeed.next()
        contentDescription = resources.getString(fanSpeed.label)
        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = when(fanSpeed){
            FanSpeed.OFF -> Color.GRAY
            FanSpeed.LOW -> fanSpeedLowColor
            FanSpeed.MEDIUM -> fanSpeedMediumColor
            FanSpeed.HIGH -> fanSpeedHighColor
        } as Int
       // paint.color = if(fanSpeed == FanSpeed.OFF) Color.GRAY else Color.GREEN
        // draw dial width,height are the View properties
        canvas.drawCircle((width/2).toFloat(),(height/2).toFloat(),radius,paint)

        // drwa the inner circle for the indicator
        val markerRadius = radius + RADIUS_OFFSET_INDICATOR
        pointPosition.computeXYForSpeed(fanSpeed,markerRadius)
        paint.color = Color.BLACK
        canvas.drawCircle(pointPosition.x,pointPosition.y,radius/12,paint)

        // draw the label on dial
        val labelRadius = radius + RADIUS_OFFSET_LABEL
        for (i in FanSpeed.values()){
            pointPosition.computeXYForSpeed(i,labelRadius)
            val label = resources.getString(i.label)
            canvas.drawText(label,pointPosition.x,pointPosition.y,paint)
        }


    }

}
