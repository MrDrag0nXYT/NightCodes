package zxc.mrdrag0nxyt.nightcodes.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;
import zxc.mrdrag0nxyt.nightcodes.config.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UpdateChecker {

    private static final String UPDATE_CHECKER_URL = "https://api.github.com/repos/MrDrag0nXYT/NightCodes/releases/latest";

    private static UpdateEntity updateEntity = null;
    private static final List<Component> updateMessage = new ArrayList<>();

    public UpdateChecker(NightCodes plugin, Config config) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(UPDATE_CHECKER_URL).openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    );
                    String inputLine;
                    StringBuilder builder = new StringBuilder();
                    while ((inputLine = reader.readLine()) != null) {
                        builder.append(inputLine);
                    }
                    reader.close();
                    connection.disconnect();

                    updateEntity = gson.fromJson(String.valueOf(builder), UpdateEntity.class);
                }

                if (updateEntity != null) {
                    String currentVersion = plugin.getDescription().getVersion();

                    if (!updateEntity.tag_name.equals(currentVersion)) {
                        MiniMessage miniMessage = MiniMessage.miniMessage();

                        updateMessage.add(miniMessage.deserialize(" "));
                        updateMessage.add(miniMessage.deserialize("<#a880ff>NightCodes <#696969>> <#fffafa>There are found update <#a880ff>" + updateEntity.name));
                        updateMessage.add(miniMessage.deserialize("<#fffafa>Your version - <#dc143c>" + currentVersion + "</#dc143c>, available <#00ff7f>" + updateEntity.tag_name + "</#00ff7f>"));
                        updateMessage.add(miniMessage.deserialize(" "));
                        updateMessage.add(miniMessage.deserialize("<#fffafa>You can download it here - <#a880ff><click:open_url:'" + updateEntity.html_url + "'>" + updateEntity.html_url + "</click>"));
                        updateMessage.add(miniMessage.deserialize(" "));

                        announceUpdate(plugin.getServer().getConsoleSender());

                        if (config.isUpdatesAnnounceEnabled())
                            Bukkit.getPluginManager().registerEvents(new JoinAnnouncer(), plugin);
                    }
                }

            } catch (Exception e) {
                plugin.getLogger().severe("Error on connecting GitHub while checking for updates");
                plugin.getLogger().severe(String.valueOf(e));
            }
        });
    }

    private static class JoinAnnouncer implements Listener {
        @EventHandler
        public void AnnounceUpdateOnJoin(PlayerJoinEvent event) {
            Player player = event.getPlayer();
            if (player.hasPermission("nightcodes.admin.announceupdates")) {
                announceUpdate(player);
            }
        }
    }

    private static record UpdateEntity(String tag_name, String name, String html_url) {
    }

    private static void announceUpdate(CommandSender sender) {
        for (Component component : updateMessage) {
            sender.sendMessage(component);
        }
    }

}
