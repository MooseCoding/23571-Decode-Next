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
import java.sql.Time
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Config
object Sensor: Subsystem {
    lateinit var cS: RevColorSensorV3

    var timer: Timer = Timer()

    private const val INTAKE_TIME: Double = 0.2

    var currentArtifact:Artifact? = null

    override fun initialize() {
        cS = ActiveOpMode.hardwareMap.get(RevColorSensorV3::class.java, "cS")
        cS.enableLed(true)
        cS.gain = 8.0f
    }

    override fun periodic() {
        val ds = cS.getDistance(DistanceUnit.MM)

        if(ds > 40.0) {
            currentArtifact = null
        }

        if(timer.elapsedTime > INTAKE_TIME && ds<25.0 && currentArtifact == null && Transfer.currentBall != 3) {
            currentArtifact = getColor()
            Transfer.ballsHeld[Transfer.currentBall] = currentArtifact
            Transfer.currentBall++

            if(Transfer.currentBall == 1) {
                Transfer.intakeBall().schedule()
            }

            if(Transfer.currentBall == 3) {
                Light.Green().schedule()
                // ActiveOpMode.gamepad1.rumble(1.0, 1.0, 10)
            }

            currentArtifact = null
            timer.resetTimer()
        }

        ActiveOpMode.telemetry.run {
            // addData("Current Color", Transfer.ballsHeld[Transfer.currentBall])
            addData("cSD", cS.getDistance(DistanceUnit.MM))
            addData("Current Ball", Transfer.currentBall)
            addData("Timer", timer.elapsedTime)
        }
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