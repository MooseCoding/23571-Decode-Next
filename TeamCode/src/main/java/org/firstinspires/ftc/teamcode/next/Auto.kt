package org.firstinspires.ftc.teamcode.next

import com.bylazar.telemetry.JoinedTelemetry
import com.bylazar.telemetry.PanelsTelemetry
import com.pedropathing.geometry.BezierCurve
import com.pedropathing.geometry.BezierLine
import com.pedropathing.geometry.Pose
import com.pedropathing.paths.PathBuilder
import com.pedropathing.paths.PathChain
import com.qualcomm.robotcore.eventloop.opmode.Autonomous
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.hardware.impl.MotorEx
import org.firstinspires.ftc.teamcode.pedroPathing.Constants


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

    var builder: PathBuilder = PathBuilder(follower)

    var line1: PathChain = builder
        .addPath(
            BezierCurve(
                Pose(25.684, 125.895),
                Pose(85.053, 83.158),
                Pose(38.947, 83.368)
            )
        )
        .setTangentHeadingInterpolation()
        .build()

    var line2: PathChain = builder
        .addPath(BezierLine(Pose(38.947, 83.368), Pose(23.579, 83.579)))
        .setTangentHeadingInterpolation()
        .build()

    var line3: PathChain = builder
        .addPath(BezierLine(Pose(23.579, 83.579), Pose(49.684, 97.684)))
        .setTangentHeadingInterpolation()
        .build()

    var line4: PathChain = builder
        .addPath(
            BezierCurve(
                Pose(49.684, 97.684),
                Pose(66.947, 59.579),
                Pose(38.947, 60.211)
            )
        )
        .setTangentHeadingInterpolation()
        .build()

    var line5: PathChain = builder
        .addPath(BezierLine(Pose(38.947, 60.211), Pose(14.316, 60.421)))
        .setTangentHeadingInterpolation()
        .build()

    var line6: PathChain = builder
        .addPath(BezierLine(Pose(14.316, 60.421), Pose(49.684, 97.684)))
        .setTangentHeadingInterpolation()
        .build()

    var line7: PathChain = builder
        .addPath(
            BezierCurve(
                Pose(49.684, 97.684),
                Pose(74.737, 35.579),
                Pose(38.947, 35.368)
            )
        )
        .setTangentHeadingInterpolation()
        .build()

    var line8: PathChain = builder
        .addPath(BezierLine(Pose(38.947, 35.368), Pose(17.895, 35.368)))
        .setTangentHeadingInterpolation()
        .build()

    var line9: PathChain = builder
        .addPath(BezierLine(Pose(17.895, 35.368), Pose(49.684, 97.684)))
        .setTangentHeadingInterpolation()
        .build()

    var line10: PathChain = builder
        .addPath(BezierLine(Pose(49.684, 97.684), Pose(49.684, 71.789)))
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

    fun autonomousPathUpdate() {
        when (pathState) {
            0 -> {
                Mercurial.runCatching {
                    OuttakeClaw.pitchUp().schedule()
                }
                follower.followPath(path.getPath(0), true)
                setPathState(1)
            }

            1 -> {
                if(!i) {
                    turnFrom(Math.PI)
                i = true
                }
            }
        }
    }

    override fun onInit() {
        super.onInit()
    }

    override fun onUpdate() {

    }

    
}
