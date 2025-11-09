package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.qualcomm.robotcore.hardware.DcMotor
import dev.nextftc.control.KineticState
import dev.nextftc.control.builder.controlSystem
import dev.nextftc.control.feedback.PIDCoefficients
import dev.nextftc.control.feedforward.BasicFeedforwardParameters
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.hardware.impl.FeedbackCRServoEx
import dev.nextftc.hardware.impl.FeedbackServoEx
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.hardware.impl.ServoEx
import org.firstinspires.ftc.teamcode.helpers.getIndex
import org.firstinspires.ftc.teamcode.next.subsystems.data.Aimbot
import org.firstinspires.ftc.teamcode.next.subsystems.data.Alliance
import org.firstinspires.ftc.teamcode.next.tuning.Drive
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

    val spin = MotorEx("spin")

    @JvmField
    var sP = PIDCoefficients(0.8,0.0,0.0)
    var sC = controlSystem {
        posPid(sP)
    }

    //Find radians per tick
    @JvmField
    var ppr = 537.7
    
    var rpt = 2*PI/(ppr*gearRatio)


    @JvmField
    var yaw = 0.0


    fun goToYaw(y:Double) {
        sC.goal = KineticState(y, 0.0)
    }

    fun getYaw(): Double {
        return normalizeAngle(spin.currentPosition * rpt)
    }


    fun normalizeAngle(angleRadians: Double): Double {
        var angle = angleRadians % (2.0 * PI)
        if (angle <= -PI) {
            angle += 2.0 * PI
        }
        if (angle > PI) {
            angle -= 2.0 * PI
        }
        return angle
    }

    /*
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
    var targetOnVelo = 835.0

    @JvmField
    var crap = 0.0

    @JvmField
    var targetBackVelo = 400.0

    var pid = PIDCoefficients(0.0033, 0.0, 0.0)
    var ff = BasicFeedforwardParameters(1.66667E-4, 0.0, 0.003)
    var controller = controlSystem {
        velPid(pid)
        basicFF(ff)
    }

    // Changing Vars
    @JvmField
    var targetVelo = 0.0

    @JvmField
    var gP = 0.0 // Gear Power

    @JvmField
    var hP = 0.81 // Hood Position

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
    var h = 0.01

    @JvmField
    var currentHeading = 0.0

    @JvmField
    var canSpin= true

    // Handling auto shooting and stuff
    @JvmField 
    var auto = false
    @JvmField
    var autoShoot = false
    @JvmField
    var autoTurret = true

    @JvmField
    var manualOn = false

    @JvmField
    var targetHeading: Double = 0.0

    var prevAngle = 0.0
    var turretHeading = 0.0
    var dHeading = 0.0
    var totalAngle = 0.0
    var dist = 0.0

    var xcord = 6.0
    var ycord = 144-8.0

    override fun initialize() {
        if (DriveTrain.alliance == Alliance.BLUE) {
            xcord = 6.0
            ycord = 144-8.0
        }
        else {
            xcord = 144-6.0
            ycord = 144-8.0
        }
    }

    override fun periodic() {
        currentX = follower.pose.x
        currentY = follower.pose.y
        currentHeading = follower.pose.heading

        if (velocityTrue) {
            f1.power = controller.calculate(f1.state)
            f2.power = f1.power
            controller.goal = KineticState(0.0, targetVelo)
        }

        if (manualOn) {
            aimDistance()
            spin.power = gP
            hS.position = hP
        }
    }

    fun betterAimbot() {
        if (DriveTrain.canShoot()) {
             outtakeBalls.schedule()
        }
    }

    fun aimbot() {
        dist = sqrt((xcord - currentX).pow(2) + (ycord - currentY).pow(2))

        val other = Aimbot.points[getIndex(dist)]

        hP = other[0] + h
        targetOnVelo = other[1] + f

        hS.position = 1-hP

    }

    fun aimTurret() {
        var mu = atan2(ycord - currentY, xcord - currentX)
        var deltaHeading = normalizeAngle(mu - currentHeading)

        val clampedHeading = deltaHeading.coerceIn(-PI/2, PI/2)

        sC.goal = KineticState(clampedHeading, 0.0)
    }

    // Commands
    val spinGearLeft = InstantCommand {
        gP = 0.6 // Some Constant
    }
    val spinGearRight = InstantCommand {
        gP = -0.6// Some Constant
    }
    val gearAlittleLeft = InstantCommand {
        gP = -0.2
    }
    val gearAlittleRight = InstantCommand {
        gP = 0.2
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
    val zeroMotor = InstantCommand {
        spin.motor.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        spin.motor.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }
    val aimUp = InstantCommand {
        manualAim += 12
    }
    val aimDown = InstantCommand{
        manualAim -= 12
    }

    val flywheelOff: InstantCommand =
        InstantCommand { velocityTrue = true; targetVelo = 0.0;}
    val flywheelBack: InstantCommand =
        InstantCommand { velocityTrue = false; f1.power = -1.0; f2.power = -1.0 }
    val flywheelOn: InstantCommand =
        InstantCommand { velocityTrue = true; targetVelo = targetOnVelo }
    val flywheelBackSlow: InstantCommand =
        InstantCommand { velocityTrue = false; f1.power = -0.5; f2.power=-0.5}



    val outtakeBalls = SequentialGroup(
        Outtake.flywheelOn,
        Delay(0.1.seconds),
        Intake.runIntake,
        Delay(0.5.seconds),
        Outtake.flywheelOff,
    )


    fun aimDistance() {
        if(canSpin) {
            when(manualAim){
                12 -> targetVelo = 835.0 // 0.81
                24 -> targetVelo = 862.0 // 0.93
                36 -> targetVelo = 844.0 // 0.71
                48 -> targetVelo = 848.0 // 0.6
                60 -> targetVelo = 908.0 // 0.62
                72 -> targetVelo = 1025.0 // 0.73
                84 -> targetVelo = 1165.0 // 0.7
                96 -> targetVelo = 1230.0 // 0.7
                108 -> targetVelo = 1070.0 // 0.42
                120 -> targetVelo = 1112.0 // 0.44
                132 -> targetVelo = 1150.0 // 0.43
                144 -> targetVelo = 1250.0 // 0.44
                else -> targetVelo = 0.0 // 0.0
            }
        }
        when(manualAim){
            12 -> hP = 0.81 // 0.81
            24 -> hP = 0.93 // 0.93
            36 -> hP = 0.71 // 0.71
            48 -> hP = 0.6 // 0.6
            60 -> hP = 0.62 // 0.62
            72 -> hP = 0.65 // 0.65
            84 -> hP = 0.7 // 0.7
            96 -> hP = 0.7 // 0.7
            108 -> hP = 0.42 // 0.42
            120 -> hP = 0.43 // 0.43
            134 -> hP = 0.44 // 0.44
            146 -> hP = 0.45 // 0.45
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
