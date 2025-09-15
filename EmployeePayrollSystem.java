import java.util.ArrayList;
import java.util.Scanner;

/*
 * ===================================================================
 * OOP CONCEPTS: Enums and Interfaces
 * (Defined first so other classes can use them)
 * ===================================================================
 */

/**
 * ENUM: Represents a fixed set of constants.
 */
enum EmployeeType {
    FULL_TIME_SALARIED,
    PART_TIME_HOURLY
}

/**
 * INTERFACE: Defines a "behavior" (what an object CAN DO).
 */
interface BonusPayable {
    /**
     * STATIC FINAL CONSTANT: This rate is shared by all implementing classes.
     */
    double BONUS_RATE = 0.05; // 5% Bonus Rate

    double calculateAnnualBonus();
}

/**
 * INTERFACE: Defines another "behavior."
 */
interface TrainingRequired {
    void attendMandatoryTraining();
}


/*
 * ===================================================================
 * FILE: HRManagementSystem.java (Main Public Class)
 * ===================================================================
 * This is the only public class and contains the main() entry point.
 */
public class EmployeePayrollSystem {

    private static Department department = new Department();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Welcome to the HR Payroll Management System v3.0.");
        loadTestData(); // Load some demo data
        boolean running = true;

        while (running) {
            printMenu();
            // UPDATED: Prompt now reflects 1-7
            int choice = getIntInput("Please enter your choice (1-7): ", scanner);

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
                    department.updateAllPartTimeHours(scanner);
                    department.generatePayrollReport();
                    break;
                case 5:
                    department.runEndOfYearReports();
                    break;
                case 6: // NEW: Call the new method to display all employees
                    department.displayAllEmployees();
                    break;
                case 7: // UPDATED: Exit is now option 7
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please select 1-7."); // UPDATED
            }
            if (running) {
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine(); // Pause screen
            }
        }

        System.out.println("Thank you for using the system. Goodbye.");
        scanner.close();
    }

    /**
     * UPDATED: Added new option 6 and renumbered Exit to 7.
     */
    public static void printMenu() {
        System.out.println("\n--- MAIN MENU ---");
        System.out.println("1. Hire Full-Time Employee");
        System.out.println("2. Hire Part-Time Employee");
        System.out.println("3. Search for Employee (by ID)");
        System.out.println("4. Run Weekly Payroll Report (Updates hours first)");
        System.out.println("5. Run End-of-Year Reports (Bonus/Training)");
        System.out.println("6. View All Employees List"); // NEW
        System.out.println("7. Exit"); // UPDATED
    }
    
    // Helper method to load test data
    public static void loadTestData(){
        System.out.println("(Loading test data...)");
        department.hireEmployee(new FullTimeEmployee("Ana", "Smith", 80000.0));
        department.hireEmployee(new PartTimeEmployee("Bob", "Johnson", 22.50));
        department.hireEmployee(new FullTimeEmployee("Carla", "Diaz", 95000.0));
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
                if (value < 0) {
                     System.out.println("Input cannot be negative.");
                } else {
                     return value; 
                }
            } catch (NumberFormatException e) {
                System.out.println("INVALID INPUT: Please enter a valid number (e.g., 50000.0 or 22.50).");
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
                 if (value < 0) {
                     System.out.println("Input cannot be negative.");
                 } else {
                    return value;
                 }
            } catch (NumberFormatException e) {
                System.out.println("INVALID INPUT: Please enter a valid whole number (e.g., 1 or 40).");
            }
        }
    }


    /**
     * UI HELPER for hiring full-time.
     */
    private static void hireFullTime() {
        System.out.println("--- Hire Full-Time ---");
        System.out.print("Enter First Name: ");
        String first = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String last = scanner.nextLine();
        
        double salary = getDoubleInput("Enter Annual Salary: ", scanner);

        FullTimeEmployee ftEmp = new FullTimeEmployee(first, last, salary);
        department.hireEmployee(ftEmp);
    }

    /**
     * UI HELPER for hiring part-time.
     */
    private static void hirePartTime() {
        System.out.println("--- Hire Part-Time ---");
        System.out.print("Enter First Name: ");
        String first = scanner.nextLine();
        System.out.print("Enter Last Name: ");
        String last = scanner.nextLine();
        
        double rate = getDoubleInput("Enter Hourly Rate: ", scanner);

        PartTimeEmployee ptEmp = new PartTimeEmployee(first, last, rate);
        department.hireEmployee(ptEmp);
    }

    /**
     * UI HELPER for searching.
     */
    private static void searchForEmployee() {
        System.out.println("--- Search Employee ---");
        System.out.print("Enter Employee ID to search (e.g., F101): ");
        String id = scanner.nextLine();

        Employee foundEmp = department.findEmployeeByID(id);

        if (foundEmp != null) {
            System.out.println("--- Employee Found ---");
            foundEmp.displayEmployeeDetails(); // Polymorphic call
        } else {
            System.out.println("ERROR: No employee found with ID " + id);
        }
    }
}


/*
 * ===================================================================
 * CLASS: Employee (Abstract Base Class)
 * ===================================================================
 * Demonstrates: Abstraction, Static variables.
 */
abstract class Employee {

    // STATIC VARIABLE: Shared by ALL Employee objects to generate unique IDs.
    private static int employeeCounter = 101; 

    private String employeeID;
    private String firstName;
    private String lastName;
    private EmployeeType type; // Enum field

    // Constructor with auto-ID generation
    public Employee(String firstName, String lastName, String idPrefix, EmployeeType type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
        
        // Auto-generate the unique ID using the static counter
        this.employeeID = idPrefix + employeeCounter; 
        employeeCounter++; // Increment static counter for the next hire
    }

    // --- Getters ---
    public String getEmployeeID() { return employeeID; }
    public String getFullName() { return firstName + " " + lastName; }
    public EmployeeType getEmployeeType() { return this.type; }

    // ABSTRACT METHOD (Abstraction)
    public abstract double calculateWeeklyPay();

    // Concrete method (can be overridden by children)
    public void displayEmployeeDetails() {
        System.out.println("\n------------------------------");
        System.out.println("ID: " + this.employeeID);
        System.out.println("Name: " + getFullName());
        System.out.println("Type: " + this.type); // Display the enum
    }
}


/*
 * ===================================================================
 * CLASS: FullTimeEmployee (Derived Class)
 * ===================================================================
 * Demonstrates: Inheritance, Polymorphism, and implementing Interfaces.
 */
class FullTimeEmployee extends Employee implements BonusPayable {

    private double annualSalary;

    // Constructor: Passes info up to the Employee (super) constructor
    public FullTimeEmployee(String firstName, String lastName, double annualSalary) {
        super(firstName, lastName, "F", EmployeeType.FULL_TIME_SALARIED); 
        this.annualSalary = annualSalary;
    }

    /**
     * POLYMORPHISM (Override from Employee)
     */
    @Override
    public double calculateWeeklyPay() {
        return this.annualSalary / 52.0; 
    }

    /**
     * REQUIRED METHOD (from BonusPayable Interface)
     */
    @Override
    public double calculateAnnualBonus() {
        // Uses the STATIC FINAL constant from the interface
        return this.annualSalary * BonusPayable.BONUS_RATE; 
    }

    /**
     * POLYMORPHISM (Override from Employee)
     * Adds more specific details to the base method.
     */
    @Override
    public void displayEmployeeDetails() {
        super.displayEmployeeDetails(); // Call parent method first
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
 * Demonstrates: Inheritance, Polymorphism, and implementing a different Interface.
 */
class PartTimeEmployee extends Employee implements TrainingRequired {

    private double hourlyRate;
    private int hoursWorkedThisWeek; 

    // Constructor: Passes info up to the Employee (super) constructor
    public PartTimeEmployee(String firstName, String lastName, double hourlyRate) {
        super(firstName, lastName, "P", EmployeeType.PART_TIME_HOURLY); 
        this.hourlyRate = hourlyRate;
        this.hoursWorkedThisWeek = 0; // Default
    }

    // Specific setter for this class
    public void setHoursWorked(int hours) {
        this.hoursWorkedThisWeek = hours;
    }

    /**
     * POLYMORPHISM (Override from Employee)
     */
    @Override
    public double calculateWeeklyPay() {
        return this.hourlyRate * this.hoursWorkedThisWeek;
    }

    /**
     * REQUIRED METHOD (from TrainingRequired Interface)
     */
    @Override
    public void attendMandatoryTraining() {
        System.out.println("TRAINING LOGGED: " + getFullName() + " has attended the mandatory compliance training.");
    }

    /**
     * POLYMORPHISM (Override from Employee)
     */
    @Override
    public void displayEmployeeDetails() {
        super.displayEmployeeDetails(); // Call parent method
        System.out.printf("Hourly Rate: $%.2f%n", this.hourlyRate);
        if (this.hoursWorkedThisWeek > 0) {
            System.out.printf("Last Calculated Pay: $%.2f (%d hours)%n", calculateWeeklyPay(), this.hoursWorkedThisWeek);
        }
        System.out.println("------------------------------");
    }
}


/*
 * ===================================================================
 * CLASS: Department (Container Class)
 * ===================================================================
 * Demonstrates: Encapsulation (hides the employeeList).
 * UPDATED: Contains the new displayAllEmployees() method.
 */
class Department {

    // ENCAPSULATION: This list is private. Access is controlled by public methods.
    private ArrayList<Employee> employeeList;

    public Department() {
        this.employeeList = new ArrayList<>();
    }

    // Public method to safely add to the list
    public void hireEmployee(Employee employee) {
        this.employeeList.add(employee);
        System.out.printf("HIRE SUCCESS: %s (%s) has been hired.%n", 
            employee.getFullName(), employee.getEmployeeID());
    }

    // Public method to safely find an item
    public Employee findEmployeeByID(String id) {
        for (Employee emp : employeeList) {
            if (emp.getEmployeeID().equalsIgnoreCase(id)) {
                return emp; // Found
            }
        }
        return null; // Not found
    }
    
    /**
     * NEW METHOD: This is the logic for the new menu option.
     * It perfectly demonstrates POLYMORPHISM. We loop the single <Employee> list,
     * call the *same* method (emp.displayEmployeeDetails()), and Java
     * automatically runs the correct overridden version for each object.
     */
    public void displayAllEmployees() {
        System.out.println("\n--- SHOWING ALL EMPLOYEES IN DEPARTMENT ---");
        if (employeeList.isEmpty()) {
            System.out.println("No employees have been hired yet.");
            return;
        }

        // Polymorphism in action:
        for (Employee emp : employeeList) {
            emp.displayEmployeeDetails(); // Runs the FullTime or PartTime version automatically
        }
        System.out.println("\n--- END OF EMPLOYEE LIST ---");
    }


    /**
     * Encapsulated logic to update hours (used by payroll).
     */
    public void updateAllPartTimeHours(Scanner scanner) {
        System.out.println("\n--- Updating Part-Time Hours for Payroll ---");
        boolean foundPartTimers = false;
        for (Employee emp : employeeList) {
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
                // Cast to access the specific subclass method
                ((PartTimeEmployee) emp).setHoursWorked(hours);
            }
        }
        if (!foundPartTimers) {
             System.out.println("No part-time employees found to update.");
        }
    }

    /**
     * Encapsulated logic for the payroll report (demonstrates Polymorphism).
     */
    public void generatePayrollReport() {
        System.out.println("\n--- WEEKLY PAYROLL REPORT ---");
        double totalPayroll = 0.0;

        for (Employee emp : employeeList) {
            double pay = emp.calculateWeeklyPay(); // Polymorphic call

            System.out.printf("PAYING: %s (%s, %s) --- $%,.2f%n",
                    emp.getFullName(), emp.getEmployeeID(), emp.getEmployeeType(), pay);

            totalPayroll += pay;
        }
        System.out.println("----------------------------------------");
        System.out.printf("TOTAL WEEKLY PAYROLL: $%,.2f%n", totalPayroll);
    }

    /**
     * Encapsulated logic for the EOY report (demonstrates Interfaces).
     */
    public void runEndOfYearReports() {
        System.out.println("\n--- END-OF-YEAR BONUS & TRAINING REPORT ---");
        
        for (Employee emp : employeeList) {
            
            // Check for Bonus BEHAVIOR (using Interface)
            if (emp instanceof BonusPayable) {
                BonusPayable bonusEmp = (BonusPayable) emp;
                double bonus = bonusEmp.calculateAnnualBonus();
                System.out.printf("BONUS REPORT: %s (%s) earned an annual bonus of $%,.2f%n",
                        emp.getFullName(), emp.getEmployeeID(), bonus);
            }

            // Check for Training BEHAVIOR (using Interface)
            if (emp instanceof TrainingRequired) {
                TrainingRequired trainEmp = (TrainingRequired) emp;
                trainEmp.attendMandatoryTraining(); // Call the interface method
            }
        }
    }
}