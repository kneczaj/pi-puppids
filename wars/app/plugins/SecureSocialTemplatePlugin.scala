package plugins

import play.api.mvc.{RequestHeader, Request}
import play.api.templates.Html
import securesocial.controllers.Registration.RegistrationInfo
import securesocial.controllers.TemplatesPlugin
import securesocial.core.{SecuredRequest, SocialUser}
import play.api.data.Form
import securesocial.core.SecureSocial._
import securesocial.controllers.PasswordChange.ChangeInfo
import views.html.secsocial._

class SecureSocialTemplatePlugin(application: play.Application) extends TemplatesPlugin {

  override def getLoginPage[A](implicit request: Request[A], form: Form[(String, String)],
                               msg: Option[String] = None): Html =
  {
    views.html.secsocial.login(form, msg)
  }

  override def getSignUpPage[A](implicit request: Request[A], form: Form[RegistrationInfo], token: String): Html = {
    views.html.secsocial.Registration.signUp(form, token)
  }

  override def getStartSignUpPage[A](implicit request: Request[A], form: Form[String]): Html = {
    views.html.secsocial.Registration.startSignUp(form)
  }

  override def getStartResetPasswordPage[A](implicit request: Request[A], form: Form[String]): Html = {
    views.html.secsocial.Registration.startResetPassword(form)
  }

  def getResetPasswordPage[A](implicit request: Request[A], form: Form[(String, String)], token: String): Html = {
    views.html.secsocial.Registration.resetPasswordPage(form, token)
  }

  def getPasswordChangePage[A](implicit request: SecuredRequest[A], form: Form[ChangeInfo]):Html = {
    views.html.secsocial.passwordChange(form)
  }

  def getSignUpEmail(token: String)(implicit request: RequestHeader): String = {
    views.html.secsocial.mails.signUpEmail(token).body
  }

  def getAlreadyRegisteredEmail(user: SocialUser)(implicit request: RequestHeader): String = {
    views.html.secsocial.mails.alreadyRegisteredEmail(user).body
  }

  def getWelcomeEmail(user: SocialUser)(implicit request: RequestHeader): String = {
    views.html.secsocial.mails.welcomeEmail(user).body
  }

  def getUnknownEmailNotice()(implicit request: RequestHeader): String = {
    views.html.secsocial.mails.unknownEmailNotice(request).body
  }

  def getSendPasswordResetEmail(user: SocialUser, token: String)(implicit request: RequestHeader): String = {
    views.html.secsocial.mails.passwordResetEmail(user, token).body
  }

  def getPasswordChangedNoticeEmail(user: SocialUser)(implicit request: RequestHeader): String = {
    views.html.secsocial.mails.passwordChangedNotice(user).body
  }
}