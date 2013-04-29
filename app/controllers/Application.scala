package controllers

import
  play.api.mvc.{ Action, Controller }

object Application extends Controller {

  def editor = Action {
    implicit request =>
      Ok(views.html.editor())
  }

  def minimal = Action {
    implicit request =>
      Ok(views.html.examples.minimal())
  }

}
