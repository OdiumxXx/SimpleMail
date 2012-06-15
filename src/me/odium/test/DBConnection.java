package me.odium.test;

import java.sql.Connection;
import java.sql.DriverManager;


public class DBConnection {
  private static DBConnection instance = new DBConnection();

  private DBConnection() {
  }

  public static synchronized DBConnection getInstance() {
      return instance;
  }

  public Connection Database() {
    Connection con;
    try{
      Class.forName("org.sqlite.JDBC");
      con = DriverManager.getConnection("jdbc:sqlite:test.db");
      return con;
    } catch(Exception e) {
      System.err.println(e);
    }
    return null;
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
