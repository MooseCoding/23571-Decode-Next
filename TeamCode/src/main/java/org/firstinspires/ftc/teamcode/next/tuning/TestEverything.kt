package org.firstinspires.ftc.teamcode.next.tuning

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Light
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Sensor
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import java.lang.Double.max
import java.lang.Math.abs
import kotlin.math.PI

@TeleOp
@Disabled
class TestEverything: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(Intake, Transfer, Turret, Outtake, DriveTrain, Light, Sensor),
            BindingsComponent,
            BulkReadComponent
        )
    }

    val fl =             MotorEx("fL")// Yse
    val fr =             MotorEx("fR")
    val bl =             MotorEx("bL")
    val br =             MotorEx("bR")// Yes

    var rightScalar: Double = 1.0
    var sens: Double = 1.0

    override fun onStartButtonPressed() {
        Gamepads.gamepad1.rightBumper whenBecomesTrue Transfer.start() whenBecomesFalse Transfer.stop()
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue Intake.runIntake() whenBecomesFalse Intake.stopIntake()
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue Intake.reverseIntake() whenBecomesFalse Intake.stopIntake()

        // Shoot
        Gamepads.gamepad1.triangle whenBecomesTrue Outtake.shoot()

        Gamepads.gamepad2.triangle whenBecomesTrue {
            if (Flywheels.spinSlow) {
                Flywheels.spin().schedule()
            } else {
                Flywheels.stop().schedule()
            }
        }

        // Gamepads.gamepad1.rightStickButton whenTrue { rightScalar = 1.0 } whenFalse { rightScalar = 0.8 }

        Gamepads.gamepad2.cross whenBecomesTrue Turret.zero()
    }

    override fun onInit() {
        DriveTrain.alliance = Alliance.BLUE
        PedroComponent.follower.setStartingPose(Pose(48+16.5/2, 17.0/2, PI/2))
    }

    override fun onUpdate() {
        val y = -gamepad1.left_stick_y * sens
        val x = gamepad1.left_stick_x * sens
        val t = gamepad1.right_stick_x * sens

        val d = max((abs(y)+abs(x)+abs(t)).toDouble(), 1.0)
        fl.power = (y+x+t)/d * rightScalar
        fr.power = (y-x-t)/d
        bl.power = (y-x+t)/d
        br.power = (y+x-t)/d

        telemetry.run {
            addData("X", PedroComponent.follower.pose.x)
            addData("Y", PedroComponent.follower.pose.y)
            addData("H", PedroComponent.follower.pose.heading)
            update()
        }
    }
}