package io.github.itsflicker.itssky

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.util.random
import taboolib.platform.util.kill

object ISListener {

    @SubscribeEvent(ignoreCancelled = true)
    fun e(e: PlayerCommandPreprocessEvent) {
        val cmd = e.message.removePrefix("/").trimIndent()
        if (cmd.startsWith("cmi tpa")) {
            if (!ItsSky.friends.getStringList(e.player.name).contains(cmd.split(" ").getOrElse(2) { "" })) {
                e.isCancelled = true
                e.player.sendMessage("§c你需要加对方为好友才能tpa! (/friend invite)")
            }
        }
    }

    @SubscribeEvent
    fun e(e: EntityDeathEvent) {
        when (e.entityType) {
            EntityType.ENDER_DRAGON -> {
                val ed = e.entity as EnderDragon
                if (ed.dragonBattle?.hasBeenPreviouslyKilled() == true) {
                    val world = e.entity.location.world!!
                    world.spawnEntity(Location(world, 0.0, 68.0, 0.0), EntityType.SHULKER)
                }
            }
            EntityType.DOLPHIN, EntityType.BAT -> {
                if (e.entity.lastDamageCause?.entity is Warden) {
                    e.drops += ItemStack(Material.ECHO_SHARD)
                }
            }
            EntityType.WARDEN -> {
                e.drops += ItemStack(Material.REINFORCED_DEEPSLATE)
            }
            else -> { }
        }
    }

    @SubscribeEvent
    fun e(e: EntityExplodeEvent) {
        val creeper = e.entity as? Creeper ?: return
        if (creeper.isPowered) {
            e.blockList().filter { it.type == Material.DIAMOND_BLOCK }.forEach {
                it.type = Material.END_PORTAL
            }
        }
    }

    @SubscribeEvent
    fun e(e: CreatureSpawnEvent) {
        if (e.entityType == EntityType.ENDERMAN && random(0.01)) {
            (e.entity as Enderman).carriedBlock = Bukkit.createBlockData(Material.CHORUS_FLOWER)
        }
    }

    @SubscribeEvent
    fun e(e: PlayerInteractEntityEvent) {
        val item = e.player.inventory.itemInMainHand
        if (e.rightClicked is Vex && e.hand == EquipmentSlot.HAND && item.type == Material.DIAMOND) {
            (e.rightClicked as Vex).kill()
            e.rightClicked.location.world!!.spawnEntity(e.rightClicked.location, EntityType.ALLAY)
            if (item.amount > 1) {
                item.amount -= 1
            } else {
                e.player.inventory.setItemInMainHand(null)
            }
            return
        }
        if (e.rightClicked is Spider && e.rightClicked !is CaveSpider && e.hand == EquipmentSlot.HAND && item.type == Material.POISONOUS_POTATO) {
            (e.rightClicked as Spider).kill()
            e.rightClicked.location.world!!.spawnEntity(e.rightClicked.location, EntityType.CAVE_SPIDER)
            if (item.amount > 1) {
                item.amount -= 1
            } else {
                e.player.inventory.setItemInMainHand(null)
            }
            return
        }
    }

    @SubscribeEvent
    fun e(e: BlockBreakEvent) {
        if (e.block.type == Material.BUDDING_AMETHYST) {
            val item = e.player.inventory.itemInMainHand
            if (item.type == Material.NETHERITE_PICKAXE && item.containsEnchantment(Enchantment.SILK_TOUCH)) {
                e.block.location.world!!.dropItemNaturally(e.block.location, ItemStack(Material.BUDDING_AMETHYST))
            }
        }
    }

}