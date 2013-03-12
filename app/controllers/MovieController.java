package controllers;

import com.google.gson.Gson;
import forms.MovieForm;
import forms.grabbers.GrabberInfoForm;
import grabbers.EGrabberType;
import grabbers.GrabberException;
import grabbers.IInfoGrabber;
import helpers.RequestToCollectionHelper;
import jsannotation.JSRoute;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This {@link Controller} handles all the edit and add {@link Movie} magic
 *
 * @author tuxburner
 */
@Security.Authenticated(Secured.class)
public class MovieController extends Controller {

  /**
   * Displays the {@link MovieForm} to the user in the add mode
   *
   * @return
   */
  @JSRoute
  public static Result showAddMovieForm() {
    final Form<MovieForm> form = Form.form(MovieForm.class);
    return Results.ok(movieform.render(form.fill(new MovieForm()), DvdController.DVD_FORM_ADD_MODE));
  }

  /**
   * Shows the edit {@link Movie} form
   *
   * @return
   */
  @JSRoute
  public static Result showEditMovieForm(final Long movieId) {

    final Movie movie = Movie.find.byId(movieId);

    if (movie == null) {
      final String message = "No Movie found to edit under the id: " + movieId;
      Logger.error(message);
      return Results.badRequest(message);
    }

    final Form<MovieForm> form = Form.form(MovieForm.class).fill(MovieForm.movieToForm(movie));
    return Results.ok(movieform.render(form, DvdController.DVD_FORM_EDIT_MODE));
  }

  /**
   * This is called when the user submits the add Dvd Form
   *
   * @return
   */
  @JSRoute
  public static Result addOrEditMovie(final String mode) {

    final Map<String, String> map = RequestToCollectionHelper.requestToFormMap(Controller.request(), "actors", "genres");
    final Form<MovieForm> movieForm = new Form<MovieForm>(MovieForm.class).bind(map);

    if (movieForm.hasErrors()) {
      return Results.badRequest(movieform.render(movieForm, mode));
    } else {
      try {
        final Movie editOrAddFromForm = Movie.editOrAddFromForm(movieForm.get());
        final ObjectNode result = Json.newObject();
        result.put("id", editOrAddFromForm.id);
        result.put("title", editOrAddFromForm.title);
        result.put("hasPoster", editOrAddFromForm.hasPoster);
        return Results.ok(result);
      } catch (final Exception e) {
        e.printStackTrace();
        return Results.badRequest(movieform.render(movieForm, mode));
      }
    }
  }

  /**
   * When the user selected a movie from the tmdb popup we fill out the form and
   * display it
   *
   * @param mode mode if we add or edit the movie
   * @return
   */
  @JSRoute
  public static Result addMovieByGrabberId(final String mode, final String grabberType) {

    try {

      final Form<GrabberInfoForm> grabberInfoForm = Form.form(GrabberInfoForm.class).bindFromRequest();

      final IInfoGrabber grabber = InfoGrabberController.getGrabber(EGrabberType.valueOf(grabberType));

      final MovieForm movieForm = grabber.fillInfoToMovieForm(grabberInfoForm.get());

      if (grabberInfoForm.get().movieToEditId != null) {
        movieForm.movieId = grabberInfoForm.get().movieToEditId;
      }

      final Form<MovieForm> form = Form.form(MovieForm.class);

      return Results.ok(movieform.render(form.fill(movieForm), mode));
    } catch (final GrabberException e) {
      if (Logger.isErrorEnabled()) {
        Logger.error("Internal Error happened", e);
      }
      return Results.badRequest("Internal Error happened");
    }
  }

  /**
   * Searches for movies
   *
   * @param term
   * @return
   */
  @JSRoute
  public static Result searchMoviesForDvdSelect(final String term) {

    final List<MovieSelect2Value> result = new ArrayList<MovieSelect2Value>();

    if (StringUtils.isEmpty(term) == false) {
      final List<Movie> searchLike = Movie.searchLike(term, 20);
      for (final Movie movie : searchLike) {
        result.add(new MovieSelect2Value(movie));
      }
    }

    final Gson gson = new Gson();

    return Results.ok(gson.toJson(result));
  }

  /**
   * Searches for {@link MovieAttribute} returns a json with
   * {@link MovieAttribute#pk} and {@link MovieAttribute#value}
   *
   * @param term
   * @param attrType
   * @return
   */
  @JSRoute
  public static Result searchForMovieAttribute(final String term, final String attrType) {
    try {
      final EMovieAttributeType eattrType = EMovieAttributeType.valueOf(attrType);
      final String result = MovieAttribute.searchAvaibleAttributesAsJson(eattrType, term);
      return Results.ok(result);
    } catch (final Exception e) {
      Logger.error("An error happend while getting: " + attrType + " " + EMovieAttributeType.class.getName() + " with search term: " + term, e);
    }

    return Results.badRequest();
  }

  /**
   * Checks if the movie already exists by the grabberId and the given grabberType
   *
   * @param grabberId
   * @param grabberType
   * @return
   */
  @JSRoute
  public static Result checkIfMovieAlreadyExists(final String grabberId, final String grabberType) {
    final Gson gson = new Gson();

    boolean movieExists = Movie.checkIfMovieWasGrabbedBefore(grabberId, EGrabberType.valueOf(grabberType));

    return ok(gson.toJson(movieExists));
  }

}
