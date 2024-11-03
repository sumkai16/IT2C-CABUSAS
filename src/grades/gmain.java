
package grades;

import java.util.Scanner;
import system.main;


public class gmain {
    
    public void grades() {
        Scanner in = new Scanner(System.in);
        String op = null;
        main sys = new main();
        gconf grades = new gconf();
        
        do {
            System.out.println("===========================================");
            System.out.println("        GRADES      ");
            System.out.println("===========================================");            
            System.out.println("|        1. ADD                            |");
            System.out.println("|        2. VIEW                           |");
            System.out.println("|        3. UPDATE                         |");
            System.out.println("|        4. DELETE                         |");
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
                   grades.addGrade();
                break;
                case 2:
                    grades.viewGrades();
                break;
                case 3: 
                    grades.viewGrades();
                    grades.updateGrade();
                break;
                case 4:
                    grades.viewGrades();
                    grades.deleteGrade();
                    break;
                case 5:
                    sys.main(new String[]{});
                    break;
                    
            }
            System.out.print("Do you want to continue?(Y/N): ");
            op = in.next();
        }while(op.equals("Y") || op.equals("y"));
        System.out.println("Thank You, See you soon!");
        
  
    }
}
