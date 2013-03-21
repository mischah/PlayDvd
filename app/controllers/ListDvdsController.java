package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.Page;
import forms.dvd.DvdSearchFrom;
import models.CopyReservation;
import models.Dvd;
import org.apache.commons.lang.StringUtils;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import views.html.dashboard.listdvds;

import java.util.List;

@Security.Authenticated(Secured.class)
public class ListDvdsController extends Controller {

  /**
   * Lists all the dvds
   * 
   * @return
   */
  public static Result listdvds(final Integer page) {

    final DvdSearchFrom currentSearchForm = DvdSearchFrom.getCurrentSearchForm();

    if (page != null) {
      currentSearchForm.currentPage = page;
    }

    return ListDvdsController.returnList(currentSearchForm);
  }

  /**
   * list all dvds we have
   * 
   * @return
   */
  public static Result listAlldvds() {
    final DvdSearchFrom dvdSearchFrom = new DvdSearchFrom();
    return ListDvdsController.returnList(dvdSearchFrom);
  }

  /**
   * Lists the dvds by the user
   * 
   * @param fromUserName
   * @return
   */
  public static Result listByUser(final String fromUserName, final Integer page) {
    if (StringUtils.isEmpty(fromUserName)) {
      return Results.internalServerError("No Username given");
    }

    final DvdSearchFrom dvdListFrom = new DvdSearchFrom();
    dvdListFrom.userName = fromUserName;

    return ListDvdsController.returnList(dvdListFrom);
  }

  /**
   * List the dvds by the genre
   * 
   * @param genreName
   * @return
   */
  public static Result listByGenre(final String genreName, final Integer pageNr) {
    if (StringUtils.isEmpty(genreName)) {
      return Results.internalServerError("No Genrename given");
    }

    final DvdSearchFrom dvdListFrom = new DvdSearchFrom();
    dvdListFrom.genre = genreName;

    return ListDvdsController.returnList(dvdListFrom);
  }

  /**
   * List the dvds by the actor
   * 
   * @param actorName
   * @return
   */
  public static Result listByActor(final String actorName) {
    if (StringUtils.isEmpty(actorName)) {
      return Results.internalServerError("No actorname given");
    }

    final DvdSearchFrom dvdListFrom = new DvdSearchFrom();
    dvdListFrom.actor = actorName;

    return ListDvdsController.returnList(dvdListFrom);
  }

  /**
   * Lists all {@link Dvd}s by the given director
   * 
   * @param directorName
   * @return
   */
  public static Result listByDirector(final String directorName) {
    if (StringUtils.isEmpty(directorName)) {
      return Results.internalServerError("No directorname given");
    }

    final DvdSearchFrom dvdListFrom = new DvdSearchFrom();
    dvdListFrom.director = directorName;

    return ListDvdsController.returnList(dvdListFrom);
  }

  /**
   * Lists all the dvd the user lend to somebody
   * 
   * @return
   */
  public static Result listLendDvd() {

    final DvdSearchFrom dvdListFrom = new DvdSearchFrom();
    dvdListFrom.lendDvd = true;
    dvdListFrom.userName = Secured.getUsername();

    return ListDvdsController.returnList(dvdListFrom);
  }

  public static Result listReviewMovies() {
    final DvdSearchFrom dvdListFrom = new DvdSearchFrom();
    dvdListFrom.moviesToReview = true;

    return ListDvdsController.returnList(dvdListFrom);

  }

  public static Result searchDvd() {
    final String[] strings = Controller.request().queryString().get("searchFor");
    if (strings == null || strings.length != 1) {
      return ListDvdsController.listAlldvds();
    } else {
      final DvdSearchFrom listFrom = new DvdSearchFrom();
      listFrom.searchFor = strings[0];
      return ListDvdsController.returnList(listFrom);
    }

  }

  /**
   * This is called when the user applys the search form above the list of
   * movies
   * 
   * @return
   */
  public static Result applySearchForm() {

    final Form<DvdSearchFrom> form = Form.form(DvdSearchFrom.class).bindFromRequest();

    return ListDvdsController.returnList(form.get());
  }

  /**
   * Returns the dvds for the template
   * 
   * @param dvdSearchFrom
   * @return
   */
  private static Result returnList(final DvdSearchFrom dvdSearchFrom) {


    final String username = Controller.request().username();
    DvdSearchFrom.setCurrentSearchForm(dvdSearchFrom);

    final Form<DvdSearchFrom> form = Form.form(DvdSearchFrom.class);

    final Page<Dvd> dvdsByForm = Dvd.getDvdsBySearchForm(dvdSearchFrom);
    return Results.ok(listdvds.render(new DvdPage(dvdsByForm), form.fill(dvdSearchFrom), username,ShoppingCartController.getShoppingCartFromCache()));
  }
}
