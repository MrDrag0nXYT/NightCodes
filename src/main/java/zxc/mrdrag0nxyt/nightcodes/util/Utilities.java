package zxc.mrdrag0nxyt.nightcodes.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.YamlConfiguration;

public class Utilities {

    public static void checkValue(YamlConfiguration config, String path, Object defaultValue) {
        if (!config.contains(path)) {
            config.set(path, defaultValue);
        }
    }

    public static Component setColor(String from) {
        return MiniMessage.miniMessage().deserialize(from);
    }

}
