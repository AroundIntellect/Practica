module com.example.practica {
    requires javafx.controls;
    requires javafx.fxml;
    requires poi;
    requires poi.ooxml;


    opens com.example.practica to javafx.fxml;
    exports com.example.practica;
}