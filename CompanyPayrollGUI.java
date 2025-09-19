import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/*
 * ===================================================================
 * OOP CONCEPTS: Enums and Interfaces (Unchanged)
 * ===================================================================
 */
enum EmployeeType {
    FULL_TIME_SALARIED,
    PART_TIME_HOURLY
}

interface BonusPayable {
    double BONUS_RATE = 0.05;
    double calculateAnnualBonus();
}

interface TrainingRequired {
    void attendMandatoryTraining();
}

/*
 * ===================================================================
 * NEW CLASS: Department (Data Model)
 * ===================================================================
 */
class Department {
    private String deptId;
    private String deptName;

    public Department(String deptId, String deptName) {
        this.deptId = deptId;
        this.deptName = deptName;
    }

    public String getDeptId() { return deptId; }
    public String getDeptName() { return deptName; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Department that = (Department) obj;
        return deptId.equals(that.deptId);
    }

    @Override
    public int hashCode() {
        return deptId.hashCode();
    }

    /**
     * IMPORTANT: Overriding toString() allows the object to be displayed
     * correctly in Swing components like JComboBox (dropdown menus).
     */
    @Override
    public String toString() {
        return this.deptName; // Display the department name in UI components
    }
}


/*
 * ===================================================================
 * FILE: CompanyPayrollGUI.java (Main Public Class)
 * ===================================================================
 */
public class CompanyPayrollGUI extends JFrame {

    private final Company company = new Company();
    private JTextArea displayArea;

    public CompanyPayrollGUI() {
        setTitle("HR Payroll Management System v5.0");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();

        loadTestData();
        displayArea.setText("Welcome to the HR Payroll Management System! ✨\n" +
                            "(Test data with Departments has been loaded.)\n\n" +
                            "Please select an option from the menu on the left.");
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));
        setContentPane(mainPanel);

        JLabel titleLabel = new JLabel("Company Payroll System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(60, 90, 130));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        buttonPanel.setBackground(new Color(240, 240, 240));

        JButton btnHireFT = createStyledButton("Hire Full-Time", "OptionPane.informationIcon");
        JButton btnHirePT = createStyledButton("Hire Part-Time", "OptionPane.informationIcon");
        JButton btnViewAll = createStyledButton("View All Employees", "Tree.openIcon");
        JButton btnSearch = createStyledButton("Search by ID", "FileView.directoryIcon");
        JButton btnRunPayroll = createStyledButton("Run Company Payroll", "FileView.floppyDriveIcon");
        JButton btnRunDeptPayroll = createStyledButton("Run Dept. Payroll", "FileChooser.listViewIcon"); // NEW BUTTON
        JButton btnRunEOY = createStyledButton("Run EOY Reports", "FileChooser.detailsViewIcon");
        JButton btnExit = createStyledButton("Exit", "OptionPane.errorIcon");

        buttonPanel.add(btnHireFT);
        buttonPanel.add(btnHirePT);
        buttonPanel.add(new JSeparator());
        buttonPanel.add(btnViewAll);
        buttonPanel.add(btnSearch);
        buttonPanel.add(new JSeparator());
        buttonPanel.add(btnRunPayroll);
        buttonPanel.add(btnRunDeptPayroll); // Add new button to panel
        buttonPanel.add(btnRunEOY);
        buttonPanel.add(new JSeparator());
        buttonPanel.add(btnExit);

        mainPanel.add(buttonPanel, BorderLayout.WEST);

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        displayArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        displayArea.setMargin(new Insets(10, 10, 10, 10));
        displayArea.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Action Listeners ---
        btnHireFT.addActionListener(e -> hireEmployee(EmployeeType.FULL_TIME_SALARIED));
        btnHirePT.addActionListener(e -> hireEmployee(EmployeeType.PART_TIME_HOURLY));
        btnViewAll.addActionListener(e -> displayArea.setText(company.getAllEmployeesDetailsAsString()));
        btnSearch.addActionListener(e -> searchForEmployee());
        btnRunPayroll.addActionListener(e -> runCompanyPayroll());
        btnRunDeptPayroll.addActionListener(e -> runDepartmentPayroll()); // New action
        btnRunEOY.addActionListener(e -> displayArea.setText(company.runEndOfYearReportsAsString()));
        btnExit.addActionListener(e -> System.exit(0));
    }
    
    private JButton createStyledButton(String text, String iconName) {
        JButton button = new JButton(text);
        Icon icon = UIManager.getIcon(iconName);
        if (icon != null) button.setIcon(icon);
        button.setToolTipText("Click to " + text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return button;
    }

    private void loadTestData() {
        displayArea.append("(Loading test data...)\n");
        Department sales = new Department("S1", "Sales");
        Department engineering = new Department("E1", "Engineering");
        Department marketing = new Department("M1", "Marketing");
        
        company.addDepartment(sales);
        company.addDepartment(engineering);
        company.addDepartment(marketing);
        
        company.hireEmployee(new FullTimeEmployee("Ana", "Smith", 80000.0, sales));
        company.hireEmployee(new PartTimeEmployee("Bob", "Johnson", 22.50, engineering));
        company.hireEmployee(new FullTimeEmployee("Carla", "Diaz", 95000.0, engineering));
        company.hireEmployee(new FullTimeEmployee("David", "Lee", 78000.0, sales));
        company.hireEmployee(new PartTimeEmployee("Eve", "Brown", 25.00, marketing));
    }

    /**
     * REFACTORED HIRE LOGIC: A single method to handle both hire types.
     */
    private void hireEmployee(EmployeeType type) {
        // Step 1: Select the Department from a dropdown.
        ArrayList<Department> depts = company.getAvailableDepartments();
        if (depts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No departments exist. Please create a department first.", "Hiring Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Department selectedDept = (Department) JOptionPane.showInputDialog(
                this, "Please select a department:", "Step 1: Select Department",
                JOptionPane.PLAIN_MESSAGE, null, depts.toArray(), depts.get(0));

        if (selectedDept == null) return; // User cancelled

        // Step 2: Enter employee details.
        boolean isFullTime = (type == EmployeeType.FULL_TIME_SALARIED);
        String title = isFullTime ? "Hire Full-Time Employee" : "Hire Part-Time Employee";
        String rateLabel = isFullTime ? "Annual Salary:" : "Hourly Rate:";

        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField rateField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("First Name:"));
        panel.add(firstNameField);
        panel.add(new JLabel("Last Name:"));
        panel.add(lastNameField);
        panel.add(new JLabel(rateLabel));
        panel.add(rateField);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Step 2: " + title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String first = firstNameField.getText();
                String last = lastNameField.getText();
                double rate = Double.parseDouble(rateField.getText());
                if (first.trim().isEmpty() || last.trim().isEmpty() || rate < 0) {
                    throw new IllegalArgumentException("All fields are required and rate/salary cannot be negative.");
                }

                Employee newEmployee;
                if (isFullTime) {
                    newEmployee = new FullTimeEmployee(first, last, rate, selectedDept);
                } else {
                    newEmployee = new PartTimeEmployee(first, last, rate, selectedDept);
                }
                
                String hireMessage = company.hireEmployee(newEmployee);
                displayArea.setText(hireMessage + "\n\n" + newEmployee.getDetailsAsString());

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void searchForEmployee(){
        String id = JOptionPane.showInputDialog(this, "Enter Employee ID to search (e.g., F101):");
        if(id != null && !id.trim().isEmpty()){
            Employee foundEmp = company.findEmployeeByID(id);
            if(foundEmp != null){
                displayArea.setText("--- Employee Found ---\n" + foundEmp.getDetailsAsString());
            } else {
                displayArea.setText("ERROR: No employee found with ID '" + id + "'");
            }
        }
    }
    
    private void runCompanyPayroll(){
        // Update hours first for an accurate report
        boolean success = company.updateAllPartTimeHoursGUI(this);
        if (success) {
            displayArea.setText(company.generatePayrollReportAsString());
        } else {
            displayArea.setText("Company payroll run cancelled during hour update.");
        }
    }

    private void runDepartmentPayroll() {
        // Also update hours first for this report
        boolean success = company.updateAllPartTimeHoursGUI(this);
        if (success) {
            displayArea.setText(company.generateDepartmentPayrollReportAsString());
        } else {
            displayArea.setText("Department payroll run cancelled during hour update.");
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.err.println("Nimbus look and feel not found. Using default.");
        }

        SwingUtilities.invokeLater(() -> new CompanyPayrollGUI().setVisible(true));
    }
}


/*
 * ===================================================================
 * BACKEND LOGIC CLASSES (Refactored for GUI and Composition)
 * ===================================================================
 */

abstract class Employee {
    private static int employeeCounter = 101;
    private String employeeID;
    private String firstName;
    private String lastName;
    private EmployeeType type;
    private Department workingDept; // COMPOSITION: Employee "has-a" Department

    public Employee(String firstName, String lastName, String idPrefix, EmployeeType type, Department workingDept) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        this.workingDept = workingDept;
        this.employeeID = idPrefix + employeeCounter++;
    }

    public String getEmployeeID() { return employeeID; }
    public String getFullName() { return firstName + " " + lastName; }
    public EmployeeType getEmployeeType() { return this.type; }
    public Department getWorkingDept() { return this.workingDept; }

    public abstract double calculateWeeklyPay();

    public String getDetailsAsString() {
        return "------------------------------\n" +
               String.format(" ID:         %s\n", this.employeeID) +
               String.format(" Name:       %s\n", getFullName()) +
               String.format(" Type:       %s\n", this.type) +
               String.format(" Department: %s (ID: %s)\n", workingDept.getDeptName(), workingDept.getDeptId());
    }
}

class FullTimeEmployee extends Employee implements BonusPayable {
    private double annualSalary;

    public FullTimeEmployee(String firstName, String lastName, double annualSalary, Department workingDept) {
        super(firstName, lastName, "F", EmployeeType.FULL_TIME_SALARIED, workingDept);
        this.annualSalary = annualSalary;
    }

    @Override public double calculateWeeklyPay() { return this.annualSalary / 52.0; }
    @Override public double calculateAnnualBonus() { return this.annualSalary * BonusPayable.BONUS_RATE; }

    @Override
    public String getDetailsAsString() {
        return super.getDetailsAsString() +
               String.format(" Annual Salary: $%,.2f%n", this.annualSalary) +
               String.format(" Weekly Pay:    $%,.2f%n", calculateWeeklyPay()) +
               String.format(" Annual Bonus:  $%,.2f%n", calculateAnnualBonus()) +
               "------------------------------\n";
    }
}

class PartTimeEmployee extends Employee implements TrainingRequired {
    private double hourlyRate;
    private int hoursWorkedThisWeek;

    public PartTimeEmployee(String firstName, String lastName, double hourlyRate, Department workingDept) {
        super(firstName, lastName, "P", EmployeeType.PART_TIME_HOURLY, workingDept);
        this.hourlyRate = hourlyRate;
        this.hoursWorkedThisWeek = 0;
    }

    public void setHoursWorked(int hours) { this.hoursWorkedThisWeek = hours; }
    @Override public double calculateWeeklyPay() { return this.hourlyRate * this.hoursWorkedThisWeek; }
    @Override public void attendMandatoryTraining() {}
    public String getTrainingLogMessage() { return String.format("TRAINING LOGGED: %s has attended mandatory training.", getFullName()); }

    @Override
    public String getDetailsAsString() {
        String details = super.getDetailsAsString() +
                         String.format(" Hourly Rate:   $%.2f%n", this.hourlyRate);
        if (this.hoursWorkedThisWeek > 0) {
            details += String.format(" Last Pay Calc: $%.2f (%d hours)%n", calculateWeeklyPay(), this.hoursWorkedThisWeek);
        }
        return details + "------------------------------\n";
    }
}

class Company {
    private ArrayList<Employee> allEmployeesList = new ArrayList<>();
    private ArrayList<Department> allDepartmentsList = new ArrayList<>();

    public String hireEmployee(Employee employee) {
        this.allEmployeesList.add(employee);
        return String.format("HIRE SUCCESS: %s (%s) has been hired into %s.",
            employee.getFullName(), employee.getEmployeeID(), employee.getWorkingDept().getDeptName());
    }

    public void addDepartment(Department dept) { this.allDepartmentsList.add(dept); }
    public Employee findEmployeeByID(String id) {
        return allEmployeesList.stream().filter(e -> e.getEmployeeID().equalsIgnoreCase(id)).findFirst().orElse(null);
    }
    public ArrayList<Department> getAvailableDepartments() { return this.allDepartmentsList; }

    public String getAllEmployeesDetailsAsString() {
        if (allEmployeesList.isEmpty()) return "No employees have been hired yet.";
        StringBuilder sb = new StringBuilder("--- ALL EMPLOYEES IN COMPANY ---\n\n");
        allEmployeesList.forEach(emp -> sb.append(emp.getDetailsAsString()).append("\n"));
        sb.append("--- END OF LIST ---");
        return sb.toString();
    }
    
    public boolean updateAllPartTimeHoursGUI(Component parent) {
        ArrayList<PartTimeEmployee> partTimers = new ArrayList<>();
        allEmployeesList.stream().filter(e -> e instanceof PartTimeEmployee).forEach(e -> partTimers.add((PartTimeEmployee)e));
        if (partTimers.isEmpty()){
             JOptionPane.showMessageDialog(parent, "No part-time employees found to update.", "Info", JOptionPane.INFORMATION_MESSAGE);
             return true;
        }
        for (PartTimeEmployee ptEmp : partTimers) {
            String input = JOptionPane.showInputDialog(parent, "Enter hours for " + ptEmp.getFullName() + ":", "Update Hours", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return false; // User cancelled
            try {
                int hours = Integer.parseInt(input);
                if (hours < 0) throw new NumberFormatException("Negative hours not allowed.");
                ptEmp.setHoursWorked(hours);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(parent, "Invalid number. Please try again.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return updateAllPartTimeHoursGUI(parent); // Recursive call to retry
            }
        }
        return true;
    }

    public String generatePayrollReportAsString() {
        StringBuilder sb = new StringBuilder("--- WEEKLY COMPANY-WIDE PAYROLL REPORT ---\n\n");
        double totalPayroll = allEmployeesList.stream().mapToDouble(Employee::calculateWeeklyPay).sum();
        allEmployeesList.forEach(emp -> sb.append(String.format("PAYING: %-20s (%s, %-12s) --- $%,.2f%n",
                emp.getFullName(), emp.getEmployeeID(), emp.getWorkingDept().getDeptName(), emp.calculateWeeklyPay())));
        sb.append("\n---------------------------------------------------------------------\n");
        sb.append(String.format("TOTAL COMPANY PAYROLL: $%,.2f%n", totalPayroll));
        return sb.toString();
    }

    public String generateDepartmentPayrollReportAsString() {
        StringBuilder sb = new StringBuilder("--- WEEKLY PAYROLL REPORT BY DEPARTMENT ---\n");
        double grandTotal = 0.0;
        for (Department dept : allDepartmentsList) {
            sb.append(String.format("%n=== DEPARTMENT: %s ===%n", dept.getDeptName().toUpperCase()));
            double deptSubtotal = 0.0;
            boolean found = false;
            for (Employee emp : allEmployeesList) {
                if (emp.getWorkingDept().equals(dept)) {
                    found = true;
                    double pay = emp.calculateWeeklyPay();
                    sb.append(String.format("  PAYING: %-20s (%s) --- $%,.2f%n", emp.getFullName(), emp.getEmployeeID(), pay));
                    deptSubtotal += pay;
                }
            }
            if (!found) {
                sb.append("  No employees processed for this department.\n");
            } else {
                sb.append(String.format("  --- DEPARTMENT SUBTOTAL: $%,.2f ---%n", deptSubtotal));
                grandTotal += deptSubtotal;
            }
        }
        sb.append("\n=============================================\n");
        sb.append(String.format("GRAND TOTAL (ALL DEPTS): $%,.2f%n", grandTotal));
        return sb.toString();
    }
    
    public String runEndOfYearReportsAsString() {
        StringBuilder sb = new StringBuilder("--- END-OF-YEAR BONUS & TRAINING REPORT ---\n\n");
        for (Employee emp : allEmployeesList) {
            if (emp instanceof BonusPayable) {
                sb.append(String.format("BONUS: %s (%s, %s) earned $%,.2f%n",
                        emp.getFullName(), emp.getEmployeeID(), emp.getWorkingDept().getDeptName(), ((BonusPayable) emp).calculateAnnualBonus()));
            }
            if (emp instanceof TrainingRequired) {
                sb.append(((PartTimeEmployee)emp).getTrainingLogMessage()).append("\n");
            }
        }
        return sb.toString();
    }
}