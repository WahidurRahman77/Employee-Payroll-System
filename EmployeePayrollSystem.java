import java.util.ArrayList;
import java.util.Scanner;

/*
 * ===================================================================
 * OOP CONCEPTS: Enums and Interfaces
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

/* * ===================================================================
 * NEW CLASS: Department (Data Model)
 * ===================================================================
 * Represents a working department (Composition).
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
}


/*
 * ===================================================================
 * FILE: EmployeePayrollSystem.java (Main Public Class)
 * ===================================================================
 */
public class EmployeePayrollSystem {

    private static Company company = new Company();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the HR Payroll Management System v5.0.");
        loadTestData(); 
        boolean running = true;

        while (running) {
            printMenu();
            int choice = getIntInput("Please enter your choice (1-8): ", scanner);

            switch (choice) {
                case 1:
                    hireFullTime();
                    break;
                case 2:
                    hirePartTime();
                    break;
                case 3:
                    searchForEmployee();
                    break;
                case 4:
                    company.updateAllPartTimeHours(scanner);
                    company.generatePayrollReport();
                    break;
                case 5:
                    company.runEndOfYearReports();
                    break;
                case 6: 
                    company.displayAllEmployees();
                    break;
                case 7: 
                    company.generateDepartmentPayrollReport();
                    break;
                case 8: 
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1-8.");
            }
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            }
        }
        System.out.println("Thank you for using the system. Goodbye.");
        scanner.close();
    }

    public static void printMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Hire Full-Time Employee");
        System.out.println("2. Hire Part-Time Employee");
        System.out.println("3. Search for Employee (by ID)");
        System.out.println("4. Run Weekly Payroll Report (All Employees)");
        System.out.println("5. Run End-of-Year Reports (Bonus/Training)");
        System.out.println("6. View All Employees List");
        System.out.println("7. Run Payroll by Department");
        System.out.println("8. Exit"); 
    }
    
    /**
     * UPDATED: Added Marketing department and a new test employee.
     */
    public static void loadTestData(){
        System.out.println("(Loading test data...)");
        
        // 1. Create Departments
        Department sales = new Department("S1", "Sales");
        Department engineering = new Department("E1", "Engineering");
        Department marketing = new Department("M1", "Marketing"); // NEW
        
        company.addDepartment(sales);
        company.addDepartment(engineering);
        company.addDepartment(marketing); // NEW
        
        // 2. Hire Employees and assign them to a Department object (Composition)
        company.hireEmployee(new FullTimeEmployee("Ana", "Smith", 80000.0, sales));
        company.hireEmployee(new PartTimeEmployee("Bob", "Johnson", 22.50, engineering));
        company.hireEmployee(new FullTimeEmployee("Carla", "Diaz", 95000.0, engineering));
        company.hireEmployee(new FullTimeEmployee("David", "Lee", 78000.0, sales));
        company.hireEmployee(new PartTimeEmployee("Eve", "Brown", 25.00, marketing)); // NEW
    }


    /**
     * HELPER: Exception Handling for getting a valid double.
     */
    private static double getDoubleInput(String prompt, Scanner scanner) {
        while (true) { 
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                double value = Double.parseDouble(input);
                if (value < 0) { System.out.println("Input cannot be negative."); } 
                else { return value; }
            } catch (NumberFormatException e) {
                System.out.println("INVALID INPUT: Please enter a valid number.");
            }
        }
    }

    /**
     * HELPER: Exception Handling for getting a valid integer.
     */
    private static int getIntInput(String prompt, Scanner scanner) {
         while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine();
            try {
                int value = Integer.parseInt(input);
                 if (value < 0) { System.out.println("Input cannot be negative."); } 
                 else { return value; }
            } catch (NumberFormatException e) {
                System.out.println("INVALID INPUT: Please enter a valid whole number.");
            }
        }
    }

    /**
     * UPDATED: This logic is rewritten to show a numbered list (like the main menu)
     * instead of asking for a String ID.
     */
    private static Department selectDepartment() {
        System.out.println("Please select a department to assign:");
        ArrayList<Department> depts = company.getAvailableDepartments();
        
        if (depts.isEmpty()) {
            System.out.println("ERROR: No departments created. Please add departments first.");
            return null; // Cannot hire
        }
        
        // NEW: Print a numbered menu of departments
        for (int i = 0; i < depts.size(); i++) {
            // (i + 1) makes the list 1-based for the user
            System.out.printf("  %d. %s%n", (i + 1), depts.get(i).getDeptName());
        }

        Department selectedDept = null;
        while (selectedDept == null) {
            // Use our robust helper to get any valid integer
            int choice = getIntInput("Enter selection (1-" + depts.size() + "): ", scanner);

            // Check if the choice is within the valid range of the list
            if (choice >= 1 && choice <= depts.size()) {
                // Get the department from the list (adjusting for 0-based index)
                selectedDept = depts.get(choice - 1);
            } else {
                System.out.println("Invalid selection. Please enter a number from the list.");
            }
        }
        return selectedDept;
    }


    /**
     * UI HELPER: Uses the new selectDepartment() method.
     */
    private static void hireFullTime() {
        System.out.println("--- Hire Full-Time ---");
        
        Department dept = selectDepartment(); // Get the department object first
        if (dept == null) return; // Abort if no departments exist

        System.out.print("Enter First Name: ");
        String first = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String last = scanner.nextLine();
        double salary = getDoubleInput("Enter Annual Salary: ", scanner);

        FullTimeEmployee ftEmp = new FullTimeEmployee(first, last, salary, dept);
        company.hireEmployee(ftEmp);
    }

    /**
     * UI HELPER: Uses the new selectDepartment() method.
     */
    private static void hirePartTime() {
        System.out.println("--- Hire Part-Time ---");
        
        Department dept = selectDepartment(); // Get the department object first
        if (dept == null) return; // Abort
        
        System.out.print("Enter First Name: ");
        String first = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String last = scanner.nextLine();
        double rate = getDoubleInput("Enter Hourly Rate: ", scanner);

        PartTimeEmployee ptEmp = new PartTimeEmployee(first, last, rate, dept);
        company.hireEmployee(ptEmp);
    }

    private static void searchForEmployee() {
        System.out.println("--- Search Employee ---");
        System.out.print("Enter Employee ID to search (e.g., F101): ");
        String id = scanner.nextLine();

        Employee foundEmp = company.findEmployeeByID(id);

        if (foundEmp != null) {
            System.out.println("--- Employee Found ---");
            foundEmp.displayEmployeeDetails();
        } else {
            System.out.println("ERROR: No employee found with ID " + id);
        }
    }
}


/*
 * ===================================================================
 * CLASS: Employee (Abstract Base Class)
 * ===================================================================
 * Contains the composition field for Department.
 */
abstract class Employee {

    private static int employeeCounter = 101; 
    private String employeeID;
    private String firstName;
    private String lastName;
    private EmployeeType type; 
    
    // COMPOSITION: Employee "has-a" Department.
    private Department workingDept; 

    // Constructor requires the Department object
    public Employee(String firstName, String lastName, String idPrefix, EmployeeType type, Department workingDept) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        this.workingDept = workingDept; // Assign the department object
        
        this.employeeID = idPrefix + employeeCounter; 
        employeeCounter++;
    }

    // --- Getters ---
    public String getEmployeeID() { return employeeID; }
    public String getFullName() { return firstName + " " + lastName; }
    public EmployeeType getEmployeeType() { return this.type; }
    public Department getWorkingDept() { return this.workingDept; } // Getter for the composite object

    public abstract double calculateWeeklyPay();

    // displays info from this class AND its composite object
    public void displayEmployeeDetails() {
        System.out.println("\n------------------------------");
        System.out.println("ID: " + this.employeeID);
        System.out.println("Name: " + getFullName());
        System.out.println("Type: " + this.type);
        System.out.println("Department: " + workingDept.getDeptName() + " (ID: " + workingDept.getDeptId() + ")");
    }
}


/*
 * ===================================================================
 * CLASS: FullTimeEmployee (Derived Class)
 * ===================================================================
 * Implements BonusPayable. Constructor passes Department up to parent.
 */
class FullTimeEmployee extends Employee implements BonusPayable {

    private double annualSalary;

    public FullTimeEmployee(String firstName, String lastName, double annualSalary, Department workingDept) {
        super(firstName, lastName, "F", EmployeeType.FULL_TIME_SALARIED, workingDept); 
        this.annualSalary = annualSalary;
    }

    @Override
    public double calculateWeeklyPay() {
        return this.annualSalary / 52.0; 
    }

    @Override
    public double calculateAnnualBonus() {
        return this.annualSalary * BonusPayable.BONUS_RATE; 
    }

    @Override
    public void displayEmployeeDetails() {
        super.displayEmployeeDetails(); 
        System.out.printf("Annual Salary: $%,.2f%n", this.annualSalary);
        System.out.printf("Weekly Pay: $%,.2f%n", calculateWeeklyPay());
        System.out.printf("Calculated Bonus: $%,.2f%n", calculateAnnualBonus());
        System.out.println("------------------------------");
    }
}


/*
 * ===================================================================
 * CLASS: PartTimeEmployee (Derived Class)
 * ===================================================================
 * Implements TrainingRequired. Constructor passes Department up to parent.
 */
class PartTimeEmployee extends Employee implements TrainingRequired {

    private double hourlyRate;
    private int hoursWorkedThisWeek; 

    public PartTimeEmployee(String firstName, String lastName, double hourlyRate, Department workingDept) {
        super(firstName, lastName, "P", EmployeeType.PART_TIME_HOURLY, workingDept); 
        this.hourlyRate = hourlyRate;
        this.hoursWorkedThisWeek = 0; 
    }

    public void setHoursWorked(int hours) {
        this.hoursWorkedThisWeek = hours;
    }

    @Override
    public double calculateWeeklyPay() {
        return this.hourlyRate * this.hoursWorkedThisWeek;
    }

    @Override
    public void attendMandatoryTraining() {
        System.out.println("TRAINING LOGGED: " + getFullName() + " has attended the mandatory compliance training.");
    }

    @Override
    public void displayEmployeeDetails() {
        super.displayEmployeeDetails(); 
        System.out.printf("Hourly Rate: $%.2f%n", this.hourlyRate);
        if (this.hoursWorkedThisWeek > 0) {
            System.out.printf("Last Calculated Pay: $%.2f (%d hours)%n", calculateWeeklyPay(), this.hoursWorkedThisWeek);
        }
        System.out.println("------------------------------");
    }
}


/*
 * ===================================================================
 * CLASS: Company (Container Class - Renamed from Department)
 * ===================================================================
 * Encapsulates BOTH the Employee list and the Department list.
 */
class Company {

    private ArrayList<Employee> allEmployeesList;
    private ArrayList<Department> allDepartmentsList; 

    public Company() {
        this.allEmployeesList = new ArrayList<>();
        this.allDepartmentsList = new ArrayList<>(); 
    }

    // --- Methods for Employees ---
    public void hireEmployee(Employee employee) {
        this.allEmployeesList.add(employee);
        System.out.printf("HIRE SUCCESS: %s (%s) has been hired into %s.%n", 
            employee.getFullName(), employee.getEmployeeID(), employee.getWorkingDept().getDeptName());
    }

    public Employee findEmployeeByID(String id) {
        for (Employee emp : allEmployeesList) {
            if (emp.getEmployeeID().equalsIgnoreCase(id)) {
                return emp; 
            }
        }
        return null; 
    }
    
    public void displayAllEmployees() {
        System.out.println("\n--- SHOWING ALL EMPLOYEES IN COMPANY ---");
        if (allEmployeesList.isEmpty()) {
            System.out.println("No employees have been hired yet.");
            return;
        }
        for (Employee emp : allEmployeesList) {
            emp.displayEmployeeDetails(); // Polymorphic call
        }
        System.out.println("\n--- END OF EMPLOYEE LIST ---");
    }

    // --- Methods for Departments ---
    public void addDepartment(Department dept) {
        this.allDepartmentsList.add(dept);
        System.out.println("New department created: " + dept.getDeptName());
    }

    public Department findDepartmentById(String id) {
        for (Department dept : allDepartmentsList) {
            if (dept.getDeptId().equalsIgnoreCase(id)) {
                return dept;
            }
        }
        return null;
    }
    
    public ArrayList<Department> getAvailableDepartments() {
        return this.allDepartmentsList;
    }


    // --- Methods for Payroll and Reports ---

    public void updateAllPartTimeHours(Scanner scanner) {
        System.out.println("\n--- Updating Part-Time Hours for Payroll ---");
        boolean foundPartTimers = false;
        for (Employee emp : allEmployeesList) {
            if (emp instanceof PartTimeEmployee) {
                foundPartTimers = true;
                int hours = -1;
                while(hours < 0){
                     System.out.print("Enter hours worked for " + emp.getFullName() + ": ");
                     String input = scanner.nextLine();
                     try {
                        hours = Integer.parseInt(input);
                        if (hours < 0) System.out.println("Hours cannot be negative.");
                     } catch (NumberFormatException e){
                        System.out.println("Invalid number. Please try again.");
                     }
                }
                ((PartTimeEmployee) emp).setHoursWorked(hours);
            }
        }
        if (!foundPartTimers) {
             System.out.println("No part-time employees found to update.");
        }
    }

    public void generatePayrollReport() {
        System.out.println("\n--- WEEKLY COMPANY-WIDE PAYROLL REPORT ---");
        double totalPayroll = 0.0;

        for (Employee emp : allEmployeesList) {
            double pay = emp.calculateWeeklyPay(); 
            System.out.printf("PAYING: %s (%s, %s) --- $%,.2f%n",
                    emp.getFullName(), emp.getEmployeeID(), emp.getWorkingDept().getDeptName(), pay);
            totalPayroll += pay;
        }
        System.out.println("----------------------------------------");
        System.out.printf("TOTAL COMPANY PAYROLL: $%,.2f%n", totalPayroll);
    }
    
    /**
     * REPORT: Uses Composition and loops departments first.
     */
    public void generateDepartmentPayrollReport() {
        System.out.println("\n--- WEEKLY PAYROLL REPORT BY DEPARTMENT ---");
        double grandTotalPayroll = 0.0;

        // Outer loop: Iterate through each Department
        for (Department dept : allDepartmentsList) {
            System.out.printf("%n=== DEPARTMENT: %s ===%n", dept.getDeptName().toUpperCase());
            double departmentSubtotal = 0.0;
            boolean foundEmployeesInDept = false;

            // Inner loop: Iterate through all employees to find matches
            for (Employee emp : allEmployeesList) {
                // Check if the employee's department object matches this department
                if (emp.getWorkingDept().equals(dept)) {
                    foundEmployeesInDept = true;
                    double pay = emp.calculateWeeklyPay(); // Polymorphic call
                    System.out.printf("  PAYING: %s (%s) --- $%,.2f%n",
                            emp.getFullName(), emp.getEmployeeID(), pay);
                    departmentSubtotal += pay;
                }
            }

            if (!foundEmployeesInDept) {
                System.out.println("  No employees processed for this department.");
            } else {
                System.out.printf("  --- DEPARTMENT SUBTOTAL: $%,.2f ---%n", departmentSubtotal);
            }
            grandTotalPayroll += departmentSubtotal;
        }
        
        System.out.println("\n========================================");
        System.out.printf("GRAND TOTAL (ALL DEPTS): $%,.2f%n", grandTotalPayroll);
    }


    /**
     * REPORT: Uses Interfaces (instanceof)
     */
    public void runEndOfYearReports() {
        System.out.println("\n--- END-OF-YEAR BONUS & TRAINING REPORT ---");
        
        for (Employee emp : allEmployeesList) {
            
            // Check for Bonus BEHAVIOR
            if (emp instanceof BonusPayable) {
                BonusPayable bonusEmp = (BonusPayable) emp;
                double bonus = bonusEmp.calculateAnnualBonus();
                System.out.printf("BONUS: %s (%s, Dept: %s) earned $%,.2f%n",
                        emp.getFullName(), emp.getEmployeeID(), emp.getWorkingDept().getDeptName(), bonus);
            }

            // Check for Training BEHAVIOR
            if (emp instanceof TrainingRequired) {
                TrainingRequired trainEmp = (TrainingRequired) emp;
                trainEmp.attendMandatoryTraining(); 
            }
        }
    }
}
