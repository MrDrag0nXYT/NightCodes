package zxc.mrdrag0nxyt.nightcodes.util.config;

import org.bukkit.configuration.file.YamlConfiguration;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;
import zxc.mrdrag0nxyt.nightcodes.util.Utilities;

import java.io.File;

public class Config {

    private final NightCodes plugin;
    private File file;
    private YamlConfiguration config;

    public Config(NightCodes plugin) {
        this.plugin = plugin;

        load();
    }

    private void load() {
        file = new File(plugin.getDataFolder(), "config.yml");
        if (!file.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(file);

        Utilities.checkValue(config, "database.type", "SQLITE");
        save();
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe(String.valueOf(e));
        }
    }

    public void reload() {
        try {
            config.load(file);
        } catch (Exception e) {
            plugin.getLogger().severe(String.valueOf(e));
        }
    }

    public YamlConfiguration getConfig() {
        return config;
    }

}
