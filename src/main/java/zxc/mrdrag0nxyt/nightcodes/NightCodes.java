package zxc.mrdrag0nxyt.nightcodes;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import zxc.mrdrag0nxyt.nightcodes.command.CodeCommand;
import zxc.mrdrag0nxyt.nightcodes.command.NightCodesCommand;
import zxc.mrdrag0nxyt.nightcodes.command.ReferralCommand;
import zxc.mrdrag0nxyt.nightcodes.config.Config;
import zxc.mrdrag0nxyt.nightcodes.util.UpdateChecker;
import zxc.mrdrag0nxyt.nightcodes.util.database.DatabaseManager;
import zxc.mrdrag0nxyt.nightcodes.config.Messages;
import zxc.mrdrag0nxyt.nightcodes.util.database.DatabaseWorker;

import java.sql.Connection;
import java.sql.SQLException;

public final class NightCodes extends JavaPlugin {

    private Config config;
    private Messages messages;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        config = new Config(this);
        messages = new Messages(this);

        databaseManager = new DatabaseManager(this, config);

        DatabaseWorker worker = databaseManager.getDatabaseWorker();
        try (Connection connection = databaseManager.getConnection()) {
            worker.initCodesTable(connection);
            worker.initUsedCodeTable(connection);
        } catch (SQLException e) {
            getLogger().severe(String.valueOf(e));
        }

        if (config.getConfig().getBoolean("enable-metrics", true)) {
            new Metrics(this, 24236);
        }

        if (config.getConfig().getBoolean("update-check.enabled", true)) {
            new UpdateChecker(this, config);
        }

        getCommand("referral").setExecutor(new ReferralCommand(this, config, messages, databaseManager));
        getCommand("code").setExecutor(new CodeCommand(this, config, messages, databaseManager));
        getCommand("nightcodes").setExecutor(new NightCodesCommand(this, config, messages));
    }

    @Override
    public void onDisable() {
        databaseManager.closeConnection();
    }

    public void reload() {
        config.reload();
        messages.reload();
    }
}
