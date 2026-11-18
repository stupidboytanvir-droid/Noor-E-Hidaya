package com.example.data.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

data class QiblaState(
    val deviceAzimuthDegrees: Float = 0f,
    val qiblaBearingDegrees: Float = 278f, // Makkah from Dhaka approx 278°
    val relativeAngleDegrees: Float = 0f,
    val isAligned: Boolean = false,
    val sensorAccuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
    val accuracyDescription: String = "কম্পাস সেন্সর অত্যন্ত নিখুঁত (High Accuracy)",
    val accuracyDescriptionEnglish: String = "Compass sensor is highly accurate",
    val isCalibrated: Boolean = true,
    val distanceToKaabaKm: Int = 5150,
    val qiblaDirectionName: String = "পশ্চিম-উত্তর-পশ্চিম (WNW)",
    val qiblaDirectionNameEnglish: String = "West-Northwest (WNW)",
    val headingDirectionName: String = "উত্তর (N)",
    val headingDirectionNameEnglish: String = "North (N)"
) {
    fun localizedAccuracyDescription(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, accuracyDescription, accuracyDescriptionEnglish)

    fun localizedQiblaDirectionName(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, qiblaDirectionName, qiblaDirectionNameEnglish)

    fun localizedHeadingDirectionName(lang: String): String =
        com.example.ui.theme.LocalizationRegistry.translate(lang, headingDirectionName, headingDirectionNameEnglish)
}

/**
 * Astronomical-grade Qibla Compass Manager.
 * Features smoothed azimuth dampening, live magnetometer accuracy verification,
 * Kaaba geodetic calculations, and haptic target-lock feedback.
 */
class QiblaCompassManager(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vm?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private val _qiblaState = MutableStateFlow(QiblaState())
    val qiblaState: StateFlow<QiblaState> = _qiblaState.asStateFlow()

    private var gravityValues = FloatArray(3)
    private var geomagneticValues = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false
    private var lastAlignedTime = 0L

    // Azimuth smoothing (exponential moving average)
    private var smoothedAzimuth = 0f
    private var isFirstReading = true

    // Kaaba Holy Sanctuary coordinates (Makkah al-Mukarramah)
    private val kaabaLat = 21.4225
    private val kaabaLng = 39.8262

    // Default coordinates: Dhaka, Bangladesh
    private var userLat = 23.8103
    private var userLng = 90.4125

    init {
        updateQiblaBearingAndDistance()
    }

    fun setLocation(lat: Double, lng: Double) {
        userLat = lat
        userLng = lng
        updateQiblaBearingAndDistance()
    }

    private fun updateQiblaBearingAndDistance() {
        // Geodetic Forward Azimuth Calculation to Kaaba
        val lat1 = Math.toRadians(userLat)
        val lon1 = Math.toRadians(userLng)
        val lat2 = Math.toRadians(kaabaLat)
        val lon2 = Math.toRadians(kaabaLng)

        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        var brng = Math.toDegrees(atan2(y, x))
        brng = (brng + 360.0) % 360.0

        // Haversine formula for Great-Circle distance to Kaaba
        val earthRadiusKm = 6371.0
        val dLat = lat2 - lat1
        val a = sin(dLat / 2.0).pow(2) + cos(lat1) * cos(lat2) * sin(dLon / 2.0).pow(2)
        val c = 2.0 * atan2(sqrt(a), sqrt(1.0 - a))
        val distanceKm = (earthRadiusKm * c).roundToInt()

        val currentAzimuth = _qiblaState.value.deviceAzimuthDegrees
        val relative = (brng.toFloat() - currentAzimuth + 360f) % 360f
        val isAligned = abs(relative) < 3.5f || abs(relative - 360f) < 3.5f

        _qiblaState.value = _qiblaState.value.copy(
            qiblaBearingDegrees = brng.toFloat(),
            relativeAngleDegrees = relative,
            isAligned = isAligned,
            distanceToKaabaKm = distanceKm,
            qiblaDirectionName = getDirectionLabelBengali(brng.toFloat()),
            qiblaDirectionNameEnglish = getDirectionLabelEnglish(brng.toFloat())
        )
    }

    fun startListening() {
        isFirstReading = true
        if (rotationSensor != null) {
            sensorManager.registerListener(this, rotationSensor, SensorManager.SENSOR_DELAY_GAME)
        } else {
            if (accelerometer != null) {
                sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
            }
            if (magnetometer != null) {
                sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_GAME)
            }
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        var rawAzimuth = 0f

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            val rotationMatrix = FloatArray(9)
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            val orientation = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientation)
            rawAzimuth = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, gravityValues, 0, 3)
            hasGravity = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, geomagneticValues, 0, 3)
            hasGeomagnetic = true
        }

        if (event.sensor.type != Sensor.TYPE_ROTATION_VECTOR && hasGravity && hasGeomagnetic) {
            val r = FloatArray(9)
            val i = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, i, gravityValues, geomagneticValues)) {
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                rawAzimuth = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360f) % 360f
            }
        }

        // Apply smooth exponential dampening to avoid magnetic jitter
        val azimuthDegrees = smoothAzimuthAngle(rawAzimuth)

        val bearing = _qiblaState.value.qiblaBearingDegrees
        val relative = (bearing - azimuthDegrees + 360f) % 360f
        val isAligned = abs(relative) < 3.5f || abs(relative - 360f) < 3.5f

        // Target Lock Haptics
        if (isAligned && !_qiblaState.value.isAligned && System.currentTimeMillis() - lastAlignedTime > 1200) {
            lastAlignedTime = System.currentTimeMillis()
            triggerTargetLockHaptic()
        }

        _qiblaState.value = _qiblaState.value.copy(
            deviceAzimuthDegrees = azimuthDegrees,
            relativeAngleDegrees = relative,
            isAligned = isAligned,
            headingDirectionName = getDirectionLabelBengali(azimuthDegrees),
            headingDirectionNameEnglish = getDirectionLabelEnglish(azimuthDegrees)
        )
    }

    private fun smoothAzimuthAngle(target: Float): Float {
        if (isFirstReading) {
            smoothedAzimuth = target
            isFirstReading = false
            return smoothedAzimuth
        }
        var diff = target - smoothedAzimuth
        while (diff < -180f) diff += 360f
        while (diff >= 180f) diff -= 360f
        smoothedAzimuth = (smoothedAzimuth + diff * 0.28f + 360f) % 360f
        return smoothedAzimuth
    }

    private fun triggerTargetLockHaptic() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(45)
            }
        } catch (_: Exception) {}
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        val (descBn, descEn, isCalibrated) = when (accuracy) {
            SensorManager.SENSOR_STATUS_ACCURACY_HIGH -> Triple(
                "কম্পাস সেন্সর নিখুঁত ও নির্ভরযোগ্য (High Accuracy)",
                "Compass sensor is accurate and reliable (High Accuracy)",
                true
            )
            SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM -> Triple(
                "কম্পাস সেন্সর স্বাভাবিক (Medium Accuracy)",
                "Compass sensor is normal (Medium Accuracy)",
                true
            )
            SensorManager.SENSOR_STATUS_ACCURACY_LOW -> Triple(
                "নিম্ন নির্ভুলতা - ফোনটি ৮-আকৃতিতে ঘুরিয়ে নিন",
                "Low accuracy - move phone in a figure-8 motion",
                false
            )
            SensorManager.SENSOR_STATUS_UNRELIABLE -> Triple(
                "চৌম্বকীয় বিভ্রান্তি শনাক্ত - ৮-আকৃতিতে ক্যালিব্রেট করুন",
                "Magnetic interference detected - calibrate in a figure-8",
                false
            )
            else -> Triple(
                "কম্পাস সেন্সর সক্রিয়",
                "Compass sensor active",
                true
            )
        }

        _qiblaState.value = _qiblaState.value.copy(
            sensorAccuracy = accuracy,
            accuracyDescription = descBn,
            accuracyDescriptionEnglish = descEn,
            isCalibrated = isCalibrated
        )
    }

    private fun getDirectionLabelBengali(degrees: Float): String {
        val d = (degrees % 360f + 360f) % 360f
        return when {
            d >= 348.75f || d < 11.25f -> "উত্তর (N)"
            d in 11.25f..33.75f -> "উত্তর-উত্তর-পূর্ব (NNE)"
            d in 33.75f..56.25f -> "উত্তর-পূর্ব (NE)"
            d in 56.25f..78.75f -> "পূর্ব-উত্তর-পূর্ব (ENE)"
            d in 78.75f..101.25f -> "পূর্ব (E)"
            d in 101.25f..123.75f -> "পূর্ব-দক্ষিণ-পূর্ব (ESE)"
            d in 123.75f..146.25f -> "দক্ষিণ-পূর্ব (SE)"
            d in 146.25f..168.75f -> "দক্ষিণ-দক্ষিণ-পূর্ব (SSE)"
            d in 168.75f..191.25f -> "দক্ষিণ (S)"
            d in 191.25f..213.75f -> "দক্ষিণ-দক্ষিণ-পশ্চিম (SSW)"
            d in 213.75f..236.25f -> "দক্ষিণ-পশ্চিম (SW)"
            d in 236.25f..258.75f -> "পশ্চিম-দক্ষিণ-পশ্চিম (WSW)"
            d in 258.75f..281.25f -> "পশ্চিম (W)"
            d in 281.25f..303.75f -> "পশ্চিম-উত্তর-পশ্চিম (WNW)"
            d in 303.75f..326.25f -> "উত্তর-পশ্চিম (NW)"
            else -> "উত্তর-উত্তর-পশ্চিম (NNW)"
        }
    }

    private fun getDirectionLabelEnglish(degrees: Float): String {
        val d = (degrees % 360f + 360f) % 360f
        return when {
            d >= 348.75f || d < 11.25f -> "North (N)"
            d in 11.25f..33.75f -> "North-Northeast (NNE)"
            d in 33.75f..56.25f -> "Northeast (NE)"
            d in 56.25f..78.75f -> "East-Northeast (ENE)"
            d in 78.75f..101.25f -> "East (E)"
            d in 101.25f..123.75f -> "East-Southeast (ESE)"
            d in 123.75f..146.25f -> "Southeast (SE)"
            d in 146.25f..168.75f -> "South-Southeast (SSE)"
            d in 168.75f..191.25f -> "South (S)"
            d in 191.25f..213.75f -> "South-Southwest (SSW)"
            d in 213.75f..236.25f -> "Southwest (SW)"
            d in 236.25f..258.75f -> "West-Southwest (WSW)"
            d in 258.75f..281.25f -> "West (W)"
            d in 281.25f..303.75f -> "West-Northwest (WNW)"
            d in 303.75f..326.25f -> "Northwest (NW)"
            else -> "North-Northwest (NNW)"
        }
    }
}
