package mymovies.gui;

import javafx.fxml.FXML;
import mymovies.BE.Movies;

import javax.swing.text.html.ListView;
import java.util.List;
import java.util.stream.Collectors;

public class MovieController {

    @FXML
    private ListView<String> movieListView;

    //method to show movies in the listView
    public void displayMoviesByCategory(String category) {
        List<Movies> movies = fetchMoviesByCategory (category);
        List<String> moviesNames = movies.stream().map(Movies::getName).toList(); //Extract the names

        movieListView.getItems().setAll(moviesNames);
    }
}
