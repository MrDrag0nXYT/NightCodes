package zxc.mrdrag0nxyt.nightcodes;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;

import org.bukkit.plugin.java.JavaPlugin;
import zxc.mrdrag0nxyt.nightcodes.command.CodeCommand;
import zxc.mrdrag0nxyt.nightcodes.command.NightCodesCommand;
import zxc.mrdrag0nxyt.nightcodes.command.ReferralCommand;
import zxc.mrdrag0nxyt.nightcodes.util.config.Config;
import zxc.mrdrag0nxyt.nightcodes.util.database.DatabaseManager;
import zxc.mrdrag0nxyt.nightcodes.util.config.Messages;
import zxc.mrdrag0nxyt.nightcodes.util.database.DatabaseWorker;

import java.sql.Connection;
import java.sql.SQLException;

public final class NightCodes extends JavaPlugin {

    private BukkitAudiences adventure;

    public BukkitAudiences adventure() {
        if(this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return this.adventure;
    }

    private Config config;
    private Messages messages;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        this.adventure = BukkitAudiences.create(this);

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

        getCommand("referral").setExecutor(new ReferralCommand(this, config, messages, databaseManager));
        getCommand("code").setExecutor(new CodeCommand(this, config, messages, databaseManager));
        getCommand("nightcodes").setExecutor(new NightCodesCommand(this, config, messages));
    }

    @Override
    public void onDisable() {
        if(this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
        }

        databaseManager.closeConnection();
    }

    public void reload() {
        config.reload();
        messages.reload();
    }
}
