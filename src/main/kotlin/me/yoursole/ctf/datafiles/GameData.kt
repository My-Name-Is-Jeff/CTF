package me.yoursole.ctf.datafiles

import me.yoursole.ctf.datafiles.items.*
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.StructureType
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffectType
import java.util.*

object GameData {
    var it: Player? = null
    var scores = HashMap<UUID, Int>()
    var timerMs: Long = -1
    val customItems = HashMap<String, ItemStack>()
    val recipeKeys = HashMap<String, NamespacedKey>()
    val haste = PotionEffectType.FAST_DIGGING.createEffect(60, 2)
    val saturation = PotionEffectType.SATURATION.createEffect(60, 0)
    val dolphin = PotionEffectType.DOLPHINS_GRACE.createEffect(60, 1)
    val nightVision = PotionEffectType.NIGHT_VISION.createEffect(2000, 68)
    val slowness = PotionEffectType.SLOW.createEffect(60, 1)
    val regen = PotionEffectType.REGENERATION.createEffect(60, 1)
    var itLoc: Location? = null
    var inNether = false
    var backUpLoc: Location? = null
    val netherHunters = ArrayList<Player>()
    val netherBackupLocs = HashMap<Player, Location>()
    var arrow = "|"
    val overworldName = "worldB"
    val netherName = "world_netherB"
    var world: World? = null
    var world_nether: World? = null
    val structureTypes = setOf(
        StructureType.VILLAGE,
        StructureType.DESERT_PYRAMID,
        StructureType.JUNGLE_PYRAMID,
        StructureType.MINESHAFT,
        StructureType.PILLAGER_OUTPOST,
        StructureType.WOODLAND_MANSION,
        StructureType.RUINED_PORTAL
    )
    val structureTypesNether = setOf(StructureType.NETHER_FORTRESS, StructureType.BASTION_REMNANT)
    var gameSpawnPoint: Location? = null
    var netherMainPoint: Location? = null

    init {
        try {
            customItems["flag"] = Flag.flag
            customItems["hermes"] = HermesBoots.item
            customItems["tunnelerspickaxe"] = TunnelersPickaxe.item
            customItems["goblinssword"] = GoblinsSword.item
            customItems["thorsaxe"] = ThorsAxe.item
            customItems["cheapapple"] = CheapApple.item
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}