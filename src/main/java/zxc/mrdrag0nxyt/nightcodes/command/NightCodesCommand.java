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
import zxc.mrdrag0nxyt.nightcodes.util.config.*;

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
                plugin.adventure().sender(sender).sendMessage(
                        Utilities.setColor(message)
                );
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                if (sender.hasPermission("nightcodes.admin.reload")) {
                    plugin.reload();
                    for (String message : messages.getStringList("nightcodes.reloaded"))
                        plugin.adventure().sender(sender).sendMessage(
                                MiniMessage.miniMessage().deserialize(message)
                        );
                }
                break;

            default:
                for (String message : messages.getStringList("nightcodes.usage"))
                    plugin.adventure().sender(sender).sendMessage(
                            Utilities.setColor(message)
                    );
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("reload");
        }

        return Collections.emptyList();
    }
}
