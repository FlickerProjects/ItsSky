package io.github.itsflicker.itssky

import io.github.itsflicker.itssky.ItsSky.islands
import org.bukkit.command.CommandSender
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.*
import taboolib.expansion.createHelper
import taboolib.module.configuration.util.getLocation

@CommandHeader("isadmin", permission = "admin")
object CommandISAdmin {

    @CommandBody(optional = true)
    val delete = subCommand {
        dynamic("island") {
            suggest {
                islands.getKeys(false).toList()
            }
            execute<CommandSender> { _, _, argument ->
                islands[argument] = null
            }
        }
    }

    @CommandBody(optional = true)
    val tp = subCommand {
        dynamic("player") {
            suggest {
                islands.getKeys(false).toList()
            }
            execute<ProxyPlayer> { sender, _, argument ->
                sender.teleport(islands.getLocation(argument)!!)
            }
        }
    }

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

}