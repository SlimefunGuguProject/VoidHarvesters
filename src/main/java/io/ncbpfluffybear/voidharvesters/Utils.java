package io.ncbpfluffybear.voidharvesters;

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.command.CommandSender;

public class Utils {

    public Utils() {}

    public static void send(CommandSender s, String msg) {
        s.sendMessage(ChatColors.color(Constants.PREFIX + msg));
    }
}
