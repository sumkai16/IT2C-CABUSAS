
package students;
import system.main;
import java.util.Scanner;
import system.config;


public class smain {
    public void students() {
        Scanner in = new Scanner(System.in);
        String op = null;
        studentsconf demo = new studentsconf();
        
        do {
            System.out.println("===========================================");
            System.out.println("        STUDENTS      ");
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
                    demo.addStudents();
                break;
                case 2:
                    demo.viewStudents();
                break;
                case 3: 
                    demo.viewStudents();
                    demo.updateStudents();
                break;
                case 4:
                    demo.viewStudents();
                    demo.deleteStudents();
                    break;
                case 5:
                     System.exit(0);
                    break;
                    
            }
            System.out.print("Do you want to continue?(Y/N): ");
            op = in.next();
        }while(op.equals("Y") || op.equals("y"));
        System.out.println("Thank You, See you soon!");
        
  
    }
    
    
    
}


