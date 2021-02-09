package com.idrok.customchart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val listValue = arrayListOf(10f, 30f, 25f, 44f, 12f, 10f, 30.2f)
//        val listText = arrayListOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
//
//        val data = BarChartData(listText, listValue, "km")
//        findViewById<CustomBarChart>(R.id.cbc).apply {
//            setData(data)
//            setChartType(BarChartType.Weekly)
//        }
//
        findViewById<SmoothLineChartEquallySpaced>(R.id.chart).apply {
            setData(
                    floatArrayOf(
                            15f,
                            21f,
                            9f,
                            21f,
                            25f,
                            35f,
                            24f
                    )
            )
        }
    }

}