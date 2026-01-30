package org.firstinspires.ftc.teamcode.wayfinder

import android.text.Layout
import com.qualcomm.hardware.dfrobot.HuskyLens
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.util.ElapsedTime
import dev.nextftc.core.commands.conditionals.IfElseCommand
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.delays.WaitUntil
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup
import dev.nextftc.core.commands.groups.ParallelGroup
import dev.nextftc.core.commands.groups.ParallelRaceGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.NullCommand
import dev.nextftc.core.commands.utility.PerpetualCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.robotcore.external.Telemetry
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Light
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Pinpoint
import org.firstinspires.ftc.teamcode.next.subsystems.Sensor
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.tuning.Drive
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import kotlin.math.PI
import kotlin.time.Duration.Companion.seconds

@Autonomous
class NineBall: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(
                DriveTrain, Intake, Outtake, Transfer, Pinpoint, Light
            ),
            BindingsComponent,
            BulkReadComponent
        )
    }

    private lateinit var wayfinder: MotorWayfinder

    val bL = MotorEx("bL")
    val bR = MotorEx("bR")
    val fL = MotorEx("fL")
    val fR = MotorEx("fR")


    override fun onInit() {
        wayfinder = MotorWayfinder(bL, bR, fR, fL, Pinpoint.pinpoint)
    }

    private var balls: Int = 18
    private var canGoTop: Boolean = false
    private var cycleRamp: Boolean = false
    private var cycleFar: Boolean = false
    private var alliance: Alliance = Alliance.RED

    override fun waitForStart() {
        Gamepads.gamepad1.leftTrigger.greaterThan(0.3) whenBecomesTrue { if(balls >= 9) balls -= 3}
        Gamepads.gamepad1.rightTrigger.greaterThan(0.3) whenBecomesTrue { if(balls <= 15) balls += 3}
        Gamepads.gamepad1.cross whenBecomesTrue { canGoTop = !canGoTop }
        Gamepads.gamepad1.circle whenBecomesTrue { cycleRamp = !cycleRamp }
        Gamepads.gamepad1.square whenBecomesTrue { cycleFar = !cycleFar }
        Gamepads.gamepad1.triangle whenBecomesTrue { alliance = when(alliance){
            Alliance.RED -> Alliance.BLUE
            Alliance.BLUE -> Alliance.RED
        }}
    }

    override fun onWaitForStart() {
        // telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML)

        telemetry.run {
            addData("balls", balls)
            addData("canGoTop", canGoTop)
            addData("cycleRamp", cycleRamp)
            addData("cycleFar", cycleFar)
            addData("Alliance", alliance)
            addLine("Gamepad 1 Left Trigger Down: Subtract 3 Balls")
            addLine("Gamepad 1 Right Trigger Down: Add 3 Balls")
            addLine("Gamepad 1 Cross: Toggle Cycle Can Go Top")
            addLine("Gamepad 1 Circle: Toggle Cycle Ramp")
            addLine("Gamepad 1 Square: Toggle Cycle Far")
            addLine("Gamepad 1 Triangle: Cycle Alliance Color")
            update()
        }
    }



    val timer: ElapsedTime = ElapsedTime()

    override fun onUpdate() {
        telemetry.run {
            addData("Timer", timer.seconds())
            update()
        }
    }


    override fun onStartButtonPressed() {

        when(alliance) {
            Alliance.BLUE -> FarPoses.switchToBlue()
            else -> {}
        }

        val cycleFarSequence = PerpetualCommand(
            SequentialGroupLocal(
                Intake.runIntake(),
                WayfinderDrive(wayfinder, FarPoses.cyclePose),
                ParallelGroup(
                    WayfinderDrive(wayfinder, FarPoses.cycleShoot),
                    SequentialGroupLocal(
                        Delay(0.2.seconds),
                        Intake.stopIntake()
                    )
                ),
                Delay(2.seconds),
                Outtake.shoot(),
            )
        )

        timer.reset()

        SequentialGroupLocal(
            ParallelDeadlineGroup(
                WaitUntil { timer.seconds() > 28.0 },
                SequentialGroupLocal(
                    Flywheels.spin(),
                    Delay(2.5.seconds),
                    Outtake.shoot(),

                    // Intake from far row
                    WayfinderDrive(wayfinder, FarPoses.row3),
                    Intake.runIntake(),
                    WayfinderDrive(wayfinder, FarPoses.row3End),
                    Intake.stopIntake(),
                    WayfinderDrive(wayfinder, FarPoses.shoot2),
                    Delay(2.seconds),
                    Outtake.shoot(),



                    // Human Player Intake
                    Intake.runIntake(),
                    WayfinderDrive(wayfinder, FarPoses.humanPlayer),
                    ParallelGroup(
                        WayfinderDrive(wayfinder, FarPoses.cycleShoot),
                        SequentialGroupLocal(
                            Delay(0.2.seconds),
                            Intake.runIntake()
                        )
                    ),
                    Delay(2.seconds),
                    Outtake.shoot(),

                    ParallelRaceGroup(
                        WaitUntil { !cycleFar },
                        cycleFarSequence
                    )
                )
            ),
            WayfinderDrive(wayfinder, FarPoses.park)
        ).schedule()
    }
}