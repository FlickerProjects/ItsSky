package io.github.itsflicker.itssky

import taboolib.common.platform.Plugin
import taboolib.module.configuration.Type
import taboolib.module.configuration.createLocal

object ItsSky : Plugin() {

    val islands by lazy { createLocal("islands.yml") }

    val friends by lazy { createLocal("friends.json", type = Type.JSON) }

}