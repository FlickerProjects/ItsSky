package io.github.itsflicker.itssky

import ink.ptms.realms.RealmManager.realms
import ink.ptms.realms.RealmManager.setRealmSize
import io.github.itsflicker.itssky.ItsSky.islands
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Biome
import org.bukkit.block.Chest
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.command.*
import taboolib.common.platform.function.getProxyPlayer
import taboolib.common.platform.function.submit
import taboolib.common.util.random
import taboolib.expansion.createHelper
import taboolib.module.configuration.util.getLocation
import taboolib.module.configuration.util.setLocation
import taboolib.platform.util.*

@CommandHeader("is", permissionDefault = PermissionDefault.TRUE)
object CommandIS {

    private val invites = mutableMapOf<String, String>()

//    @CommandBody(optional = true)
//    val tp = subCommand {
//        dynamic("player") {
//            suggestion<Player> { sender, _ ->
//                val location = islands.getLocation(sender.name) ?: return@suggestion emptyList()
//                islands.getKeys(false).filter { it != sender.name && islands.getLocation(it) == location }
//            }
//            restrict<Player> { sender, _, _ ->
//                sender.isOnGround
//            }
//            execute<Player> { sender, _, argument ->
//                sender.teleport(Bukkit.getPlayer(argument)!!.location)
//            }
//        }
//    }

    @CommandBody(optional = true)
    val invite = subCommand {
        dynamic("player") {
            suggestion<Player> { sender, _ ->
                onlinePlayers.map { it.name }.filter { it !in islands } - sender.name
            }
            execute<Player> { sender, _, argument ->
                if (!islands.contains(sender.name)) {
                    return@execute sender.sendMessage("§c你没有岛屿!")
                }
                getProxyPlayer(argument)?.sendMessage("§6${sender.name}邀请你加入ta的岛屿, 输入/is confirm来同意") ?: return@execute sender.sendMessage("§c目标玩家不在线!")
                sender.sendMessage("§2邀请已发送!")
                invites[argument] = sender.name
            }
        }
    }

    @CommandBody(optional = true)
    val confirm = subCommand {
        execute<Player> { sender, _, _ ->
            val inviter = invites.remove(sender.name) ?: return@execute sender.sendMessage("§c你没有收到邀请!")
            val player = Bukkit.getPlayer(inviter) ?: return@execute sender.sendMessage("§c邀请者不在线!")
            val location = islands.getLocation(player.name)!!
            islands.setLocation(sender.name, location)
            sender.teleport(player)
            sender.setBedSpawnLocation(player.bedSpawnLocation, true)
            sender.performCommand("sethome")
            player.sendMessage("§3${sender.name}已加入你的岛屿!")
            sender.sendMessage("§3你已加入${player.name}的岛屿!")
        }
    }

    @CommandBody(optional = true)
    val rtp = subCommand {
        execute<Player> { sender, _, _ ->
            if (islands.contains(sender.name)) {
                return@execute sender.sendMessage("§c你只能随机传送一次!")
            }
            sender.sendMessage("§b寻找可用位置中...")
            submit(async = true) {
                val exists = islands.getKeys(false).map {
                    islands.getLocation(it)!!.toBukkitLocation()
                }
                val world = Bukkit.getWorld("world")!!
                var location = Location(world, random(-5000, 5000).toDouble(), 90.0, random(-5000, 5000).toDouble())
                while (
                    world.getBiome(location) in listOf(Biome.SAVANNA, Biome.DESERT, Biome.BADLANDS, Biome.SAVANNA_PLATEAU) ||
                    (-64..319).any { world.getBlockAt(location.blockX, it, location.blockZ).type.isNotAir() } ||
                    exists.any { it.distance(location) <= 300 } ||
                    world.realms().any { it.inside(location) }
                ) {
                    location = Location(world, random(-5000, 5000).toDouble(), 90.0, random(-5000, 5000).toDouble())
                }
                islands.setLocation(sender.name, location.toProxyLocation())
                sender.sendMessage("§a传送中...")

                submit {
                    location.clone().subtract(0.0, 2.0, 0.0).block.type = Material.DIRT
                    val chest = location.clone().subtract(0.0, 1.0, 0.0).block.also { it.type = Material.CHEST }
                    val data = chest.state as Chest
                    data.inventory.addItem(
                        ItemStack(Material.DIRT, 48),
                        ItemStack(Material.OAK_SAPLING, 3),
                        ItemStack(Material.GRASS_BLOCK, 1),
                        ItemStack(Material.MYCELIUM, 1),
                        ItemStack(Material.CRIMSON_NYLIUM, 1),
                        ItemStack(Material.WARPED_NYLIUM, 1),
                        buildItem(Material.BLACK_WOOL) {
                            name = "&8初级领域"
                            lore += ""
                            lore += "&7摆在地上即可生成领域"
                            lore += "&7破坏后掉落&225&7个&b领域之尘"
                            lore += ""
                            lore += "&7大小: &225"
                            shiny()
                            colored()
                        }.also { it.setRealmSize(25) },
                        buildItem(Material.EMERALD) {
                            name = "&6&l死亡护身符"
                            lore += "&7将此护身符放在背包中，当你"
                            lore += "&7死亡时，你的背包将会受到保护"
                            lore += "&7而不会掉落。"
                            enchants += Enchantment.DURABILITY to 10
                            colored()
                            amount = 10
                        }
                    )
                    sender.teleport(location)
                    sender.setBedSpawnLocation(location, true)
                    sender.performCommand("sethome")
                }
            }
        }
    }

    @CommandBody
    val main = mainCommand {
        createHelper()
    }
}