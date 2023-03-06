package mynameisjeff.sbmeventplugin.game

import com.booksaw.betterTeams.Team
import mynameisjeff.sbmeventplugin.Utils.getArrowFor
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.event.listen
import net.axay.kspigot.event.register
import net.axay.kspigot.event.unregister
import net.axay.kspigot.extensions.bukkit.toComponent
import net.axay.kspigot.runnables.firstAsync
import net.axay.kspigot.runnables.thenSync
import org.bukkit.GameMode
import org.bukkit.event.player.PlayerMoveEvent

fun loadNearestPlayerTracking() {
    toggled
    listener
    command
}

private var toggled = false

private val listener = listen<PlayerMoveEvent>(register = false) { e ->
    firstAsync {
        val myTeam = Team.getTeam(e.player)

        e.player.world.players.filter { p -> p != e.player && p.gameMode == GameMode.SURVIVAL && myTeam?.members?.contains(p) != true && !p.getMetadata("vanished").any { it.asBoolean() } }.associateWith { it.location.distance(e.to) }.minByOrNull { it.value }.let {
            if (it == null) {
                return@let "§cNobody's nearby!".toComponent()
            }
            val (nearestEnemy, enemyDistance) = it

            if (enemyDistance >= 250) {
                return@let "§cNobody's nearby!".toComponent()
            }

            val arrow = e.player.getArrowFor(nearestEnemy.location)
            return@let "§aTracking ".toComponent().append(nearestEnemy.teamDisplayName()).append(" §f§l${enemyDistance.toInt()}m ${arrow.char}".toComponent())
        }
    }.thenSync {
        e.player.sendActionBar(it)
    }.execute()
}

private val command = command("togglenearestplayer") {
    requires {
        it.hasPermission(3)
    }
    runs {
        if (toggled) {
            listener.unregister()
            sender.bukkitSender.sendMessage("§cNearest player tracking disabled.")
        } else {
            listener.register()
            sender.bukkitSender.sendMessage("§aNearest player tracking enabled.")
        }
        toggled = !toggled
    }
}