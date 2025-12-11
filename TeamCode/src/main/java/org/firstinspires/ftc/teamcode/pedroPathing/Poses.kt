package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import dev.nextftc.core.commands.groups.ParallelGroup
import dev.nextftc.core.commands.groups.SequentialGroup
import dev.nextftc.extensions.pedro.FollowPath
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import kotlin.math.PI

public class Far12(a: Alliance) {
    companion object {
        // These poses are for the red alliance

        var start = Pose(89.0,8.0, PI/2)
        var row1 = Pose(110.0, 35.5, 0.0)// Row closest to the front of the field
        var row1End = Pose(129.0, 35.5, 0.0) // End of row 1 (e.g. where we stop intake)
        var row2 = Pose(110.0, 59.5, 0.0) // Row seconds closest
        var row2End = Pose(129.0, 59.5, 0.0)
        var row3 = Pose(110.0, 83.5, 0.0) // Row closest to the classifier
        var row3End = Pose(129.0, 83.5, 0.0)
        var shootFar = Pose(84.5, 14.0, PI/2)
        var park = Pose(84.5,40.0,PI/2)

        var humanPlayerStart = Pose()
        /*
            // Actual Values
        var
         */
    }

    var index = 0
    var pathCount = 11
    init {
        if (a == Alliance.RED) {
            start = start.mirror()
            row1 = row1.mirror()
            row2 = row2.mirror()
            row3 = row3.mirror()
            shootFar = shootFar.mirror()
        }
    }

    var ShootStart = SequentialGroup(
        FollowPath(
            follower.pathBuilder().addPath(
                BezierLine(
                    start, shootFar
                )
            ).setConstantHeadingInterpolation(Math.PI/2).build()
        )
    ) // Move to Shooting Position with Preloads while shooting the preloads
    var StartToRow1 = SequentialGroup(
        Intake.runIntake,
        FollowPath(follower.pathBuilder()
            .addPath(
                BezierCurve(
                    shootFar,
                    Pose(89.1021, 36.9896),
                    row1
                )
            )
            .setTangentHeadingInterpolation()
            .build()
    ))
    var Row1Intake = SequentialGroup(
        FollowPath(follower.pathBuilder()
            .addPath(
                BezierLine(
                    row1,
                    row1End
                )
            )
            .setConstantHeadingInterpolation(0.0)
            .build()),
    )
    var Row1ToShoot = SequentialGroup(
        Intake.stopIntake,
        ParallelGroup(
            FollowPath(
                follower.pathBuilder()
                    .addPath(
                        BezierLine(
                            row1End,
                            shootFar
                        )
                    )
                    .setTangentHeadingInterpolation().setReversed()
                    .build()
            )
        ),
    )
    var ShootToRow2 = SequentialGroup(
        Intake.runIntake,
        FollowPath(follower.pathBuilder()
            .addPath(
                BezierCurve(
                    shootFar,
                    Pose(84.0519, 59.2379),
                    row2
                )
            )
            .setTangentHeadingInterpolation()
            .build()
    ))
    var Row2Intake = SequentialGroup(
        FollowPath(follower.pathBuilder()
            .addPath(
                BezierLine(
                    row2,
                    row2End
                )
            )
            .setConstantHeadingInterpolation(0.0)
            .build()),
    )
    var Row2ToShoot = SequentialGroup(
        Intake.stopIntake,
        ParallelGroup(
            FollowPath(
                follower.pathBuilder()
                    .addPath(
                        BezierLine(
                            row2End,
                            shootFar
                        )
                    )
                    .setTangentHeadingInterpolation().setReversed()
                    .build()
            )
        ),
    )
    var ShootToRow3 = SequentialGroup(
        Intake.runIntake,
        FollowPath(follower.pathBuilder()
            .addPath(
                BezierCurve(
                    shootFar,
                    Pose(88.4197, 84.4891),
                    row3
                )
            )
            .setTangentHeadingInterpolation()
            .build()
    ))
    var Row3Intake = SequentialGroup(
        FollowPath(follower.pathBuilder()
            .addPath(
                BezierLine(
                    row3,
                    row3End
                )
            )
            .setConstantHeadingInterpolation(0.0)
            .build()),
    )
    var Row3ToShoot = SequentialGroup(
        Intake.stopIntake,
        ParallelGroup(
            FollowPath(
                follower.pathBuilder()
                    .addPath(
                        BezierLine(
                            row3End,
                            shootFar
                        )
                    )
                    .setTangentHeadingInterpolation().setReversed()
                    .build()
            )
        ),
    )
    var ToPark = SequentialGroup(
        FollowPath(
            follower.pathBuilder()
                .addPath(
                    BezierLine(
                        follower.pose,
                        park
                    )
                )
                .setConstantHeadingInterpolation(PI/2)
                .build()
        )
    )
    var StartToPark = SequentialGroup(
        FollowPath(
            follower.pathBuilder()
                .addPath(
                    BezierLine(
                        start,
                        park
                    )
                )
                .setConstantHeadingInterpolation(Math.PI/2)
                .build()
        )
    )

    fun Next(): SequentialGroup {
        return when(index++) {
            1 -> ShootStart
            2 -> StartToRow1
            3 -> Row1Intake
            4 -> Row1ToShoot
            5 -> ShootToRow2
            6 -> Row2Intake
            7 -> Row2ToShoot
            8 -> ShootToRow3
            9 -> Row3Intake
            10 -> Row3ToShoot
            else -> ToPark
        }
    }
}

public class Close12 {

}