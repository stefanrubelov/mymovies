package mymovies;

import mymovies.dal.db.connection.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mymovies.utils.Env;

import javax.swing.*;
import java.sql.SQLException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/views/homepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(Env.get("APP_NAME", "My Movies"));
        stage.setScene(scene);
        stage.show();

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Reminder, you have movies with rating of 6.0 or below and that you haven't opened in more than 2 years.", "Warning", JOptionPane.ERROR_MESSAGE);
        });

        DBConnection db = new DBConnection();
        try {
            db.testConnection();
            System.out.println("Connection successful!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}