package mynameisjeff.sbmeventplugin.game

import mynameisjeff.sbmeventplugin.doTelekinesis
import net.axay.kspigot.event.listen
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent

fun loadTelekinesis() {
    listen<BlockBreakEvent>(priority = EventPriority.LOWEST) {event ->
        if (event.isCancelled || !event.isDropItems || event.player.gameMode == GameMode.CREATIVE) return@listen
        val drops = event.block.getDrops(event.player.inventory.itemInMainHand)
        if (drops.isEmpty()) return@listen
        drops.forEach(event.player::doTelekinesis)
        event.player.giveExp(event.expToDrop, true)
        event.isDropItems = false
        event.expToDrop = 0
    }
    listen<EntityDeathEvent>(priority = EventPriority.LOWEST) {event ->
        val entity = event.entity
        val killer = entity.killer
        if (killer == null || entity is Player) return@listen
        killer.giveExp(event.droppedExp, true)
        event.drops.forEach(killer::doTelekinesis)
        event.drops.clear()
        event.droppedExp = 0
    }
}