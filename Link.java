import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import org.apache.commons.lang3.*;

public class Link {
    private String file_name = "Grades.csv";
    public List<Student> students = new ArrayList();

    public void generateChart() {
        // use processbuilder to call python code to generate charts
        try {
            ProcessBuilder pb = new ProcessBuilder("charts.exe", "GenerateHist", "GPA");

            pb.directory(new File(System.getProperty("user.dir")));
            System.out.println(pb.directory());
            // start the python
            Process process = pb.start();

            process.waitFor();

            System.out.println("Python finished.");

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public List<Student> getStudents() { return students; }

    // save the updated CSV file
    public void updateFile() {
        File file = new File(file_name);
        try {
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            String[] header = { "ID", "Name", "GPA" };
            writer.writeNext(header);
            for (Student student : students) {
                String[] temp = {String.valueOf(student.getId()), student.getName(), student.getGPA()};
                writer.writeNext(temp);

            }
            writer.close();
        }
        catch (Exception e) {
            //System.out.println("Something went wrong in updateFile()");
            e.printStackTrace();
        }
    }

    public void readFile() {
        readFile(file_name);
    }

    public void readFile(String fileName) {
        List<String[]> temp;
        try {
            FileReader freader = new FileReader(fileName);
            CSVReader reader = new CSVReaderBuilder(freader).withSkipLines(1).build();;
            temp = reader.readAll();
            if (temp.isEmpty()) {
                System.out.println("file is empty.");
                throw new Exception();
            }
            reader.close();
            for(String[] temp_s : temp)
            {
                Student s = new Student(Integer.parseInt(temp_s[0]), temp_s[1], temp_s[2]);
                students.add(s);
            }
            file_name = fileName;
        }
        catch (Exception e) {
            //System.out.println("error in readFile()");
            e.printStackTrace();
        }

    }
    //

    public void addStudent(String id, String name, String GPA) {
        boolean added = false;
        int int_id = Integer.parseInt(id);
        Student temp = new Student(int_id, name, GPA);
        students.add(temp);
//        if(!students.contains(temp)) {
//            students.add(temp);
//            //System.out.println("Student added");
//        }
//        else {
//            // replace with a JOptionPane or something
//            //System.out.println("Student already exists.");
//        }
        updateFile();
    }

    public void deleteStudent(String id) {
        boolean found = false;
        for(int i = 0; i < students.size(); i++) {
            if(students.get(i).getId() == Integer.parseInt(id)) {
                students.remove(i);
                //    System.out.println("Student with ID: " + id + " deleted.");
                found = true;
                break;
            }
        }
        if(!found) {
            // System.out.println("Student with ID: " + id + " not found and thus not deleted.");
        } else {
            updateFile();
        }
    }

    // troubleshooting method
    public void printStudents() {
        for(Student student : students) { System.out.println(student); }
    }
}

 // edit student information
    public void editStudent(String id, String name, String gpa) {
        for (Student student : students) {
            if (student.getId() == Integer.parseInt(id)) {
                student.name = name;
                student.GPA = gpa;
                updateFile(); 
                break;
            }
        }
    }

class Student { String name; String GPA; int id; String edit; String delete;

    public Student() { this(0,"",""); }

    public Student(int id, String name, String GPA) {
        this.id = id;
        this.name = name;
        this.GPA = GPA;
    }

    public Student(int id, String name, String GPA, String edit, String delete) {
        this(id,name,GPA);
        this.edit = edit;
        this.delete = delete;
    }

    public String getName() { return name; }

    public int getId() { return id; }

    public String getGPA() { return GPA; }

    @Override
    public String toString() { return id + ", " + name + ", " + GPA; }
}
