package org.firstinspires.ftc.teamcode.next.subsystems

import dev.nextftc.core.subsystems.Subsystem
import dev.nextftc.ftc.ActiveOpMode
import org.firstinspires.ftc.teamcode.next.subsystems.data.Artifact
import org.firstinspires.ftc.teamcode.next.subsystems.data.Motif

object Spindexer: Subsystem {
    var targetMotif: Motif? = null
    var motifMode:Boolean = false // Determine if the spindexer should sort for a motif
    var currentMotifShot:Array<Artifact?> = arrayOf(null, null, null)
    var ballsHeld:Array<Artifact?> = arrayOf(null, null, null) // First position is intake, third is turret/transfer, second is the other

    init {
        ballsHeld = arrayOf(Artifact.GREEN, Artifact.PURPLE, Artifact.PURPLE)
    }

    override fun initialize() {

    }

    override fun periodic() {
        if (ActiveOpMode.opModeInInit) {
            targetMotif = Limelight.motif()
        }

        if(ActiveOpMode.isStarted) {
            if(motifMode) {

            }
            else {

            }
        }
    }
}