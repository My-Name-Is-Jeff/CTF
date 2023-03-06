package mynameisjeff.sbmeventplugin.game

import com.booksaw.betterTeams.Team
import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent
import mynameisjeff.sbmeventplugin.isPlaying
import mynameisjeff.sbmeventplugin.isVanished
import net.axay.kspigot.chat.sendText
import net.axay.kspigot.event.listen
import net.axay.kspigot.runnables.taskRunLater
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerGameModeChangeEvent


fun loadSpectatorLock() {
    listen<PlayerStartSpectatingEntityEvent> { e ->
        if (e.player.isVanished) return@listen
        if (e.newSpectatorTarget !is Player) {
            e.player.sendText {
                text("§cYou can only spectate players.")
            }
            e.isCancelled = true
            return@listen
        }
        val myTeam = Team.getTeam(e.player) ?: return@listen
        if (!myTeam.members.contains(e.newSpectatorTarget as Player) && myTeam.onlineMemebers.any { it != e.player && it.isPlaying }) {
            e.player.sendText {
                text("§cYou still have alive teammates, so you can't start spectating")
                component(e.newSpectatorTarget.teamDisplayName())
                text("§c.")
            }
            e.isCancelled = true
        }
    }
    listen<PlayerStopSpectatingEntityEvent> { e ->
        if (e.player.isVanished || e.player.gameMode != GameMode.SPECTATOR) return@listen
        if (e.spectatorTarget !is Player) return@listen
        val myTeam = Team.getTeam(e.player) ?: return@listen
        if (myTeam.onlineMemebers.any { it != e.player && it.isPlaying }) {
            e.player.sendText {
                text("§cYou still have alive teammates, so you can't stop spectating.")
            }
            if (!(e.spectatorTarget as Player).isOnline) {
                val nearestTeammate = myTeam.onlineMemebers.filter { it != e.player && it.isPlaying }.minByOrNull { it.location.distance(e.player.location) } ?: return@listen
                e.player.sendText {
                    text("§aThe player you were spectating disconnected, so you are now spectating ")
                    component(nearestTeammate.teamDisplayName())
                    text("§a.")
                }
            }
            e.isCancelled = true
        }
    }
    listen<PlayerGameModeChangeEvent> { e ->
        if (e.cause == PlayerGameModeChangeEvent.Cause.HARDCORE_DEATH) {
            val myTeam = Team.getTeam(e.player) ?: return@listen
            val nearestTeammate = myTeam.onlineMemebers.filter { it != e.player && it.isPlaying }.minByOrNull { it.location.distance(e.player.location) }
            if (nearestTeammate != null) {
                taskRunLater(1L) {
                    e.player.spectatorTarget = nearestTeammate
                    e.player.sendText {
                        text("§cYou died!")
                        newLine()
                        text("§aYou still have alive teammates, so you are now spectating ")
                        component(nearestTeammate.teamDisplayName())
                        text("§a.")
                    }
                }
            }
        }
    }
}