package org.firstinspires.ftc.teamcode.next.subsystems

import androidx.annotation.IntegerRes
import com.acmerobotics.dashboard.config.Config
import com.pedropathing.util.Timer
import com.qualcomm.hardware.rev.RevColorSensorV3
import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.Servo
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Artifact
import kotlin.math.max

@Config
object Sensor: Subsystem {
    val cS: RevColorSensorV3 = ActiveOpMode.hardwareMap.get(RevColorSensorV3::class.java, "cs")

    var timer: Timer = Timer()
    val framesReq = 5
    var frames = 0

    var currentArtifact:Artifact? = null

    override fun initialize() {
        cS.enableLed(true)
        cS.gain = 8.0f
    }
    override fun periodic() {
        val ds = cS.getDistance(DistanceUnit.MM)

        if(ds > 40.0) {
            currentArtifact = null
        }

        if(frames > framesReq && ds<25.0 && currentArtifact != null) {
            currentArtifact = getColor()

            frames = 0
        }

        ActiveOpMode.telemetry.run {
            addData("green", cS.normalizedColors.green)
            addData("red", cS.normalizedColors.red)
            addData("blue", cS.normalizedColors.blue)
            addData("color", currentArtifact)
            addData("frames", frames)
            addData("loop time", timer.elapsedTime)
            addData("Distance", cS.getDistance(DistanceUnit.MM))
        }

        timer.resetTimer()

        frames++
    }

    fun getColor(): Artifact? {
        val c = cS.normalizedColors
        val d = cS.getDistance(DistanceUnit.MM)

        if (d > 30.0) {
            return null
        }

        val r = c.red/max(c.alpha,1.0f)
        val g = c.green/ max(c.alpha, 1.0f)
        val b = c.blue/ max(c.alpha, 1.0f)

        if ((g/r) > 2.0 && g>b) {
            return Artifact.GREEN
        }
        else if ((b/g) > 1.3 && b>r) {
            return Artifact.PURPLE
        }
        return null
    }
}