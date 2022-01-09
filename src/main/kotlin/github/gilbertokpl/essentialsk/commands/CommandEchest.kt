package github.gilbertokpl.essentialsk.commands

import github.gilbertokpl.essentialsk.EssentialsK
import github.gilbertokpl.essentialsk.configs.GeneralLang
import github.gilbertokpl.essentialsk.manager.ICommand
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class CommandEchest : ICommand {
    override val consoleCanUse: Boolean = false
    override val commandName = "echest"
    override val timeCoolDown: Long? = null
    override val permission: String = "essentialsk.commands.ec"
    override val minimumSize = 0
    override val maximumSize = 1
    override val commandUsage = listOf(
        "/echest",
        "essentialsk.commands.ec.other_/ec <PlayerName>"
    )

    override fun kCommand(s: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (args.isEmpty()) {
            s.sendMessage(GeneralLang.getInstance().echestSendSuccess)
            val inv = (s as Player).enderChest
            s.openInventory(inv)
            return false
        }

        if(!s.hasPermission("essentialsk.commands.ec.other"))
            return false
        
        val p = EssentialsK.instance.server.getPlayer(args[0]) ?: run {
            s.sendMessage(GeneralLang.getInstance().generalPlayerNotOnline)
            return false
        }

        s.sendMessage(GeneralLang.getInstance().echestSendOtherSuccess.replace("%player%", p.name))
        val inv = p.enderChest
        (s as Player).openInventory(inv)

        return false
    }
}
