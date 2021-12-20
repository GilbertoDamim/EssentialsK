package github.gilbertokpl.essentialsk.util

import github.gilbertokpl.essentialsk.configs.MainConfig
import github.gilbertokpl.essentialsk.configs.OtherConfig
import github.gilbertokpl.essentialsk.loops.Announcements
import github.gilbertokpl.essentialsk.manager.IInstance

class OtherConfigUtil {

    fun start() {
        val commandsConfig = ConfigUtil.getInstance().configList["CommandsConfig"]!!

        val resourcesConfig = ConfigUtil.getInstance().configList["ResourcesConfig"]!!

        val vanish = ConfigUtil.getInstance().getStringList(commandsConfig, "vanish.blocked-other-cmds", false)

        val announce = ConfigUtil.getInstance().getStringList(resourcesConfig, "announcements.list-announce", true)

        val deathMessage = ConfigUtil.getInstance().getStringList(ConfigUtil.getInstance().langYaml, "deathmessages.cause-replacer", true)

        val deathMessageEntity = ConfigUtil.getInstance().getStringList(ConfigUtil.getInstance().langYaml, "deathmessages.entity-replacer", true)

        OtherConfig.getInstance().deathmessageListReplacer.clear()

        for (d in deathMessageEntity) {
            val to = d.split("-")
            try {
                OtherConfig.getInstance().deathmessageListReplacer[to[0].lowercase()] = to[1]
            }
            catch (ignored : Exception) {}
        }

        for (d in deathMessage) {
            val to = d.split("-")
            try {
                OtherConfig.getInstance().deathmessageListReplacer[to[0].lowercase()] = to[1]
            }
            catch (ignored : Exception) {}
        }

        OtherConfig.getInstance().vanishBlockedOtherCmds.clear()

        for (v in vanish) {
            val split = v.split(" ")
            var bol = false
            for (i in 0..split.size) {
                if (split[i] == "<player>") {
                    OtherConfig.getInstance().vanishBlockedOtherCmds[split[0]] = i
                    bol = true
                    break
                }
            }
            if (bol) {
                continue
            }
        }

        var dif = false

        val hash = HashMap<Int, String>()

        var to = 1

        for (a in announce) {
            hash[to] = a
            to += 1
        }

        if (hash.size != OtherConfig.getInstance().announcementsListAnnounce.size) {
            dif = true
        } else {
            for (i in hash) {
                if (OtherConfig.getInstance().announcementsListAnnounce[i.key] != i.value) {
                    dif = true
                    break
                }
            }
        }

        if (dif) {
            OtherConfig.getInstance().announcementsListAnnounce = hash
            if (MainConfig.getInstance().announcementsEnabled) {
                TaskUtil.getInstance().restartAnnounceExecutor()
                Announcements.getInstance().start(announce.size, MainConfig.getInstance().announcementsTime)
            }
        }
    }


    companion object : IInstance<OtherConfigUtil> {
        private val instance = createInstance()
        override fun createInstance(): OtherConfigUtil = OtherConfigUtil()
        override fun getInstance(): OtherConfigUtil {
            return instance
        }
    }
}