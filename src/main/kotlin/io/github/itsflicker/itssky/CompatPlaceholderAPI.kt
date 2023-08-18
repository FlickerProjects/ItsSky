package io.github.itsflicker.itssky

import org.bukkit.entity.Player
import taboolib.platform.compat.PlaceholderExpansion

object CompatPlaceholderAPI : PlaceholderExpansion {

    override val identifier: String
        get() = "itssky"

    override fun onPlaceholderRequest(player: Player?, args: String): String {
        player ?: return "ERROR"
        val params = args.split("_")

        return when (params[0]) {
            "friend" -> {
                ItsSky.friends.getStringList(player.name).contains(params[1])
            }
            else -> "ERROR"
        }.toString()
    }

}