package mynameisjeff.sbmeventplugin.game

import mynameisjeff.sbmeventplugin.doTelekinesis
import net.axay.kspigot.event.listen
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

fun loadTelekinesis() {
    listen<BlockBreakEvent>(priority = EventPriority.HIGHEST, ignoreCancelled = true) {event ->
        if (event.player.gameMode == GameMode.CREATIVE) return@listen
        event.player.giveExp(event.expToDrop, true)
        event.expToDrop = 0
    }
    listen<BlockDropItemEvent>(priority = EventPriority.HIGHEST, ignoreCancelled = true) {event ->
        if (event.player.gameMode == GameMode.CREATIVE) return@listen
        event.items.forEach {
            event.player.doTelekinesis(it.itemStack)
        }
        event.items.clear()
    }
    listen<EntityDeathEvent>(priority = EventPriority.HIGHEST, ignoreCancelled = true) {event ->
        val entity = event.entity
        val killer = entity.killer
        if (killer == null || entity is Player) return@listen
        killer.giveExp(event.droppedExp, true)
        event.drops.forEach(killer::doTelekinesis)
        event.drops.clear()
        event.droppedExp = 0
    }
}