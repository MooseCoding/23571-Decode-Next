package org.firstinspires.ftc.teamcode.next.subsystems

import com.bylazar.configurables.annotations.Configurable
import com.pedropathing.geometry.Pose
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import dev.nextftc.core.commands.utility.InstantCommand
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.extensions.pedro.PedroComponent.Companion.follower
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

    fun motif(): Motif? {
        val fR = ll.latestResult.fiducialResults
        if(fR.isNotEmpty()) {
            val f = fR[0]
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
        val botpose_mt = lR.botpose

        if (botpose_mt != null) {
            return Pose(botpose_mt.position.x, botpose_mt.position.y)
        }

        return null
    }
}