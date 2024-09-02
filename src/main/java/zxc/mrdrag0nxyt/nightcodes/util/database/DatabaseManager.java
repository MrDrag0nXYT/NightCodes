package zxc.mrdrag0nxyt.nightcodes.util.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;
import zxc.mrdrag0nxyt.nightcodes.util.config.Config;
import zxc.mrdrag0nxyt.nightcodes.util.database.implementation.SQLiteDatabaseWorker;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseManager {

    private final NightCodes plugin;
    private YamlConfiguration pluginConfig;

    private HikariDataSource dataSource;
    @Getter
    private DatabaseWorker databaseWorker;

    public DatabaseManager(NightCodes plugin, Config config) {
        this.plugin = plugin;
        this.pluginConfig = config.getConfig();

        initConnection();
    }

    private void initConnection() {
        HikariConfig hikariConfig = new HikariConfig();

        switch (pluginConfig.getString("database.type", "SQLITE").toLowerCase()) {
            default:
                hikariConfig.setJdbcUrl("jdbc:sqlite:" + plugin.getDataFolder() + File.separator + "database.db");
                databaseWorker = new SQLiteDatabaseWorker();
                break;
        }

        dataSource = new HikariDataSource(hikariConfig);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closeConnection() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
