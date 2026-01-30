package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.core.commands.delays.WaitUntil
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.extensions.pedro.FollowPath
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.NextFTCOpMode
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal

@Autonomous
class PedroTest: NextFTCOpMode(){
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
        )
    }

    override fun onStartButtonPressed() {
        /*SequentialGroupLocal(
            InstantCommand {
                follower.followPath(
                    follower.pathBuilder()
                        .addPath(
                            BezierLine(
                                follower.pose,
                                Pose(follower.pose.x, follower.pose.y + 10.0, follower.heading)
                            )

                        )
                        .setConstantHeadingInterpolation(follower.heading)
                        .build()
                )
            },
            WaitUntil { !follower.isBusy }
        ).schedule()*/
        SequentialGroupLocal(
            FollowPath(
                follower.pathBuilder()
                    .addPath(
                        BezierLine(
                            follower.pose,
                            Pose(follower.pose.x, follower.pose.y + 10.0, follower.heading)
                        )

                    )
                    .setConstantHeadingInterpolation(follower.heading)
                    .build()
            )
        ).schedule()
    }

    override fun onUpdate() {
        telemetry.run {
            addData("X", follower.pose.x)
            addData("Y", follower.pose.y)
            addData("H", follower.pose.heading)
            update()
        }
    }
}