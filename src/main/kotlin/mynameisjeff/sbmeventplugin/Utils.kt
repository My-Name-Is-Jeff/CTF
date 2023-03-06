package mynameisjeff.sbmeventplugin

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.*

object Utils {
    fun Player.getArrowFor(loc: Location): Arrows {
        val a = loc.x - this.location.x
        val c = sqrt(a * a + (loc.z - this.location.z).pow(2.0))
        var angleBetween = asin(a / c) * 180 / 3.14
        if (this.location.z < loc.z) {
            angleBetween = 180 - abs(angleBetween)
            if (this.location.x > loc.x) {
                angleBetween *= -1.0
            }
        }
        val angle = Math.toRadians(this.location.yaw.toDouble())
        val x = cos(angle)
        var angleFacing = 180 - acos(x) * 180 / 3.14
        if (this.location.direction.x < 0) {
            angleFacing *= -1.0
        }
        var combined = angleFacing - angleBetween
        if (abs(combined) > 180) {
            combined = if (combined > 0) {
                -1 * (360 - combined)
            } else {
                360 - abs(combined)
            }
        }
        return when (combined) {
            in -22.5..22.5 -> Arrows.UP
            in 22.5..67.5 -> Arrows.UPLEFT
            in 67.5..112.5 -> Arrows.LEFT
            in 112.5..157.5 -> Arrows.DOWNLEFT
            in 157.0..180.0, in -180.0..-157.5 -> Arrows.DOWN
            in -157.5..-112.5 -> Arrows.DOWNRIGHT
            in -112.5..-67.5 -> Arrows.RIGHT
            in -67.5..-22.5 -> Arrows.UPRIGHT
            else -> Arrows.NONE
        }
    }

    enum class Arrows(val char: Char) {
        NONE('|'),
        LEFT('←'),
        UP('↑'),
        RIGHT('→'),
        DOWN('↓'),
        UPLEFT('↖'),
        UPRIGHT('↗'),
        DOWNRIGHT('↘'),
        DOWNLEFT('↙');
    }
}

val Player.isVanished
    get() = getMetadata("vanished").any { it.asBoolean() }

val Player.isPlaying
    get() = isOnline && gameMode == GameMode.SURVIVAL && !isVanished && !isDead

fun Player.doTelekinesis(stack: ItemStack) {
    if (stack.type == Material.AIR) return
    val items = inventory.addItem(stack)
    if (items.isNotEmpty()) {
        for (item in items.values) {
            val drop = world.dropItemNaturally(location, item)
            drop.pickupDelay = 5
            drop.owner = uniqueId
            drop.thrower = uniqueId
        }
    }
}