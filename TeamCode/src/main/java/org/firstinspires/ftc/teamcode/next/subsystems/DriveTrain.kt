package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.follower.Follower
import com.pedropathing.follower.FollowerConstants
import com.pedropathing.ftc.drivetrains.Mecanum
import com.pedropathing.ftc.drivetrains.MecanumConstants
import com.pedropathing.ftc.localization.constants.PinpointConstants
import com.pedropathing.ftc.localization.localizers.PinpointLocalizer
import com.pedropathing.geometry.Pose
import com.pedropathing.localization.Localizer
import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.LambdaCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.ftc.Gamepads
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower;
import org.firstinspires.ftc.teamcode.next.subsystems.data.Alliance

@Configurable
object DriveTrain: Subsystem {
     val fL = MotorEx("frontLeft")
     val fR = MotorEx("frontRight").reversed()
     val bL = MotorEx("backLeft")
     val bR = MotorEx("backRight").reversed()

    @JvmField
    var alliance = Alliance.RED


    @JvmField
    var sensistivity = 0.6

    var currentPose = Pose(0.0,0.0,0.0)

    override val defaultCommand: Command
        get() = MecanumDriverControlled(
            fL,
            fR,
            bL,
            bR,
            Gamepads.gamepad1.leftStickY.map {it * sensistivity},
            -Gamepads.gamepad1.leftStickX.map {it * sensistivity},
            -Gamepads.gamepad1.rightStickX.map {it * sensistivity}
        )

    override fun initialize() {
        // follower.setStartingPose(Pose(0.0, 0.0, Math.PI)) // Set starting Pos
    }

    override fun periodic() {
        //  follower.update()
        // currentPose = follower.currentPose
    }

    // val StartTeleOPDrive = InstantCommand { follower.startTeleopDrive() }

    // Stuff to determine autoshoot
    data class Point(val x:Double, val y:Double)
    
    fun pointInTriangle(p: Point, a: Point, b: Point, c: Point): Boolean {
        val det = (b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y)
        if (kotlin.math.abs(det) < 1e-6) return false
    
        val u = ((b.y - c.y) * (p.x - c.x) + (c.x - b.x) * (p.y - c.y)) / det
        val v = ((c.y - a.y) * (p.x - c.x) + (a.x - c.x) * (p.y - c.y)) / det
        val w = 1 - u - v
    
        return u >= 0 && v >= 0 && w >= 0
    }
    
    fun canShoot(): Boolean {
        val x = 0.0 // Replace with follower.pose.x
        val y = 0.0 // Replace with follower.pose.y
        
        val obstacle = listOf(
            Point(0.0, 115.0),
            Point(25.0, 144.0),
            Point(0.0, 141.0)
        )
    
        val upper = listOf(
            Point(0.0, 115.0),
            Point(25.0, 144.0),
            Point(72.0, 72.0)
        )
    
        val lower = listOf(
            Point(48.0, 0.0),
            Point(72.0, 24.0),
            Point(72.0, 0.0)
        )
    
        val hw = 13.0 / 2.0
        val hl = 13.0 / 2.0
        val corners = listOf(
            Point(x - hw, y - hl),
            Point(x + hw, y - hl),
            Point(x + hw, y + hl),
            Point(x - hw, y + hl)
        )
    
        fun overlaps(tri: List<Point>): Boolean {
            return corners.any { pointInTriangle(it, tri[0], tri[1], tri[2]) }
        }
    
        val inUpper = overlaps(upper)
        val inLower = overlaps(lower)
        val inObstacle = overlaps(obstacle)
    
        return (inUpper || inLower) && !inObstacle
    }

    fun relocalizeWithLimelight() {

    }
}