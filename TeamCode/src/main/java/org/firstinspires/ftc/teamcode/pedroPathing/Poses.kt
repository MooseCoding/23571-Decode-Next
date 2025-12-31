package org.firstinspires.ftc.teamcode.pedroPathing

import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import dev.nextftc.core.commands.groups.ParallelGroup
import org.firstinspires.ftc.teamcode.helpers.SequentialGroupLocal
import dev.nextftc.extensions.pedro.FollowPath
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import org.firstinspires.ftc.teamcode.next.subsystems.Intake
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Alliance
import kotlin.math.PI

public class Far12(a: Alliance) {
    companion object {
        // These poses are for the red alliance

        var start = Pose(89.0,9.0, PI/2)
        var row1 = Pose(110.0, 35.5, 0.0)// Row closest to the front of the field
        var row1CP = Pose(88.0, 35.35)
        var row1End = Pose(129.0, 35.5, 0.0) // End of row 1 (e.g. where we stop intake)
        var row1EndCP = Pose(84.5, 14.0)

        var row2 = Pose(110.0, 59.5, 0.0) // Row seconds closest
        var row2End = Pose(129.0, 59.5, 0.0)
        var row3 = Pose(110.0, 83.5, 0.0) // Row closest to the classifier
        var row3End = Pose(129.0, 83.5, 0.0)
        var shootFar = Pose(84.5, 18.0, PI/2)
        var park = Pose(110.0,15.0,0.0)

        var humanPlayerStart = Pose(134.5, 17.0)
        var humanPlayerEnd = Pose(137.5, 6.42, 0.0)
    }

    var index = 0
    var pathCount = 11
    init {
        if (a == Alliance.BLUE) {
            row1 = flipPose(row1)
            start = flipPose(start)
            row1End = flipPose(row1End)
            shootFar = flipPose(shootFar)
            park = flipPose(park)
            humanPlayerStart = flipPose(humanPlayerStart)
            humanPlayerEnd = flipPose(humanPlayerEnd)
        }
    }

    fun flipPose(p:Pose): Pose {
        return Pose(144-p.x, p.y)
    }

    var StartToRow1 = SequentialGroupLocal(
        FollowPath(follower.pathBuilder()
            .addPath(
                BezierCurve(
                    shootFar,
                    row1CP,
                    row1
                )
            )
            .setLinearHeadingInterpolation(PI/2, 0.0, 0.8)
            .build()
    ))
    var Row1Intake = SequentialGroupLocal(
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
    var Row1ToShoot = SequentialGroupLocal(
        ParallelGroup(
            FollowPath(
                follower.pathBuilder()
                    .addPath(
                        BezierCurve(
                            row1End,
                            shootFar
                        )
                    )
                    .setLinearHeadingInterpolation(0.0, 55.0, 0.8)
                    .build()
            )
        ),
    )
    var ShootToHuman = SequentialGroupLocal(
        FollowPath(
            follower.pathBuilder()
                    .addPath(
                        BezierCurve(
                            shootFar, 
                            Pose(116.02, 48.18),
                            humanPlayerStart
                        )
                    )
                .setLinearHeadingInterpolation(55.0, 280.0, 0.8)
                .build()
        )
    )
    var HumanIntake = SequentialGroupLocal(
        FollowPath(
            follower.pathBuilder()
                .addPath(
                    BezierLine(
                        humanPlayerStart,
                        humanPlayerEnd
                    )
                ).setConstantHeadingInterpolation(280.0) 
            .build()
        )
    )
    var ToPark = SequentialGroupLocal(
        FollowPath(
            follower.pathBuilder()
                .addPath(
                    BezierLine(
                        shootFar,
                        park
                    )
                )
                .setLinearHeadingInterpolation(55.0, 0.0,0.8)
                .build()
        )
    )
    var anywhereToPark = SequentialGroupLocal(
        FollowPath(
            follower.pathBuilder()
                .addPath(
                    BezierLine(
                        follower.pose,
                        park
                    )
                )
                .setConstantHeadingInterpolation(0.0)
                .build()
        )
    )

    // Old Stuff


    var ShootStart = SequentialGroupLocal(
        FollowPath(
            follower.pathBuilder().addPath(
                BezierLine(
                    start,
                    shootFar
                )
            ).setConstantHeadingInterpolation(Math.PI/2).build()
        )
    ) // Move to Shooting Position with Preloads while shooting the preloads
    
    var ShootToRow2 = SequentialGroupLocal(
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
    var Row2Intake = SequentialGroupLocal(
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
    var Row2ToShoot = SequentialGroupLocal(
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
    var ShootToRow3 = SequentialGroupLocal(
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
    var Row3Intake = SequentialGroupLocal(
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
    var Row3ToShoot = SequentialGroupLocal(
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

    var StartToPark = SequentialGroupLocal(
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

    fun Next(): SequentialGroupLocal {
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
