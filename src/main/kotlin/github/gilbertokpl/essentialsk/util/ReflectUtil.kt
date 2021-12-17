package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.manager.EType
import github.gilbertokpl.essentialsk.manager.IInstance
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.simpleyaml.configuration.file.YamlFile
import java.io.File
import java.lang.reflect.Type

class ReflectUtil {

    private var getPlayersList : Boolean? = null

    fun setValuesOfClass(cl: Class<*>, clInstance: Any, configList: List<YamlFile>) {
        for (it in cl.declaredFields) {
            val checkedValue = checkTypeField(it.genericType) ?: continue

            val nameFieldComplete = nameFieldHelper(it.name)

            for (yml in configList) {
                val value = checkedValue.getValueConfig(yml, nameFieldComplete) ?: continue
                it.set(clInstance, value)
                break
            }
        }
    }

    fun setValuesFromClass(cl: Class<*>, clInstance: Any, configList: List<YamlFile>) {
        for (it in cl.declaredFields) {
            val checkedValue = checkTypeField(it.genericType) ?: continue

            val nameFieldComplete = nameFieldHelper(it.name)

            for (yml in configList) {
                val value = checkedValue.setValueConfig(yml, nameFieldComplete) ?: continue
                value.set(nameFieldComplete, it.get(clInstance))
                value.save(File(yml.filePath))
                break
            }
        }
    }

    fun getPlayers(): List<Player> {
        if (getPlayersList == null) {
            val onlinePlayersMethod = Bukkit.getServer()::class.java.getMethod("getOnlinePlayers")
            getPlayersList = !onlinePlayersMethod.returnType.equals(Collection::class.java)
        }
        return if (getPlayersList!!) {
            val list = Bukkit.getServer()::class.java.getMethod("getOnlinePlayers")
            @Suppress("UNCHECKED_CAST")
            (list.invoke(Bukkit.getServer()) as Array<Player>).toList()
        } else {
            Bukkit.getOnlinePlayers().toList()
        }
    }

    private fun nameFieldHelper(name: String): String {
        val nameField = name.split("(?=\\p{Upper})".toRegex())

        var nameFieldComplete = ""

        var quanta = 0

        for (value in nameField) {
            quanta += 1
            if (nameFieldComplete == "") {
                nameFieldComplete = "${value.lowercase()}."
                continue
            }
            if (quanta == 2) {
                nameFieldComplete += value.lowercase()
                continue
            }
            nameFieldComplete += "-${value.lowercase()}"
        }

        return nameFieldComplete
    }


    private fun checkTypeField(type: Type): EType? {
        return when (type.typeName!!.lowercase()) {
            "java.lang.string" -> EType.STRING
            "java.util.list<java.lang.string>" -> EType.STRING_LIST
            "java.lang.boolean" -> EType.BOOLEAN
            "boolean" -> EType.BOOLEAN
            "java.lang.integer" -> EType.INTEGER
            "integer" -> EType.INTEGER
            "int" -> EType.INTEGER
            else -> {
                null
            }
        }
    }

    fun getHealth(p: Player): Double {
        return try {
            p.health
        } catch (e: NoSuchMethodError) {
            try {
                (p.javaClass.getMethod("getHealth", *arrayOfNulls(0)).invoke(p, *arrayOfNulls(0)) as Int).toInt()
                    .toDouble()
            } catch (e1: Throwable) {
                0.0
            }
        }
    }

    fun setHealth(p: Player, health: Int) {
        try {
            p.javaClass.getMethod("setHealth", Double::class.javaPrimitiveType)
                .invoke(p, java.lang.Double.valueOf(health.toDouble()))
        } catch (e: Throwable) {
            try {
                p.javaClass.getMethod("setHealth", Int::class.javaPrimitiveType).invoke(
                    p, Integer.valueOf(health)
                )
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        }
    }

    companion object : IInstance<ReflectUtil> {
        private val instance = createInstance()
        override fun createInstance(): ReflectUtil = ReflectUtil()
        override fun getInstance(): ReflectUtil {
            return instance
        }
    }
}