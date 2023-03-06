package mynameisjeff.sbmeventplugin.game

import net.axay.kspigot.commands.argument
import net.axay.kspigot.commands.command
import net.axay.kspigot.commands.literal
import net.axay.kspigot.commands.runs
import net.axay.kspigot.runnables.task
import org.bukkit.Bukkit
import org.bukkit.boss.BarColor
import org.bukkit.boss.BarStyle

fun loadTimer() {
    command
    code
}

private val timers = mutableListOf<Timer>()

private val command = command("timer") {
    requires {
        it.hasPermission(3)
    }
    literal("create") {
        argument<String>("title") {
            literal("fromNow") {
                argument<Long>("seconds") {
                    runs {
                        val title = getArgument<String>("title")
                        val seconds = getArgument<Long>("seconds")
                        val endTime = System.currentTimeMillis() + seconds * 1000
                        val timer = Timer(title, endTime)
                        timers.add(timer)
                    }
                }
            }
            literal("endTime") {
                argument<Long>("epoch") {
                    runs {
                        val title = getArgument<String>("title")
                        val endTime = getArgument<Long>("epoch")
                        val timer = Timer(title, endTime)
                        timers.add(timer)
                    }
                }
            }
        }
    }
}

private val code = task(period = 10L, endCallback = {
    timers.removeAll {
        it.bossBar.hide()
        it.bossBar.removeAll()
        true
    }
}) {
    timers.removeAll {
        val ended = it.timeLeft <= 0
        if (ended) {
            it.bossBar.players.forEach { p ->
                p.sendMessage("§eTimer ${it.title} §ehas ended!")
            }
            it.bossBar.hide()
            it.bossBar.removeAll()
        } else {
            it.bossBar.progress = it.progress
            it.bossBar.setTitle(it.title + " §a§l${it.formattedTime}")
            Bukkit.getOnlinePlayers().forEach(it.bossBar::addPlayer)
        }

        return@removeAll ended
    }
}

data class Timer(val title: String, val endTime: Long) {
    val startTime = System.currentTimeMillis()
    val difference = endTime - startTime
    val progress: Double
        get() = timeLeft.toDouble() / difference
    val timeLeft: Long
        get() = endTime - System.currentTimeMillis()
    val bossBar by lazy {
        Bukkit.createBossBar(title, BarColor.WHITE, BarStyle.SOLID)
    }

    val formattedTime: String
        get() {
            val timeLeft = timeLeft
            val seconds = timeLeft / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            if (hours > 0) {
                return "${hours}h ${minutes % 60}m ${seconds % 60}s"
            } else if (minutes > 0) {
                return "${minutes}m ${seconds % 60}s"
            } else {
                return "${seconds}s"
            }
        }
}