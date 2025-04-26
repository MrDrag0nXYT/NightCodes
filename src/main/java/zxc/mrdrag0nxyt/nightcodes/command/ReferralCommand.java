package zxc.mrdrag0nxyt.nightcodes.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;
import zxc.mrdrag0nxyt.nightcodes.entity.ReferralCode;
import zxc.mrdrag0nxyt.nightcodes.config.*;
import zxc.mrdrag0nxyt.nightcodes.util.database.DatabaseManager;
import zxc.mrdrag0nxyt.nightcodes.util.exception.CodeNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ReferralCommand implements CommandExecutor, TabCompleter {

    private final Pattern playerPattern = Pattern.compile("%player%");
    private final Pattern countPattern = Pattern.compile("%count%");
    private final Pattern statePattern = Pattern.compile("%state%");

    private final NightCodes plugin;
    private final Messages messages;
    private final DatabaseManager database;

    public ReferralCommand(NightCodes plugin, Messages messages, DatabaseManager database) {
        this.plugin = plugin;
        this.messages = messages;
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            for (Component component : messages.getReferralUsage())
                sender.sendMessage(component);
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.getOnlyForPlayers());
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "create" -> {
                return createSubcommand(player);
            }

            case "delete" -> {
                return deleteSubcommand(player);
            }

            case "pause", "unpause" -> {
                return pauseSubcommand(player, args[0]);
            }

            case "stats" -> {
                return statsSubcommand(player);
            }

            default -> {
                return usageSubcommand(player);
            }
        }
    }


    private boolean createSubcommand(Player player) {
        if (!player.hasPermission("nightcodes.player.create")) {
            player.sendMessage(messages.getNoPermission());
            return false;
        }

        TextReplacementConfig textReplacementConfig = TextReplacementConfig.builder()
                .match(playerPattern)
                .replacement(player.getName())
                .build();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = database.getConnection()) {

                try {
                    database.getDatabaseWorker().getReferralCodeByUsername(connection, player.getName());
                    player.sendMessage(messages.getReferralExists().replaceText(textReplacementConfig));

                } catch (CodeNotFoundException e) {
                    ReferralCode code = new ReferralCode(player.getName(), player.getUniqueId());
                    database.getDatabaseWorker().createReferralCode(connection, code);

                    for (Component component : messages.getReferralCreated())
                        player.sendMessage(component.replaceText(textReplacementConfig));
                }

            } catch (SQLException e) {
                player.sendMessage(messages.getDatabaseError());
                plugin.getLogger().severe(e.getMessage());
            }
        });

        return true;
    }

    private boolean deleteSubcommand(Player player) {
        if (!player.hasPermission("nightcodes.player.delete")) {
            player.sendMessage(messages.getNoPermission());
            return false;
        }

        TextReplacementConfig textReplacementConfig = TextReplacementConfig.builder()
                .match(playerPattern)
                .replacement(player.getName())
                .build();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = database.getConnection()) {
                database.getDatabaseWorker().deleteReferralCode(connection, player.getUniqueId());
                player.sendMessage(messages.getReferralDeleted().replaceText(textReplacementConfig));

            } catch (SQLException e) {
                player.sendMessage(messages.getDatabaseError());
                plugin.getLogger().severe(e.getMessage());

            } catch (CodeNotFoundException e) {
                player.sendMessage(messages.getReferralNotExist().replaceText(textReplacementConfig));
            }
        });

        return true;
    }

    private boolean pauseSubcommand(Player player, String subcommand) {
        if (!player.hasPermission("nightcodes.player.pause")) {
            player.sendMessage(messages.getNoPermission());
            return false;
        }

        boolean isPause = subcommand.equalsIgnoreCase("pause");
        Component successMessage = isPause ? messages.getReferralPaused() : messages.getReferralUnPaused();
        Component failMessage = isPause ? messages.getReferralUnPaused() : messages.getReferralPaused();

        TextReplacementConfig textReplacementConfig = TextReplacementConfig.builder()
                .match(playerPattern)
                .replacement(player.getName())
                .build();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = database.getConnection()) {
                boolean isChanged = database.getDatabaseWorker().setPaused(connection, player.getUniqueId(), isPause);

                if (isChanged) {
                    player.sendMessage(successMessage.replaceText(textReplacementConfig));
                } else {
                    player.sendMessage(failMessage.replaceText(textReplacementConfig));
                }

            } catch (SQLException e) {
                player.sendMessage(messages.getDatabaseError());
                plugin.getLogger().severe(e.getMessage());

            } catch (CodeNotFoundException e) {
                player.sendMessage(messages.getReferralNotExist().replaceText(textReplacementConfig));
            }
        });

        return true;
    }

    private boolean statsSubcommand(Player player) {
        if (!player.hasPermission("nightcodes.player.stats")) {
            player.sendMessage(messages.getNoPermission());
            return false;
        }

        TextReplacementConfig playerNameReplacer = TextReplacementConfig.builder()
                .match(playerPattern)
                .replacement(player.getName())
                .build();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = database.getConnection()) {
                ReferralCode code = database.getDatabaseWorker().getReferralCodeByUuid(connection, player.getUniqueId());

                Component state = code.getIsPaused() == 1 ? messages.getReferralStatePaused() : messages.getReferralStateUnPaused();
                String usagesCount = String.valueOf(code.getUsages());

                TextReplacementConfig stateReplacer = TextReplacementConfig.builder()
                        .match(statePattern)
                        .replacement(state)
                        .build();

                TextReplacementConfig countReplacer = TextReplacementConfig.builder()
                        .match(countPattern)
                        .replacement(usagesCount)
                        .build();

                for (Component component : messages.getReferralStats()) {
                    player.sendMessage(
                            component
                                    .replaceText(playerNameReplacer)
                                    .replaceText(stateReplacer)
                                    .replaceText(countReplacer)
                    );
                }

            } catch (SQLException e) {
                player.sendMessage(messages.getDatabaseError());
                plugin.getLogger().severe(e.getMessage());

            } catch (CodeNotFoundException e) {
                player.sendMessage(messages.getReferralNotExist().replaceText(playerNameReplacer));
            }
        });

        return true;
    }

    private boolean usageSubcommand(Player player) {
        for (Component component : messages.getReferralUsage()) {
            player.sendMessage(component);
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
