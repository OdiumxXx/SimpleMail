package me.odium.test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import me.odium.test.simplemail;


public class DBConnection {
    private static DBConnection instance = new DBConnection();
    public Connection con = null;
    public  int Timeout = 30;
    public Statement stmt;

    public simplemail plugin;

    private DBConnection() {
    }

    public static synchronized DBConnection getInstance() {
        return instance;
    }
    
    /**
     * We set the plugin that is to be used for these connections.
     * @param plugin
     */
    public void setPlugin(simplemail plugin) {
        this.plugin = plugin;
    }

    public void setConnection() throws Exception {
        Class.forName("org.sqlite.JDBC");
        con = DriverManager.getConnection("jdbc:sqlite:"+plugin.getDataFolder().getAbsolutePath()+File.separator+"SimpleMail.db");
    }

    public Connection getConnection() {
        return con;
    }

    public void closeConnection() {
        try { con.close(); } catch (Exception ignore) {}
    }

    public void createTable() {
        Statement stmt;
        try {
            stmt = con.createStatement();
            String queryC = "CREATE TABLE IF NOT EXISTS SM_Mail (id INTEGER PRIMARY KEY, sender varchar(16) collate nocase, target varchar(16) collate nocase, date timestamp, message varchar(30), read varchar(10), expiration timestamp)";
            stmt.executeUpdate(queryC);
        } catch(Exception e) {
            plugin.log.info("[SimpleMail] "+"Error: "+e);
        }
    }  

    public void setStatement() throws Exception {
        if (con == null) {
            setConnection();
        }
        Statement stmt = con.createStatement();
        stmt.setQueryTimeout(Timeout);  // set timeout to 30 sec.
    }

    public  Statement getStatement() {
        return stmt;
    }

    public void executeStmt(String instruction) throws SQLException {
        stmt.executeUpdate(instruction);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException("Clone is not allowed.");
    }
}

//public class DBConnection {
//
//  public simplemail plugin;
//  public DBConnection(simplemail plugin)  {
//    this.plugin = plugin;
//  }
//
//  public Connection DBConnect() {
//    Connection con;
//    try{
//      Class.forName("org.sqlite.JDBC");
//      con = DriverManager.getConnection("jdbc:sqlite:test.db");
//      return con;
//    } catch(Exception e) {
//      System.err.println(e);
//    }
//    return null;
//  }
//  
//  public Statement DBStatement() {
//    java.sql.Statement stmt;
//    try {
//      stmt = DBConnect().createStatement();
//      return stmt;
//    } catch(Exception e) {
//      System.err.println(e);
//    }
//    return null;
//  }
//
//  public void DBCreatetable() {
//    Statement stmt;
//    try {
//      stmt = DBStatement();
//      String queryC = "CREATE TABLE IF NOT EXISTS SM_Mail (id INTEGER PRIMARY KEY, sender varchar(16), target varchar(16), date timestamp, message varchar(30), read varchar(10))";
//      stmt.executeUpdate(queryC);
//    } catch(Exception e) {
//      System.err.println(e);
//    }
//  }  
// 
//  public void DBcloseconnection() {
//    try{    
//      DBConnect().close();    
//    } catch(Exception e) {
//      System.err.println(e);
//    }
//  }
//
//}
