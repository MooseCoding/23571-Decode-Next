package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.FeedbackCRServoEx
import dev.nextftc.hardware.impl.FeedbackServoEx
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.hardware.impl.ServoEx
import org.firstinspires.ftc.teamcode.helpers.getIndex
import org.firstinspires.ftc.teamcode.next.subsystems.data.Aimbot
import org.firstinspires.ftc.teamcode.next.subsystems.data.Alliance
import kotlin.math.*
import kotlin.time.Duration.Companion.seconds

@Configurable
object Outtake: Subsystem {
    val gS = FeedbackCRServoEx(
        cacheTolerance = 0.01,
        feedbackFactory = { ActiveOpMode.hardwareMap.analogInput.get("gSA") },
        servoFactory = { ActiveOpMode.hardwareMap.crservo.get("gS") }
    )

    val servo = FeedbackServoEx("analog", "servo");

    val f1 = MotorEx("f1M")
    val f2 = MotorEx("f2M").reversed()
    val hS = ServoEx("flap")

    var gearRatio = 3.47

    // Spin motor
    /*
    val spin = MotorEx("spin")

    @JvmField
    var sP = PIDCoefficients(0.0,0.0,0.0)
    var sC = controlSystem {
        posPid(sP)
    }

    //Find radians per tick
    @JvmField
    var ppr = 781.5
    
    var rpt = 2*PI/(4*ppr*gearRatio)


    @JvmField
    var yaw = 0.0

    periodic {
        yaw = normalizeAngle(spin.currentPosition*rpt)
        spin.power = sC.calculate(spin.state) 
    }

    fun goToYaw(y:Double) {
        sC.goal = y
    }

    --------Baron's Code------------

    
    public static double normalizeAngle(double angleRadians) {
        double angle = angleRadians % (Math.PI * 2D);
        if (angle <= -Math.PI) angle += Math.PI * 2D;
        if (angle > Math.PI) angle -= Math.PI * 2D;
        return angle;
    }


    public double getYaw() {
        return normalizeAngle(getTurret() * rpt);
    }

    public void setYaw(double radians) {
        radians = normalizeAngle(radians);
        setTurretTarget(radians/rpt);
    }

    public void addYaw(double radians) {
        setYaw(getYaw() + radians);
    }

    public double getTurret() {
        return m.getCurrentPosition();
    }
    */

    // Constants
    @JvmField
    var targetOnVelo = 950.0

    @JvmField
    var targetBackVelo = 400.0

    var pid = PIDCoefficients(0.0033, 0.0, 0.0)
    var ff = BasicFeedforwardParameters(1.66667E-4, 0.0, 0.003)
    var controller = controlSystem {
        velPid(pid)
        basicFF(ff)
    }

    @JvmField
    var gPid = PIDCoefficients(0.0, 0.0, 0.0)
    var gController = controlSystem { // Controller goal will alwyas be in Turret Rads
        posPid(gPid)
    }

    // Changing Vars
    @JvmField
    var targetVelo = 0.0

    @JvmField
    var gP = 0.0 // Gear Power

    @JvmField
    var hP = 0.0 // Hood Position

    @JvmField
    var velocityTrue = true // Use the VPID

    @JvmField
    var turrentAngle = 0.0 // Turrent Angle relative Pedro Pathing's starting orientation

    @JvmField
    var currentX = 0.0

    @JvmField
    var currentY = 0.0

    @JvmField
    var manualAim = 0

    @JvmField
    var f = 100.0
    @JvmField
    var h = 0.006

    @JvmField
    var currentHeading = 0.0

    @JvmField
    var turretOffset = 0.0

    // Handling auto shooting and stuff
    @JvmField 
    var auto = true
    @JvmField
    var autoShoot = true

    @JvmField
    var manualOn = false

    @JvmField
    var targetHeading: Double = 0.0

    var prevAngle = 0.0
    var turretHeading = 0.0
    var dHeading = 0.0
    var totalAngle = 0.0
    var dist = 0.0

    var xcord = 12.80
    var ycord = 138.35
    var height = 36.0 // Units initally
    var turretHeight = 0.0 // Inches

    override fun initialize() {
        if (DriveTrain.alliance == Alliance.BLUE) {
            xcord = 12.80
            ycord = 138.35
        }
        else {
            xcord = 144-12.80
            ycord = 138.35
        }
    }

    override fun periodic() {
        if (velocityTrue) {
            f1.power = controller.calculate(f1.state)
            f2.power = f1.power
            controller.goal = KineticState(0.0, targetVelo)
        }

        turrentAngle = calculateAngle()

        if (manualOn) {
            aimDistance()
        }
        else {
            if (auto) {
                aimbot()
            }
            if (autoShoot) {
                betterAimbot()
            }
        }
    }

    fun betterAimbot() {
        if (DriveTrain.canShoot()) {
             SequentialGroup(
                 Intake.runIntake,
                 Delay(0.5.seconds),
                 Intake.stopIntake
             ) 
        }
    }

    fun aimbot() {
        // currentX = DriveTrain.follower.pose.x
        // currentY = DriveTrain.follower.pose.y
        // currentHeading = DriveTrain.follower.pose.heading

        var mu = atan2(ycord - currentY, xcord - currentX)
        var deltaHeading = mu - currentHeading
        var targetHeading = turretHeading + deltaHeading

        // Normalize only the final heading to [-π, π)
        targetHeading = ((targetHeading + PI) % (2 * PI)) - PI

        gController.goal = KineticState(targetHeading, 0.0)

        // Find theta (MAYBE I THINK I CAN JUST HARD CODE IN POSITION).
        dist = sqrt((xcord - currentX).pow(2) + (ycord - currentY).pow(2))

        // Find hP, and Power to run at using our lookup table
        val other = Aimbot.points[getIndex(dist)]

        hP = other[0] + h
        targetOnVelo = other[1] + f

        hS.position = 1-hP

        gS.power = gController.calculate(KineticState(turretFromServo(gS.state.position), 0.0))
    }

    // Commands
    val spinGearLeft = InstantCommand {
        gP = 0.7 // Some Constant
    }
    val spinGearRight = InstantCommand {
        gP = -0.7 // Some Constant
    }
    val gearAlittleLeft = InstantCommand {
        gP = -0.2
    }
    val gearAlittleRight = InstantCommand {
        gP = -0.2
    }
    val stopGear = InstantCommand {
        gP = 0.0
    }
    val FlapDown = InstantCommand {
        hP += 0.05
    }
    val FlapUp = InstantCommand {
        hP -= 0.05
    }

    val aimUp = InstantCommand {
        manualAim += 12
    }
    val aimDown = InstantCommand{
        manualAim -= 12
    }



    val flywheelOff: InstantCommand =
        InstantCommand { velocityTrue = false; targetVelo = 0.0; f1.power = 0.0; f2.power = 0.0 }
    val flywheelBack: InstantCommand =
        InstantCommand { velocityTrue = false; f1.power = -1.0; f2.power = -1.0 }
    val flywheelOn: InstantCommand =
        InstantCommand { velocityTrue = true; targetVelo = targetOnVelo }
    val flywheelBackSlow: InstantCommand =
        InstantCommand { velocityTrue = false; f1.power = -0.5; f2.power=-0.5}

    val initBalls = SequentialGroup(
        Intake.reverseIntake,
        Delay(0.2.seconds),
        Outtake.flywheelBack,
        Delay(0.1.seconds),
        Intake.stopIntake,
        Delay(0.2.seconds),
        Outtake.flywheelOff,
        Outtake.flywheelOn,
        Intake.runIntake,
        Delay(1.seconds),
        Intake.stopIntake,
        Outtake.flywheelOff
    )

    val outtakeBalls = SequentialGroup(
        Outtake.flywheelOn,
        Delay(0.1.seconds),
        Intake.runIntake,
        Delay(1.seconds),
        Outtake.flywheelOff,
    )

    fun calculateAngle(): Double {
        val cA = gS.currentPosition // current servo angle in radians
        var dHeading = 0.0

        // Handle wraparound across ±π
        if(cA<PI/2 && prevAngle>3 * PI/2){
//wrapped around the positive side
            dHeading= cA+(2*PI-prevAngle);
        }else if(cA>3*PI/2 && prevAngle<PI/2){
//wrapped around the negative side
            dHeading=-(prevAngle+(2*PI-cA));
        }else{
            dHeading=cA-prevAngle   ;
        }

        // Accumulate total angle continuously
        totalAngle += dHeading
        prevAngle = cA

        // Convert to turret angle (gear ratio + offset correction)
        val turretAngle = turretFromServo(totalAngle) - turretOffset

        return ((turretAngle + PI) % (2 * PI)) - PI
    }

    fun turretFromServo(totalAngle: Double): Double {
        return totalAngle / gearRatio
    }



    fun aimDistance() {
        when(manualAim){
            12 -> targetVelo = 835.0 // 0.81
            24 -> targetVelo = 862.0 // 0.93
            36 -> targetVelo = 844.0 // 0.71
            48 -> targetVelo = 848.0 // 0.51
            60 -> targetVelo = 908.0 // 0.51
            72 -> targetVelo = 1025.0 // 0.73
            84 -> targetVelo = 1165.0 // 1
            96 -> targetVelo = 1260.0 // 1
            108 -> targetVelo = 1100.0 // 0.42 Broken
            120 -> targetVelo = 1112.0 // 0.44 Broken
            132 -> targetVelo = 1150.0 // 0.43 Broken
            144 -> targetVelo = 1172.0 // 0.44 Broken
            else -> targetVelo = 0.0 // 0.0
        }
        when(manualAim){
            12 -> hP = 0.81 // 0.81
            24 -> hP = 0.93 // 0.93
            36 -> hP = 0.71 // 0.71
            48 -> hP = 0.51 // 0.51
            60 -> hP = 0.51 // 0.51
            72 -> hP = 0.73 // 0.73
            84 -> hP = 1.0 // 1
            96 -> hP = 1.0 // 1
            108 -> hP = 0.42 // Broken
            120 -> hP = 0.43 // Broken
            134 -> hP = 0.44 // Broken
            146 -> hP = 0.45 // Broken
            else -> hP = 0.0 // 0.0
        }
        if(manualAim > 146){
            manualAim = 146
        }else if (manualAim < 12){
            manualAim = 12
        }

        if(manualAim % 12 != 0){
            manualAim -= manualAim % 12
        }
    }
}
