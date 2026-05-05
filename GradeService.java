package service;

import model.Student;
import java.util.ArrayList;

public class GradeService {

    private ArrayList<Student> students = new ArrayList<>();

    public String addStudent(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Please enter the student's name.";
        }
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name.trim())) {
                return "A student with this name already exists.";
            }
        }
        students.add(new Student(name.trim()));
        return null; 
    }

  
    public String setSubjectMark(String studentName, int subjectIndex, String markText) {

        Student student = findStudent(studentName);
        if (student == null) return "Student not found.";

       
        if (markText == null || markText.trim().isEmpty()) {
            student.skipMark(subjectIndex);
            return null; 
        }

        double mark;
        try {
            mark = Double.parseDouble(markText.trim());
        } catch (NumberFormatException e) {
            return "Marks for " + Student.SUBJECTS[subjectIndex] + " must be a valid number.";
        }

        if (mark < 0 || mark > 100) {
            return "Marks for " + Student.SUBJECTS[subjectIndex] + " must be between 0 and 100.";
        }

        student.setMark(subjectIndex, mark);
        return null; 
    }

  
    public Student findStudent(String name) {
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name)) return s;
        }
        return null;
    }

    public ArrayList<Student> getAllStudents()  { return students; }

    public ArrayList<String> getStudentNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Student s : students) names.add(s.getName());
        return names;
    }

    public String getSummaryReport() {
        if (students.isEmpty()) {
            return "No students found. Please add students first.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("============================================\n");
        sb.append("      STUDENT GRADE SUMMARY REPORT          \n");
        sb.append("============================================\n\n");

        int count = 1;
        for (Student s : students) {
            sb.append("Student #").append(count).append("\n");
            sb.append("  Name             : ").append(s.getName()).append("\n\n");

            if (!s.hasAnyMark()) {
                sb.append("  Status           : No marks entered yet.\n");
            } else {
               
                sb.append("  Subject Marks:\n");
                for (int i = 0; i < Student.SUBJECTS.length; i++) {
                    if (s.isEntered(i)) {
                        sb.append(String.format("    %-12s : %.2f%n", Student.SUBJECTS[i], s.getMark(i)));
                    } else {
                        sb.append(String.format("    %-12s : (Skipped)%n", Student.SUBJECTS[i]));
                    }
                }

                sb.append("\n");
                sb.append("  Highest Marks    : ").append(s.getHighestInfo()).append("\n");
                sb.append("  Lowest Marks     : ").append(s.getLowestInfo()).append("\n");
                sb.append(String.format("  Average          : %.2f%n", s.getAverage()));
                sb.append("\n");
                sb.append("  Grade            : ").append(s.getLetterGrade()).append("\n");
            }

            sb.append("--------------------------------------------\n\n");
            count++;
        }

        return sb.toString();
    }
}