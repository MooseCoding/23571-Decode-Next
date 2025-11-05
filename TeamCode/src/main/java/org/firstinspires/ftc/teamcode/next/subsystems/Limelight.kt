package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.geometry.Pose
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D
import org.firstinspires.ftc.teamcode.next.subsystems.data.Motif

@Configurable
object Limelight: Subsystem {
    lateinit var ll: Limelight3A

    @JvmField
    var limelightOn:Boolean = true

    @JvmField
    var grabMegaTag = false

    @JvmField
    var m: Motif = Motif.NONE

    override fun initialize() {
        ll = ActiveOpMode.hardwareMap.get(Limelight3A::class.java, "ll")
        ll.setPollRateHz(100)
        ll.pipelineSwitch(0)
        ll.start()
    }

    val motif = InstantCommand {
        val fR = ll.latestResult.fiducialResults
        if(fR.isNotEmpty()) {
            val f = fR[0]
            m = when(f.fiducialId) {
                21 -> Motif.GPP
                22 -> Motif.PGP
                else -> Motif.PPG
            }
        }
    }

    fun grabResultData(): LLResult? {
        var lR = ll.latestResult
        if (lR != null && lR.isValid) {
            return lR
        }
        return null
    }

    fun megaTag(): Pose? {
        var lR = ll.latestResult
        val yaw = DriveTrain.currentPose.heading
        ll.updateRobotOrientation(yaw)
        if (lR != null && lR.isValid) {
            val botpose_mt2 = lR.botpose_MT2

            if (botpose_mt2 != null) {
                return Pose(botpose_mt2.position.x, botpose_mt2.position.y, yaw)
            }
        }
        return null
    }
}