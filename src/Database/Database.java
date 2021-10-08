
package Database;

import java.sql.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;




public class Database {
    
public static void main(String[] args) {
        
        JSONArray array = (JSONArray) getJSONData();
        System.out.println("\n (Converting DATABASE TO JSON Results)");
        System.out.println("=====================================");
        System.out.println(array);
        System.out.println();
        
    }

    public static JSONArray getJSONData() {
                
        Connection connection = null;
        PreparedStatement pstmt = null, pstUpdate = null;
        ResultSet resultset = null;
        ResultSetMetaData metadata = null;
        
        JSONArray list = new JSONArray();
        
        String query, key, value;
      
        ArrayList<String> records = new ArrayList<>();
        
        
        boolean hasresults;
        int resultCount, columnCount, updateCount = 0;
        
        try {
            
            /* Identify the Server */
            String server = ("jdbc:mysql://localhost/p2_test?serverTimezone=America/Chicago");
            String username = "root";
            String password = "CS488";
            System.out.println("Connecting to " + server + "...");

     
            /* Open Connection */

            connection = DriverManager.getConnection(server, username, password);

            /* Test Connection */
            
            if (connection.isValid(0)) {
                
                /* Opened connection */
                
                System.out.println("Connected Successfully!");
  
                /* Preparing data from people */
                
                query = "SELECT * FROM people";
                pstmt = connection.prepareStatement(query);
                
                
                hasresults = pstmt.execute();                
                
                /* Getting   final Results */
                
                System.out.println("Getting Results ...");
                
                while ( hasresults || pstmt.getUpdateCount() != -1 ) {

                    if ( hasresults ) {
                        
                        /* Get ResultSet Metadata */
                        
                        resultset = pstmt.getResultSet();
                        metadata = resultset.getMetaData();
                        columnCount = metadata.getColumnCount();
                        
                        /* Get Column Names; Append them in an ArraList "key" */
                        
                        for (int i = 2; i <= columnCount; i++) {
                            records.add(metadata.getColumnLabel(i));
                        }
                        
                        /* Get Data; Put the data in JSONObject */
                        
                        while(resultset.next()) {
                            
                            /* Begin Next ResultSet Row; Loop Through ResultSet 
                            Columns; Append to jsonObject */
                            
                            JSONObject object = new JSONObject();

                            for (int i = 2; i <= columnCount; i++) {
                                
                                JSONObject jsonObject = new JSONObject();
                                value = resultset.getString(i);
                                
                                if (resultset.wasNull()) {
                                    jsonObject.put(records.get(i-2), "NULL");
                                    jsonObject.toJSONString();
                                }

                                else {
                                    jsonObject.put(records.get(i-2), value);
                                    jsonObject.toString();
                                }
                                
                                object.putAll(jsonObject);

                            }
                            list.add(object);

                        }
                        
                    }

                    else {

                        resultCount = pstmt.getUpdateCount();  

                        if ( resultCount == -1 ) {
                            break;
                        }

                    }
                    
                    /* iterating through any other data */

                    hasresults = pstmt.getMoreResults();

                }
                
            }
            
            /* close connection*/
            
            connection.close();
            
        }
        
        catch (Exception e) {
            System.err.println(e.toString());
        }
        
        /* Cclosing the rest of any database objects left */
        
        finally {
            
            if (resultset != null) { try { resultset.close(); resultset = null; } catch (Exception e) {} }
            
            if (pstmt != null) { try { pstmt.close(); pstmt = null; } catch (Exception e) {} }
                       
        }
        return list;
    }
    
}
    
