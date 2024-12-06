package students;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import system.main;

public class StudentMain {
    public void students() throws IOException {
        Scanner in = new Scanner(System.in);
        String op = null;
        StudentsConfig demo = new StudentsConfig();
        main sys = new main();
        
        do {
            System.out.println("===========================================");
            System.out.println("        STUDENTS      ");
            System.out.println("===========================================");            
            System.out.println("        1. ADD                            ");
            System.out.println("        2. VIEW                           ");
            System.out.println("        3. UPDATE                         ");
            System.out.println("        4. DELETE                         ");
            System.out.println("        5. BACK TO MAIN                   ");
            System.out.println("===========================================");
            
            int action = getValidActionInput(in);

            switch(action){
                case 1:
                    demo.addStudents();
                    break;
                case 2:
                    demo.viewStudents();
                    demo.wantUpdate();
                    break;
                case 3: 
                    demo.viewStudentss();
                    demo.updateStudents();
                    break;
                case 4:
                    demo.viewStudentss();
                    demo.deleteStudents();
                    break;
                case 5:
                    sys.main(new String[]{}); 
                    break;    
            }
            
            System.out.print("Do you want to go back to Students Menu? (Y/N): ");
            op = in.next();
        } while(op.equalsIgnoreCase("Y"));
        
        System.out.println("Thank You, See you soon!");
    }
    
    private int getValidActionInput(Scanner in) {
        int action = -1;
        boolean valid = false;
        
        while (!valid) {
            try {
                System.out.print("Enter Action (1-5 only): ");
                action = in.nextInt();
                if (action < 1 || action > 5) {
                    throw new IllegalArgumentException("Error: Action must be between 1 and 5.");
                }
                valid = true;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number between 1 and 5.");
                System.out.print(": ");
                in.nextLine(); 
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
        return action;
    }
}
