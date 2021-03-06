package controllers;

import forms.user.UserProfileForm;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.Logger;
import play.data.Form;
import play.i18n.Messages;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import views.html.user.userprofile;

/**
 * User: tuxburner
 * Date: 2/3/13
 * Time: 4:24 PM
 */
@Security.Authenticated(Secured.class)
public class UserProfileController extends Controller {

  /**
   * Displays the user profile mask
   *
   * @return
   */
  public Result showProfile() {

    User currentUser = User.getCurrentUser();
    if(currentUser == null) {
      if(Logger.isErrorEnabled()) {
        Logger.error("No user was found by the username: "+Secured.getUsername());
        return internalServerError();
      }
    }

    if(StringUtils.isEmpty(currentUser.rssAuthKey) == true) {
      final String userRssAuthKey = User.createUserRssAuthKey();
      currentUser.rssAuthKey = userRssAuthKey;
    }

    UserProfileForm userProfileForm = new UserProfileForm();
    userProfileForm.defaultCopyType = currentUser.defaultCopyType;
    userProfileForm.email = currentUser.email;
    userProfileForm.rssAuthKey = currentUser.rssAuthKey;

    return ok(userprofile.render(Form.form(UserProfileForm.class).fill(userProfileForm)));
  }

  public Result updateProfile() {

    final Form<UserProfileForm> form = Form.form(UserProfileForm.class).bindFromRequest();
    if(form.hasErrors()) {
      return Results.badRequest(userprofile.render(form));
    }

    Controller.flash("success", Messages.get("msg.success.profileUpdated"));
    // redirect so the form gets empty and no passwords are written to the form
    return redirect(routes.UserProfileController.showProfile());
  }
}
