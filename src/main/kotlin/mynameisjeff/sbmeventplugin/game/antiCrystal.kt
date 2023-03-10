package mynameisjeff.sbmeventplugin.game

import net.axay.kspigot.event.listen
import org.bukkit.entity.EnderCrystal
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByBlockEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause

fun loadAntiCrystal() {
    listen<EntityDamageEvent> { e ->
        if (e.entity !is Player) return@listen

        if (e is EntityDamageByEntityEvent && e.cause == DamageCause.ENTITY_EXPLOSION && e.damager is EnderCrystal) {
            e.isCancelled = true
            return@listen
        } else if (e is EntityDamageByBlockEvent && e.cause == DamageCause.BLOCK_EXPLOSION) {
            e.isCancelled = true
            return@listen
        }
    }
}