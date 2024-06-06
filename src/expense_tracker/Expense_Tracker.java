

package expense_tracker;

import javax.swing.BorderFactory;
import javax.swing.border.LineBorder;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.JTableHeader;
//import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jain1
 */
public class Expense_Tracker {
    
    // variable for main frame and UI components
    private JFrame frame;
    private JPanel titleBar;
    private JLabel titleLabel;
    private JLabel closeLabel;
    private JLabel minimizeLabel;
    private JPanel dashboardPanel;
    private JPanel buttonsPanel;
    private JButton addTransactionButton;
    private JButton removeTransactionButton;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    
    // variable to store total amount
    private double totalAmount = 0.0;
    
    // arraylist to store datapanel values
    private ArrayList<String> dataPanelValues = new ArrayList<>();
    
    // variables for form dragging
    private boolean isDragging = false;
    private Point mouseOffset;
    
    // constructor
    public Expense_Tracker(){
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800,500);
        frame.setLocationRelativeTo(null);
        // remove form border and default close and minimize buttons
        frame.setUndecorated(true);
        // set custom border to the frame
        frame.getRootPane().setBorder(BorderFactory.createMatteBorder(5,5,5,5,new Color(75,0,130)));
        
        // create  and setup titlebar
        titleBar = new JPanel();
        titleBar.setLayout(null);
        titleBar.setBackground(new Color(75,0,130));
        titleBar.setPreferredSize(new Dimension(frame.getWidth(),30));
        frame.add(titleBar, BorderLayout.NORTH);
        
        // create and  setup title label
        titleLabel = new JLabel("Expense And Income Tracker");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 17));
        titleLabel.setBounds(10,0,250,30);
        titleBar.add(titleLabel);
       
        // create and  setup the close label 
        closeLabel = new JLabel("X");
        closeLabel.setForeground(Color.WHITE);
        closeLabel.setFont(new Font("Arial", Font.BOLD,17));
        closeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        closeLabel.setBounds(frame.getWidth() - 50,0,30,30);
        closeLabel.setCursor(new  Cursor(Cursor.HAND_CURSOR));
          
        // Add mouse listeners for close label interactions
        closeLabel.addMouseListener(new MouseAdapter(){
            
          @Override
          public void mouseClicked(MouseEvent e){
              System.exit(0);
          }
          
          @Override
          public void mouseEntered(MouseEvent e){
              closeLabel.setForeground(Color.red);
          }
          
          @Override
           public void mouseExited(MouseEvent e){
              closeLabel.setForeground(Color.white);
          }
                    
        });
        
        titleBar.add(closeLabel);
        
        // create and  setup the minimize label 
        minimizeLabel = new JLabel("-");
        minimizeLabel.setForeground(Color.WHITE);
        minimizeLabel.setFont(new Font("Arial", Font.BOLD,28));
        minimizeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        minimizeLabel.setBounds(frame.getWidth() - 80,0,30,30);
        minimizeLabel.setCursor(new  Cursor(Cursor.HAND_CURSOR));
        
        // Add mouse listeners for minimize label interactions
        minimizeLabel.addMouseListener(new MouseAdapter(){
            
          @Override
          public void mouseClicked(MouseEvent e){
              frame.setState(JFrame.ICONIFIED);
          }
          
          @Override
          public void mouseEntered(MouseEvent e){
              minimizeLabel.setForeground(Color.GRAY);
          }
          
          @Override
           public void mouseExited(MouseEvent e){
              minimizeLabel.setForeground(Color.white);
          }
                    
        });
        
        titleBar.add(minimizeLabel);
        
        // setup form dragging functionality
        // mouse listener for window dragging 
        titleBar.addMouseListener(new MouseAdapter(){
            
            @Override
            public void mousePressed(MouseEvent e){
                isDragging = true;
                mouseOffset = e.getPoint();
            }
            
            @Override
            public void mouseReleased(MouseEvent e){
                isDragging = false;
            }
        });
        
        // Mouse otion listener for window dragging 
        titleBar.addMouseListener(new MouseAdapter(){
            
            @Override
            public void mouseDragged(MouseEvent e){
                if(isDragging){
                    //when mouse is dragged, this event is triggered
                    // get the current location of the mouse on the screen
                    Point newLocation = e.getLocationOnScreen();
                    // calculate new window location by adjusting for the initial mouse offset
                    newLocation.translate(-mouseOffset.x,-mouseOffset.y);
                    // set new location of the main window to achieve dragging effect
                    frame.setLocation(newLocation);
                    
                    
                }
            }
        });
        
        //create and setup dashboard panel 
        dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));
        dashboardPanel.setBackground(new Color(236,240,241));
        frame.add(dashboardPanel,BorderLayout.CENTER);
        
        // Calculate total amount and populate data panel values
        totalAmount = TransactionValuesCalculation.getTotalValue(TransactionDAO.getAllTransaction());
        dataPanelValues.add(String.format("₹%,.2f",TransactionValuesCalculation.getTotalExpenses(TransactionDAO.getAllTransaction())));
        dataPanelValues.add(String.format("₹%,.2f", TransactionValuesCalculation.getTotalIncomes(TransactionDAO.getAllTransaction())));
        dataPanelValues.add("₹"+totalAmount);


        
        
        // add data panels for Expense,Income,total
        addDataPanel("Expense",0);
        addDataPanel("Income",1);
        addDataPanel("Total",2);
        
        //create and setup buttons panel
        addTransactionButton = new JButton("Add Transaction");
        addTransactionButton.setBackground(new Color(60,179,113));
        addTransactionButton.setForeground(Color.WHITE);
        addTransactionButton.setFocusPainted(false);
        addTransactionButton.setBorderPainted(false);
        addTransactionButton.setFont(new Font("Arial",Font.BOLD,14));
        addTransactionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addTransactionButton.addActionListener((e) -> { showAddTransactionDialog();});
        
        removeTransactionButton = new  JButton("Remove Transaction");
        removeTransactionButton.setBackground(new Color(220,20,60));
        removeTransactionButton.setForeground(Color.WHITE);
        removeTransactionButton.setFocusPainted(false);
        removeTransactionButton.setBorderPainted(false);
        removeTransactionButton.setFont(new Font("Arial",Font.BOLD,14));
        removeTransactionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        removeTransactionButton.addActionListener((e) -> {
            removeSelectedTransaction();
        });
        
        buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BorderLayout(10,5));
        buttonsPanel.add(addTransactionButton, BorderLayout.NORTH);
        buttonsPanel.add(removeTransactionButton, BorderLayout.SOUTH);
        dashboardPanel.add(buttonsPanel);
        
        // setup transaction table 
        String[] columnNames = {"ID","Type","Description","Amount"};
        tableModel = new DefaultTableModel(columnNames,0){
            
            @Override
            public boolean isCellEditable(int row, int column){
                return false;
            }
        };
        
        transactionTable = new JTable(tableModel);
        
        configureTransactionTable();
       
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        configureScrollPane(scrollPane);
        dashboardPanel.add(scrollPane);
        
        frame.setVisible(true);
    }
    
    // fix the negative value
    private String fixNegativeValueDisplay(double value){
        
        String newVal = String.format("₹%.2f",value);
        
        if(newVal.startsWith("₹-")){
            String numericPart = newVal.substring(2);
            newVal = "-₹"+numericPart;
        }
        return newVal;
    }
    
    
    // remove transaction from database
    private void removeSelectedTransaction(){
        
        int selectedRow = transactionTable.getSelectedRow();
        
        if(selectedRow != -1){
            int transactionId = (int) transactionTable.getValueAt(selectedRow, 0);
            String type =  transactionTable.getValueAt(selectedRow,1).toString();
            String amountStr = transactionTable.getValueAt(selectedRow,3).toString();
            double amount = Double.parseDouble(amountStr.replace("₹","").replace(" ","").replace(",",""));
            
            if(type.equals("Income")){
                totalAmount -= amount;
            }
            else{
                totalAmount += amount;
            }
            
            JPanel totalPanel = (JPanel) dashboardPanel.getComponent(2);
            totalPanel.repaint();
            
            // determine the index of data panel to update (0 for expense and 1 for income)
            int indexToUpdate = type.equals("Income") ? 1: 0;
            
            // update the data panel value and repaint it
            String currentValue = dataPanelValues.get(indexToUpdate);
            double currentAmount = Double.parseDouble(currentValue.replace("₹","").replace(" ","").replace(",","").replace("--","-"));
            double updatedAmount = currentAmount + (type.equals("Income") ? -amount : amount);
            //dataPanelValues.set(indexToUpdate,String.format("₹%,.2f", updatedAmount));
            
            if(indexToUpdate ==1){
                dataPanelValues.set(indexToUpdate,String.format("₹%,.2f",updatedAmount));
            }
            else{
                dataPanelValues.set(indexToUpdate, fixNegativeValueDisplay(updatedAmount));
            }
            
            // repaint the corresponding data panel
            JPanel dataPanel = (JPanel) dashboardPanel.getComponent(indexToUpdate);
            dataPanel.repaint();
            
            // remove selected row from table model
            tableModel.removeRow(selectedRow);
            // remove transaction from database 
            removeTransactionFromDatabase(transactionId);
         
        }
    }
    
    
    private void removeTransactionFromDatabase(int transactionId){
        
        try {
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement ps = connection.prepareStatement("DELETE FROM `transaction_table` WHERE `id` = ?");
            
            ps.setInt(1,transactionId);
            ps.executeLargeUpdate();
            System.out.println("Transaction Removed");
            
            
        } catch (SQLException ex) {
            Logger.getLogger(Expense_Tracker.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    // displays the dialog for adding a new transaction
    private void showAddTransactionDialog(){
        // create a new JDialog for adding transaction
    JDialog dialog = new JDialog(frame,"Add Transaction", true);
    dialog.setSize(400,250);
    dialog.setLocationRelativeTo(frame);
    
    // create panel to hold the components in grid layout
    JPanel dialogPanel = new JPanel(new GridLayout(4,0,10,10));
    dialogPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    dialogPanel.setBackground(Color.LIGHT_GRAY);
    
    JLabel typeLabel = new JLabel("Type: ");
    JComboBox<String> typeCombobox = new JComboBox<>(new String[]{"Expense", "Income"});
    typeCombobox.setBackground(Color.WHITE);
    typeCombobox.setBorder(BorderFactory.createLineBorder(Color.black));
    
    JLabel descriptionLabel = new JLabel("Description:");
    JTextField descriptionField = new JTextField();
    descriptionField.setBorder(BorderFactory.createLineBorder(Color.black));
    
    JLabel amountLabel = new JLabel("Amount:");
    JTextField amountField = new JTextField();
    amountField.setBorder(BorderFactory.createLineBorder(Color.black));
    
    // create and commfigure the "Add" button
    JButton addButton = new JButton("Add");
    addButton.setBackground(new Color(255,140,0));
    addButton.setForeground(Color.BLACK);
    addButton.setFocusPainted(false);
    addButton.setBorderPainted(false);
    addButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    addButton.addActionListener((e) -> {
        addTransaction(typeCombobox,descriptionField,amountField);
    });
    
    // add componennts to dialog panel
    dialogPanel.add(typeLabel);
    dialogPanel.add(typeCombobox);
    dialogPanel.add(descriptionLabel);
    dialogPanel.add(descriptionField);
    dialogPanel.add(amountLabel);
    dialogPanel.add(amountField);
    dialogPanel.add(new JLabel()); // empty label for spacing 
    dialogPanel.add(addButton);
    
    DatabaseConnection.getConnection();
    
    dialog.add(dialogPanel);
    dialog.setVisible(true);
    
    }
    
    
    // Add a new transaction to the database 
    private void addTransaction(JComboBox<String> typeCombobox, JTextField descriptionField, JTextField amountField){
        
        
            // retrieve transaction details from the input fields
            String type = (String)typeCombobox.getSelectedItem();
            String description = descriptionField.getText();
            String amount = amountField.getText();
            
            // parse amount string to a double value
            double newAmount = Double.parseDouble(amount.replace("₹", "").replace(" ", "").replace(",", ""));
            
            // update total amount based on transaction type (income and expense)
            if(type.equals("Income")){
            totalAmount += newAmount;
            }
            
            else {
            totalAmount -= newAmount;
            }
            
            // update and display total amount on dashboard panel
            JPanel totalPanel = (JPanel)dashboardPanel.getComponent(2);
            totalPanel.repaint();
            
            // Determine index of data panel to update based on transaction
            int indexToUpdate = type.equals("Income") ? 1: 0;
            
            // Retrieve current value of data panel
            String currentValue = dataPanelValues.get(indexToUpdate);
            
            // parse current amount string to double value 
            double currentAmount = Double.parseDouble(currentValue.replace("₹", "").replace(" ", "").replace(",", ""));
            
            // calculate updated amount based on transaction type
            double updatedAmount = currentAmount + (type.equals("Income") ? newAmount : -newAmount);
            
            // update data panel with new amount
            if(indexToUpdate ==1){
                dataPanelValues.set(indexToUpdate,String.format("₹%,.2f",updatedAmount));
            }
            else{
                dataPanelValues.set(indexToUpdate, fixNegativeValueDisplay(updatedAmount));
            }
            
            // update displayed data panel on dashboard panel 
            JPanel dataPanel = (JPanel)dashboardPanel.getComponent(indexToUpdate);
            dataPanel.repaint();
            
            
            
            
            try {
                    Connection connection = DatabaseConnection.getConnection();
                    String insertQuery = "INSERT INTO `transaction_table`(`transaction_type`, `description`, `amount`) VALUES (?,?,?)";
                    PreparedStatement ps = connection.prepareStatement(insertQuery);
                    
                    
                    ps.setString(1,type);
                    ps.setString(2,description);
                    ps.setDouble(3,Double.parseDouble(amount));
                    ps.executeUpdate();
                    System.out.println("Data Inserted Successfully.");
                    
                    tableModel.setRowCount(0);
                    populateTableTransactions();
                    
        } catch (SQLException ex) {
            System.out.println("ERROR - Data not inserted.");
        }
        
        
        
    }
    
    // populate table transactions
    private void populateTableTransactions(){
        
        for(Transaction transaction : TransactionDAO.getAllTransaction()){
            Object[] rowData = { transaction.getId(), transaction.getType(), 
                                 transaction.getDescription(),transaction.getAmount() };
            tableModel.addRow(rowData);
        
    }
   }
   
    //configures the appearance and behavior of transaction table 
    private void configureTransactionTable(){
        transactionTable.setBackground(new Color(236,240,241));
        transactionTable.setRowHeight(30);
        transactionTable.setShowGrid(false);
        transactionTable.setBorder(null);
        transactionTable.setFont(new Font("Arial",Font.ITALIC,16));
        transactionTable.setDefaultRenderer(Object.class, new TransactionTableCellRenderer());
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        populateTableTransactions();
        
        JTableHeader tableHeader = transactionTable.getTableHeader();
        tableHeader.setForeground(Color.BLACK);
        tableHeader.setFont(new Font("Arial", Font.BOLD,15));
        tableHeader.setDefaultRenderer(new GradientHeaderRenderer());
    }
    
    // configure the appearance of scrollpane 
    private void configureScrollPane(JScrollPane scrollPane){
        
         scrollPane.getVerticalScrollBar().setUI(new CustomScrollBarUI());
         scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
         scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
         scrollPane.setPreferredSize(new Dimension(750,300));
    }
    
    
    
    // Add data panel to dashboard panel
    private void addDataPanel(String title, int index){
    //create a new JPanel for the data panel 
    JPanel dataPanel = new JPanel(){
        //Override the paintCoomponent method to customize the appearance 
        @Override
        protected void  paintComponent(Graphics g){
            // call the PaintComponent method of the  superclass
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            // make drawing smooth
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            // check if the title is "Total" to determine the content to display
            if(title.equals("Total")){
               // if title is "Total",draw data panel with the total amount   
              //drawDataPanel(g2d,title,String.format("₹%,.2f", totalAmount),getWidth(),getHeight());  
              drawDataPanel(g2d,title, fixNegativeValueDisplay(totalAmount),getWidth(),getHeight());  
            }
            else{
                // if the title is not "Total", draw the data panel with the corresponding value from the list 
                drawDataPanel(g2d,title,dataPanelValues.get(index),getWidth(),getHeight());
            }
        }
        
    };
    
    // set layout, size, background color  and border for the data panel
    dataPanel.setLayout(new GridLayout(2,1));
    dataPanel.setPreferredSize(new Dimension(170,100));
    dataPanel.setBackground(new Color(255,255,255));
    dataPanel.setBorder(new LineBorder(new Color(149,165,166),2));
    dashboardPanel.add(dataPanel);
    
    }
    // draws data panel with specified title and values
    private void drawDataPanel(Graphics g, String title, String value, int width, int height){
        
        Graphics2D g2d = (Graphics2D)g;
        
        //draw the panel
        g2d.setColor(new Color(255,255,255));
        g2d.fillRoundRect(0,0,width,height,20,20);
        
        g2d.setColor(new Color(236,240,241));
        g2d.fillRect(0,0, width, 40);
        
        // draw title
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial",Font.BOLD,20));
        g2d.drawString(title, 20, 30);
        
        //draw value 
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial",Font.PLAIN,16));
        g2d.drawString(value, 20, 75);
    }
    
    // main method
    public static void main(String[] args){
        new Expense_Tracker();
    }
 
}

// custom table header renderer with gradient background

class GradientHeaderRenderer extends JLabel implements TableCellRenderer{
    
    private final Color startColor = new Color(192,192,192);
    private final Color endColor = new Color(50,50,50);
    
    public GradientHeaderRenderer(){
        setOpaque(false);
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(Color.WHITE);
        setFont(new Font("Arial",Font.BOLD,17));
        setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0,0,1,1,new Color(244,164,96)),(BorderFactory.createEmptyBorder(2,5,2,5))));
        
        
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        setText(value.toString());
        return this;
    }
    
    @Override
    protected void paintComponent(Graphics g){
        
        Graphics2D g2d = (Graphics2D)g;
        
        int width = getWidth();
        int height = getHeight();
        
        GradientPaint gradientPaint = new GradientPaint(0,0,startColor,width,0,endColor);
        
        g2d.setPaint(gradientPaint);
        g2d.fillRect(0,0,width,height);
        
        super.paintComponent(g);
        
    }
 
}


// create custom scrollbar UI class for the ScrollPane
class CustomScrollBarUI extends BasicScrollBarUI{
         // colors for thumb and track of scroll bar
         private Color thumbColor = new Color(176,196,222);
         private Color trackColor = new Color(236,240,241);
         
         // Override method to configure the scroll bar colors 
         @Override
         protected void configureScrollBarColors(){
             // call superclass method to ensure default configurations
             super.configureScrollBarColors();
         }
         
         @Override
         protected JButton createDecreaseButton(int orientation){
             return createEmptybutton();
         }
         
         @Override
         protected JButton createIncreaseButton(int orientation){
             return createEmptybutton();
         }
         
         @Override
         protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds){
             g.setColor(thumbColor);
             g.fillRect(thumbBounds.x,thumbBounds.y,thumbBounds.width,thumbBounds.height);
        }
         
         @Override
         protected void paintTrack(Graphics g,JComponent c, Rectangle trackBounds){
             g.setColor(trackColor);
             g.fillRect(trackBounds.x,trackBounds.y,trackBounds.width,trackBounds.height);
         }
         
         private JButton createEmptybutton(){
             JButton button = new JButton();
             button.setPreferredSize(new Dimension(0,0));
             button.setMaximumSize(new Dimension(0,0));
             button.setMinimumSize(new Dimension(0,0));
             return button;
             
         }
               
}
    

// custom cell renderer for the transaction table 
class TransactionTableCellRenderer extends DefaultTableCellRenderer {
    

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column){ 
        
        // call superclass method to get default rendering component
        Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        // get transaction type from second column of table
        String type = (String) table.getValueAt(row,1);
        
        if(isSelected){
            c.setForeground(Color.BLACK);
            c.setBackground(Color.ORANGE);
        }
        else {
            if("Income".equals(type)){
                c.setBackground(new Color(60,179,113));
                c.setForeground(Color.BLACK);
            }
            else{
                c.setBackground(new Color(255,160,122));
                c.setForeground(Color.BLACK);
            }
        }
        
        return c;
    }
}