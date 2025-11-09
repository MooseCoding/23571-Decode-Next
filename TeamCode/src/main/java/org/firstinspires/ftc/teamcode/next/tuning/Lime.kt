package org.firstinspires.ftc.teamcode.next.tuning

import com.qualcomm.hardware.limelightvision.LLFieldMap.Fiducial
import com.qualcomm.hardware.limelightvision.LLResult
import com.qualcomm.hardware.limelightvision.LLResultTypes.FiducialResult
import com.qualcomm.hardware.limelightvision.Limelight3A
import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp

@TeleOp
@Disabled
class Lime: OpMode() {
    lateinit var ll:Limelight3A

    override fun init() {
        ll = hardwareMap.get(Limelight3A::class.java, "ll")
        ll.setPollRateHz(90)
        ll.pipelineSwitch(0)
        ll.start()
    }

    override fun loop() {
        var r = ll.latestResult

        if (r!=null && r.isValid) {
            var x = r.tx
            var y = r.ty
            var ta = r.ta

            telemetry.addData("targetx", x);

            var f: List<FiducialResult> = r.fiducialResults

            for(fr in f) {
                var id = fr.fiducialId
                var fx = fr.targetXDegrees
                var strafe = fr.robotPoseTargetSpace.position.x
                telemetry.addData("Fidiciual is", id)
            }
        }
        else {
            telemetry.addData("No targets", "wah")
        }

        telemetry.update()
    }
}