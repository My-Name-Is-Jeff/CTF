package mynameisjeff.sbmeventplugin

import mynameisjeff.sbmeventplugin.game.loadNearestPlayerTracking
import net.axay.kspigot.main.KSpigot

class SBMEventPlugin : KSpigot() {
    override fun startup() {
        loadNearestPlayerTracking()
    }
}