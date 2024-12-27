package zxc.mrdrag0nxyt.nightcodes.config;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;

import java.io.File;

public class Messages {

    private final NightCodes plugin;
    private final File file;
    @Getter
    private YamlConfiguration config;

    public Messages(NightCodes plugin) {
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "messages.yml");
        load();
    }

    private void extractIfNotExists() {
        if (!file.exists()) {
            plugin.saveResource("messages.yml", false);
        }
    }

    private void load() {
        extractIfNotExists();
        config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception e) {
            plugin.getLogger().severe(String.valueOf(e));
        }
    }

    public void reload() {
        extractIfNotExists();
        try {
            config.load(file);
        } catch (Exception e) {
            plugin.getLogger().severe(String.valueOf(e));
        }
    }

}
