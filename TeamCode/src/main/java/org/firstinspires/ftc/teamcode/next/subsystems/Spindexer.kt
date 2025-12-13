package org.firstinspires.ftc.teamcode.next.subsystems

import androidx.core.content.pm.ShortcutInfoCompatSaver.NoopImpl
import com.qualcomm.robotcore.hardware.Servo
import dev.nextftc.control.ControlSystem
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.LambdaCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.ServoEx
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Artifact
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Motif
import org.firstinspires.inspection.InspectionState
import kotlin.time.Duration.Companion.seconds

object Spindexer : Subsystem {

    // ---- SEQUENCING ----
    var targetMotif: Motif? = null
    var motifMode = false
    var sort = false
    var intaking = false

    var motif = arrayOf<Artifact?>(null, null, null)
    var motifShot: Int = 0
    var currentShot = arrayOf<Artifact?>(null, null, null)
    var ballsHeld = arrayOf<Artifact?>(null, null, null)

    val pos = arrayOf(0.13, 0.55, 0.99)
    var currentPos = 0 // Current Position Under Shoot

    enum class cmds { spin_pos, none }
    var currentCmd = cmds.none

    val servo: ServoEx = ServoEx("s0")
    val light: Servo = ActiveOpMode.hardwareMap.get(Servo::class.java, "light")

    private val greenLED = 0.5
    private val purpleLED = 0.7

    private var lastStoredColor: Artifact? = null

    @JvmField
    val servoCoeffs = PIDCoefficients(0.0, 0.0, 0.0)
    val servoPID: ControlSystem = controlSystem { servoCoeffs }

    init {
        ballsHeld = arrayOf(Artifact.GREEN, Artifact.PURPLE, Artifact.PURPLE)
    }

    override fun initialize() {}

    override fun periodic() {
        if (ActiveOpMode.opModeInInit) {
            targetMotif = Limelight.motif()
            if(motif[0] == null) {
                motif = when (targetMotif) {
                    Motif.GPP -> arrayOf(Artifact.GREEN, Artifact.PURPLE, Artifact.PURPLE)
                    Motif.PGP -> arrayOf(Artifact.PURPLE, Artifact.GREEN, Artifact.PURPLE)
                    else -> arrayOf(Artifact.PURPLE, Artifact.PURPLE, Artifact.GREEN)
                }
            }
        }

        if (sort) autoSort()
        if (intaking) intakeSort()

        updateLED()
        ActiveOpMode.telemetry.addData("Spindexer Pos", currentPos)
    }

    fun autoSort() {
        for(i: Int in 0 .. 2) {
            if(i != currentPos) {
                if(ballsHeld[i] == motif[motifShot]) {
                    currentPos = i
                    goToCurrent()
                    break
                }
            }

            if(i==2) {

            }
        }

        shootBall().schedule()
    }

    fun shootBall(): Command {
        return LambdaCommand("test")
    }

    fun intakeSort() {
        ballsHeld[currentPos] = Sensor.currentArtifact

        for(i: Int in 0 .. 2) {
            if(i != currentPos) {
                if(ballsHeld[i] == null) {
                    currentPos = i
                    goToCurrent()
                    break
                }
            }
        }
    }

    val spinTo0: Command = SequentialGroup( InstantCommand {
        currentPos = 0
        servo.position = pos[0]
    }, Delay(0.2.seconds))

    val spinTo1: Command = SequentialGroup( InstantCommand {
        currentPos = 1
        servo.position = pos[1]
    }, Delay(0.2.seconds))

    val spinTo2: Command = SequentialGroup( InstantCommand {
        currentPos = 2
        servo.position = pos[2]
    }, Delay(0.2.seconds))

    fun goToCurrent(): Command =
        when(currentPos) {
            0 -> spinTo0
            1 -> spinTo1
            2 -> spinTo2
            else -> error("uh oh")
        }

    fun spinRight(): Command =
        when(currentPos) {
            0 -> spinTo1
            1 -> spinTo2
            2 -> spinTo0
            else -> error("uh oh")
        }

    fun spinLeft(): Command =
        when(currentPos) {
            0 -> spinTo2
            1 -> spinTo0
            2 -> spinTo1
            else -> error("uh oh")
        }


    private fun updateLED() {
        light.position = when (ballsHeld[0]) {
            Artifact.GREEN -> greenLED
            Artifact.PURPLE -> purpleLED
            null -> 0.0
        }
    }
}