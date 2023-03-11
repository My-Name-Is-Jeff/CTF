package mynameisjeff.sbmeventplugin.game

import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.taskRunLater
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer
import org.bukkit.event.EventPriority
import org.bukkit.event.entity.PlayerDeathEvent

fun loadAutoRespawn() {
    listen<PlayerDeathEvent>(priority = EventPriority.LOWEST, ignoreCancelled = true) {
        taskRunLater(1) {
            (it.player as CraftPlayer).handle.connection.handleClientCommand(ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN))
        }
    }
}