package me.yoursole.ctf.events

import me.yoursole.ctf.CTF
import me.yoursole.ctf.datafiles.items.CheapApple
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack


object CraftEvent : Listener {
    @EventHandler
    fun onPrepareCraft(event: PrepareItemCraftEvent) {
        if (event.recipe == null) {
            val matrix: Array<out ItemStack?> = event.inventory.matrix
            if (matrix.all {
                    it == null || it.type.key.key.endsWith("_leaves")
                }) {
                val leaves: Int = matrix.filterNotNull().sumOf { it.amount }
                if (leaves > 0 && leaves % 8 == 0) {
                    val clone: ItemStack = CheapApple.item.clone()
                    clone.amount = leaves / 8
                    event.inventory.result = clone
                }
            }
        }
    }

    @EventHandler
    fun onCraft(event: InventoryClickEvent) {
        if (event.clickedInventory != event.inventory || (event.inventory.type != InventoryType.CRAFTING && event.inventory.type != InventoryType.WORKBENCH)) return
        if (event.currentItem?.isSimilar(CheapApple.item) == true) {
            Bukkit.getScheduler().runTask(CTF.instance, Runnable {
                (when (event.inventory.type) {
                    InventoryType.CRAFTING -> 1..4
                    InventoryType.WORKBENCH -> 1..9
                    else -> error("")
                }).forEach {
                    event.inventory.clear(it)
                }
            })
        }
    }

    @EventHandler
    fun onAnvil(event: PrepareAnvilEvent) {
        event.result?.apply {
            itemMeta = itemMeta.apply {
                setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName))
            }
        }
    }
}