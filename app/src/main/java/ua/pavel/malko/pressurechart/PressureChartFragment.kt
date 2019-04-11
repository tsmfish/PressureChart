package ua.pavel.malko.pressurechart

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries

class PressureChartFragment : Fragment() {
    companion object {
        var LOG_TAG = PressureChartFragment::class.java.simpleName
    }

    val dataSeries by lazy { LineGraphSeries<DataPoint>() }
    val graph by lazy {
        var graphView = view?.findViewById(R.id.graph) as GraphView
        graphView.addSeries(dataSeries)
        graphView.viewport.setScrollableY(true)
        graphView.viewport.setScalableY(true)
        graphView
    }
    val btStartStop by lazy { view?.findViewById(R.id.bt_start_stop) as Button }
    var time = 1.0
    private val sensorEventListener = object : android.hardware.SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onSensorChanged(event: SensorEvent?) {
            Log.d(LOG_TAG, "onSensorChanged: $event")
            Log.d(LOG_TAG, "value ${event?.values?.get(0)?.toDouble()}, time $time")
            dataSeries.appendData(DataPoint(time , event?.values?.get(0)?.toDouble() as Double), true, 100)
            time += 1.0
            Log.d(LOG_TAG, "series size ${dataSeries.getValues(Double.MIN_VALUE, Double.MAX_VALUE).asSequence().toList().size}")
        }
    }
    private val sensorManager by lazy { context?.getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val pressureSensor by lazy { sensorManager.getSensorList(Sensor.TYPE_PRESSURE)[0] }

    var state = State.PAUSED

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        return super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.fragmnet_pressure_chart, container, false)

        view.findViewById<Button>(R.id.bt_start_stop).setOnClickListener {
            when (state) {
                State.RUNNING -> {
                    sensorManager.unregisterListener(sensorEventListener)
                    state = State.PAUSED
                    btStartStop?.background = resources.getDrawable(android.R.color.holo_green_light)
                }
                State.PAUSED -> {
                    sensorManager.registerListener(
                        sensorEventListener,
                        pressureSensor,
                        500 * 1000
                    )
                    state = State.RUNNING
                    btStartStop?.background = resources.getDrawable(android.R.color.holo_red_light)
                }
            }
        }

        return view
    }


    enum class State {
        RUNNING, PAUSED
    }
}