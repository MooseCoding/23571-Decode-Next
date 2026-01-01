package org.firstinspires.ftc.teamcode.next.subsystems

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.vision.VisionPortal
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor

object TestCamera: Subsystem {
    //@Disabled
    object ConceptAprilTagEasy : LinearOpMode() {
        var Rotation = 0.0
        var Distance = 0.0
        var Target = 20
        var targetAcquired = false

        private lateinit var aprilTag: AprilTagProcessor

        private lateinit var visionPortal: VisionPortal

        override fun runOpMode() {
            initAprilTag()

            // Wait for the DS start button to be touched.
            waitForStart()

            if (opModeIsActive()) {
                while (opModeIsActive()) {
                    telemetryAprilTag()

                    // Push telemetry to the Driver Station.
                    telemetry.update()

                    // Save CPU resources; can resume streaming when needed.
                    when {
                        Outtake.manual -> visionPortal.stopStreaming()
                        !Outtake.manual -> visionPortal.resumeStreaming()
                    }

                    // Share the CPU.
                    sleep(20)
                }
            }

            // Save more CPU resources when camera is no longer needed.
            visionPortal.close()
        }

        /**
         * Initialize the AprilTag processor.
         */
        private fun initAprilTag() {
            // Create the AprilTag processor the easy way.
            aprilTag = AprilTagProcessor.easyCreateWithDefaults()

            // Create the vision portal the easy way.
            visionPortal = VisionPortal.Builder()
                .setCamera(ActiveOpMode.hardwareMap.get(WebcamName::class.java, "The Eye")) // Webcam name from config
                .addProcessor(aprilTag)
                .enableLiveView(true)
                .build()
        }

        /**
         * Add telemetry about AprilTag detections.
         */
        private fun telemetryAprilTag() {
            val currentDetections: List<AprilTagDetection> = aprilTag.detections
            telemetry.addData("# AprilTags Detected", currentDetections.size)

            // Step through the list of detections and display info for each one.
            for (detection in currentDetections) {
                if (detection.metadata.id == Target) {
                    Rotation = detection.ftcPose.x
                    Distance = detection.ftcPose.y
                    targetAcquired = true
                }else{
                    Rotation = 0.0
                    Distance = 100.0
                    targetAcquired = false
                }
            }

        }
    }

}