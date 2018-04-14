package reg.devices;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import reg.devices.WorkflowClass.RegInfo;
import reg.devices.WorkflowClass2.UpInfo;
import reg.devices.WorkflowClass4.DeviceInfo;

public class SQLiteJDBC {
	
	

/**
 * <h1>insert</h1>
 * @param name
 * @param type
 * @param address
 * @param password
 * @param manufacturer
 * @param model
 */
public static void insert(String id, String name, String type, String address, String password, String manufacturer, String model) {
      Connection c = null;
      Statement stmt = null;
      //int row = 0;
      
      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:/home/mkoutli/Documents/iotivity_devices");
         c.setAutoCommit(false);
         System.out.println("Opened database successfully");
         
         DatabaseMetaData md = c.getMetaData();
         ResultSet rs = md.getTables(null, null, "%", null);
         while (rs.next()) {
           System.out.println(rs.getString(3));
         }

         stmt = c.createStatement();
         
         String sql = "INSERT INTO ALL_DEVICES (id, name, type, address, password, manufacturer, model ) " +
                        "VALUES ( \'" + id + "\', \'" + name + "\', \'" +  type + "\', \'" + address + "\', \'" + password + "\', \'" + manufacturer + "\', \'" + model + "\' );"; 
         stmt.executeUpdate(sql);
         // get key
         //row = stmt.getGeneratedKeys().getInt(1);
         //System.out.println(row);

         stmt.close();
         c.commit();
         c.close();
      } catch ( Exception e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         //System.exit(0);
      }
      System.out.println("Records created successfully");
      //return row;
   }
   
   /**
 * @param ID: the device ID
 */
public static void delete(String ID) {
	   Connection c = null;
	      Statement stmt = null;
	      
	      try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:/home/mkoutli/Documents/iotivity_devices");
	         c.setAutoCommit(false);
	         System.out.println("Opened database successfully");

	         stmt = c.createStatement();
	         String sql = "DELETE from ALL_DEVICES where ID LIKE \'" + ID + "\';";
	         stmt.executeUpdate(sql);
	         c.commit();

	         ResultSet rs = stmt.executeQuery( "SELECT * FROM ALL_DEVICES;" );
	         
	         while ( rs.next() ) {
	         String address = rs.getString("address");
	         String  name = rs.getString("name");
	         String  id = rs.getString("ID");
	         
	         
	         System.out.println( "address = " + address );
	         System.out.println( "name = " + name );
	         System.out.println( "ID = " + id );
	         
	         System.out.println();
	      }
	      rs.close();
	      stmt.close();
	      c.close();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         //System.exit(0);
	      }
	      System.out.println("Delete operation done successfully");
   }

   
   /**
 * @param info
 */
public static void update(UpInfo info) {
	  Connection c = null;
      Statement stmt = null;
      
      if (info.getId() != null) {
	      try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:/home/mkoutli/Documents/iotivity_devices");
	         c.setAutoCommit(false);
	         System.out.println("Opened database successfully");
	         String sql = "UPDATE ALL_DEVICES\nSET ";
	         
	         // check how many properties need update
	         int count = 0;
	         HashMap<String, String> hmap = new HashMap<String, String>();
	
	         if (info.getPassword() != null) {
	        	 count++;
	        	 hmap.put("password", info.getPassword());
	         }
	         if (info.getName() != null) {
	        	 count++;
	        	 hmap.put("name", info.getName());
	         }
	         if (info.getManufacturer() != null) {
	        	 count++;
	        	 hmap.put("manufacturer", info.getManufacturer());
	         }
	         if (info.getModel() != null) {
	        	 count++;
	        	 hmap.put("model", info.getModel());
	         }
	         if (info.getAddress() != null) {
	        	 count++;
	        	 hmap.put("address", info.getAddress());
	         }
	         if (info.getType() != null) {
	        	 count++;
	        	 hmap.put("type", info.getType());
	         }
	        	 
	         
	         if (count>0) {
	        	 // iterete in hashmap
	        	 
	        	 Set set = hmap.entrySet();
	             Iterator iterator = set.iterator();
	             while(iterator.hasNext()) {
	                Map.Entry mentry = (Map.Entry)iterator.next();
	                sql +=  mentry.getKey() + " = \'" + mentry.getValue() + "\'";
	                if (iterator.hasNext()) {
	                	sql += ",\n";
	                } else {
	                	sql += "\n";
	                }
	             }
	        	 
		         
		         sql += "WHERE\n" + 
		         		"             ID LIKE \'" + info.getId() + "\';";
		         
		         stmt = c.createStatement();
		         
		         stmt.executeUpdate(sql);
		         c.commit();
	
	         }
		      stmt.close();
		      c.close();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         //System.exit(0);
	      }
	      System.out.println("Update operation done successfully");
      }
   }
   

   /**
 * @return the list of registered devices
 */
public static ArrayList<DeviceInfo> getAllDevices() {
	  Connection c = null;
      Statement stmt = null;
      ArrayList<DeviceInfo> list = new ArrayList<DeviceInfo>();
      
      try {
         Class.forName("org.sqlite.JDBC");
         c = DriverManager.getConnection("jdbc:sqlite:/home/mkoutli/Documents/iotivity_devices");
         c.setAutoCommit(false);
         System.out.println("Opened database successfully");

         stmt = c.createStatement();
         ResultSet rs = stmt.executeQuery( "SELECT * FROM ALL_DEVICES;" );
         
         while ( rs.next() ) {
	         String address = rs.getString("address");
	         String  name = rs.getString("name");
	         String  type = rs.getString("type");
	         String  password = rs.getString("password");
	         String  manufacturer = rs.getString("manufacturer");
	         String  model = rs.getString("model");
	         String  id = rs.getString("ID");
	         
	         
	         System.out.println( "address = " + address );
	         System.out.println( "name = " + name );
	         System.out.println( "ID = " + id );
	         
	         System.out.println();
	         DeviceInfo i = new DeviceInfo(id, name, type, address, password, manufacturer, model);
	         list.add(i);
	      }
	      rs.close();
	      stmt.close();
	      c.close();
      } catch ( Exception e ) {
         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
         //System.exit(0);
      }
      return list;
   }
   
   public static void createTable(String name, String location) {
	      Connection c = null;
	      Statement stmt = null;
	      
	      try {
	         Class.forName("org.sqlite.JDBC");
	         // create db if it does not exist
	         c = DriverManager.getConnection("jdbc:sqlite:" + location + "/iotivity_devices");
	         System.out.println("Opened database successfully");

	         stmt = c.createStatement();
	         // name: ALL_DEVICES
	         String sql = "CREATE TABLE IF NOT EXISTS " + name +  " " +
	                        "(ID TEXT PRIMARY KEY," +
	                        " name           TEXT    NOT NULL, " + 
	                        " type            TEXT     NOT NULL, " + 
	                        " address        TEXT     NOT NULL,  " + 
	                        " password         TEXT," +
	                        " manufacturer         TEXT," +
	                        " model         TEXT)"; 
	         stmt.executeUpdate(sql);
	         stmt.close();
	         c.close();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         //System.exit(0);
	      }
	      System.out.println("Table with name " + name + " was created successfully");
	   }
   
   public static void deleteTable(String name, String location) {
	      Connection c = null;
	      Statement stmt = null;
	      
	      try {
	         Class.forName("org.sqlite.JDBC");
	         c = DriverManager.getConnection("jdbc:sqlite:" + location + "/iotivity_devices");
	         //c = DriverManager.getConnection("jdbc:sqlite:/home/mkoutli/Documents/iotivity_devices");
	         System.out.println("Opened database successfully");

	         stmt = c.createStatement();
	         String sql = "DROP TABLE " + name; 
	         stmt.executeUpdate(sql);
	         stmt.close();
	         c.close();
	      } catch ( Exception e ) {
	         System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	         //System.exit(0);
	      }
	      System.out.println("Table with name " + name + " was deleted successfully");
	   }
}
