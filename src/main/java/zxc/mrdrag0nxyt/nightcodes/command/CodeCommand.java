package zxc.mrdrag0nxyt.nightcodes.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;
import zxc.mrdrag0nxyt.nightcodes.util.Utilities;
import zxc.mrdrag0nxyt.nightcodes.util.config.Config;
import zxc.mrdrag0nxyt.nightcodes.util.config.Messages;
import zxc.mrdrag0nxyt.nightcodes.util.database.DatabaseManager;
import zxc.mrdrag0nxyt.nightcodes.util.exception.CodeAlreadyUsedException;
import zxc.mrdrag0nxyt.nightcodes.util.exception.CodeNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class CodeCommand implements CommandExecutor, TabCompleter {

    private final NightCodes plugin;
    private Config config;
    private Messages messages;
    private DatabaseManager database;

    public CodeCommand(NightCodes plugin, Config config, Messages messages, DatabaseManager database) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        YamlConfiguration messages = this.messages.getConfig();

        if (args.length == 0) {
            for (String message : messages.getStringList("referral.usage"))
                plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
            return true;
        }

        if (!(sender instanceof Player)) {
            for (String message : messages.getStringList("global.only-for-players"))
                plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
            return true;
        }

        Player player = (Player) sender;

        try (Connection connection = database.getConnection()) {

            database.getDatabaseWorker().useCode(
                    connection,
                    player.getName(),
                    player.getUniqueId(),
                    args[0]
            );

            for (String message : messages.getStringList("code.activated"))
                plugin.adventure().sender(sender).sendMessage(
                        Utilities.setColor(message.replace("%referral_code%", args[0]))
                );

        } catch (SQLException e) {
            for (String message : messages.getStringList("global.database-error"))
                plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));

        } catch (CodeNotFoundException e) {
            for (String message : messages.getStringList("code.not-found"))
                plugin.adventure().sender(sender).sendMessage(
                        Utilities.setColor(message.replace("%referral_code%", args[0]))
                );

        } catch (CodeAlreadyUsedException e) {
            for (String message : messages.getStringList("code.already-activated"))
                plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return Collections.singletonList(
                messages.getConfig().getString("code.autocomplete-placeholder", "code")
        );
    }
}
