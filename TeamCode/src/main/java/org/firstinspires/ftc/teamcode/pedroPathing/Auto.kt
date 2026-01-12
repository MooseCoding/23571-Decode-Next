package org.firstinspires.ftc.teamcode.pedroPathing

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import dev.nextftc.core.commands.conditionals.IfElseCommand
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.delays.WaitUntil
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.commands.utility.PerpetualCommand
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.core.units.Angle
import dev.nextftc.extensions.pedro.FollowPath
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.extensions.pedro.TurnBy
import dev.nextftc.extensions.pedro.TurnTo
import dev.nextftc.ftc.ActiveOpMode
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import dev.nextftc.units.unittypes.gradians
import dev.nextftc.units.unittypes.radians
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.alliance
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Motif
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import kotlin.math.PI
import kotlin.time.Duration.Companion.seconds

@Autonomous(name = "BlueAuto")
@Configurable
class Auto: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(DriveTrain, Intake, Transfer, Outtake),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    private lateinit var timer: Timer

    override fun onWaitForStart() {
        Gamepads.gamepad1.a whenBecomesTrue { alliance = Alliance.RED }
        Gamepads.gamepad1.b whenBecomesTrue  { alliance = Alliance.BLUE }
        telemetry.run {
            addData("Alliance ", alliance)
            addData("follower X", follower.pose.x)
            addData("follower Y", follower.pose.y)
            addData("follower heading", follower.heading)

            update()
        }
        Flywheels.targetVelocity = 0.0
        Hood.hoodPosition = 0.5
    }

    val fL = MotorEx("fL")
    val fR = MotorEx("fR").reversed()
    val bL = MotorEx("bL")
    val bR = MotorEx("bR").reversed()

    override fun onInit() {
        follower.setStartingPose(Far12.startPoint)
    }

    override fun waitForStart() {

    }

    override fun onStop() {
    }

    override fun onStartButtonPressed() {
        timer = Timer()
        val p = Far12(alliance)  //41.5 In at 0.5 power for 1 second

        SequentialGroupLocal(
            // Shoot the 3 preloads
            InstantCommand { Flywheels.targetVelocity = 1350.0 },
            Flywheels.spin(),
            InstantCommand { //forward
                fR.power = 0.5
                fL.power = -0.5
                bL.power = -0.5
                bR.power = 0.5
            },
            Delay(1.0.seconds),
            InstantCommand{//shoot
                fR.power = 0.0
                fL.power = 0.0
                bL.power = 0.0
                bR.power = 0.0
            },
            Delay(1.0),
            Intake.runIntake(),
            Transfer.start(),
            Delay(2.0),
            Transfer.stop(),
            InstantCommand{//turn
                fR.power = -0.5
                fL.power = -0.5
                bR.power = -0.5
                bL.power = -0.5
            },
            Delay(0.2) ,
            InstantCommand{//strafe left
                fR.power = -0.7
                fL.power = -0.7
                bR.power = 0.7
                bL.power = 0.7
            },
            Delay(1.5),
            //CUT FOR FAILURE
            /*
            InstantCommand{//turn
                fR.power = -0.0
                fL.power = -0.0
                bR.power = -0.0
                bL.power = -0.0
            },
            Delay(0.1),
            InstantCommand{ //forward
                fR.power = 0.5
                fL.power = -0.5
                bR.power = -0.5
                bL.power = 0.5
            },
            Delay(0.8),
            InstantCommand{ //back
                fR.power = -0.5
                fL.power = 0.5
                bR.power = 0.5
                bL.power = -0.5
            },
            Delay(0.8),
            InstantCommand{//strafe right
                fR.power = 0.5
                fL.power = 0.5
                bR.power = -0.5
                bL.power = -0.5
            },
            Delay(0.25),
            InstantCommand{//turn
                fR.power = 0.5
                fL.power = 0.5
                bR.power = 0.5
                bL.power = 0.5
            },
            Delay(0.25),
            Transfer.start(),
            Delay(1.0),
             */
            InstantCommand{//turn
                fR.power = -0.0
                fL.power = -0.0
                bR.power = -0.0
                bL.power = -0.0
            },
            Delay(0.1),
            Transfer.stop(),
            Intake.stopIntake(),
            Flywheels.hardStop()

            /*

                    // Intake from row 2
            FollowPath(Far12.ToRow2),
            FollowPath(Far12.Row2Intake),

            // Hit ramp here
            TurnBy(Angle.fromRad(-PI/2)),

            // Shoot
            FollowPath(Far12.RampToShoot),
            Outtake.shoot(),
            Delay(0.3.seconds),

                    // Intake from row 1
            FollowPath(Far12.ToRow1),
            FollowPath(Far12.Row1Intake),

            // Shoot
            FollowPath(Far12.ShootRow1),
            Outtake.shoot(),
            Delay(0.3.seconds),

                    // Human Player Intake Constantly
            ParallelDeadlineGroup(
                WaitUntil { timer.elapsedTime > 28.0},
                PerpetualCommand(
                    SequentialGroupLocal(
                        FollowPath(Far12.HumanPlayerIntake),
                        FollowPath(Far12.HumanPlayerShoot),
                        Outtake.shoot(),
                        Delay(0.3.seconds)
                    )
                ),
            ),

            FollowPath(Far12.Park),*/
        ).schedule()
    }


    public override fun onUpdate() {
        telemetry.run {
            addData("follower X", follower.pose.x)
            addData("follower Y", follower.pose.y)
            addData("follower heading", follower.heading)

            update()
        }
    }
}