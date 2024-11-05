package system;
import java.util.*;
import students.StudentMain;
import teachers.TeacherMain;
import courses.CoursesMain;
import grades.gmain;

public class main {
    public static void main(String[] args) {
        Scanner in = new Scanner(   System.in);
        String op = null;
        StudentMain stud = new StudentMain();
        TeacherMain teach = new TeacherMain();
        CoursesMain cour = new CoursesMain();
        gmain grade = new gmain();
        do {
            System.out.println("===========================================");
            System.out.println("|    Welcome to Student Grading System    |");
            System.out.println("===========================================");            
            System.out.println("|        1. STUDENT                        |");
            System.out.println("|        2. TEACHER                        |");
            System.out.println("|        3. COURSES                        |");
            System.out.println("|        4. GRADES                         |");
            System.out.println("|        5. EXIT                           |");
            System.out.println("===========================================");
            System.out.print("Enter Action (1-5 only): ");
            int action = in.nextInt();
            while (action>5 || action<0){
               System.out.print("Error, Enter Action Again (1-5 only): ");
                 action = in.nextInt(); 
            }
            
             switch(action){
                case 1:
                    stud.students();
                break;
                case 2:
                  teach.teachers();
                break;
                case 3: 
                    cour.courses();
                break;
                case 4:
                   grade.grades();
                break;
                case 5:
                    System.out.println("Exiting...");
                    System.out.println("Thank you, See you soon!");
                     System.exit(0);
                    break;
                    
            }          
            System.out.print("Do you want to continue?(Y/N): ");
            op = in.next();
        }while(op.equals("Y") || op.equals("y"));
         System.out.println("Thank You, See you soon!");
    }

    
    
}
