package system;
import java.util.*;
import students.StudentMain;
import teachers.TeacherMain;
import courses.CoursesMain;
import courses.CoursesConfig;
import courses.GradesC;
import java.io.IOException;
import courses.addGrades;

public class main {
    
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        String op = null;
        StudentMain stud = new StudentMain();
        TeacherMain teach = new TeacherMain();
        CoursesMain cour = new CoursesMain();
        addGrades ad = new addGrades();
        CoursesConfig c = new CoursesConfig();
        GradesC gc = new GradesC();
        
        do {
            System.out.println("===========================================");
            System.out.println("|    Welcome to Student Grading System    |");
            System.out.println("===========================================");            
            System.out.println("|        1. STUDENT                        |");
            System.out.println("|        2. COURSES                        |");
            System.out.println("|        3. TEACHER                        |");
            System.out.println("|        4. GRADES                         |");
            System.out.println("|        5. EXIT                           |");
            System.out.println("===========================================");
            System.out.print("Enter Action (1-5 only): ");
            
            int action = -1;
            // Input validation for action
            while (action < 1 || action > 5) {
                try {
                    action = in.nextInt();
                    in.nextLine();  // Clear the buffer
                    if (action < 1 || action > 5) {
                        System.out.println("Invalid choice. Please enter a number between 1 and 5.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Error: Invalid input. Please enter a valid number between 1 and 5.");
                    System.out.print(": ");
                    in.nextLine();  // Clear the invalid input from the buffer
                }
            }

            // Perform the chosen action
            switch(action) {
                case 1:
                    stud.students();
                    break;
                case 2:
                    cour.courses();
                    break;
                case 3: 
                    teach.teachers();
                    break;
                case 4:
                    ad.viewGradesMenu();
                    
                    break;
                case 5:
                    System.out.println("Exiting...");
                    System.out.println("Thank you, See you soon!");
                    System.exit(0);
                    break;
            }
            
            // Ask if the user wants to continue
            System.out.print("Do you want to continue? (Y/N): ");
            op = in.nextLine();  // Use nextLine() to clear the buffer after integer input
        } while(op.equalsIgnoreCase("Y"));
        
        System.out.println("Thank You, See you soon!");
    }
}
