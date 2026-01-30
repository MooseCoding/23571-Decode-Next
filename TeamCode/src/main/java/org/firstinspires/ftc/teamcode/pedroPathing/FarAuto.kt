package org.firstinspires.ftc.teamcode.pedroPathing

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.delays.WaitUntil
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup
import dev.nextftc.core.commands.groups.ParallelGroup
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
import kotlin.math.PI
import kotlin.time.Duration.Companion.seconds

@Autonomous(preselectTeleOp = "TeleOp")
@Configurable
class FarAuto: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(DriveTrain, Intake, Transfer, Outtake, FlywheelLight, Light),
            BulkReadComponent,
            BindingsComponent,
        )
    }

    private lateinit var timer: Timer

    val fL = MotorEx("fL")
    val fR = MotorEx("fR").reversed()
    val bL = MotorEx("bL")
    val bR = MotorEx("bR").reversed()

    val far12: Far12 by lazy { Far12(alliance) }

    override fun onInit() {
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


    override fun onStop() {
    }

    /**
     * Executes the autonomous sequence once the start button is pressed.
     * The sequence involves:
     * 1. Initial shot.
     * 2. Intaking and scoring from Row 2 and Row 1.
     * 3. Continuous human player intake cycles until the end of the match.
     * 4. Parking.
     */
    override fun onStartButtonPressed() {
        far12.init()

        follower.pose = (far12.startPoint)

        timer = Timer()

        if(balls == 12.0) {
            SequentialGroupLocal(
                Flywheels.spin(),
                Delay(2.2.seconds),
                Outtake.shoot(),
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
                Outtake.shoot(),
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
                Outtake.shoot(),
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
                            Outtake.shoot(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shoot(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shoot(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shoot(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shoot(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shoot(),
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
                Delay(2.2.seconds),
                Outtake.shoot(),

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
                Outtake.shoot(),
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
                            Outtake.shoot(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shoot(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shoot(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shoot(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shoot(),
                            Delay(0.3.seconds)
                        ),
                        SequentialGroupLocal(
                            Intake.runIntake(),
                            FollowPath(far12.HumanPlayerIntake),
                            FollowPath(far12.HumanPlayerShoot),
                            Delay(0.5.seconds),
                            Outtake.shoot(),
                            Delay(0.3.seconds)
                        ),
                    )
                ),

                FollowPath(far12.Park),
            ).schedule()
        }
    }

    /**
     * Periodic update loop that provides real-time telemetry of the follower's position.
     */
    public override fun onUpdate() {

        telemetry.run {
            addData("follower X", follower.pose.x)
            addData("follower Y", follower.pose.y)
            addData("follower heading", follower.heading)
            addData("timer", timer.elapsedTimeSeconds)
            update()
        }
    }
}
