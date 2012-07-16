package forms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.EMovieAttributeType;
import models.Movie;
import models.MovieAttribute;
import play.data.validation.Constraints.Required;

/**
 * This is used when the user adds or edits a new movie
 * 
 * @author tuxburner
 * 
 */
public class MovieForm {

  public Long movieId;

  @Required
  public String title;

  @Required
  public Integer year;

  public Integer runtime;

  public String plot;

  public String posterUrl;

  public String backDropUrl;

  public List<String> genres = new ArrayList<String>();

  public List<String> actors = new ArrayList<String>();

  public String director;

  public String trailerUrl;

  /**
   * This describes in which series the movie is for example Alien, Terminator,
   * Indiana Jones are Series of movies
   */
  public String series;

  public Boolean hasBackdrop;

  public Boolean hasPoster;

  /**
   * Transforms a {@link Movie} to a {@link MovieForm} for editing the dvd in
   * the frontend
   * 
   * @param dvd
   * @return
   */
  public static MovieForm movieToForm(final Movie movie) {
    final MovieForm movieForm = new MovieForm();

    movieForm.movieId = movie.id;
    movieForm.title = movie.title;
    movieForm.year = movie.year;
    movieForm.runtime = movie.runtime;
    movieForm.plot = movie.description;
    movieForm.trailerUrl = movie.trailerUrl;

    movieForm.hasBackdrop = movie.hasBackdrop;
    movieForm.hasPoster = movie.hasPoster;

    final Set<MovieAttribute> attributes = movie.attributes;
    for (final MovieAttribute movieAttibute : attributes) {
      if (EMovieAttributeType.GENRE.equals(movieAttibute.attributeType)) {
        movieForm.genres.add(movieAttibute.value);
      }

      if (EMovieAttributeType.ACTOR.equals(movieAttibute.attributeType)) {
        movieForm.actors.add(movieAttibute.value);
      }

      if (EMovieAttributeType.DIRECTOR.equals(movieAttibute.attributeType)) {
        movieForm.director = movieAttibute.value;
      }

      if (EMovieAttributeType.MOVIE_SERIES.equals(movieAttibute.attributeType)) {
        movieForm.series = movieAttibute.value;
      }
    }

    Collections.sort(movieForm.actors);
    Collections.sort(movieForm.genres);

    return movieForm;
  }

  /**
   * Gathers all attributes for the given type from the db and checks if the dvd
   * 
   * @param attributeType
   * @return
   */
  public List<MovieFormAttribute> getDvdAttributes(final EMovieAttributeType attributeType, final Map<Integer, String> formVals) {

    final List<MovieFormAttribute> result = new ArrayList<MovieFormAttribute>();

    // merge wit the attributes from the database
    final List<MovieAttribute> genres = MovieAttribute.getAllByType(attributeType);
    final Set<String> newGenreMatchedWithDb = new HashSet<String>();
    for (final MovieAttribute movieAttibute : genres) {

      final String value = movieAttibute.value;
      boolean selected = false;
      for (final String formAttrs : formVals.values()) {
        if (value.equals(formAttrs)) {
          newGenreMatchedWithDb.add(formAttrs);
          selected = true;
          break;
        }
      }

      result.add(new MovieFormAttribute(selected, value));
    }

    // add all attributes which where not in the database :)
    for (final String genre : formVals.values()) {
      if (newGenreMatchedWithDb.contains(genre) == false) {
        result.add(new MovieFormAttribute(true, genre));
      }
    }

    return result;
  }

  /**
   * This returns the values as a , seperated string
   * 
   * @param values
   * @return
   */
  public final String getDvdFormAttributesAsString(final List<String> values) {
    String returnVal = "";

    String sep = "";
    for (final String value : values) {
      returnVal += sep + value;
      sep = ",";
    }

    return returnVal;
  }
}
