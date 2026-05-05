package model;

public class Student {

    private String name;

    public static final String[] SUBJECTS = {
        "Math", "English", "Urdu", "Science", "Physics", "Chemistry", "Computer"
    };

    private double[] marks;

    public Student(String name) {
        this.name = name;
        marks = new double[SUBJECTS.length];
        for (int i = 0; i < marks.length; i++) {
            marks[i] = -1;
        }
    }

    public void setMark(int index, double mark) {
        marks[index] = mark;
    }

    public void skipMark(int index) {
        marks[index] = -1;
    }

    public double getMark(int index) {
        return marks[index];
    }

    public boolean isEntered(int index) {
        return marks[index] != -1;
    }

    public boolean hasAnyMark() {
        for (double m : marks) {
            if (m != -1) return true;
        }
        return false;
    }

    public double getAverage() {
        double total = 0;
        int count = 0;
        for (double m : marks) {
            if (m != -1) {
                total += m;
                count++;
            }
        }
        return count == 0 ? 0 : total / count;
    }

    public String getHighestInfo() {
        double max = -1;
        String subject = "";
        for (int i = 0; i < marks.length; i++) {
            if (marks[i] != -1 && marks[i] > max) {
                max = marks[i];
                subject = SUBJECTS[i];
            }
        }
        return max == -1 ? "-" : max + "  (" + subject + ")";
    }
    public String getLowestInfo() {
        double min = 101;
        String subject = "";
        for (int i = 0; i < marks.length; i++) {
            if (marks[i] != -1 && marks[i] < min) {
                min = marks[i];
                subject = SUBJECTS[i];
            }
        }
        return min == 101 ? "-" : min + "  (" + subject + ")";
    }

    public String getLetterGrade() {
        double avg = getAverage();
        if      (avg >= 90) return "A";
        else if (avg >= 80) return "B";
        else if (avg >= 70) return "C";
        else if (avg >= 60) return "D";
        else                return "F";
    }

    public String getName() { return name; }
}