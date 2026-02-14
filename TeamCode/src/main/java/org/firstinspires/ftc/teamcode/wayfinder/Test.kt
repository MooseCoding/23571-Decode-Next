package org.firstinspires.ftc.teamcode.wayfinder

import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.util.ElapsedTime
import dev.nextftc.core.commands.delays.Delay
import dev.nextftc.core.commands.delays.WaitUntil
import dev.nextftc.core.commands.groups.ParallelDeadlineGroup
import dev.nextftc.core.commands.groups.ParallelGroup
import dev.nextftc.core.commands.groups.ParallelRaceGroup
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.Gamepads
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import org.firstinspires.ftc.teamcode.next.subsystems.DriveTrain
import org.firstinspires.ftc.teamcode.next.subsystems.FlywheelLight
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.Light
import org.firstinspires.ftc.teamcode.next.subsystems.Outtake
import org.firstinspires.ftc.teamcode.next.subsystems.Pinpoint
import org.firstinspires.ftc.teamcode.next.subsystems.Transfer
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Flywheels
import org.firstinspires.ftc.teamcode.next.subsystems.outtake.Turret
import org.firstinspires.ftc.teamcode.next.tuning.Drive
import org.firstinspires.ftc.teamcode.pedroPathing.Constants
import org.firstinspires.ftc.teamcode.pedroPathing.Constants.createFollower
import kotlin.math.PI
import kotlin.time.Duration.Companion.seconds

@Autonomous
@Disabled
class Test: NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(
                Pinpoint, FlywheelLight, Turret, DriveTrain, Outtake
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
        Pinpoint.init()
        wayfinder = MotorWayfinder(bL, bR, fR, fL, Pinpoint.pinpoint)
    }

    enum class Auto {
        SHOOT,
        MOVE1,
        INTAKE1,
        GATE,
        SHOOT1,
        MOVE2,
        INTAKE2,
        SHOOT2,
        INTAKE3,
        SHOOT3,
        CYCLE,
        PARK
    }

    var state: Auto = Auto.SHOOT

    val timer: ElapsedTime = ElapsedTime()

    var start = Pose(50.0, 90.0, PI/2)

    fun switchToBlue() {
        start = Pose(50.8, 100.0, PI/3)
    }

    var alliance: Alliance = Alliance.RED

    override fun waitForStart() {
        Gamepads.gamepad1.triangle whenBecomesTrue { alliance = when(alliance){
            Alliance.RED -> Alliance.BLUE
            Alliance.BLUE -> Alliance.RED
        }}
    }

    override fun onWaitForStart() {
        // telemetry.setDisplayFormat(Telemetry.DisplayFormat.HTML)


        if(gamepad1.triangleWasPressed()) {
            alliance = when(alliance) {
                Alliance.RED -> Alliance.BLUE
                Alliance.BLUE -> Alliance.RED
            }
        }

        telemetry.run {
            addData("Alliance", alliance)
            addLine("Gamepad 1 Triangle: Cycle Alliance Color")
            update()
        }
    }

    override fun onStartButtonPressed() {
        when(alliance) {
            Alliance.BLUE -> switchToBlue()
            else -> {}
        }

        DriveTrain.alliance = alliance
        PedroComponent.follower.setStartingPose(PedroFromPose2D((FarPoses.park)))


        Pinpoint.setPose(FarPoses.park)

        timer.reset()

        SequentialGroupLocal(

            WaitUntil { timer.seconds() > 4.0 },
            InstantCommand {
                state = Auto.PARK
            },
            // WayfinderDrive(wayfinder, FarPoses.park),
            FlywheelLight.Sage(),
        ).schedule()
    }

    override fun onUpdate() {
        telemetry.run {
            addData("Timer", timer.seconds())
            addData("State", state)
            addData("X", Pinpoint.getX())
            addData("Y", Pinpoint.getY())
            addData("H", Pinpoint.getHeading())
            addData("Park X", FarPoses.park.getX(DistanceUnit.INCH))
            addData("Park Y", FarPoses.park.getY(DistanceUnit.INCH))
            addData("Park H", FarPoses.park.getHeading(AngleUnit.RADIANS))
            addData("Is wayfinder done", wayfinder.atTarget)
            update()
        }
    }
}