
package courses;
import java.util.Scanner;
import system.main;
import students.StudentsConfig;
public class CoursesMain {
    public void courses() {
        Scanner in = new Scanner(System.in);
        String op = null;
        CoursesConfig course = new CoursesConfig();
        enrollCourse en = new enrollCourse();
        StudentsConfig st = new StudentsConfig();
        main sys = new main ();
        do {
            System.out.println("===========================================");
            System.out.println("          COURSES      ");
            System.out.println("===========================================");            
            System.out.println("|        1. ADD                            |");
            System.out.println("|        2. ENROLL STUDENT IN COURSE       |");
            System.out.println("|        3. VIEW                           |");
            System.out.println("|        4. UPDATE                         |");
            System.out.println("|        5. DELETE                         |");
            System.out.println("|        6. EXIT                           |");
            System.out.println("===========================================");
            System.out.print("Enter Action (1-6 only): ");
            int action = in.nextInt();
            while (action>6 || action<0){
               System.out.print("Error, Enter Action Again (1-5 only): ");
                 action = in.nextInt(); 
            }
            switch(action){
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
            System.out.print("Do you want to continue?(Y/N): ");
            op = in.next();
        }while(op.equals("Y") || op.equals("y"));
        System.out.println("Thank You, See you soon!");
    }
}
