package cn.jingzhuan.tableview.demo.elements

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.Gravity
import cn.jingzhuan.tableview.demo.dp
import cn.jingzhuan.tableview.element.RowShareElements
import cn.jingzhuan.tableview.element.TextColumn

class SimpleHeaderColumn(val source: RowData) : TextColumn() {

    init {
        gravity = Gravity.CENTER
        paddingRight = 20
    }

    private val backgroundPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        style = Paint.Style.FILL
        color = Color.parseColor("#D4D4D4")
    }

    override fun getText(context: Context): CharSequence? {
        return source.title
    }

    override fun visible(): Boolean {
        return true
    }

    override fun draw(context: Context, canvas: Canvas, rowShareElements: RowShareElements) {
        canvas.drawRect(
            columnLeft.toFloat(),
            columnTop.toFloat(),
            columnRight.toFloat(),
            columnBottom.toFloat(),
            backgroundPaint
        )
        super.draw(context, canvas, rowShareElements)
    }

}