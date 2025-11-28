package org.firstinspires.ftc.teamcode.next.subsystems

import dev.nextftc.control.ControlSystem
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.FeedbackElement
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.LambdaCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.teamcode.next.subsystems.data.Artifact
import org.firstinspires.ftc.teamcode.next.subsystems.data.Motif
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.util.Camera.incoming
import kotlin.math.PI
import kotlin.time.Duration.Companion.seconds

object Spindexer: Subsystem {
    var targetMotif: Motif? = null
    var motifMode:Boolean = false // Determine if the spindexer should sort for a motif
    var currentMotifShot:Array<Artifact?> = arrayOf(null, null, null)
    var ballsHeld:Array<Artifact?> = arrayOf(null, null, null) // First position is intake, third is turret/transfer, second is the other
    val pos: Array<Double> = arrayOf(0.0, 0.0, 0.0)

    var currentAngle: Double = 0.0
    var prevAngle: Double = 0.0

    val servo:CRFServo = CRFServo(
        "ANALOG",
        "SERVO",
        0.01
    )

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
        currentAngle()

        if (ActiveOpMode.opModeInInit) {
            targetMotif = Limelight.motif()
        }

        if(ActiveOpMode.isStarted) {
            if(motifMode) {
                sortMotif()
            }
            else {
                fastShuffle()
            }
        }
    }

    fun currentAngle() {
        val pos = servo.currentPosition
        var delta = pos - prevAngle

        if (delta > Math.PI) {
            delta-=2* PI
        }
        else if (delta < -Math.PI) {
            delta+=2* PI
        }

        currentAngle += delta
        prevAngle = pos
    }

    val addIncomingBall = LambdaCommand()
        .setStart {

        }
    .setUpdate {
        // If shooter slot (2) is empty always put ball there
        if (ballsHeld[2] == null) {
            spin(2).schedule()
            ballsHeld[2] = incoming
        }

        // Otherwise try position 1
        if (ballsHeld[1] == null) {
            spin(1).schedule()
            ballsHeld[1] = incoming
        }

        // Finally try position 0
        if (ballsHeld[0] == null) {
            spin(0).schedule()
            ballsHeld[0] = incoming
        }
        // If all three are full, optionally do nothing or reject ball
    }
    .setIsDone {
        incoming == null
    }


    fun sortMotif() {
        if (!motifMode || targetMotif == null) return

        val ballsAvailable = ballsHeld.count { it != null }
        if (ballsAvailable == 0) return

        // Use your currentMotifShot array to store what we intend to shoot
        when (targetMotif) {
            Motif.GPP -> {
                currentMotifShot = arrayOf(
                    Artifact.GREEN,
                    Artifact.PURPLE,
                    Artifact.PURPLE
                )
            }

            Motif.PGP -> {
                currentMotifShot = arrayOf(
                    Artifact.PURPLE,
                    Artifact.GREEN,
                    Artifact.PURPLE
                )
            }

            Motif.PPG -> {
                currentMotifShot = arrayOf(
                    Artifact.PURPLE,
                    Artifact.PURPLE,
                    Artifact.GREEN
                )
            }

            null,Motif.NONE -> {
                fastShuffle()
                return
            }
        }

        // Now actually shoot up to the number of balls we have
        var shotsTaken = 0

        for (i in currentMotifShot.indices) {
            if (shotsTaken >= ballsAvailable) break

            val required = currentMotifShot[i] ?: continue
            shootArtifact(required)
            shotsTaken++
        }
    }


    private fun shootArtifact(required: Artifact) {
        val index = ballsHeld.indexOf(required)

        if (index != -1) {
            spin(index)
            shoot.schedule()
            ballsHeld[index] = null
        }
    }

    fun spin(target:Int): Command {
        return LambdaCommand()
            .setStart {
                servoPID.goal = KineticState(pos[target], 0.0)
            }
            .setUpdate {
                servo.power = servoPID.calculate(KineticState(currentAngle, 0.0))
                currentAngle()
            }
            .setIsDone {
                currentAngle == Pos[target]
            }
            .named("Spin")
            .addRequirements(Spindexer)
    }

    val shoot = LambdaCommand()
        .setStart {

        }
        .setUpdate {

        }
        .setIsDone {
            true
        }
        .setStop {

        }
        .named("Shoot")
        .addRequirements(Flywheels)


    fun fastShuffle() {
        // shoot until all 3 null
        for (i in 0 until ballsHeld.size) {
            val index = ballsHeld.indexOfFirst { it != null }
            if (index == -1) return  // no more balls

            spin(index)
            shoot()
            ballsHeld[index] = null
        }
    }

}