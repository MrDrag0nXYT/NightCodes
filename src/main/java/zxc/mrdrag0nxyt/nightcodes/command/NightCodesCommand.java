package zxc.mrdrag0nxyt.nightcodes.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;
import zxc.mrdrag0nxyt.nightcodes.config.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NightCodesCommand implements CommandExecutor, TabCompleter {

    private final List<Component> aboutStrings;

    private final NightCodes plugin;
    private final Messages messages;

    public NightCodesCommand(NightCodes plugin, Messages messages) {
        this.plugin = plugin;
        this.messages = messages;

        MiniMessage miniMessage = MiniMessage.miniMessage();
        aboutStrings = Stream.of(" ", " <#fcfcfc><#745c97>NightCodes " + plugin.getDescription().getVersion() + "</#745c97> plugin by <click:open_url:'https://drakoshaslv.ru'><#745c97>MrDrag0nXYT</#745c97></click></#fcfcfc>", " <#fcfcfc>Made specially for <click:open_url:'https://nshard.ru'><#745c97>NightShard</#745c97></click></#fcfcfc>", " ").map(miniMessage::deserialize).collect(Collectors.toList());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            for (Component component : messages.getAdminUsage())
                sender.sendMessage(component);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload" -> {
                return reloadSubcommand(sender);
            }

            case "about" -> {
                return aboutSubcommand(sender);
            }

            default -> {
                return usageSubcommand(sender);
            }
        }
    }


    private boolean reloadSubcommand(CommandSender sender) {
        if (!sender.hasPermission("nightcodes.admin.reload")) {
            sender.sendMessage(messages.getNoPermission());
            return false;
        }

        plugin.reload();
        sender.sendMessage(messages.getAdminReloaded());
        return true;
    }

    private boolean aboutSubcommand(CommandSender sender) {
        if (!sender.hasPermission("nightcodes.admin.about")) {
            sender.sendMessage(messages.getNoPermission());
            return false;
        }

        for (Component component : aboutStrings) {
            sender.sendMessage(component);
        }
        return true;
    }

    private boolean usageSubcommand(CommandSender sender) {
        for (Component component : messages.getAdminUsage()) {
            sender.sendMessage(component);
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload", "about");
        }

        return Collections.emptyList();
    }
}
