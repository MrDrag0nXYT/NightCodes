package zxc.mrdrag0nxyt.nightcodes.command;

import org.bukkit.Bukkit;
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
import zxc.mrdrag0nxyt.nightcodes.config.*;
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
                sender.sendMessage(Utilities.setColor(message));
            return true;
        }

        if (!(sender instanceof Player player)) {
            for (String message : messages.getStringList("global.only-for-players"))
                sender.sendMessage(Utilities.setColor(message));
            return true;
        }


        // todo: work with UUID

        switch (args[0].toLowerCase()) {
            case "create":
                if (sender.hasPermission("nightcodes.player.create")) {

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        try (Connection connection = database.getConnection()) {

                            try {
                                ReferralCode checkCode = database.getDatabaseWorker().getReferralCodeByUsername(connection, player.getName());

                                for (String message : messages.getStringList("referral.exists"))
                                    sender.sendMessage(
                                            Utilities.setColor(message.replace("%player%", player.getName()))
                                    );

                            } catch (CodeNotFoundException e) {
                                ReferralCode code = new ReferralCode(player.getName(), player.getUniqueId());
                                database.getDatabaseWorker().createReferralCode(connection, code);

                                for (String message : messages.getStringList("referral.created"))
                                    sender.sendMessage(
                                            Utilities.setColor(message.replace("%player%", player.getName()))
                                    );
                            }

                        } catch (SQLException e) {
                            for (String message : messages.getStringList("global.database-error"))
                                sender.sendMessage(Utilities.setColor(message));
                        }
                    });

                } else {
                    for (String message : messages.getStringList("global.no-permission"))
                        sender.sendMessage(Utilities.setColor(message));
                }
                break;

            case "delete":
                if (sender.hasPermission("nightcodes.player.delete")) {

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        try (Connection connection = database.getConnection()) {

                            try {
                                ReferralCode checkCode = database.getDatabaseWorker().getReferralCodeByUsername(connection, player.getName());

                                database.getDatabaseWorker().deleteReferralCode(connection, player.getName());

                                for (String message : messages.getStringList("referral.deleted"))
                                    sender.sendMessage(
                                            Utilities.setColor(message.replace("%player%", player.getName()))
                                    );

                            } catch (CodeNotFoundException e) {
                                for (String message : messages.getStringList("referral.not-exist"))
                                    sender.sendMessage(
                                            Utilities.setColor(message.replace("%player%", player.getName()))
                                    );
                            }

                        } catch (SQLException e) {
                            for (String message : messages.getStringList("global.database-error"))
                                sender.sendMessage(Utilities.setColor(message));
                        }
                    });

                } else {
                    for (String message : messages.getStringList("global.no-permission"))
                        sender.sendMessage(Utilities.setColor(message));
                    return true;
                }
                break;

            case "pause":
                if (sender.hasPermission("nightcodes.player.pause")) {

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        try (Connection connection = database.getConnection()) {

                            try {
                                ReferralCode checkCode = database.getDatabaseWorker().getReferralCodeByUsername(connection, player.getName());

                                if (checkCode.getIsPaused() == 1) {
                                    for (String message : messages.getStringList("referral.already-paused"))
                                        sender.sendMessage(
                                                Utilities.setColor(message.replace("%player%", player.getName()))
                                        );
                                    return;
                                }

                                database.getDatabaseWorker().setPaused(connection, player.getName(), (byte) 1);

                                for (String message : messages.getStringList("referral.paused"))
                                    sender.sendMessage(
                                            Utilities.setColor(message.replace("%player%", player.getName()))
                                    );

                            } catch (CodeNotFoundException e) {
                                for (String message : messages.getStringList("referral.not-exist"))
                                    sender.sendMessage(
                                            Utilities.setColor(message.replace("%player%", player.getName()))
                                    );
                            }

                        } catch (SQLException e) {
                            for (String message : messages.getStringList("global.database-error"))
                                sender.sendMessage(Utilities.setColor(message));
                        }
                    });

                } else {
                    for (String message : messages.getStringList("global.no-permission"))
                        sender.sendMessage(Utilities.setColor(message));
                    return true;
                }
                break;

            case "unpause":
                if (sender.hasPermission("nightcodes.player.unpause")) {

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        try (Connection connection = database.getConnection()) {

                            try {
                                ReferralCode checkCode = database.getDatabaseWorker().getReferralCodeByUsername(connection, player.getName());

                                if (checkCode.getIsPaused() != 1) {
                                    for (String message : messages.getStringList("referral.already-unpaused"))
                                        sender.sendMessage(
                                                Utilities.setColor(message.replace("%player%", player.getName()))
                                        );
                                    return;
                                }

                                database.getDatabaseWorker().setPaused(connection, player.getName(), (byte) 0);

                                for (String message : messages.getStringList("referral.unpaused"))
                                    sender.sendMessage(
                                            Utilities.setColor(message.replace("%player%", player.getName()))
                                    );

                            } catch (CodeNotFoundException e) {
                                for (String message : messages.getStringList("referral.not-exist"))
                                    sender.sendMessage(
                                            Utilities.setColor(message.replace("%player%", player.getName()))
                                    );
                            }

                        } catch (SQLException e) {
                            for (String message : messages.getStringList("global.database-error"))
                                sender.sendMessage(Utilities.setColor(message));
                        }
                    });

                } else {
                    for (String message : messages.getStringList("global.no-permission"))
                        sender.sendMessage(Utilities.setColor(message));
                    return true;
                }
                break;

            case "stats":
                if (sender.hasPermission("nightcodes.player.stats")) {

                    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                        try (Connection connection = database.getConnection()) {

                            try {
                                ReferralCode checkCode = database.getDatabaseWorker().getReferralCodeByUsername(connection, player.getName());

                                String state = checkCode.getIsPaused() == 1 ? messages.getString("referral.state.paused", "<#d45079>paused</#d45079>") : messages.getString("referral.state.unpaused", "<#ace1af>active</#ace1af>");

                                for (String message : messages.getStringList("referral.stats"))
                                    sender.sendMessage(
                                            Utilities.setColor(
                                                    message
                                                            .replace("%player%", player.getName())
                                                            .replace("%count%", String.valueOf(checkCode.getUsages()))
                                                            .replace("%state%", state)
                                            )
                                    );

                            } catch (CodeNotFoundException e) {
                                for (String message : messages.getStringList("referral.not-exist"))
                                    sender.sendMessage(
                                            Utilities.setColor(message.replace("%player%", player.getName()))
                                    );
                            }

                        } catch (SQLException e) {
                            for (String message : messages.getStringList("global.database-error"))
                                sender.sendMessage(Utilities.setColor(message));
                        }
                    });

                } else {
                    for (String message : messages.getStringList("global.no-permission"))
                        sender.sendMessage(Utilities.setColor(message));
                    return true;
                }
                break;

            default:
                for (String message : messages.getStringList("referral.usage"))
                    sender.sendMessage(Utilities.setColor(message));
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
