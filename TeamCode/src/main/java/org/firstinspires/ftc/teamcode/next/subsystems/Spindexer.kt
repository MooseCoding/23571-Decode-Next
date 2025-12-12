package org.firstinspires.ftc.teamcode.next.subsystems

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
import kotlin.time.Duration.Companion.seconds

object Spindexer: Subsystem {
    var targetMotif: Motif? = null
    var motifMode:Boolean = false // Determine if the spindexer should sort for a motif
    var currentMotifShot:Array<Artifact?> = arrayOf(null, null, null)
    var ballsHeld:Array<Artifact?> = arrayOf(null, null, null) // First position is intake, third is turret/transfer, second is the other
    val pos: Array<Double> = arrayOf(0.13, 0.55, 0.99)
    var sort: Boolean = false

    private var lastStoredColor: Artifact? = null // Track what we last stored

    val light:Servo = ActiveOpMode.hardwareMap.get(Servo::class.java, "light")

    private val green = 0.5 // Value for green
    private val purple = 0.7 // Value for purple

    enum class cmds {
        spin_pos,
        none
    }

    var currentCmd: cmds = cmds.none

    var currentPos = 0

    val servo:ServoEx = ServoEx("s0")

    @JvmField val servoCoeffs: PIDCoefficients = PIDCoefficients(0.0,0.0,0.0)
    val servoPID: ControlSystem = controlSystem {
        servoCoeffs
    }

    init {
        ballsHeld = arrayOf(Artifact.GREEN, Artifact.PURPLE, Artifact.PURPLE)
    }

    override fun initialize() {

    }

    override fun periodic() {
        if (ActiveOpMode.opModeInInit) {
            targetMotif = Limelight.motif()
        }

        if(sort) {
            autoSort()
        }

        updateLED()

        ActiveOpMode.telemetry.addData("currentPOs", currentPos)
    }

    fun topherSort() {

    }

    private fun updateLED() {
        light.position = when(ballsHeld[0]) {
            Artifact.GREEN -> green
            Artifact.PURPLE -> purple
            null -> 0.0
        }
    }

    private fun goToPosition(index:Int):Command = SequentialGroup(InstantCommand {
        currentCmd = cmds.spin_pos
        currentPos = index
        servo.position = pos[currentPos]
    }, InstantCommand { currentCmd = cmds.none })


    val spinRight: SequentialGroup = SequentialGroup(InstantCommand {
        if (currentPos == 2) {
            currentPos = 0
        } else {
            currentPos += 1
        }
    }, goToPosition(currentPos)
    )

    val spinLeft: SequentialGroup = SequentialGroup(InstantCommand{
        if (currentPos == 0) {
            currentPos = 2
        } else {
            currentPos -= 1
        }
    }, goToPosition(currentPos))

    fun autoSort() {
        // Don't sort if we're in the middle of a command or not in motif mode
        if (currentCmd != cmds.none || !motifMode || targetMotif == null || targetMotif == Motif.NONE) {
            return
        }

        // Get the target sequence based on motif
        val targetSequence = when(targetMotif) {
            Motif.GPP -> arrayOf(Artifact.GREEN, Artifact.PURPLE, Artifact.PURPLE)
            Motif.PGP -> arrayOf(Artifact.PURPLE, Artifact.GREEN, Artifact.PURPLE)
            Motif.PPG -> arrayOf(Artifact.PURPLE, Artifact.PURPLE, Artifact.GREEN)
            else -> return
        }

        // Determine next ball needed in the shooting sequence
        val shotsCompleted = currentMotifShot.count { it != null }
        if (shotsCompleted >= 3) {
            // Sequence complete, reset for next round
            currentMotifShot = arrayOf(null, null, null)
            return
        }

        val nextNeededColor = targetSequence[shotsCompleted]

        // Check if position 0 already has the correct ball
        if (ballsHeld[0] == nextNeededColor && currentPos == 0) {
            // Already ready to shoot
            return
        }

        // Search other buckets for the next needed ball
        for (i in 1..2) {
            if (ballsHeld[i] == nextNeededColor) {
                // Found the ball we need, rotate to it then to position 0
                if (currentPos != i) {
                    goToPosition(i).schedule()
                } else {
                    // We're at the bucket with the needed ball, now move to shooting position
                    goToPosition(0).schedule()
                }
                return
            }
        }

        // Don't have the needed ball in storage, make sure we're at position 0 to receive intake
        if (currentPos != 0 && ballsHeld[0] == null) {
            goToPosition(0).schedule()
        }
    }

    // Check if we're ready to shoot (correct ball is at position 0)
    fun readyToShoot(): Boolean {
        if (currentPos != 0) {
            return false
        }

        if (!motifMode || targetMotif == null || targetMotif == Motif.NONE) {
            return ballsHeld[0] != null
        }

        val targetSequence = when(targetMotif) {
            Motif.GPP -> arrayOf(Artifact.GREEN, Artifact.PURPLE, Artifact.PURPLE)
            Motif.PGP -> arrayOf(Artifact.PURPLE, Artifact.GREEN, Artifact.PURPLE)
            Motif.PPG -> arrayOf(Artifact.PURPLE, Artifact.PURPLE, Artifact.GREEN)
            else -> return false
        }

        val shotsCompleted = currentMotifShot.count { it != null }
        if (shotsCompleted >= 3) return false

        val nextNeededColor = targetSequence[shotsCompleted]
        return ballsHeld[0] == nextNeededColor
    }

    // Call this after each successful shot to update state
    fun markShotComplete() {
        val shotsCompleted = currentMotifShot.count { it != null }

        if (shotsCompleted < 3 && motifMode) {
            val targetSequence = when(targetMotif) {
                Motif.GPP -> arrayOf(Artifact.GREEN, Artifact.PURPLE, Artifact.PURPLE)
                Motif.PGP -> arrayOf(Artifact.PURPLE, Artifact.GREEN, Artifact.PURPLE)
                Motif.PPG -> arrayOf(Artifact.PURPLE, Artifact.PURPLE, Artifact.GREEN)
                else -> return
            }
            currentMotifShot[shotsCompleted] = ballsHeld[0]
        }

        ballsHeld[0] = null

        // 👇 This makes the whole system work.
        autoSort()
    }


    fun intakeSort(): Command = LambdaCommand("intake_sort")
        .setStart {
            // Find first empty bucket and rotate to it
            val firstEmpty = ballsHeld.indexOfFirst { it == null }
            if (firstEmpty != -1 && currentPos != firstEmpty) {
                goToPosition(firstEmpty).schedule()
            }
            lastStoredColor = null // Reset tracking
        }
        .setUpdate {
            // Wait until we're not moving before checking sensor
            if (currentCmd != cmds.none) {
                return@setUpdate
            }

            val detectedColor = Sensor.cC

            // Check if we have a valid detection and haven't stored this ball yet
            if (detectedColor != null && ballsHeld[currentPos] == null) {
                // Check if this is a new ball (different from what we just stored)
                // OR if we haven't stored anything yet
                if (lastStoredColor == null ||
                    (lastStoredColor != detectedColor) ||
                    (Sensor.cS.getDistance(DistanceUnit.MM) < 20.0)) { // Close proximity = definitely a ball

                    // Store the ball
                    ballsHeld[currentPos] = detectedColor
                    lastStoredColor = detectedColor

                    // Wait a moment for ball to settle
                     // waiter.wait(0.1)

                    // Find next empty bucket and rotate to it
                    val nextEmpty = ballsHeld.indexOfFirst { it == null }
                    if (nextEmpty != -1 && nextEmpty != currentPos) {
                        goToPosition(nextEmpty).schedule()
                    } else {
                        // All buckets full, move to position 0
                        if (currentPos != 0) {
                            goToPosition(0).schedule()
                        }
                    }
                }
            } else if (detectedColor == null && Sensor.cS.getDistance(DistanceUnit.MM) > 30.0) {
                // No ball detected and nothing close, ready for next ball
                lastStoredColor = null
            }
        }
        .setIsDone { false } // Runs continuously until cancelled
        .setStop {
            lastStoredColor = null // Reset on stop
        }
        .addRequirements(this)

    val stopIntakeSort: Command = LambdaCommand("stop_intake_sort")
        .setStart {
            // Move to position 0 when done intaking
            if (currentPos != 0 && currentCmd == cmds.none) {
                goToPosition(0).schedule()
            }
        }
        .setIsDone { currentPos == 0 && currentCmd == cmds.none }
        .addRequirements(this)


}