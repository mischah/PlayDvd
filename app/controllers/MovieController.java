package controllers;

import grabbers.EGrabberType;
import grabbers.GrabberException;
import grabbers.IInfoGrabber;
import helpers.RequestToCollectionHelper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import models.EMovieAttributeType;
import models.Movie;
import models.MovieAttribute;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import views.html.movie.movieform;
import forms.GrabberInfoForm;
import forms.MovieForm;

/**
 * This {@link Controller} handles all the edit and add {@link Movie} magic
 * 
 * @author tuxburner
 * 
 */
@Security.Authenticated(Secured.class)
public class MovieController extends Controller {

  /**
   * Displays the {@link MovieForm} to the user in the add mode
   * 
   * @return
   */
  public static Result showAddMovieForm() {
    final Form<MovieForm> form = Controller.form(MovieForm.class);
    return Results.ok(movieform.render(form.fill(new MovieForm()), MovieController.getMovieSeries(null), DvdController.DVD_FORM_ADD_MODE));
  }

  /**
   * Shows the edit {@link Movie} form
   * 
   * @return
   */
  public static Result showEditMovieForm(final Long movieId) {

    final Movie movie = Movie.find.byId(movieId);

    if (movie == null) {
      final String message = "No Movie found to edit under the id: " + movieId;
      Logger.error(message);
      return Results.badRequest(message);
    }

    final Form<MovieForm> form = Controller.form(MovieForm.class).fill(MovieForm.movieToForm(movie));
    return Results.ok(movieform.render(form, MovieController.getMovieSeries(form), DvdController.DVD_FORM_EDIT_MODE));
  }

  /**
   * This is called when the user submits the add Dvd Form
   * 
   * @return
   */
  public static Result addOrEditMovie(final String mode) {

    final Map<String, String> map = RequestToCollectionHelper.requestToFormMap(Controller.request(), "actors", "genres");
    final Form<MovieForm> movieForm = new Form<MovieForm>(MovieForm.class).bind(map);

    if (movieForm.hasErrors()) {
      return Results.badRequest(movieform.render(movieForm, MovieController.getMovieSeries(movieForm), mode));
    } else {
      try {
        final Movie editOrAddFromForm = Movie.editOrAddFromForm(movieForm.get());
        final ObjectNode result = Json.newObject();
        result.put("id", editOrAddFromForm.id);
        result.put("title", editOrAddFromForm.title);
        return Results.ok(result);
      } catch (final Exception e) {
        e.printStackTrace();
        return Results.badRequest(movieform.render(movieForm, MovieController.getMovieSeries(movieForm), mode));
      }

    }
  }

  /**
   * When the user selected a movie from the tmdb popup we fill out the form and
   * display it
   * 
   * @param mode
   *          mode if we add or edit the movie
   * @return
   */
  public static Result addMovieByGrabberId(final String mode, final String grabberType) {

    try {

      final Form<GrabberInfoForm> grabberInfoForm = Controller.form(GrabberInfoForm.class).bindFromRequest();

      final IInfoGrabber grabber = InfoGrabberController.getGrabber(EGrabberType.valueOf(grabberType));

      final MovieForm movieForm = grabber.filleInfoToMovieForm(grabberInfoForm.get());

      if (grabberInfoForm.get().movieToEditId != null) {
        movieForm.movieId = grabberInfoForm.get().movieToEditId;
      }

      final Form<MovieForm> form = Controller.form(MovieForm.class);

      return Results.ok(movieform.render(form.fill(movieForm), MovieController.getMovieSeries(form), mode));
    } catch (final GrabberException e) {
      return Results.badRequest("Internal Error happend");
    }
  }

  /**
   * Get all series of a movie
   * 
   * @param form
   * @return
   */
  private static List<String> getMovieSeries(final Form<MovieForm> movieForm) {

    final String series = movieForm.field("series").value();

    final List<String> allByTypeAsValue = MovieAttribute.getAllByTypeAsValue(EMovieAttributeType.MOVIE_SERIES);
    if (StringUtils.isEmpty(series) == false && allByTypeAsValue.contains(series) == false) {
      allByTypeAsValue.add(series);
      Collections.sort(allByTypeAsValue);
    }

    return allByTypeAsValue;

  }

}
