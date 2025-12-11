package org.firstinspires.ftc.teamcode.next.subsystems.helpers

import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.delays.Delay
import kotlin.time.Duration.Companion.seconds

class Waiter {
    lateinit var waitCommand:Command

    fun wait(t:Double) {
        waitCommand = Delay(t.seconds)
        waitCommand.schedule()
    }

    fun isDone(): Boolean {
        return waitCommand.isDone
    }
}