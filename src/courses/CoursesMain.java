package courses;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;
import system.main;
import students.StudentsConfig;

public class CoursesMain {
    public void courses() throws IOException {
        Scanner in = new Scanner(System.in);
        String op = null;
        CoursesConfig course = new CoursesConfig();
        enrollCourse en = new enrollCourse();
        StudentsConfig st = new StudentsConfig();
        main sys = new main();

        do {
            System.out.println("===========================================");
            System.out.println("          COURSES      ");
            System.out.println("===========================================");            
            System.out.println("        1. ADD                           ");
            System.out.println("        2. ENROLL STUDENT IN COURSE      ");
            System.out.println("        3. VIEW                          ");
            System.out.println("        4. UPDATE                        ");
            System.out.println("        5. DELETE                        ");
            System.out.println("        6. BACK TO MAIN                  ");
            System.out.println("===========================================");
            
            int action = -1;
            // Input validation for action
            while (action < 1 || action > 6) {
                try {
                    System.out.print("Enter Action (1-6 only): ");
                    action = in.nextInt();
                    in.nextLine();  // Clear the buffer
                    if (action < 1 || action > 6) {
                        System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Error: Invalid input. Please enter a valid number between 1 and 6.");
                    in.nextLine();  // Clear the invalid input from the buffer
                }
            }

            // Perform the chosen action
            switch(action) {
                case 1:
                    course.addCourses();
                    break;
                case 2:
                    st.viewStudents();
                    en.enrollStudentInCourses();
                    break;
                case 3:
                    course.viewCourses();
                    course.wantView();
                    break;
                case 4: 
                    course.viewCourses();
                    course.updateCourses();
                    break;
                case 5:
                    course.viewCourses();
                    course.deleteCourses();
                    break;
                case 6:
                    sys.main(new String[]{});
                    break;
            }

            // Ask if the user wants to continue
            System.out.print("Do you want to go Back to Courses Menu? (Y/N): ");
            op = in.next();
        } while(op.equalsIgnoreCase("Y"));

        System.out.println("Thank You, See you soon!");
    }
}
