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

public final class NightCodes extends JavaPlugin {

    private Config config;
    private Messages messages;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        config = new Config(this);
        messages = new Messages(this);
        databaseManager = new DatabaseManager(this, config);

        if (config.isMetricsEnabled()) {
            new Metrics(this, 24236);
        }

        if (config.isUpdateCheckEnabled()) {
            new UpdateChecker(this, config);
        }

        getCommand("referral").setExecutor(new ReferralCommand(this, messages, databaseManager));
        getCommand("code").setExecutor(new CodeCommand(this, config, messages, databaseManager));
        getCommand("nightcodes").setExecutor(new NightCodesCommand(this, messages));
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.closeConnection();
        }
    }

    public void reload() {
        config.reload();
        messages.reload();
        databaseManager.reloadConnection();
    }
}
