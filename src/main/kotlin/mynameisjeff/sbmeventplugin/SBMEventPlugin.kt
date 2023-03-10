package mynameisjeff.sbmeventplugin

import mynameisjeff.sbmeventplugin.game.*
import net.axay.kspigot.main.KSpigot

class SBMEventPlugin : KSpigot() {
    override fun startup() {
        loadAntiCrystal()
        loadAutoRespawn()
        loadNearestPlayerTracking()
        loadSpectatorLock()
        loadTelekinesis()
        loadTimer()
    }
}