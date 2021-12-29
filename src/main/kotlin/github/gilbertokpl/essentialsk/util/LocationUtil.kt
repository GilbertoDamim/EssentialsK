package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.manager.IInstance
import org.bukkit.Bukkit.getServer
import org.bukkit.Location
import java.io.File


class LocationUtil {
    fun locationSerializer(loc: Location): String {
        return loc.x.toString() + ";" + loc.y.toString() + ";" + loc.z.toString() + ";" + loc.world?.name + ";" + loc.pitch + ";" + loc.yaw
    }

    fun locationSerializer(s: String): Location? {
        val parts = s.split(";").toTypedArray()
        val x = parts[0].toDouble()
        val y = parts[1].toDouble()
        val z = parts[2].toDouble()
        val w = try {
            EssentialsK.instance.server.getWorld(parts[3])
        } catch (e: Exception) {
            return null
        }
        val loc = Location(w, x, y, z)
            loc.pitch = try { parts[3].toFloat() } catch (e: Exception) { 0F }
            loc.yaw = try { parts[4].toFloat() } catch (e: Exception) { 0F }
        return Location(w, x, y, z)
    }

    companion object : IInstance<LocationUtil> {
        private val instance = createInstance()
        override fun createInstance(): LocationUtil = LocationUtil()
        override fun getInstance(): LocationUtil {
            return instance
        }
    }
}