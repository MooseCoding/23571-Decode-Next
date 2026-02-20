package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.util.Timer
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.delays.WaitUntil
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup
import dev.nextftc.core.commands.groups.ParallelGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.core.units.Angle
import dev.nextftc.extensions.pedro.FollowPath
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.extensions.pedro.TurnBy
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain.alliance
import org.firstinspires.ftc.teamcode.next.subsystems.FlywheelLight
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Light
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Hood
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import kotlin.math.PI
import kotlin.time.Duration.Companion.seconds

@Autonomous
class CloseAuto: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(DriveTrain, Intake, Transfer, Outtake, FlywheelLight, Light),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    val far12: Far12 by lazy { Far12(alliance) }

    override fun onInit() {
        Turret.autoTurret = true
    }

    var balls: Double = 12.0

    override fun onWaitForStart() {
        if (gamepad1.triangle) { alliance = Alliance.RED }
        if (gamepad1.circle) { alliance = Alliance.BLUE }

        if(gamepad1.right_bumper) {
            if(balls != 12.0) {
                balls = 12.0
            }
        }
        if(gamepad1.left_bumper) {
            if(balls != 9.0) {
                balls = 9.0
            }
        }


        telemetry.run {
            addData("Alliance ", alliance)
            addData("follower X", follower.pose.x)
            addData("follower Y", follower.pose.y)
            addData("follower heading", follower.heading)
            addData("Balls", balls)
            update()
        }

        when(alliance) {
            Alliance.RED -> {
                FlywheelLight.Red().schedule()
            }
            Alliance.BLUE -> {
                FlywheelLight.Blue().schedule()
            }
        }
    }

    private lateinit var timer: Timer

    override fun onStartButtonPressed() {
        Flywheels.targetVelocity = Outtake.farVelocity - 110.00
        Hood.hoodPosition = Outtake.farHood

        far12.init()

        follower.pose = (far12.startPoint)

        timer = Timer()

        if(balls == 12.0) {
            SequentialGroupLocal(
                Flywheels.spin(),
                Delay(2.2.seconds),
                Outtake.shootFar(),
                // Intake from row 2
                FollowPath(far12.ToRow2),
                Intake.runIntake(),
                FollowPath(far12.Row2Intake),
                ParallelGroup(
                    SequentialGroupLocal(
                        Delay(0.5.seconds),
                        Intake.stopIntake(),
                    ),

                    // Hit ramp here
                    TurnBy(Angle.fromRad(-PI / 2)),
                ),
                FollowPath(far12.PullOut),

                // Shoot
                FollowPath(far12.RampToShoot),
                Delay(1.seconds),
                Outtake.shootFar(),
                Delay(0.3.seconds),

                // Intake from row 1
                FollowPath(far12.ToRow1),
                Intake.runIntake(),
                FollowPath(far12.Row1Intake),
                ParallelGroup(
                    SequentialGroupLocal(
                        Delay(0.5.seconds),
                        Intake.stopIntake()
                    ),
                    FollowPath(far12.ShootRow1),

                    ),
                // Shoot
                Delay(1.seconds),
                Outtake.shootFar(),
                Delay(0.3.seconds),

                // Human Player Intake Constantly
                ParallelDeadlineGroup(
                    WaitUntil { timer.elapsedTimeSeconds > 28.0 },
                    SequentialGroupLocal(
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shootFar(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shootFar(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shootFar(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shootFar(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shootFar(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shootFar(),
                            Delay(0.3.seconds)
                        ),
                    )
                ),

                FollowPath(far12.Park),
            ).schedule()
        }
        else {
            SequentialGroupLocal(
                Flywheels.spin(),
                Delay(3.5.seconds),
                Outtake.shootFar(),

                // Intake from row 1
                FollowPath(far12.ToRow1),
                Intake.runIntake(),
                FollowPath(far12.Row1Intake),
                ParallelGroup(
                    SequentialGroupLocal(
                        Delay(0.5.seconds),
                        Intake.stopIntake()
                    ),
                    FollowPath(far12.ShootRow1),

                    ),
                // Shoot
                Delay(1.seconds),
                Outtake.shootFar(),
                Delay(0.3.seconds),

                // Human Player Intake Constantly
                ParallelDeadlineGroup(
                    WaitUntil { timer.elapsedTimeSeconds > 28.0 },
                    SequentialGroupLocal(
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shootFar(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shootFar(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shootFar(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shootFar(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shootFar(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shootFar(),
                            Delay(0.3.seconds)
                        ),
                    )
                ),

                FollowPath(far12.Park),
            ).schedule()
        }
    }
}