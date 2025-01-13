package mymovies.bll;

import mymovies.be.Movie;
import mymovies.dal.MovieRepository;

public class MovieManager {
    final private MovieRepository movieRepository = new MovieRepository();

    public void addMovie(Movie movie) {
        movieRepository.create(movie);
    }
}
