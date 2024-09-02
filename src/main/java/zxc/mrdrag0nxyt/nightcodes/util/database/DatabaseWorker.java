package zxc.mrdrag0nxyt.nightcodes.util.database;

import zxc.mrdrag0nxyt.nightcodes.entity.ReferralCode;
import zxc.mrdrag0nxyt.nightcodes.util.exception.CodeAlreadyUsedException;
import zxc.mrdrag0nxyt.nightcodes.util.exception.CodeNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public interface DatabaseWorker {
    ReferralCode getReferralCodeByUsername(Connection connection, String username) throws SQLException, CodeNotFoundException;
    ReferralCode getReferralCodeByUuid(Connection connection, UUID uuid) throws SQLException, CodeNotFoundException;
    void initCodesTable(Connection connection) throws SQLException;
    void createReferralCode(Connection connection, ReferralCode referralCode) throws SQLException;
    void deleteReferralCode(Connection connection, String username) throws SQLException;
    void setPaused(Connection connection, String username, byte isPaused) throws SQLException;
    void deleteReferralCodeByUuid(Connection connection, UUID uuid) throws SQLException;
    void setPausedByUuid(Connection connection, UUID uuid, boolean isPaused) throws SQLException;

    void initUsedCodeTable(Connection connection) throws SQLException;
    void useCode(Connection connection, String username, UUID uuid, String referralCode) throws SQLException, CodeNotFoundException, CodeAlreadyUsedException;
}
