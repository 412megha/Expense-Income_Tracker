

package expense_tracker;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jain1
 */

// DAO (data access object) class for handling transactions in database
public class TransactionDAO {
    
    //method to retrieve all transactions from the database 
    public static List<Transaction> getAllTransaction(){
        
        // create list to store transaction objects 
        List<Transaction> transactions = new ArrayList<>();
        
        Connection connection = DatabaseConnection.getConnection();
        
       PreparedStatement ps;
       ResultSet rs;
        try {
            ps = connection.prepareStatement("SELECT * FROM `transaction_table`");
            rs =  ps.executeQuery();
            
            // Iterate through the resultset obtained from sql query 
            while(rs.next()){
                // extract transaction details from result set
                int id = rs.getInt("id");
                String type = rs.getString("transaction_type");
                String description = rs.getString("description");
                double amount = rs.getDouble("amount");
                
                // create transaction obbject withh retrieved details
                Transaction transaction = new Transaction(id,type,description,amount);
                transactions.add(transaction);
                
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(TransactionDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
       
             return transactions;   
                
    }
    
}
