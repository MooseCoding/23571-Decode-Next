package org.firstinspires.ftc.teamcode.next.subsystems

import com.qualcomm.robotcore.hardware.Servo
import dev.nextftc.control.ControlSystem
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.LambdaCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.ServoEx
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Artifact
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Motif

object Spindexer : Subsystem {

    // ---- SEQUENCING ----
    var targetMotif: Motif? = null
    var motifMode = false
    var sort = false

    var currentMotifShot = arrayOf<Artifact?>(null, null, null)
    var ballsHeld = arrayOf<Artifact?>(null, null, null)

    // ---- HARDWARE ----
    val pos = arrayOf(0.13, 0.55, 0.99)
    var currentPos = 0

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
        }

        if (sort) autoSort()

        updateLED()
        ActiveOpMode.telemetry.addData("Spindexer Pos", currentPos)
    }

    // ---------------------------------------------------------
    // MOVEMENT — uses only currentPos
    // ---------------------------------------------------------
    fun goToPosition(): Command =
        InstantCommand {
            currentCmd = cmds.spin_pos
            servo.position = pos[currentPos]
            currentCmd = cmds.none
        }

    val spinRight: Command
        get() {
            val newPos = if (currentPos == 2) 0 else currentPos + 1
            return SequentialGroup(
                InstantCommand { currentPos = newPos },
                goToPosition()
            )
        }

    val spinLeft: Command
        get() {
            val newPos = if (currentPos == 0) 2 else currentPos - 1
            return SequentialGroup(
                InstantCommand { currentPos = newPos },
                goToPosition()
            )
        }

    // ---------------------------------------------------------
    // LED
    // ---------------------------------------------------------
    private fun updateLED() {
        light.position = when (ballsHeld[0]) {
            Artifact.GREEN -> greenLED
            Artifact.PURPLE -> purpleLED
            null -> 0.0
        }
    }

    // ---------------------------------------------------------
    // AUTO SORTING
    // ---------------------------------------------------------
    fun autoSort() {
        if (!motifMode || targetMotif == null || currentCmd != cmds.none) return
        if (targetMotif == Motif.NONE) return

        val targetSequence = when (targetMotif) {
            Motif.GPP -> arrayOf(Artifact.GREEN, Artifact.PURPLE, Artifact.PURPLE)
            Motif.PGP -> arrayOf(Artifact.PURPLE, Artifact.GREEN, Artifact.PURPLE)
            Motif.PPG -> arrayOf(Artifact.PURPLE, Artifact.PURPLE, Artifact.GREEN)
            else -> return
        }

        val completed = currentMotifShot.count { it != null }
        if (completed >= 3) {
            currentMotifShot = arrayOf(null, null, null)
            return
        }

        val needed = targetSequence[completed]

        // Already at intake with correct ball
        if (currentPos == 0 && ballsHeld[0] == needed) return

        // Search other slots
        for (i in 1..2) {
            if (ballsHeld[i] == needed) {
                if (currentPos != i) {
                    currentPos = i
                    goToPosition().schedule()
                } else {
                    currentPos = 0
                    goToPosition().schedule()
                }
                return
            }
        }

        // No needed ball → prepare intake slot
        if (currentPos != 0 && ballsHeld[0] == null) {
            currentPos = 0
            goToPosition().schedule()
        }
    }

    fun readyToShoot(): Boolean {
        if (currentPos != 0) return false
        if (!motifMode || targetMotif == null || targetMotif == Motif.NONE)
            return ballsHeld[0] != null

        val sequence = when (targetMotif) {
            Motif.GPP -> arrayOf(Artifact.GREEN, Artifact.PURPLE, Artifact.PURPLE)
            Motif.PGP -> arrayOf(Artifact.PURPLE, Artifact.GREEN, Artifact.PURPLE)
            Motif.PPG -> arrayOf(Artifact.PURPLE, Artifact.PURPLE, Artifact.GREEN)
            else -> return false
        }

        val completed = currentMotifShot.count { it != null }
        return ballsHeld[0] == sequence[completed]
    }

    fun markShotComplete() {
        val completed = currentMotifShot.count { it != null }

        if (motifMode && completed < 3) {
            currentMotifShot[completed] = ballsHeld[0]
        }

        ballsHeld[0] = null
        autoSort()
    }

    // ---------------------------------------------------------
    // INTAKE SORTING
    // ---------------------------------------------------------
    fun intakeSort(): Command =
        LambdaCommand("intake_sort")
            .setStart {
                val empty = ballsHeld.indexOfFirst { it == null }
                if (empty != -1 && empty != currentPos) {
                    currentPos = empty
                    goToPosition().schedule()
                }
                lastStoredColor = null
            }
            .setUpdate {
                if (currentCmd != cmds.none) return@setUpdate

                val color = Sensor.cC
                val dist = Sensor.cS.getDistance(DistanceUnit.MM)

                if (color != null && ballsHeld[currentPos] == null) {
                    if (lastStoredColor == null || lastStoredColor != color || dist < 20.0) {

                        ballsHeld[currentPos] = color
                        lastStoredColor = color

                        val nextEmpty = ballsHeld.indexOfFirst { it == null }

                        if (nextEmpty != -1 && nextEmpty != currentPos) {
                            currentPos = nextEmpty
                            goToPosition().schedule()
                        } else if (currentPos != 0) {
                            currentPos = 0
                            goToPosition().schedule()
                        }
                    }
                } else if (color == null && dist > 30.0) {
                    lastStoredColor = null
                }
            }
            .setIsDone { false }
            .setStop { lastStoredColor = null }
            .addRequirements(this)

    val stopIntakeSort: Command =
        LambdaCommand("stop_intake_sort")
            .setStart {
                if (currentPos != 0 && currentCmd == cmds.none) {
                    currentPos = 0
                    goToPosition().schedule()
                }
            }
            .setIsDone { currentPos == 0 && currentCmd == cmds.none }
            .addRequirements(this)
}