package mymovies;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mymovies.dal.db.connection.DBConnection;
import mymovies.utils.Env;

import java.sql.SQLException;

public class App extends Application {
    private static boolean hasCheckedForOlderMovies = false;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/views/homepage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle(Env.get("APP_NAME", "My Movies"));
        stage.setScene(scene);
        stage.show();

        DBConnection db = new DBConnection();
        try {
            db.testConnection();
            System.out.println("Connection successful!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasCheckedForOlderMovies() {
        return hasCheckedForOlderMovies;
    }

    public static void setHasCheckedForOlderMovies(boolean checked) {
        hasCheckedForOlderMovies = checked;
    }

    public static void main(String[] args) {
        launch(args);
    }
}