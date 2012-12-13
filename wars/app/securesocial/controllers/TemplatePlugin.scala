import play.api.data.Form
import play.api.mvc.Request
import play.api.mvc.RequestHeader
import play.api.templates.Html
import securesocial.controllers.PasswordChange.ChangeInfo
import securesocial.controllers.Registration.RegistrationInfo
import securesocial.controllers.TemplatesPlugin
import securesocial.core.SecuredRequest
import securesocial.core.SocialUser
import views.html.secsocial._

class TemplatePlugin(application: Application) extends TemplatesPlugin {
  
  def getLoginPage[A](implicit request: Request[A], form: Form[(String, String)],
                               msg: Option[String]): 
                               Html = { 
    views.html.secsocial.login(form, msg)
  }

  override def getSignUpPage[A](implicit request: Request[A], form: Form[RegistrationInfo], token: String): Html = {
    views.html.secsocial.registration.signUp(form, token)
  }

  override def getStartSignUpPage[A](implicit request: Request[A], form: Form[String]): Html = {
    views.html.secsocial.registration.startSignUp(form)
  }

  override def getStartResetPasswordPage[A](implicit request: Request[A], form: Form[String]): Html = {
    views.html.secsocial.registration.startResetPassword(form)
  }

  def getResetPasswordPage[A](implicit request: SecuredRequest[A], form: Form[(String, String)], token: String): Html = {
    views.html.secsocial.registration.resetPasswordPage(form, token)
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
    views.html.secsocial.mails.unknownEmailNotice().body
  }

  def getSendPasswordResetEmail(user: SocialUser, token: String)(implicit request: RequestHeader): String = {
    views.html.secsocial.mails.passwordResetEmail(user, token).body
  }

  def getPasswordChangedNoticeEmail(user: SocialUser)(implicit request: RequestHeader): String = {
    views.html.secsocial.mails.passwordChangedNotice(user).body
  }
}