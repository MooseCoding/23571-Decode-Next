package org.firstinspires.ftc.teamcode

import com.qualcomm.robotcore.eventloop.opmode.Disabled
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import org.psilynx.psikit.core.Logger
import org.psilynx.psikit.core.rlog.RLOGServer
import org.psilynx.psikit.core.rlog.RLOGWriter
import org.psilynx.psikit.ftc.PsiKitOpMode

@Disabled
class Tester : PsiKitOpMode() {
    override fun psiKit_init() {
        Logger.addDataReceiver(RLOGServer())
        Logger.addDataReceiver(RLOGWriter("log.rlog"))

        Logger.recordMetadata("some metadata", "string value")
    }

    override fun psiKit_init_loop() {
        /*

         init loop logic goes here

        */
    }

    override fun psiKit_start() {
        // start logic here
    }

    override fun psiKit_loop() {
        /*

                OpMode logic goes here

               */

        Logger.recordOutput("OpMode/example", 2.0)

        // example
    }

    override fun psiKit_stop() {
    }
}