package com.example.saravjeet_sensors

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private  lateinit var lightSensor : Sensor
    val lightSensorEvent = "foundLightSensorEvent"
    private var lastTimeEventChange:Long = 0
    private var processTimeInterval:Long = 5000
    private var darkMode = false
    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

//        findViewById<TextView>(R.id.textView).text = if (!darkMode) "LightMode"

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    }


    override fun onSensorChanged(event: SensorEvent?) {
        // process sensor event

        // control the frequency of changes based on last processed time
        var currentTime = System.currentTimeMillis()
//        if (currentTime - lastTimeEventChange >= processTimeInterval) {
            lastTimeEventChange = currentTime

            var luminance = event?.values!![0]
            Log.i(lightSensorEvent, "Event found$luminance")



            if (luminance <= 5000 && !darkMode){
                findViewById<TextView>(R.id.textView).text = "Dark Mode"

            }else{
                findViewById<TextView>(R.id.textView).text = "Light Mode"
            }

//        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this, lightSensor)
    }

}