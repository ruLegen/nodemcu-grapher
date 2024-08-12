package com.embedded.grapher.components

import android.content.Context
import android.widget.TextView
import com.embedded.grapher.R
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.Utils

class LineChartMarker : MarkerView {
    private var tv: TextView

    constructor(context : Context) : super(context, R.layout.marker) {
        tv = findViewById<TextView>(R.id.marker_text_view)
    }
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        tv.setText( Utils.formatNumber(e?.getY() ?:0f , 2, true))
        super.refreshContent(e, highlight)
    }
}