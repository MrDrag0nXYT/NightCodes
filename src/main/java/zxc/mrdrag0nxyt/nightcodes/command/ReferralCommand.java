package zxc.mrdrag0nxyt.nightcodes.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;
import zxc.mrdrag0nxyt.nightcodes.entity.ReferralCode;
import zxc.mrdrag0nxyt.nightcodes.util.Utilities;
import zxc.mrdrag0nxyt.nightcodes.util.config.*;
import zxc.mrdrag0nxyt.nightcodes.util.database.DatabaseManager;
import zxc.mrdrag0nxyt.nightcodes.util.exception.CodeNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ReferralCommand implements CommandExecutor, TabCompleter {

    private final NightCodes plugin;
    private Config config;
    private Messages messages;
    private DatabaseManager database;

    public ReferralCommand(NightCodes plugin, Config config, Messages messages, DatabaseManager database) {
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


        // todo: work with UUID

        switch (args[0].toLowerCase()) {
            case "create":
                if (sender.hasPermission("nightcodes.player.create")) {

                    try (Connection connection = database.getConnection()) {

                        try {
                            ReferralCode checkCode = database.getDatabaseWorker().getReferralCodeByUsername(connection, player.getName());

                            for (String message : messages.getStringList("referral.exists"))
                                plugin.adventure().sender(sender).sendMessage(
                                        Utilities.setColor(message.replace("%player%", player.getName()))
                                );
                            return true;

                        } catch (CodeNotFoundException e) {
                            ReferralCode code = new ReferralCode(player.getName(), player.getUniqueId());
                            database.getDatabaseWorker().createReferralCode(connection, code);

                            for (String message : messages.getStringList("referral.created"))
                                plugin.adventure().sender(sender).sendMessage(
                                        Utilities.setColor(message.replace("%player%", player.getName()))
                                );
                        }

                    } catch (SQLException e) {
                        for (String message : messages.getStringList("global.database-error"))
                            plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
                    }

                } else {
                    for (String message : messages.getStringList("global.no-permission"))
                        plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
                }
                break;

            case "delete":
                if (sender.hasPermission("nightcodes.player.delete")) {

                    try (Connection connection = database.getConnection()) {

                        try {
                            ReferralCode checkCode = database.getDatabaseWorker().getReferralCodeByUsername(connection, player.getName());

                            database.getDatabaseWorker().deleteReferralCode(connection, player.getName());

                            for (String message : messages.getStringList("referral.deleted"))
                                plugin.adventure().sender(sender).sendMessage(
                                        Utilities.setColor(message.replace("%player%", player.getName()))
                                );

                        } catch (CodeNotFoundException e) {
                            for (String message : messages.getStringList("referral.not-exist"))
                                plugin.adventure().sender(sender).sendMessage(
                                        Utilities.setColor(message.replace("%player%", player.getName()))
                                );
                            return true;
                        }

                    } catch (SQLException e) {
                        for (String message : messages.getStringList("global.database-error"))
                            plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
                        return true;
                    }

                } else {
                    for (String message : messages.getStringList("global.no-permission"))
                        plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
                    return true;
                }
                break;

            case "pause":
                if (sender.hasPermission("nightcodes.player.pause")) {

                    try (Connection connection = database.getConnection()) {

                        try {
                            ReferralCode checkCode = database.getDatabaseWorker().getReferralCodeByUsername(connection, player.getName());

                            if (checkCode.getIsPaused() == 1) {
                                for (String message : messages.getStringList("referral.already-paused"))
                                    plugin.adventure().sender(sender).sendMessage(
                                            Utilities.setColor(message.replace("%player%", player.getName()))
                                    );
                                return true;
                            }

                            database.getDatabaseWorker().setPaused(connection, player.getName(), (byte) 1);

                            for (String message : messages.getStringList("referral.paused"))
                                plugin.adventure().sender(sender).sendMessage(
                                        Utilities.setColor(message.replace("%player%", player.getName()))
                                );

                        } catch (CodeNotFoundException e) {
                            for (String message : messages.getStringList("referral.not-exist"))
                                plugin.adventure().sender(sender).sendMessage(
                                        Utilities.setColor(message.replace("%player%", player.getName()))
                                );
                            return true;
                        }

                    } catch (SQLException e) {
                        for (String message : messages.getStringList("global.database-error"))
                            plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
                        return true;
                    }

                } else {
                    for (String message : messages.getStringList("global.no-permission"))
                        plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
                    return true;
                }
                break;

            case "unpause":
                if (sender.hasPermission("nightcodes.player.unpause")) {

                    try (Connection connection = database.getConnection()) {

                        try {
                            ReferralCode checkCode = database.getDatabaseWorker().getReferralCodeByUsername(connection, player.getName());

                            if (checkCode.getIsPaused() != 1) {
                                for (String message : messages.getStringList("referral.already-unpaused"))
                                    plugin.adventure().sender(sender).sendMessage(
                                            Utilities.setColor(message.replace("%player%", player.getName()))
                                    );
                                return true;
                            }

                            database.getDatabaseWorker().setPaused(connection, player.getName(), (byte) 0);

                            for (String message : messages.getStringList("referral.unpaused"))
                                plugin.adventure().sender(sender).sendMessage(
                                        Utilities.setColor(message.replace("%player%", player.getName()))
                                );

                        } catch (CodeNotFoundException e) {
                            for (String message : messages.getStringList("referral.not-exist"))
                                plugin.adventure().sender(sender).sendMessage(
                                        Utilities.setColor(message.replace("%player%", player.getName()))
                                );
                            return true;
                        }

                    } catch (SQLException e) {
                        for (String message : messages.getStringList("global.database-error"))
                            plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
                        return true;
                    }

                } else {
                    for (String message : messages.getStringList("global.no-permission"))
                        plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
                    return true;
                }
                break;

            case "stats":
                if (sender.hasPermission("nightcodes.player.stats")) {

                    try (Connection connection = database.getConnection()) {

                        try {
                            ReferralCode checkCode = database.getDatabaseWorker().getReferralCodeByUsername(connection, player.getName());

                            String state = checkCode.getIsPaused() == 1 ? messages.getString("referral.state.paused", "<#d45079>paused</#d45079>") : messages.getString("referral.state.unpaused", "<#ace1af>active</#ace1af>");

                            for (String message : messages.getStringList("referral.stats"))
                                plugin.adventure().sender(sender).sendMessage(
                                        Utilities.setColor(
                                                message
                                                        .replace("%player%", player.getName())
                                                        .replace("%count%", String.valueOf(checkCode.getUsages()))
                                                        .replace("%state%", state)
                                        )
                                );

                        } catch (CodeNotFoundException e) {
                            for (String message : messages.getStringList("referral.not-exist"))
                                plugin.adventure().sender(sender).sendMessage(
                                        Utilities.setColor(message.replace("%player%", player.getName()))
                                );
                            return true;
                        }

                    } catch (SQLException e) {
                        for (String message : messages.getStringList("global.database-error"))
                            plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
                        return true;
                    }

                } else {
                    for (String message : messages.getStringList("global.no-permission"))
                        plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
                    return true;
                }
                break;

            default:
                for (String message : messages.getStringList("referral.usage"))
                    plugin.adventure().sender(sender).sendMessage(Utilities.setColor(message));
                return true;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {

        if (args.length == 1) {
            return Arrays.asList("create", "delete", "pause", "unpause", "stats");
        }

        return Collections.emptyList();
    }
}
