package models;

import grabbers.EGrabberType;
import helpers.EImageType;
import helpers.ImageHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.data.validation.Constraints.Required;
import com.avaje.ebean.Model;
import play.db.ebean.Transactional;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Query;

import forms.MovieForm;

@Entity
public class Movie extends Model {

  private static final long serialVersionUID = -2107177668174396511L;

  @Id
  public Long id;

  @Required
  public String title;

  public Boolean hasPoster;

  public Boolean hasBackdrop;

  /**
   * If true the movie has to be reviewed is for mass imports etc importand
   */
  @Column(nullable = false)
  public Boolean hasToBeReviewed = false;

  @Lob
  public String description;

  @Required
  @Column(nullable = false)
  public Integer year;

  public Integer runtime;

  @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "movies")
  public Set<MovieAttribute> attributes;

  @OneToMany(fetch = FetchType.LAZY, mappedBy = "movie")
  public Set<Dvd> dvds;

  public String trailerUrl;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  public EGrabberType grabberType = EGrabberType.NONE;

  //@OneToOne(fetch = FetchType.LAZY)
  //@Column(nullable = false)
  //public Commentable commentable;


  /**
   * Id to the imdb
   */
  public String imdbId;

  /**
   * If the EGrabberType is not null this is the id which is to use to find the movie via the grabber
   */
  public String grabberId;

  /**
   * The finder for the database for searching in the database
   */
  public static Finder<Long, Movie> finder = new Finder<Long, Movie>(Long.class, Movie.class);

  /**
   * This creates a movie from the information of the given {@link forms.dvd.DvdForm}
   * 
   * @param movieForm
   * @return
   * @throws Exception
   */
  public static Movie editOrAddFromForm(final MovieForm movieForm) throws Exception {

    Movie movie = null;

    if (movieForm.movieId != null) {
      movie = Movie.finder.byId(movieForm.movieId);
      if (movie == null) {
        final String message = "No Movie by the id: " + movieForm.movieId + " found !";
        Logger.error(message);
        throw new Exception(message);
      }
    }

    if (movie == null) {
      movie = new Movie();
    }

    movie.title = movieForm.title;
    movie.description = movieForm.plot;
    movie.year = movieForm.year;
    movie.runtime = movieForm.runtime;
    movie.trailerUrl = movieForm.trailerUrl;
    movie.hasToBeReviewed = false;
    movie.imdbId = movieForm.imdbId;
    if(movieForm.grabberType != null && movieForm.grabberType != EGrabberType.NONE && StringUtils.isEmpty(movieForm.grabberId) == false) {
      movie.grabberType = movieForm.grabberType;
      movie.grabberId = movieForm.grabberId;
    } else {
      movie.grabberType = EGrabberType.NONE;
    }

    if (movie.id == null) {
      movie.hasPoster = false;
      movie.hasBackdrop = false;
      movie.save();
    } else {
      Ebean.deleteManyToManyAssociations(movie, "attributes");
    }

    // add the images if we have some :)
    final Boolean newPoster = ImageHelper.createFileFromUrl(movie.id, movieForm.posterUrl, EImageType.POSTER);
    if (movie.hasPoster == false || movie.hasPoster == null) {
      movie.hasPoster = newPoster;
    }

    final Boolean newBackDrop = ImageHelper.createFileFromUrl(movie.id, movieForm.backDropUrl, EImageType.BACKDROP);
    if (movie.hasBackdrop == false || movie.hasBackdrop == null) {
      movie.hasBackdrop = newBackDrop;
    }

    movie.attributes = new HashSet<MovieAttribute>();

    // gather all the genres and add them to the dvd
    final Set<MovieAttribute> genres = MovieAttribute.gatherAndAddAttributes(new HashSet<String>(movieForm.genres), EMovieAttributeType.GENRE);
    movie.attributes.addAll(genres);

    final Set<MovieAttribute> actors = MovieAttribute.gatherAndAddAttributes(new HashSet<String>(movieForm.actors), EMovieAttributeType.ACTOR);
    movie.attributes.addAll(actors);

    Movie.addSingleAttribute(movieForm.series, EMovieAttributeType.MOVIE_SERIES, movie);

    Movie.addSingleAttribute(movieForm.director, EMovieAttributeType.DIRECTOR, movie);

    movie.update();

    return movie;
  }

  /**
   * Adds a single Attribute to the dvd
   * 
   * @param attrToAdd
   * @param attributeType
   * @param movie
   */
  private static void addSingleAttribute(final String attrToAdd, final EMovieAttributeType attributeType, final Movie movie) {
    if (StringUtils.isEmpty(attrToAdd) == true) {
      return;
    }
    final Set<String> attribute = new HashSet<String>();
    attribute.add(attrToAdd);
    final Set<MovieAttribute> dbAttrs = MovieAttribute.gatherAndAddAttributes(attribute, attributeType);
    movie.attributes.addAll(dbAttrs);
  }

  /**
   * Searches all {@link Movie}s by the term and returns a list of the result
   * 
   * @param term
   * @param numberOfResults
   * @return
   */
  @Transactional
  public static List<Movie> searchLike(final String term, final int numberOfResults) {
    final Query<Movie> order = Movie.finder.where().ilike("title", "%" + term + "%").select("id ,title, hasPoster").order("title asc");
    if (numberOfResults <= 0) {
      return order.findList();
    } else {
      return order.findPagedList(0,numberOfResults).getList();
    }
  }

  /**
   * Searches for {@link Movie}s which have the same title, or the where the {@link Dvd} has the same ean nr
   * @param term
   * @param eanNr
   * @return
   */
  public static List<Movie> searchLikeAndAmazoneCode(final String term, final String eanNr) {
    final List<Movie> movies = searchLike(term, 0);

    final List<Dvd> dvds = Dvd.find.where().eq("eanNr", eanNr).findList();
    for (Dvd dvd : dvds) {
      final Long movieId = dvd.movie.id;
      boolean foundMovie = false;
      for(Movie movie : movies) {
        if(movie.id.equals(movieId) == true) {
          foundMovie = true;
          break;
        }
      }
      if(foundMovie == false) {
        movies.add(dvd.movie);
      }
    }

    return movies;

  }

  /**
   * Checks if a movie already exists with the grabberId and the grabberType
   * @param grabberId
   * @param grabberType
   * @return
   */
  public static boolean checkIfMovieWasGrabbedBefore(final String grabberId, final EGrabberType grabberType) {
    if(StringUtils.isEmpty(grabberId) == true || EGrabberType.NONE.equals(grabberType) == true) {
      return false;
    }

    int rowCount = Movie.finder.where().eq("grabberId", grabberId).eq("grabberType", grabberType).findRowCount();
    return rowCount > 0;
  }

  /**
   * Adds a  {@link Comment} to the {@link Movie} with the given id
   * @param movieId
   * @param commentText
   */
  public static Commentable addComment(final Long movieId, final String commentText) {

   /* final Movie movie = find.byId(movieId);
    if(movie == null) {
      if(Logger.isErrorEnabled() == true) {
        Logger.error("Could not find movie with the id: "+movieId);
      }
    }

    if(movie.commentable == null) {
      movie.commentable = new Commentable();
      movie.commentable.save();
    }

    final Comment  comment = new Comment();
    comment.comment = commentText;
    comment.user = User.getCurrentUser();
    movie.commentable.addNewComment(comment);

    movie.save();

    return movie.commentable;*/

    return null;

  }

}
