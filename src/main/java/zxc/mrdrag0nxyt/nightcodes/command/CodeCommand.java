package zxc.mrdrag0nxyt.nightcodes.command;

import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;
import zxc.mrdrag0nxyt.nightcodes.util.Utilities;
import zxc.mrdrag0nxyt.nightcodes.config.Config;
import zxc.mrdrag0nxyt.nightcodes.config.Messages;
import zxc.mrdrag0nxyt.nightcodes.util.database.DatabaseManager;
import zxc.mrdrag0nxyt.nightcodes.util.exception.CannotActivateOwnCodeException;
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
        YamlConfiguration config = this.config.getConfig();

        if (args.length == 0) {
            for (String message : messages.getStringList("referral.usage"))
                sender.sendMessage(Utilities.setColor(message));
            return true;
        }

        if (!(sender instanceof Player player)) {
            for (String message : messages.getStringList("global.only-for-players"))
                sender.sendMessage(Utilities.setColor(message));
            return true;
        }

        if ((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) <= config.getLong("requirements.played_time", 3600L)) {
            for (String message : messages.getStringList("code.requirements.time"))
                sender.sendMessage(
                        Utilities.setColor(message)
                );
            return true;
        }

        if (sender.hasPermission("nightcodes.player.activate")) {

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                try (Connection connection = database.getConnection()) {

                    database.getDatabaseWorker().useCode(
                            connection,
                            player.getName(),
                            player.getUniqueId(),
                            args[0]
                    );

                    for (String bonusCommand : config.getStringList("commands")) {
                        String finalBonusCommand = bonusCommand
                                .replace("%codeOwner%", args[0])
                                .replace("%player%", player.getName());

                        Bukkit.getScheduler().runTask(plugin, () -> {
                            plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalBonusCommand);
                        });
                    }

                    for (String message : messages.getStringList("code.activated"))
                        sender.sendMessage(
                                Utilities.setColor(message.replace("%referral_code%", args[0]))
                        );

                } catch (SQLException e) {
                    for (String message : messages.getStringList("global.database-error"))
                        sender.sendMessage(Utilities.setColor(message));

                } catch (CodeNotFoundException e) {
                    for (String message : messages.getStringList("code.not-found"))
                        sender.sendMessage(
                                Utilities.setColor(message.replace("%referral_code%", args[0]))
                        );

                } catch (CodeAlreadyUsedException e) {
                    for (String message : messages.getStringList("code.already-activated"))
                        sender.sendMessage(Utilities.setColor(message));

                } catch (CannotActivateOwnCodeException e) {
                    for (String message : messages.getStringList("code.cannot-activate-own-code"))
                        sender.sendMessage(Utilities.setColor(message));
                }
            });

        } else {
            for (String message : messages.getStringList("global.no-permission"))
                sender.sendMessage(Utilities.setColor(message));
            return true;
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
