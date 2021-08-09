package app.psqlModule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.math.BigInteger;  
import java.nio.charset.StandardCharsets; 
import java.security.MessageDigest;  
import java.security.NoSuchAlgorithmException;  
 
public class PsqlModule {

   private Connection connection;
   private String namespace;
   private Statement s;

   private String dbase;
   private String user;
   private String password;

   private boolean autoflush = true;


   public PsqlModule(String _dbase, String _user, String _password) throws ClassNotFoundException, SQLException {
      Class.forName("org.postgresql.Driver");
      dbase = _dbase;
      user = _user;
      password = _password;
   }

   public void openConnection() throws SQLException{

      connection = DriverManager.getConnection(dbase,user,password);
      connection.setAutoCommit(false);
      //System.out.println("THIS CONNECTION IS BEING OPENED.");
   }

   public void closeConnection() throws SQLException{
      connection.close();
      //System.out.println("THIS CONNECTION IS BEING CLOSED.");
   }

   public void executeStatement(String sql) throws SQLException{
      if(autoflush) flushStatement();
      s = connection.createStatement();
      s.executeUpdate(sql);
   }

   public PreparedStatement getPreparedStatement(String sql) throws SQLException{
      return connection.prepareStatement(sql);
   }

   public int executePreparedStatement(PreparedStatement ps) throws SQLException{
      int i = ps.executeUpdate();
      ps.close();
      connection.commit();
      return i;
   }

   public void flushStatement() throws SQLException{
      if(s!=null) s.close();
   }

   public ResultSet executeQuery(String sql) throws SQLException{
      if(autoflush) flushStatement();
      s = connection.createStatement();
      ResultSet rs = s.executeQuery(sql);
      return rs;
   }
    
   public static String hashSHA(String input) throws NoSuchAlgorithmException { 
      MessageDigest md = MessageDigest.getInstance("SHA-256");
      byte[] hash =  md.digest(input.getBytes(StandardCharsets.UTF_8));  
      BigInteger h = new BigInteger(1, hash);   
      StringBuilder s = new StringBuilder(h.toString(16));  
      while (s.length() < 32){  
         s.insert(0, '0');  
      }  
      return s.toString();  
   } 

   public void setNoFlushMode(){
      autoflush = false;
   }

   public void setAutoFlushMode(){
      autoflush = true;
   }

}

