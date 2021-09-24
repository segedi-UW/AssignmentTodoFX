module com.todo.assignmenttodofx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires java.desktop;
    requires java.prefs;

    opens com.todo.assignmenttodofx to javafx.fxml;
    exports com.todo.assignmenttodofx;
}