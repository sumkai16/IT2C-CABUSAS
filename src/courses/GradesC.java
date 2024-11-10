package courses;
import courses.CoursesConfig;
import java.io.IOException;
import java.util.*;

public class GradesC {
    CoursesConfig c = new CoursesConfig();
    addGrades ad = new addGrades();
    Scanner in = new Scanner(System.in);
    public void Grades() throws IOException{
        System.out.print("Enter Course ID: ");
                        int courseIdForGrades = in.nextInt();
                        in.nextLine(); // Clear buffer
                        ad.viewCourseGrades(courseIdForGrades);
    }
}
