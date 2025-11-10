package org.firstinspires.ftc.teamcode.next.subsystems

import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.hardware.impl.ServoEx
import org.firstinspires.ftc.teamcode.next.subsystems.data.Alliance
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import org.firstinspires.ftc.teamcode.helpers.getIndex
import org.firstinspires.ftc.teamcode.next.subsystems.data.Aimbot
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

class NewOuttake: Subsystem {
    // Hardware init
    val hoodServo = ServoEx("flap")
    // val spindexer = MotorEx("spindexer")
    val turret = MotorEx("spin")
    val f1 = MotorEx("f1M")
    val f2 = MotorEx("f2M").reversed()
    var gearRatio = 3.47

    // Current Position
    @JvmField var currentX = 0.0
    @JvmField var currentY = 0.0
    @JvmField var currentHeading = 0.0

    // Target Position
    var goalX = 0.0
    var goalY = 144-8.0

    // Turret Control
    @JvmField var turretPID = PIDCoefficients(0.8,0.0,0.0)
    var turretController = controlSystem {
        posPid(turretPID)
    }

    var ppr = 537.7 // The resolution of our motor encoder on the goBilda site
    var rpt = 2* PI /(Outtake.ppr * Outtake.gearRatio) // The amount of radians per turn of the motor

    fun goToYaw(y:Double) { // Go to a specific position
        turretController.goal = KineticState(y, 0.0)
    }

    fun getYaw(): Double { // Get the current yaw of the turret from [-pi, pi]
        return normalizeAngle(turret.currentPosition * Outtake.rpt)
    }


    fun normalizeAngle(angleRadians: Double): Double { // Returns a normalized angle between [-pi, pi]
        var angle = angleRadians % (2.0 * PI)
        if (angle <= -PI) {
            angle += 2.0 * PI
        }
        if (angle > PI) {
            angle -= 2.0 * PI
        }
        return angle
    }


    // Flywheel Stuff
    @JvmField var flywheelPID = PIDCoefficients(0.0033, 0.0, 0.0)
    @JvmField var flywheelFF = BasicFeedforwardParameters(1.66667E-4, 0.0, 0.003)
    var flywheelController = controlSystem {
        velPid(flywheelPID)
        basicFF(flywheelFF)
    }

    @JvmField var targetVelocity = 0.0

    // Manual Controls
    @JvmField var autoShoot = true
    @JvmField var autoTurret = true
    @JvmField var fullManual = false



    // Actual subsystem stuff

    override fun initialize() {
        goalX = if (DriveTrain.alliance == Alliance.RED) {
            144-6.0
        } else {
            6.0
        }
    }

    override fun periodic() {
        currentX = follower.pose.x
        currentY = follower.pose.y
        currentHeading = follower.heading

        f1.power = flywheelController.calculate(f1.state)
        f2.power = f1.power
        flywheelController.goal = KineticState(0.0, targetVelocity)


        if(fullManual) {
           // Add in the manual control
        }
        else {
            if (autoShoot) {
                autoShoot()
            }
            if (autoTurret) { // Note that auto turret also encapsulates the velocity and hood position
                autoTurret()
            }
        }
    }

    // Auto Turret
    fun autoTurret() {
        val dist = sqrt((currentX - Outtake.currentX).pow(2) + (currentY - Outtake.currentY).pow(2))
        val other = Aimbot.points[getIndex(dist)]
        val hoodPosition = other[0] + 0.01
        targetVelocity = other[1] + 100
        hoodServo.position = 1-hoodPosition
    }

     // Auto Shoot
    fun autoShoot() {
        if (canShoot()) {
            // Start our transfer
        }
    }

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
            Point(currentX - hw, currentY - hl),
            Point(currentX + hw, currentY - hl),
            Point(currentX + hw, currentY + hl),
            Point(currentX - hw, currentY + hl)
        )
        fun overlaps(tri: List<Point>): Boolean {
            return corners.any { pointInTriangle(it, tri[0], tri[1], tri[2]) }
        }
        val inUpper = overlaps(upper)
        val inLower = overlaps(lower)
        val inObstacle = overlaps(obstacle)
        return (inUpper || inLower) && !inObstacle
    }
}