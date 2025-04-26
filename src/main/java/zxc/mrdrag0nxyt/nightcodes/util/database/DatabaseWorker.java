package zxc.mrdrag0nxyt.nightcodes.util.database;

import zxc.mrdrag0nxyt.nightcodes.entity.ReferralCode;
import zxc.mrdrag0nxyt.nightcodes.util.exception.CannotActivateOwnCodeException;
import zxc.mrdrag0nxyt.nightcodes.util.exception.CodeAlreadyUsedException;
import zxc.mrdrag0nxyt.nightcodes.util.exception.CodeNotFoundException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public interface DatabaseWorker {

    void initUsedCodeTable(Connection connection) throws SQLException;

    ReferralCode getReferralCodeByUsername(Connection connection, String username) throws SQLException, CodeNotFoundException;

    ReferralCode getReferralCodeByUuid(Connection connection, UUID uuid) throws SQLException, CodeNotFoundException;

    void initCodesTable(Connection connection) throws SQLException;

    void createReferralCode(Connection connection, ReferralCode referralCode) throws SQLException;

    void deleteReferralCode(Connection connection, UUID uuid) throws SQLException, CodeNotFoundException;

    boolean setPaused(Connection connection, UUID uuid, boolean isPaused) throws SQLException, CodeNotFoundException;

    void useCode(Connection connection, String username, UUID uuid, String referralCode) throws SQLException, CodeNotFoundException, CodeAlreadyUsedException, CannotActivateOwnCodeException;
}
