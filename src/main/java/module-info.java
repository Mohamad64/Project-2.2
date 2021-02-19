module project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    opens project to javafx.fxml;
    exports project;
}