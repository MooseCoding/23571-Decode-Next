package org.firstinspires.ftc.teamcode.next.subsystems

import com.acmerobotics.dashboard.config.Config
import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.geometry.Pose
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.LLResultTypes.FiducialResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import dev.nextftc.control2.filters.KalmanFilter
import dev.nextftc.control2.model.LinearModel
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.teamcode.next.subsystems.helpers.Motif

@Config
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

    override fun periodic() {
        val p = megatag2()

        if (p != null) {
            follower.pose = Pose(p.x, p.y, follower.heading)
        }
    }

    fun motif(): Motif {
        val fR = grabResultData()
        if(fR != null) {
            for (f: FiducialResult in fR.fiducialResults) {
                when (f.fiducialId) {
                    21 -> return Motif.GPP
                    22 -> return Motif.PGP
                }
            }
        }
        return Motif.PPG
    }

    fun grabResultData(): LLResult? {
        var lR = ll.latestResult
        if (lR != null && lR.isValid) {
            return lR
        }
        return null
    }

    fun megatag2(): Pose? {
        var lR = grabResultData() ?: return null // Return null if the tag is null
        if(lR.fiducialResults.isEmpty()) return null

        val botpose_mt2 = lR.botpose_MT2

        if (botpose_mt2 != null) {
            return Pose(botpose_mt2.position.x, botpose_mt2.position.y)
        }

        return null
    }
}