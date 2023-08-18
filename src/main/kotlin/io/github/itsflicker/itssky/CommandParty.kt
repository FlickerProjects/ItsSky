package io.github.itsflicker.itssky

import io.github.itsflicker.itssky.ItsSky.friends
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.PermissionDefault
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.getProxyPlayer
import taboolib.platform.util.onlinePlayers

@CommandHeader("friend", permissionDefault = PermissionDefault.TRUE)
object CommandParty {

    private val invites = mutableMapOf<String, String>()

    @CommandBody(optional = true)
    val invite = subCommand {
        dynamic("player") {
            suggestion<Player> { sender, _ ->
                onlinePlayers.map { it.name }.filter { it !in friends.getStringList(sender.name) } - sender.name
            }
            execute<Player> { sender, _, argument ->
                getProxyPlayer(argument)?.sendMessage("§6${sender.name}邀请你成为ta的好友, 输入/friend confirm来同意") ?: return@execute sender.sendMessage("§c目标玩家不在线!")
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
            friends[sender.name] = friends.getStringList(sender.name) + player.name
            friends[player.name] = friends.getStringList(player.name) + sender.name
            player.sendMessage("§3${sender.name}已成为你的好友!")
            sender.sendMessage("§3你已成为${player.name}的好友!")
        }
    }

    @CommandBody(optional = true)
    val revoke = subCommand {
        dynamic("player") {
            suggestion<Player> { sender, _ ->
                friends.getStringList(sender.name)
            }
            execute<Player> { sender, _, argument ->
                getProxyPlayer(argument)?.sendMessage("§c你不再和 ${sender.name} 是好友了")
                sender.sendMessage("§c你不再和 $argument 是好友了")
                friends[sender.name] = friends.getStringList(sender.name) - argument
                friends[argument] = friends.getStringList(argument) - sender.name
            }
        }
    }

}