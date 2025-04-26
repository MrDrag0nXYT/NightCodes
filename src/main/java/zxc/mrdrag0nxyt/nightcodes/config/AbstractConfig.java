package zxc.mrdrag0nxyt.nightcodes.config;

import org.bukkit.configuration.file.YamlConfiguration;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;

import java.io.File;

public abstract class AbstractConfig {
    protected final NightCodes plugin;
    protected final File file;
    protected final String fileName;
    protected YamlConfiguration yamlConfiguration;

    public AbstractConfig(NightCodes plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        this.file = new File(plugin.getDataFolder(), fileName);

        load();
        updateConfig();
    }


    protected void extractIfNotExists() {
        if (!file.exists()) {
            plugin.saveResource(fileName, false);
        }
    }

    protected void load() {
        extractIfNotExists();
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            yamlConfiguration.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe(String.valueOf(e));
        }
    }

    public void reload() {
        extractIfNotExists();
        try {
            yamlConfiguration.load(file);
            updateConfig();
        } catch (Exception e) {
            plugin.getLogger().severe(String.valueOf(e));
        }
    }

    protected abstract void updateConfig();


    protected <T> T checkValue(String path, T defaultValue) {
        if (!yamlConfiguration.contains(path)) {
            yamlConfiguration.set(path, defaultValue);
            return defaultValue;

        } else {
            return (T) yamlConfiguration.get(path);
        }
    }
}
