package zxc.mrdrag0nxyt.nightcodes.util.database.implementation;

import zxc.mrdrag0nxyt.nightcodes.entity.ReferralCode;
import zxc.mrdrag0nxyt.nightcodes.util.database.DatabaseWorker;
import zxc.mrdrag0nxyt.nightcodes.util.exception.CodeAlreadyUsedException;
import zxc.mrdrag0nxyt.nightcodes.util.exception.CodeNotFoundException;

import java.sql.*;
import java.util.UUID;

public class SQLiteDatabaseWorker implements DatabaseWorker {

    @Override
    public ReferralCode getReferralCodeByUsername(Connection connection, String username) throws SQLException, CodeNotFoundException {
        String sql = "SELECT * FROM referral_codes WHERE username = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new ReferralCode(
                    resultSet.getLong("id"),
                    resultSet.getString("username"),
                    UUID.fromString(resultSet.getString("uuid")),
                    resultSet.getByte("is_paused"),
                    resultSet.getLong("usages")
            );

        } else {
            throw new CodeNotFoundException();
        }
    }

    @Override
    public ReferralCode getReferralCodeByUuid(Connection connection, UUID uuid) throws SQLException, CodeNotFoundException {
        String sql = "SELECT * FROM referral_codes WHERE uuid = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, uuid.toString());
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            return new ReferralCode(
                    resultSet.getLong("id"),
                    resultSet.getString("username"),
                    uuid,
                    resultSet.getByte("is_paused"),
                    resultSet.getLong("usages")
            );

        } else {
            throw new CodeNotFoundException();
        }
    }

    @Override
    public void initCodesTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS `referral_codes`\n" +
                "(\n" +
                "    `id` INTEGER NOT NULL UNIQUE,\n" +
                "    `username` TEXT NOT NULL UNIQUE,\n" +
                "    `uuid` TEXT UNIQUE,\n" +
                "    `is_paused` INTEGER NOT NULL DEFAULT 0 CHECK(is_paused >= 0 AND is_paused <= 1),\n" +
                "    `usages` INTEGER NOT NULL DEFAULT 0,\n" +
                "    PRIMARY KEY(`id` AUTOINCREMENT)" +
                ");";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }

    @Override
    public void createReferralCode(Connection connection, ReferralCode referralCode) throws SQLException {
        String sql;

        sql = "INSERT INTO referral_codes (username, uuid) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, referralCode.getUsername());
        statement.setString(2, referralCode.getUuid().toString());
        statement.executeUpdate();
    }

    @Override
    public void deleteReferralCode(Connection connection, String username) throws SQLException {
        String sql = "DELETE FROM referral_codes WHERE username = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, username);
        statement.executeUpdate();
    }

    @Override
    public void setPaused(Connection connection, String username, byte isPaused) throws SQLException {
        String sql = "UPDATE referral_codes SET is_paused = ? WHERE username = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setByte(1, isPaused);
        statement.setString(2, username);
        statement.executeUpdate();
    }

    @Override
    public void deleteReferralCodeByUuid(Connection connection, UUID uuid) throws SQLException {
        String sql = "DELETE FROM referral_codes WHERE uuid = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, uuid.toString());
        statement.executeUpdate();
    }

    @Override
    public void setPausedByUuid(Connection connection, UUID uuid, boolean isPaused) throws SQLException {
        String sql = "UPDATE referral_codes SET is_paused = ? WHERE uuid = ?";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setBoolean(1, isPaused);
        statement.setString(2, uuid.toString());
        statement.executeUpdate();
    }



    // Used codes table

    @Override
    public void initUsedCodeTable(Connection connection) throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS `used_referral_codes`\n" +
                "(\n" +
                "    `id` INTEGER NOT NULL UNIQUE,\n" +
                "    `username` TEXT NOT NULL UNIQUE,\n" +
                "    `uuid` TEXT NOT NULL UNIQUE,\n" +
                "    `used_code` TEXT NOT NULL,\n" +
                "    PRIMARY KEY( `id` AUTOINCREMENT)\n" +
                ");";

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.executeUpdate();
    }

    @Override
    public void useCode(Connection connection, String username, UUID uuid, String referralCode) throws SQLException, CodeNotFoundException, CodeAlreadyUsedException {
        String searchCodeSql = "SELECT * FROM referral_codes WHERE username = ?";

        PreparedStatement searchStatement = connection.prepareStatement(searchCodeSql);
        searchStatement.setString(1, referralCode);
        ResultSet searchResultSet = searchStatement.executeQuery();

        if (searchResultSet.next()) {
            ReferralCode foundCode = new ReferralCode(
                    searchResultSet.getLong("id"),
                    searchResultSet.getString("username"),
                    UUID.fromString(searchResultSet.getString("uuid")),
                    searchResultSet.getByte("is_paused"),
                    searchResultSet.getLong("usages")
            );
        } else {
            throw new CodeNotFoundException();
        }

        String checkUsedCodeSql = "SELECT * FROM used_referral_codes WHERE username = ?";

        PreparedStatement checkUsedCodeStatement = connection.prepareStatement(checkUsedCodeSql);
        checkUsedCodeStatement.setString(1, username);
        ResultSet checkUsedCodeResultSet = checkUsedCodeStatement.executeQuery();

        if (checkUsedCodeResultSet.next()) {
            throw new CodeAlreadyUsedException();
        }

        String addUsageCount = "UPDATE referral_codes SET usages = usages + 1 WHERE username = ?";

        PreparedStatement addUsageStatement = connection.prepareStatement(addUsageCount);
        addUsageStatement.setString(1, referralCode);
        addUsageStatement.executeUpdate();

        String addUsedCodeSql = "INSERT INTO used_referral_codes (username, uuid, used_code) VALUES (?, ?, ?);";

        PreparedStatement addCodeStatement = connection.prepareStatement(addUsedCodeSql);
        addCodeStatement.setString(1, username);
        addCodeStatement.setString(2, uuid.toString());
        addCodeStatement.setString(3, referralCode);
        addCodeStatement.executeUpdate();
    }

}
