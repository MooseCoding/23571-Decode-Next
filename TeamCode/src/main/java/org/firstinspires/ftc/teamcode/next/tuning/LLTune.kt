package org.firstinspires.ftc.teamcode.next.tuning

import com.pedropathing.geometry.Pose
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.components.BindingsComponent
import dev.nextftc.core.components.SubsystemComponent
import dev.nextftc.extensions.pedro.PedroComponent
import dev.nextftc.ftc.NextFTCOpMode
import dev.nextftc.ftc.components.BulkReadComponent
import org.firstinspires.ftc.teamcode.next.subsystems.Limelight
import org.firstinspires.ftc.teamcode.pedroPathing.Constants

@TeleOp
class LLTune:NextFTCOpMode() {
    init {
        addComponents(
            PedroComponent(Constants::createFollower),
            SubsystemComponent(Limelight),
            BulkReadComponent,
            BindingsComponent
        )
    }

    override fun onInit() {
        PedroComponent.follower.setStartingPose(Pose(0.0,0.0,0.0))
    }

    override fun onUpdate() {
        val r = Limelight.grabResultData()

        if (r!=null) {
            telemetry.addData("r.x", r.tx)
            telemetry.addData("r.y", r.ty)
            telemetry.addData("r.a", r.ta)
            telemetry.addData("fiducial", r.fiducialResults[0].fiducialId)
        }

        val p = PedroComponent.follower.pose

        telemetry.run {
            addData("Pose", p)
        }

        telemetry.update()
    }
}