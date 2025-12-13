package org.firstinspires.ftc.teamcode.helpers

import dev.nextftc.core.commands.Command
import dev.nextftc.core.commands.groups.CommandGroup
/**
 * A [CommandGroup] that runs its children one at a time.
 */
class SequentialGroupLocal(vararg commands: Command) : CommandGroup(*commands) {
    init {
        named("SequentialGroup(${children.joinToString { it.name }})")
    }
    /**
     * This returns true once all of its children have finished running.
     */
    override val isDone: Boolean
        get() = children.isEmpty()

    /**
     * In a Sequential Group, we will start the first command and wait until it has completed
     * execution before starting the next.
     */
    override fun start() {
        children.first().start()
    }

    /**
     * Now, every update we must check if the currently active command is complete. If it is, remove
     * it and start the next one (if there is one).
     */
    override fun update() {
        if (children.isEmpty()) {
            return
        }

        children.first().update()

        if (!children.first().isDone) return

        children.removeFirst().stop(false)

        if (children.isNotEmpty()) children.first().start()
    }

    override fun stop(interrupted: Boolean) {
        if (children.isNotEmpty()) children.first().stop(interrupted)

        super.stop(interrupted)
    }
}