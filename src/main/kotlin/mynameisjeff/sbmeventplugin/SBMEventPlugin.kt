package mynameisjeff.sbmeventplugin

import mynameisjeff.sbmeventplugin.game.loadAutoRespawn
import mynameisjeff.sbmeventplugin.game.loadNearestPlayerTracking
import mynameisjeff.sbmeventplugin.game.loadSpectatorLock
import mynameisjeff.sbmeventplugin.game.loadTimer
import net.axay.kspigot.main.KSpigot

class SBMEventPlugin : KSpigot() {
    override fun startup() {
        loadAutoRespawn()
        loadNearestPlayerTracking()
        loadSpectatorLock()
        loadTimer()
    }
}