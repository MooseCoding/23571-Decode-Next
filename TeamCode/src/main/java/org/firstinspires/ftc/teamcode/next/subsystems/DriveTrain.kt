package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.geometry.Pose
import dev.nextftc.core.commands.Command
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.ftc.Gamepads
import dev.nextftc.hardware.driving.MecanumDriverControlled
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.pedroPathing.Far12
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor

@Configurable
object DriveTrain: Subsystem {
     val fL = MotorEx("fL")
     val fR = MotorEx("fR")
     val bL = MotorEx("bL")
     val bR = MotorEx("bR")

    @JvmField var alliance:Alliance = Alliance.BLUE
    @JvmField var sensitivity = 0.8
    var currentX = 0.0
    var currentY = 0.0
    var currentHeading = 0.0

    override val defaultCommand: Command
        get() = MecanumDriverControlled(
            fL,
            fR,
            bL,
            bR,
            -Gamepads.gamepad1.leftStickY.map {it * sensitivity},
            Gamepads.gamepad1.leftStickX.map {it * sensitivity},
            Gamepads.gamepad1.rightStickX.map {it * sensitivity}
        )

    override fun periodic() {
        currentX = follower.pose.x
        currentY = follower.pose.y
        currentHeading = follower.heading
    }

    override fun initialize() {
    }

    /*

    fun PoseInTriangle(p: Pose, a: Pose, b: Pose, c: Pose): Boolean {
        val det = (b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y)
        if (kotlin.math.abs(det) < 1e-6) return false
        val u = ((b.y - c.y) * (p.x - c.x) + (c.x - b.x) * (p.y - c.y)) / det
        val v = ((c.y - a.y) * (p.x - c.x) + (a.x - c.x) * (p.y - c.y)) / det
        val w = 1 - u - v
        return u >= 0 && v >= 0 && w >= 0
    }

    fun inShootZone(): Boolean {
        if(currentX > 144 || currentX < 0 || currentY > 144 || currentY < 0) {
            return false // Stop if odometry cannot track us
        }
        val obstacle = listOf(
            Pose(0.0, 115.0),
            Pose(25.0, 144.0),
            Pose(0.0, 141.0)
        )
        val upper = listOf(
            Pose(0.0, 115.0),
            Pose(25.0, 144.0),
            Pose(72.0, 72.0)
        )
        val lower = listOf(
            Pose(48.0, 0.0),
            Pose(72.0, 24.0),
            Pose(72.0, 0.0)
        )
        val hw = 13.0 / 2.0
        val hl = 13.0 / 2.0
        val corners = listOf(
            Pose(currentX - hw, currentY - hl),
            Pose(currentX + hw, currentY - hl),
            Pose(currentX + hw, currentY + hl),
            Pose(currentX - hw, currentY + hl)
        )
        fun overlaps(tri: List<Pose>): Boolean {
            return corners.any { PoseInTriangle(it, tri[0], tri[1], tri[2]) }
        }
        val inUpper = overlaps(upper)
        val inLower = overlaps(lower)
        val inObstacle = overlaps(obstacle)
        return (inUpper || inLower) && !inObstacle
    }

     */
}
