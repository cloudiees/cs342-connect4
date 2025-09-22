import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

public class Database {
    Connection con;
    public Database() throws SQLException {
        con = DriverManager.getConnection("jdbc:sqlite:users.db");
        createTable();
    }
    private void createTable() throws SQLException {
        String createTable = "CREATE TABLE IF NOT EXISTS Users ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "username TEXT NOT NULL UNIQUE,"
                + "password TEXT NOT NULL,"
                + "wins integer, "
                + "losses integer);";
        Statement stmt = con.createStatement();
        stmt.executeUpdate(createTable);
    }
    public void incrementLosses(String username) throws SQLException {
        String increment = "UPDATE Users SET losses = losses + 1 WHERE username = ?";
        PreparedStatement pstmt = con.prepareStatement(increment);
        pstmt.setString(1,username);
        pstmt.executeUpdate();
    }
    public void incrementWins(String username) throws SQLException {
        String increment = "UPDATE Users SET wins = wins + 1 WHERE username = ?";
        PreparedStatement pstmt = con.prepareStatement(increment);
        pstmt.setString(1,username);
        pstmt.executeUpdate();
    }
    public void displayDatabase(String table) throws SQLException {
        String select = "SELECT * FROM " + table;
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(select);
        System.out.println("Table: " + table);
        while (rs.next()) {
            System.out.println(rs.getInt(1) + "\t" + rs.getString(2) + "\t" + rs.getString(3) + "\t" + rs.getInt(4) + "\t" + rs.getInt(5));
        }
    }
    public void insertUser(String username, String password) throws SQLException {
        String insert = "INSERT INTO Users(username, password, wins, losses) VALUES(?,?,?,?)";
        PreparedStatement pstmt = con.prepareStatement(insert);
        pstmt.setString(1,username);
        pstmt.setString(2,password);
        pstmt.setInt(3,0);
        pstmt.setInt(4,0);
        pstmt.executeUpdate();
    }
    public boolean validatePassword(String username, String password) throws SQLException {
        String findPass = "SELECT password FROM Users WHERE username = ?";
        PreparedStatement pstmt = con.prepareStatement(findPass);
        pstmt.setString(1,username);
        ResultSet rs = pstmt.executeQuery();
        return Objects.equals(rs.getString(1), password);
    }
    public Message.Account getAccount(String username) throws SQLException {
        String select = "SELECT * FROM Users WHERE username = ?";
        PreparedStatement pstmt = con.prepareStatement(select);
        pstmt.setString(1,username);
        ResultSet rs = pstmt.executeQuery();
        Message.Account account = new Message.Account();
        account.username = rs.getString(2);
        account.password = rs.getString(3);
        account.wins = rs.getInt(4);
        account.losses = rs.getInt(5);
        return account;
    }
    public ArrayList<Message.Account> getLeaderboard() throws SQLException {
        String getLB = "SELECT Users.username, Users.wins, Users.losses FROM Users ORDER BY wins DESC;";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(getLB);
        ArrayList<Message.Account> accounts = new ArrayList<>();
        while (rs.next()) {
            Message.Account account = new Message.Account();
            account.username = rs.getString(1);
            account.wins = rs.getInt(2);
            account.losses = rs.getInt(3);
            accounts.add(account);
        }
        return accounts;
    }
}
