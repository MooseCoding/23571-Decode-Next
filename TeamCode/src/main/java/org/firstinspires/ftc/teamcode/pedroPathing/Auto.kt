package org.firstinspires.ftc.teamcode.pedroPathing

import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.follower.Follower
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathBuilder
import com.pedropathing.util.Timer
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.hardware.impl.MotorEx


@Autonomous
class Auto() : NextFTCOpMode() {
    val telem = JoinedTelemetry(telemetry, PanelsTelemetry.ftcTelemetry)
    val fL = MotorEx("frontLeft").reversed()
    val fR = MotorEx("frontRight")
    val bL = MotorEx("backLeft").reversed()
    val bR = MotorEx("backRight")
    val follower = Constants.createFollower(hardwareMap)
    private var pathTimer: Timer? = null
    private var opmodeTimer: Timer? = null
    private var pathState = 0
    var startingPose = Pose() // Starting pose
    var targetX: Double = 0.0 // TargetX Pose

    var builder: PathBuilder = PathBuilder(follower)

    var Path1 = follower // Move to Shooting Position with Preloads
        .pathBuilder()
        .addPath(
            BezierLine(Pose(24.541, 126.802), Pose(40.000, 110.559))
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path2 = follower // Head to pickup 2 Purple Balls from line closest to classifier
        .pathBuilder()
        .addPath(
            BezierCurve(
                Pose(40.000, 110.559),
                Pose(66.507, 81.982),
                Pose(39.692, 83.807)
            )
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path3 = follower // Forwards to intake the 2 purple balls
        .pathBuilder()
        .addPath(
            BezierLine(Pose(39.692, 83.807), Pose(14.168, 83.534))
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path4 = follower // Head back to inside the loading zone
        .pathBuilder()
        .addPath(
            BezierLine(Pose(14.168, 83.534), Pose(56.071, 84.353))
        )
        .setConstantHeadingInterpolation(Math.toRadians(180.0))
        .build()

    var Path5 = follower // Head to grab 1 green and then 1 purple ball on the furthest line
        .pathBuilder()
        .addPath(
            BezierCurve(
                Pose(56.071, 84.353),
                Pose(59.655, 34.258),
                Pose(40.000, 35.352)
            )
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path6 = follower // Forwards to intake the 1 Green then 1 Purple
        .pathBuilder()
        .addPath(
            BezierLine(Pose(40.000, 35.352), Pose(11.301, 35.625))
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path7 = follower // Drive backwards to the tip of the far shooting zone
        .pathBuilder()
        .addPath(
            BezierLine(Pose(11.301, 35.625), Pose(71.768, 20.337))
        )
        .setConstantHeadingInterpolation(Math.toRadians(180.0))
        .build()

    var Path8 = follower // Head to pickup 1 Purple then 1 Green Ball
        .pathBuilder()
        .addPath(
            BezierCurve(
                Pose(71.768, 20.337),
                Pose(71.823, 60.364),
                Pose(40.000, 59.784)
            )
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path9 = follower // Forwards to intake 1 purple then 1 green ball
        .pathBuilder()
        .addPath(
            BezierLine(Pose(40.000, 59.784), Pose(11.574, 59.647))
        )
        .setTangentHeadingInterpolation()
        .build()

    var Path10 =  follower // Move backwards to the tip of the closer shooting zone
        .pathBuilder()
        .addPath(
            BezierLine(Pose(11.574, 59.647), Pose(72.859, 74.525))
        )
        .setConstantHeadingInterpolation(Math.toRadians(180.0))
        .build()

    var Path11 = follower // Move out of the shooting zone
        .pathBuilder()
        .addPath(
            BezierLine(Pose(72.859, 74.525), Pose(51.623, 61.782))
        )
        .setTangentHeadingInterpolation()
        .build()


    fun turn(radians:Double) {
        var temp: Pose = Pose(follower.pose.x, follower.pose.y, radians)
        follower.holdPoint(temp)
    }

    fun turnFrom(radians:Double) {
        var originalHeading: Double = follower.pose.heading + radians
        var temp: Pose = Pose(follower.pose.x, follower.pose.y, follower.pose.heading + radians)
        follower.holdPoint(temp)
    }

    fun setPathState(pState: Int) {
        pathState = pState
        pathTimer!!.resetTimer()
    }

    lateinit var target: Pose
    var x:Boolean = false
    var y:Boolean = false

    var i:Boolean = false

    private fun autonomousPathUpdate() {
        when (pathState) {
            0 -> {

            }

            1 -> {
                if(!i) {
                    turnFrom(Math.PI)
                i = true
                }
            }
            2 -> {

            }
            3 -> {

            }
            4 -> {

            }
            5 -> {

            }
        }
    }

    override fun onInit() {
        pathTimer = Timer()
        opmodeTimer = Timer()

        opmodeTimer!!.resetTimer()

        follower.setStartingPose(startingPose)
        follower.update()
    }

    override fun onStartButtonPressed() {

    }

    override fun onUpdate() {
        follower.update()
        autonomousPathUpdate()

        telemetry.run {
            addData("Follower X", follower.pose.x)
            addData("Follower Y", follower.pose.y)
            addData("Follower Heading", follower.pose.heading)
            addLine()
            addData("Path State", pathState)
            addData("Path timer", pathTimer!!.elapsedTimeSeconds)
            addData("Target X", targetX)
        }
    }

    
}
