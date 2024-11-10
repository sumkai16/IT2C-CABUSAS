
package teachers;

import java.io.IOException;
import java.util.Scanner;
import system.main;


public class TeacherMain {
     public void teachers() throws IOException {
        Scanner in = new Scanner(System.in);
        String op = null;
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
            System.out.println("        5. BACK TO MAIN                   ");
            System.out.println("===========================================");
            System.out.print("Enter Action (1-5 only): ");
            int action = in.nextInt();
            while (action>5 || action<0){
               System.out.print("Error, Enter Action Again (1-5 only): ");
                 action = in.nextInt(); 
            }
            switch(action){
                case 1:
                    teach.addTeachers();
                break;
                case 2:
                   teach.viewTeachers();
                break;
                case 3: 
                   teach.viewTeachers();
                   teach.selections();
                   teach.updateTeachers();
                break;
                case 4:
                    teach.deleteTeachers();
                    break;
                case 5:
                    sys.main(new String[]{});
                    break;
                    
            }
            System.out.print("Do you want to go back to Teachers Menu?(Y/N): ");
            op = in.next();
        }while(op.equals("Y") || op.equals("y"));
        System.out.println("Thank You, See you soon!");
    }
     
     
}
