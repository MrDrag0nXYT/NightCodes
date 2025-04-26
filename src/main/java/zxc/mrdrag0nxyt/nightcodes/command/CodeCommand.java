package zxc.mrdrag0nxyt.nightcodes.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;
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
import java.util.regex.Pattern;

public class CodeCommand implements CommandExecutor, TabCompleter {

    private final Pattern referralCodePattern = Pattern.compile("%referral_code%");

    private final NightCodes plugin;
    private final Config config;
    private final Messages messages;
    private final DatabaseManager database;

    public CodeCommand(NightCodes plugin, Config config, Messages messages, DatabaseManager database) {
        this.plugin = plugin;
        this.config = config;
        this.messages = messages;
        this.database = database;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {

        if (args.length == 0) {
            for (Component component : messages.getCodeUsage())
                sender.sendMessage(component);
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(messages.getOnlyForPlayers());
            return true;
        }

        if ((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) <= config.getMinimalPlayedTime()) {
            sender.sendMessage(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 + " seconds, " + config.getMinimalPlayedTime() + " need");
            sender.sendMessage(messages.getCodeCannotActivateByPlayedTime());
            return false;
        }

        if (!sender.hasPermission("nightcodes.player.activate")) {
            sender.sendMessage(messages.getNoPermission());
            return false;
        }

        TextReplacementConfig textReplacementConfig = TextReplacementConfig.builder()
                .match(referralCodePattern)
                .replacement(args[0])
                .build();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Connection connection = database.getConnection()) {

                database.getDatabaseWorker().useCode(
                        connection,
                        player.getName(),
                        player.getUniqueId(),
                        args[0]
                );

                for (String bonusCommand : config.getOnCodeUseCommands()) {
                    String finalBonusCommand = bonusCommand
                            .replace("%codeOwner%", args[0])
                            .replace("%player%", player.getName());

                    Bukkit.getScheduler().runTask(plugin, () -> {
                        plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), finalBonusCommand);
                    });
                }

                sender.sendMessage(messages.getCodeActivated().replaceText(textReplacementConfig));

            } catch (SQLException e) {
                sender.sendMessage(messages.getDatabaseError());
                plugin.getLogger().severe(e.getMessage());

            } catch (CodeNotFoundException e) {
                sender.sendMessage(messages.getCodeNotFound().replaceText(textReplacementConfig));

            } catch (CodeAlreadyUsedException e) {
                sender.sendMessage(messages.getCodeAlreadyActivated());

            } catch (CannotActivateOwnCodeException e) {
                sender.sendMessage(messages.getCodeCannotActivateOwn());
            }
        });


        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        return Collections.singletonList(
                messages.getAutocompletePlaceholder()
        );
    }
}
