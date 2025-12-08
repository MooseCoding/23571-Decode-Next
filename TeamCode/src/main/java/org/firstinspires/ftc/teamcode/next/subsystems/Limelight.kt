package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.geometry.Pose
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
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

    fun motif(): Motif? {
        val fR = grabResultData()
        if(fR != null) {
            val f = fR.fiducialResults[0]
            return when(f.fiducialId) {
                21 -> Motif.GPP
                22 -> Motif.PGP
                else -> Motif.PPG
            }
        }
        return null
    }

    fun grabResultData(): LLResult? {
        var lR = ll.latestResult
        if (lR != null && lR.isValid) {
            return lR
        }
        return null
    }

    fun megaTag(): Pose? {
        var lR = grabResultData() ?: return null // Return null if the tag is null
        val botpose_mt2 = lR.botpose_MT2

        if (botpose_mt2 != null) {
            return Pose(botpose_mt2.position.x, botpose_mt2.position.y)
        }

        return null
    }
}