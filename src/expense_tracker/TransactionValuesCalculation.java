

package expense_tracker;

import java.util.List;
/**
 *
 * @author jain1
 */
// class to calculatr various transaction values 
public class TransactionValuesCalculation {
    
    // Method to calculate the total incomes from a list of transactions 
    public static Double getTotalIncomes(List<Transaction> transactions){
        
        double totalIncome = 0.0;
        for(Transaction transaction : transactions){
            if("Income".equals(transaction.getType())){
                totalIncome += transaction.getAmount();
            }
        }
        
        return totalIncome;
        
    }
    
    
    // Method to calculate the total expenses from a list of transactions 
    public static Double getTotalExpenses(List<Transaction> transactions){
        
        double totalExpenses = 0.0;
        for(Transaction transaction : transactions){
            if("Expense".equals(transaction.getType())){
                totalExpenses += transaction.getAmount();
            }
        }
        
        return totalExpenses;
        
    } 
    
    
    // method to calculate total value  (income-expenses) from a list of transactions
    public static Double getTotalValue(List<Transaction> transactions){
        Double totalIncome = getTotalIncomes(transactions);
        Double totalExpense = getTotalExpenses(transactions);
        return totalIncome - totalExpense;
        
    }
    
    
    
}
