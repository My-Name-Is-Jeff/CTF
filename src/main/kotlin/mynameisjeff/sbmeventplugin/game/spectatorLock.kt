package mynameisjeff.sbmeventplugin.game

import com.booksaw.betterTeams.Team
import com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent
import com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent
import mynameisjeff.sbmeventplugin.isPlaying
import mynameisjeff.sbmeventplugin.isVanished
import net.axay.kspigot.chat.sendText
import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.runs
import net.axay.kspigot.commands.suggestList
import net.axay.kspigot.event.listen
import net.axay.kspigot.extensions.bukkit.toComponent
import net.axay.kspigot.runnables.taskRunLater
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.commands.arguments.TeamArgument
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerGameModeChangeEvent
import org.bukkit.event.player.PlayerJoinEvent


fun loadSpectatorLock() {
    listen<PlayerJoinEvent> { e ->
        if (e.player.isVanished) return@listen
        if (e.player.gameMode == GameMode.SPECTATOR) {
            val myTeam = Team.getTeam(e.player) ?: return@listen
            if (myTeam.onlineMemebers.none { it != e.player && it.isPlaying }) {
                if (e.player.isOp) {
                    e.player.sendText {
                        text("§aYou joined as spectator and your team has no more alive players, but you are an op so you can still spectate.")
                    }
                } else {
                    e.player.kick("§cYou joined as spectator and your team has no more alive players.".toComponent())
                }
            } else {
                val nearestTeammate = myTeam.onlineMemebers.filter { it != e.player && it.isPlaying }.minByOrNull { it.location.distance(e.player.location) }
                if (nearestTeammate != null) {
                    taskRunLater(1L) {
                        e.player.spectatorTarget = nearestTeammate
                        e.player.sendText {
                            text("§aYou are now spectating ")
                            component(nearestTeammate.teamDisplayName())
                            text("§a.")
                        }
                    }
                }
            }
        }
    }
    listen<PlayerStartSpectatingEntityEvent>(priority = EventPriority.HIGHEST, ignoreCancelled = true) { e ->
        if (e.player.isVanished) return@listen
        if (e.newSpectatorTarget !is Player) {
            e.player.sendText {
                text("§cYou can only spectate players.")
            }
            e.isCancelled = true
            return@listen
        }
        val myTeam = Team.getTeam(e.player) ?: return@listen
        if (myTeam.onlineMemebers.any { it != e.player && it.isPlaying }) {
            if (!myTeam.members.contains(e.newSpectatorTarget as Player)) {
                e.player.sendText {
                    text("§cYou still have alive teammates, so you can't start spectating")
                    component(e.newSpectatorTarget.teamDisplayName())
                    text("§c.")
                }
                e.isCancelled = true
            }
        } else {
            e.player.kick("§cYou started spectating and your team has no more alive players.".toComponent())
        }
    }
    listen<PlayerStopSpectatingEntityEvent>(priority = EventPriority.HIGHEST, ignoreCancelled = true) { e ->
        if (e.player.isVanished || e.player.gameMode != GameMode.SPECTATOR) return@listen
        if (e.spectatorTarget !is Player) return@listen
        val myTeam = Team.getTeam(e.player) ?: return@listen
        if (myTeam.onlineMemebers.any { it != e.player && it.isPlaying }) {
            if (!(e.spectatorTarget as Player).isOnline) {
                val nearestTeammate = myTeam.onlineMemebers.filter { it != e.player && it.isPlaying }.minByOrNull { it.location.distance(e.player.location) }
                if (nearestTeammate != null) {
                    taskRunLater(1L) {
                        e.player.spectatorTarget = nearestTeammate
                        e.player.sendText {
                            text("§aThe player you were spectating disconnected, so you are now spectating ")
                            component(nearestTeammate.teamDisplayName())
                            text("§a.")
                        }
                    }
                } else {
                    e.player.kick("§cThe player you were spectating disconnected and your team has no more alive players.".toComponent())
                }
            } else {
                e.player.sendText {
                    text("§cYou still have alive teammates, so you can't stop spectating.")
                }
            }
            e.isCancelled = true
        } else {
            e.player.kick("§cYou stopped spectating and your team has no more alive players.".toComponent())
        }
    }
    listen<PlayerGameModeChangeEvent>(priority = EventPriority.LOWEST, ignoreCancelled = true) { e ->
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
            } else {
                taskRunLater(1L) {
                    e.player.kick("§cYou died and your team has no more alive players.".toComponent())
                }
            }
        }
    }
    command("teamspectate") {
        requires {
            (it.bukkitSender as? Player)?.gameMode == GameMode.SPECTATOR
        }
        argument("teammate", EntityArgument.player()) {
            suggestList {
                val sender = it.source.playerOrException.bukkitEntity
                val myTeam = Team.getTeam(sender) ?: return@suggestList emptyList()
                myTeam.onlineMemebers.filter { p -> p != sender && p.isPlaying }.map { p -> p.name }
            }
            runs {
                val sender = player
                val myTeam = Team.getTeam(sender) ?: return@runs
                val teammate = EntityArgument.getPlayer(nmsContext, "teammate").bukkitEntity
                if (teammate == sender || !teammate.isPlaying) {
                    sender.sendText {
                        text("§cNo player was found")
                    }
                    return@runs
                }
                if (myTeam.members.contains(teammate)) {
                    sender.spectatorTarget = teammate
                    sender.sendText {
                        text("§aYou are now spectating ")
                        component(teammate.teamDisplayName())
                        text("§a.")
                    }
                } else {
                    sender.sendText {
                        text("§cYou can't spectate ")
                        component(teammate.teamDisplayName())
                        text("§c.")
                    }
                }
            }
        }
    }
}