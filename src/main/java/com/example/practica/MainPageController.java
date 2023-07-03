package com.example.practica;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;

public class MainPageController {

    @FXML
    private Button checkSeparatorButton;

    @FXML
    private Button chooseFileButton;

    @FXML
    private Button choosePathButton;

    @FXML
    private Label pathLabel;

    @FXML
    private TextField separatorField;

    @FXML
    private Button startButton;

    private File selectedFile;
    private File selectedDirectory;
    private File excelFile;
    private String date;
    private String time;
    private String separator;
    public static String informationString;
    @FXML
    void initialize() {
        choosePathButton.setVisible(false);
        pathLabel.setVisible(false);
        separatorField.setVisible(false);
        startButton.setVisible(false);
        checkSeparatorButton.setVisible(false);

        chooseFileButton.setOnAction(ActionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Выберите файл");
            selectedFile = fileChooser.showOpenDialog(chooseFileButton.getScene().getWindow());
            if (selectedFile != null) {
                choosePathButton.setVisible(true);

                separatorField.setVisible(true);
                separatorField.setEditable(false);

                checkSeparatorButton.setVisible(true);
                checkSeparatorButton.setDisable(true);
            }
        });

        choosePathButton.setOnAction(ActionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Выберите папку");
            selectedDirectory = directoryChooser.showDialog(choosePathButton.getScene().getWindow());

            if (selectedDirectory != null) {
                date = String.valueOf(LocalDate.now()).replace("-", ".") + ";";
                time = String.valueOf(LocalTime.now().truncatedTo(java.time.temporal.ChronoUnit.SECONDS)).replace(":", "-");
                excelFile = new File(selectedDirectory, "logExcel " + date + time + ".xlsx");

                try (Workbook workbook = new XSSFWorkbook()) {
                    // Создаем новую электронную таблицу
                    workbook.createSheet("LogSheet");

                    // Сохраняем файл
                    try (FileOutputStream outputStream = new FileOutputStream(excelFile)) {
                        workbook.write(outputStream);
                    }
                } catch (IOException e) {
                    pathLabel.setText("По указанному пути не удалось создать файл, проверьте доступ к папке или выберите другую");
                    return;
                }

                pathLabel.setVisible(true);
                pathLabel.setText("Путь по которому будет создан Excel файл: " + excelFile.getPath());

                separatorField.setEditable(true);
                checkSeparatorButton.setDisable(false);
            }
        });

        checkSeparatorButton.setOnAction(ActionEvent -> {
            if (!separatorField.getText().equals("")) {
                separatorField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: green;");
                checkSeparatorButton.setTextFill(Paint.valueOf("green"));

                separator = separatorField.getText();

                startButton.setVisible(true);
                startButton.setDisable(false);
                checkSeparatorButton.setDisable(true);
            }
        });

        separatorField.setOnMouseClicked(MouseEvent -> {
            if (checkSeparatorButton.isDisable()) {
                checkSeparatorButton.setDisable(false);
            }

            separatorField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: red;");
            checkSeparatorButton.setTextFill(Paint.valueOf("red"));

            startButton.setDisable(true);
        });

        separatorField.setOnKeyPressed(KeyEvent -> {
            if (checkSeparatorButton.isDisable()) {
                checkSeparatorButton.setDisable(false);
            }

            separatorField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: red;");
            checkSeparatorButton.setTextFill(Paint.valueOf("red"));

            startButton.setDisable(true);

            if (KeyEvent.getCode() == KeyCode.ENTER) {
                if (!separatorField.getText().equals("")) {
                    separatorField.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: green;");
                    checkSeparatorButton.setTextFill(Paint.valueOf("green"));

                    separator = separatorField.getText();

                    startButton.setVisible(true);
                    startButton.setDisable(false);
                    checkSeparatorButton.setDisable(true);
                }
            }
        });

        startButton.setOnAction(ActionEvent -> {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
                String line;
                line = reader.readLine();
                if (!line.contains(separator)) {
                    reader.readLine();
                }

                try (Workbook workbook = new XSSFWorkbook(new FileInputStream(excelFile))) {
                    Sheet sheet = workbook.getSheetAt(0); // Получаем первый лист из файла

                    // Читаем строки из файла и записываем их в таблицу
                    readLinesFromFileAndWriteToSheet(selectedFile, sheet);

                    // Сохраняем изменения в файле
                    try (FileOutputStream outputStream = new FileOutputStream(excelFile)) {
                        workbook.write(outputStream);
                    }

                    informationString = "Файл успешно конвертирован! Спасибо за использование программы";
                    openInformationPage(startButton);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

    }

    private void openInformationPage(Node node) {
        try {
            FXMLLoader loader = new FXMLLoader(Start.class.getResource("informationPage.fxml"));
            Parent newRoot = loader.load();
            Scene scene = new Scene(newRoot);
            Stage newStage = new Stage(); // Создаем новое окно (Stage)
            newStage.setScene(scene);
            newStage.setTitle("Спасибо за использование");
            newStage.setResizable(false);
            newStage.initOwner(node.getScene().getWindow()); // Устанавливаем владельцем нового окна текущее окно
            newStage.initModality(Modality.WINDOW_MODAL); // Устанавливаем модальность нового окна для блокировки взаимодействия с предыдущим окном
            newStage.show(); // Отображаем новое окно поверх старого окна
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readLinesFromFileAndWriteToSheet(File file, Sheet sheet) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int rowNum = sheet.getLastRowNum() + 1; // Получаем индекс следующей строки
            while ((line = reader.readLine()) != null) {

                if (!line.contains(";")) {
                    line = reader.readLine();
                }

                String[] data = line.split(separator);
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < data.length; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(data[i]);
                }
            }
        }
    }

}
