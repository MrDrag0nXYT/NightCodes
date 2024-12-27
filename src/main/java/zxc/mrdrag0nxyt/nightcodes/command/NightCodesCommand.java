package zxc.mrdrag0nxyt.nightcodes.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;
import zxc.mrdrag0nxyt.nightcodes.util.Utilities;
import zxc.mrdrag0nxyt.nightcodes.config.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class NightCodesCommand implements CommandExecutor, TabCompleter {

    private final NightCodes plugin;
    private Config config;
    private Messages messages;

    public NightCodesCommand(NightCodes plugin, Config config, Messages messages) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        YamlConfiguration messages = this.messages.getConfig();

        if (args.length == 0) {
            for (String message : messages.getStringList("nightcodes.usage"))
                sender.sendMessage(
                        Utilities.setColor(message)
                );
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (sender.hasPermission("nightcodes.admin.reload")) {
                    plugin.reload();
                    for (String message : messages.getStringList("nightcodes.reloaded"))
                        sender.sendMessage(
                                MiniMessage.miniMessage().deserialize(message)
                        );

                } else {
                    for (String message : messages.getStringList("global.no-permission"))
                        sender.sendMessage(Utilities.setColor(message));
                }
                break;

            case "about":
                if (sender.hasPermission("nightcodes.admin.about")) {
                    List<String> aboutStrings = Arrays.asList(
                            " ",
                            " <#fcfcfc><#745c97>NightCodes</#745c97> plugin by <click:open_url:'https://drakoshaslv.ru'><#745c97>MrDrag0nXYT</#745c97></click></#fcfcfc>",
                            " <#fcfcfc>Made specially for <click:open_url:'https://nshard.ru'><#745c97>NightShard</#745c97></click></#fcfcfc>",
                            " "
                    );

                    for (String message : aboutStrings)
                        sender.sendMessage(
                                MiniMessage.miniMessage().deserialize(message)
                        );

                } else {
                    for (String message : messages.getStringList("global.no-permission"))
                        sender.sendMessage(Utilities.setColor(message));
                }
                break;

            default:
                for (String message : messages.getStringList("nightcodes.usage"))
                    sender.sendMessage(
                            Utilities.setColor(message)
                    );
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
