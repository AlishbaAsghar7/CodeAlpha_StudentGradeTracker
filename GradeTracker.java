package ui;

import model.Student;
import service.GradeService;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GradeTracker extends Application {

    GradeService service = new GradeService();

   
    public void start(Stage stage) {

        TextArea outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefRowCount(20);
        outputArea.setText(
            "Welcome to Student Grade Tracker.\n" +
            "Step 1: Add a student.\n" +
            "Step 2: Select student and enter subject marks.\n" +
            "Step 3: Click 'View Summary Report'."
        );

       
        TableView<Student> table = new TableView<>();
        table.setPrefHeight(200);

        TableColumn<Student, String> nameCol = new TableColumn<>("Student Name");
        nameCol.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().getName()));
        nameCol.setPrefWidth(130);

        TableColumn<Student, String> highCol = new TableColumn<>("Highest");
        highCol.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().hasAnyMark() ? d.getValue().getHighestInfo() : "-"));
        highCol.setPrefWidth(150);

        TableColumn<Student, String> lowCol = new TableColumn<>("Lowest");
        lowCol.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().hasAnyMark() ? d.getValue().getLowestInfo() : "-"));
        lowCol.setPrefWidth(150);

        TableColumn<Student, String> avgCol = new TableColumn<>("Average");
        avgCol.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().hasAnyMark() ?
                String.format("%.2f", d.getValue().getAverage()) : "-"));
        avgCol.setPrefWidth(80);

        TableColumn<Student, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setCellValueFactory(d ->
            new SimpleStringProperty(d.getValue().hasAnyMark() ? d.getValue().getLetterGrade() : "-"));
        gradeCol.setPrefWidth(60);

        table.getColumns().addAll(nameCol, highCol, lowCol, avgCol, gradeCol);

        
        ComboBox<String> studentDropdown = new ComboBox<>();
        studentDropdown.setPromptText("Select Student");
        studentDropdown.setMaxWidth(Double.MAX_VALUE);

        
        Label sec1 = new Label("Add New Student");
        sec1.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        TextField studentNameField = new TextField();
        studentNameField.setPromptText("Enter Student Name");

        Button addStudentBtn = new Button("Add Student");
        addStudentBtn.setMaxWidth(Double.MAX_VALUE);

        addStudentBtn.setOnAction(e -> {
            String error = service.addStudent(studentNameField.getText());
            if (error != null) {
                outputArea.setText("Error: " + error);
            } else {
                outputArea.setText("Student added: " + studentNameField.getText().trim() +
                    "\nNow select this student and enter subject marks.");
                studentDropdown.getItems().clear();
                studentDropdown.getItems().addAll(service.getStudentNames());
                table.getItems().clear();
                table.getItems().addAll(service.getAllStudents());
                studentNameField.clear();
            }
        });

        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(15));
        leftPanel.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
        leftPanel.getChildren().addAll(sec1, studentNameField, addStudentBtn);
        leftPanel.setPrefWidth(300);

       
        Label sec2 = new Label("Enter Subject Marks");
        sec2.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        TextField[] subjectFields = new TextField[Student.SUBJECTS.length];
        VBox subjectInputs = new VBox(6);

        for (int i = 0; i < Student.SUBJECTS.length; i++) {
            subjectFields[i] = new TextField();
            subjectFields[i].setPromptText(Student.SUBJECTS[i]);
            subjectInputs.getChildren().add(subjectFields[i]);
        }

        Button saveMarksBtn = new Button("Save Marks");
        saveMarksBtn.setMaxWidth(Double.MAX_VALUE);

        saveMarksBtn.setOnAction(e -> {
            String selected = studentDropdown.getValue();
            if (selected == null) {
                outputArea.setText("Please select a student first.");
                return;
            }

            boolean anyError = false;
            for (int i = 0; i < Student.SUBJECTS.length; i++) {
                String markText = subjectFields[i].getText();
                String error = service.setSubjectMark(selected, i, markText);
                if (error != null) {
                    outputArea.setText("Error: " + error);
                    anyError = true;
                    break;
                }
            }

            if (!anyError) {
                Student s = service.findStudent(selected);
                StringBuilder sb = new StringBuilder();
                sb.append("Marks saved for: ").append(selected).append("\n\n");

                for (int i = 0; i < Student.SUBJECTS.length; i++) {
                    if (s.isEntered(i)) {
                        sb.append(String.format("  %-12s : %.2f%n", Student.SUBJECTS[i], s.getMark(i)));
                    } else {
                        sb.append(String.format("  %-12s : (Skipped)%n", Student.SUBJECTS[i]));
                    }
                }

                if (s.hasAnyMark()) {
                    sb.append("\n");
                    sb.append("  Highest Marks : ").append(s.getHighestInfo()).append("\n");
                    sb.append("  Lowest Marks  : ").append(s.getLowestInfo()).append("\n");
                    sb.append(String.format("  Average       : %.2f%n", s.getAverage()));
                    sb.append("  Grade         : ").append(s.getLetterGrade()).append("\n");
                }

                outputArea.setText(sb.toString());

                for (TextField tf : subjectFields) tf.clear();


                table.getItems().clear();
                table.getItems().addAll(service.getAllStudents());
            }
        });

        VBox rightPanel = new VBox(10);
        rightPanel.setPadding(new Insets(15));
        rightPanel.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
        rightPanel.getChildren().addAll(sec2, studentDropdown, subjectInputs, saveMarksBtn);
        rightPanel.setPrefWidth(380);

        HBox topRow = new HBox(15, leftPanel, rightPanel);
        HBox.setHgrow(leftPanel,  Priority.ALWAYS);
        HBox.setHgrow(rightPanel, Priority.ALWAYS);


        Button summaryBtn = new Button("View Summary Report");
        summaryBtn.setOnAction(e -> outputArea.setText(service.getSummaryReport()));

        Label title = new Label("STUDENT GRADE TRACKER");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 15px;");

        Label sec3 = new Label("Students List");
        sec3.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        Label outputLabel = new Label("Output / Summary:");
        outputLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");

        VBox root = new VBox(12);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(
            title,
            new Separator(),
            topRow,
            new Separator(),
            sec3,
            table,
            new Separator(),
            summaryBtn,
            outputLabel,
            outputArea
        );

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);

        stage.setScene(new Scene(scroll, 750, 900));
        stage.setTitle("Student Grade Tracker");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}