package org.firstinspires.ftc.teamcode.next.subsystems

import androidx.annotation.IntegerRes
import com.pedropathing.util.Timer
import com.qualcomm.hardware.rev.RevColorSensorV3
import com.qualcomm.robotcore.hardware.ColorSensor
import com.qualcomm.robotcore.hardware.Servo
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Artifact
import java.sql.Time
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object Sensor: Subsystem {
    lateinit var cS: RevColorSensorV3

    var timer: Timer = Timer()
    var artitimer: Timer = Timer()

    private const val INTAKE_TIME: Double = 0.22

    var currentArtifact:Artifact? = null

    override fun initialize() {
        cS = ActiveOpMode.hardwareMap.get(RevColorSensorV3::class.java, "cS")
        cS.enableLed(true)
        cS.gain = 8.0f
    }

    var ballPresent = false

    override fun periodic() {
        val ds = cS.getDistance(DistanceUnit.MM)

        if(ds > 45.0) {
            Light.Azure().schedule()
            timer.resetTimer()
        }

        if(timer.elapsedTime > 62 && ds<40.00) {
            Light.Green().schedule()
        }

        val art = getColor()
        val currentlySeeingBall = art != null

        if (currentlySeeingBall && !ballPresent && Transfer.currentBall <= 2) {
            Transfer.ballsHeld[Transfer.currentBall] = art
            Transfer.currentBall++
        }

        ballPresent = currentlySeeingBall
    }

    /**
     * @return An [Artifact] or null based on the color of the ball
     */
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