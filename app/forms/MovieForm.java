package forms;

import java.util.*;

import grabbers.EGrabberType;
import models.EMovieAttributeType;
import models.Movie;
import models.MovieAttribute;

import org.apache.commons.lang.StringUtils;

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

  public String grabberId;

  public EGrabberType grabberType = EGrabberType.NONE;

  public String imdbId;

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
   * @param movie
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
    movieForm.imdbId = movie.imdbId;
    if(EGrabberType.NONE.equals(movie.grabberType) == false && StringUtils.isEmpty(movie.grabberId) == false) {
      movieForm.grabberType = movie.grabberType;
      movieForm.grabberId = movie.grabberId;
    }

    final Set<String> genreSet = new HashSet<String>();
    final Set<String> actorSet = new HashSet<String>();

    final Set<MovieAttribute> attributes = movie.attributes;
    for (final MovieAttribute movieAttibute : attributes) {
      if (EMovieAttributeType.GENRE.equals(movieAttibute.attributeType)) {
        genreSet.add(movieAttibute.value.trim());
      }

      if (EMovieAttributeType.ACTOR.equals(movieAttibute.attributeType)) {
        actorSet .add(movieAttibute.value);
      }

      if (EMovieAttributeType.DIRECTOR.equals(movieAttibute.attributeType)) {
        movieForm.director = movieAttibute.value;
      }

      if (EMovieAttributeType.MOVIE_SERIES.equals(movieAttibute.attributeType)) {
        movieForm.series = movieAttibute.value;
      }
    }


    movieForm.actors.addAll(actorSet);
    Collections.sort(movieForm.actors);

    movieForm.genres.addAll(genreSet);
    Collections.sort(movieForm.genres);

    return movieForm;
  }

  /**
   * This returns the values as a , seperated string
   * 
   * @param values
   * @return
   */
  public final static String getDvdFormAttributesAsString(final String values) {
    return StringUtils.replace(StringUtils.replaceChars(values, "[]", ""),", ",",");
  }
}
