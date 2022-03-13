package io.ncbpfluffybear.voidharvesters;

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import javax.annotation.Nonnull;
import java.util.List;

public class VoidHarvesterCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@Nonnull CommandSender cs, @Nonnull Command cmd, @Nonnull String s, String[] args) {
        cs.sendMessage("test");
        if(args.length == 0) {
            cs.sendMessage(color("&7指令 // 信息", true));
            cs.sendMessage(color("    信息 || 显示挖掘机当前信息"));
            cs.sendMessage(color("    升级模块 || 升级收割机 [提升工作范围]"));
            return true;
        }

        //USER COMMANDS
        switch (args[0].toUpperCase()) {
            case "MATS":
                for(Material mat : SlimefunTag.CROPS.getValues()) {
                    cs.sendMessage(mat.name());
                }
                break;
            default:
                break;
        }
        //ADMIN COMMANDS
        if(!cs.hasPermission("vh.admin")) {
            return true;
        }

        if (args[0].equalsIgnoreCase("save")) {
            VoidHarvesters.saveHarvesters();
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        return null;
    }

    private String color (String msg) {
        return color(msg, false);
    }

    private String color (String msg, boolean prefix) {
        return ChatColors.color( (prefix ? Constants.PREFIX : "") + msg);
    }
}
