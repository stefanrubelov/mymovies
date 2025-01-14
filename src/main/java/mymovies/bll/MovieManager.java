package mymovies.bll;

import mymovies.be.Movie;
import mymovies.dal.MovieRepository;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MovieManager {
    final private MovieRepository movieRepository = new MovieRepository();
    final private Logger logger = Logger.getAnonymousLogger();

    public void addMovie(Movie movie, List<Integer> categoryIds) {
        Movie newMovie = movieRepository.create(movie);

        attachCategoriesToMovie(newMovie, categoryIds);
    }

    public void attachCategoriesToMovie(Movie movie, List<Integer> categoryIds) {
        for (Integer categoryId : categoryIds) {
            movieRepository.addCategory(movie, categoryId);
        }
    }

    public void deleteMovie(Movie movie) {
        movieRepository.delete(movie.getId());
    }

    public void playMovie(Movie movie) {
        String filePath = movie.getFilePath();

        if (filePath == null || filePath.isEmpty()) {
            System.out.println("Error: File path is null or empty.");
            return;
        }

        File movieFile = new File(filePath);
        if (!movieFile.exists()) {
            System.out.println("Error: The file does not exist at path: " + filePath);
            return;
        }

        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(movieFile);
            } catch (IOException e) {
                System.out.println("An error occurred while trying to open the file: " + e.getMessage());
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
        } else {
            System.out.println("Desktop is not supported on this platform.");
        }
    }
}
