package zxc.mrdrag0nxyt.nightcodes.config;

import lombok.Getter;
import zxc.mrdrag0nxyt.nightcodes.NightCodes;
import zxc.mrdrag0nxyt.nightcodes.util.database.DatabaseManager;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Config extends AbstractConfig {

    private boolean isMetricsEnabled = true;
    private boolean isUpdateCheckEnabled, isUpdatesAnnounceEnabled = true;

    private List<String> onCodeUseCommands = List.of(
            "betterdonate give %codeOwner% money 3500",
            "p give %codeOwner% 10",
            "p give %player% 5"
    );
    private long minimalPlayedTime;

    private DatabaseManager.DatabaseType databaseType;

    public Config(NightCodes plugin) {
        super(plugin, "config.yml");
    }

    @Override
    protected void updateConfig() {
        isMetricsEnabled = checkValue("enable-metrics", isMetricsEnabled);
        isUpdateCheckEnabled = checkValue("update-check.enabled", isUpdateCheckEnabled);
        isUpdatesAnnounceEnabled = checkValue("update-check.announce-on-join", isUpdatesAnnounceEnabled);

        onCodeUseCommands = checkValue("commands", onCodeUseCommands);
        minimalPlayedTime = (long) checkValue("requirements.played-time", 3600);

        String databaseTypeString = checkValue("database.type", "SQLITE");
        databaseType = DatabaseManager.DatabaseType.valueOf(databaseTypeString);
    }

}
