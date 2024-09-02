package zxc.mrdrag0nxyt.nightcodes.entity;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ReferralCode {

    public ReferralCode(String username, UUID uuid) {
        this.username = username;
        this.uuid = uuid;
    }

    private long id;
    private String username;
    private UUID uuid;
    private byte isPaused;
    private long usages;
}
