package controllers;

import forms.user.LostPasswordForm;
import forms.user.PasswordResetForm;
import helpers.MailerHelper;
import models.User;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Results;
import views.html.user.lostpassword;
import views.html.user.passwordreset;

import java.util.UUID;

/**
 * User: tuxburner
 * Date: 2/4/13
 * Time: 1:48 AM
 */
public class PasswordResetController extends Controller {

  /**
   * Displays the user a simple form where he can insert his mail address
   *
   * @return
   */
  public static Result showPasswordForget() {
    if (MailerHelper.mailerActive() == false) {
      return Controller.internalServerError("Cannot display this form.");
    }

    return ok(lostpassword.render(Controller.form(LostPasswordForm.class)));
  }

  /**
   * Checks if the {@link models.User} exists and if so it sends a password reset mail to the mail the user belongs to
   *
   * @return
   */
  public static Result sendPasswordForget() {

    Form<LostPasswordForm> form = Controller.form(LostPasswordForm.class).bindFromRequest();
    flash("success", "Please check your email.");
    if (form.hasErrors() == false && form.hasGlobalErrors() == false) {


      User userByName = User.getUserByName(form.get().username);
      if (userByName == null) {
        if (Logger.isErrorEnabled() == true) {
          Logger.error("A user tries to reset his password with an username (" + form.get().username + ") which does not exists.");
          return redirect(routes.PasswordResetController.showPasswordForget());
        }
      }


      userByName.passwordResetToken = UUID.randomUUID().toString();
      userByName.update();

      final StringBuffer sb = new StringBuffer("Hello ");
      sb.append(userByName.userName);
      sb.append("\n");
      sb.append("You requested to reset the password for the PlayDvd database please click the link to reset the password:");
      sb.append("\n\t");
      final String activationUrl = routes.PasswordResetController.showPasswordReset(userByName.passwordResetToken).absoluteURL(request());
      sb.append(activationUrl);

      if (Logger.isDebugEnabled() == true) {
        Logger.debug("Email send to: " + userByName.email + " with activation code: " + activationUrl);
      }

      MailerHelper.sendMail("Password reset by PlayDvd", userByName.email, sb.toString(), false);
    }

    return redirect(routes.PasswordResetController.showPasswordForget());
  }

  /**
   * Displays the password reset form
   *
   * @param token
   * @return
   */
  public static Result showPasswordReset(final String token) {


    if (StringUtils.isEmpty(token) == true) {
      return redirect(routes.Application.index());
    }

    return ok(passwordreset.render(Controller.form(PasswordResetForm.class), token));
  }

  /**
   * Checks if an {@link User} with the token exists and if so resets the password
   *
   * @param token
   * @return
   */
  public static Result passwordReset(final String token) {

    if (StringUtils.isEmpty(token) == true) {
      return redirect(routes.Application.index());
    }

    Form<PasswordResetForm> passwordResetForm = Controller.form(PasswordResetForm.class).bindFromRequest();
    if (passwordResetForm.hasErrors()) {
      return Results.badRequest(passwordreset.render(passwordResetForm, token));
    }

    User userByResetToken = User.getUserByResetToken(token);
    if (userByResetToken != null) {
      userByResetToken.password = User.cryptPassword(passwordResetForm.get().password);
      userByResetToken.update();
    }

    flash("success", "Password was changed.");
    return redirect(routes.RegisterLoginController.login());
  }
}