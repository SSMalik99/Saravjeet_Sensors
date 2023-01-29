package com.example.saravjeet_sensors

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private  lateinit var lightSensor : Sensor
    val lightSensorEvent = "foundLightSensorEvent"
    private var lastTimeEventChange:Long = 0
    private var processTimeInterval:Long = 5000
    private lateinit var rotationVector:Sensor
    val rotationVectorEvent = "foundRotation"
    private var darkMode = false
    private val rMatrix = FloatArray(9)
    private val tempRMatrix = FloatArray(9)

//    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

//        findViewById<TextView>(R.id.textView).text = if (!darkMode) "LightMode"

        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

//        findViewById<TextView>(R.id.textView).setTextColor(R.color.white)
//        findViewById<ConstraintLayout>(R.id.rootView).setBackgroundColor(R.color.black)

    }



    private fun convertToDegrees(vector: FloatArray) {
        for (i in vector.indices) {
            vector[i] = Math.round(Math.toDegrees(vector[i].toDouble())).toFloat()
        }
    }
    /**
     * @param result the array of Euler angles in the order: yaw, roll, pitch
     * @param rVector the rotation vector
     */
    fun calculateAngles(result: FloatArray?, rVector: FloatArray?) {
        //caculate temp rotation matrix from rotation vector first
        SensorManager.getRotationMatrixFromVector(tempRMatrix, rVector)
        //translate rotation matrix according to the new orientation of the device
//        SensorManager.remapCoordinateSystem(tempRMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Y, rMatrix)
        SensorManager.remapCoordinateSystem(tempRMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, rMatrix)

        //calculate Euler angles now
        SensorManager.getOrientation(rMatrix, result)

        //Now we can convert it to degrees
        if (result != null) {
            convertToDegrees(result)
        }
    }


    override fun onSensorChanged(event: SensorEvent?) {
        // process sensor event

        val sensor  = event?.sensor

        if (sensor?.type == Sensor.TYPE_LIGHT){
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
        }else{
            val x = event?.values?.get(0)
            val y = event?.values?.get(1)
            val z = event?.values?.get(2)

//            Log.i("Rotation az", "x=$x y = $y z = $z")
            val rvector = FloatArray(9)
            rvector[0] = x!!
            rvector[1] = y!!
            rvector[2] = z!!

            val result = FloatArray(3)

            calculateAngles(result, rvector)
            val textView:TextView = findViewById<TextView>(R.id.textView)
            var layoutParams : ConstraintLayout.LayoutParams = textView.layoutParams as ConstraintLayout.LayoutParams

            val horizontalBias = (result[0] + 90) / 180
            val verticalBias = (result[1] + 90) / 180
            Log.i("value of bial", "$horizontalBias,,,,,$verticalBias")

            layoutParams.horizontalBias = horizontalBias
            layoutParams.verticalBias = verticalBias
            textView.layoutParams = layoutParams
//            layoutParams. = verticalBias


        }


    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        // unregister one by one
//        sensorManager.unregisterListener(this, lightSensor)
//        sensorManager.unregisterListener(this, rotationVector)

        // unregister all at once
        sensorManager.unregisterListener(this)
    }

}