package teachers;

import java.io.IOException;
import java.util.Scanner;
import system.main;

public class TeacherMain {
    public void teachers() throws IOException {
        Scanner in = new Scanner(System.in);
        String op;
        TeacherConfig teach = new TeacherConfig();
        main sys = new main();

        do {
            System.out.println("===========================================");
            System.out.println("          TEACHERS      ");
            System.out.println("===========================================");
            System.out.println("        1. ADD                            ");
            System.out.println("        2. VIEW                           ");
            System.out.println("        3. UPDATE                         ");
            System.out.println("        4. DELETE                         ");
            System.out.println("        5. VIEW FULL INFORMATION          "); // New option
            System.out.println("        6. BACK TO MAIN                   ");
            System.out.println("===========================================");

            int action = getValidIntInput("Enter Action (1-6 only): ", 1, 6, in);

            switch (action) {
                case 1:
                    teach.addTeachers();
                    break;
                case 2:
                    teach.viewTeachers();
                    break;
                case 3:
                    teach.viewTeachers();
                    teach.updateTeachers();  
                    break;
                case 4:
                    teach.deleteTeachers();
                    break;
                case 5:
                    teach.viewFullTeacherInfo();  // Call to view full information
                    break;
                case 6:
                    sys.main(new String[]{});  // Return to main menu
                    return;  
            }

            System.out.print("Do you want to go back to Teachers Menu? (Y/N): ");
            op = in.nextLine();
        } while (op.equalsIgnoreCase("Y"));
        
        System.out.println("Thank you, see you soon!");
    }

    private int getValidIntInput(String prompt, int min, int max, Scanner scanner) {
        int value;
        while (true) {
            try {
                System.out.print(prompt);
                value = Integer.parseInt(scanner.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.println("Error: Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
            }
        }
    }
}
