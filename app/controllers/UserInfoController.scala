package controllers

import akka.stream.scaladsl.Source
import model.UserInfo
import model.UserInfo._
import play.api._
import play.api.libs.json.Json
import play.api.mvc._
import repo.UserInfoRepo

import java.time.LocalDate
import java.util.UUID
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

@Singleton
final class UserInfoController @Inject()(cc: ControllerComponents)(userInfoRepo: UserInfoRepo)(
  implicit ec: ExecutionContext) extends AbstractController(cc) with Logging {

  def addUser(): Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson.flatMap(_.validate[UserInfo].asOpt) match {
      case Some(userInfo@UserInfo(id, _, _, _)) => userInfoRepo.add(userInfo) transform {
        case Success(_) => Success(Created(s"User with id: {$id}, added to system."))

        case Failure(exception) if exception.getMessage.contains("primary key violation") =>
          Success(Conflict(s"User with id: {$id}, already present in the database."))

        case Failure(exception) =>
          logger.error("AddUserInfo API", exception)
          Success(InternalServerError(exception.getMessage))
      }

      case None => Future.successful(BadRequest("Malformed request body."))
    }
  }

  def addRandomUserInfo(count: Int): Action[AnyContent] = Action.async {
    userInfoRepo.addBatch(Set.tabulate(count)(index =>
      UserInfo(s"RandomId-${UUID.randomUUID().toString}", s"RandomFirstName-$index", s"RandomLastName-$index", LocalDate.now()))) transform {
      case Success(_) => Success(Ok(s"Added $count random users info records."))

      case Failure(exception) =>
        logger.error("AddRandomUserInfo API", exception)
        Success(InternalServerError(exception.getMessage))
    }
  }

  def getUser(id: String): Action[AnyContent] = Action.async {
    userInfoRepo.get(id) transform {
      case Success(Some(userInfo)) => Success(Ok(Json.toJson(userInfo)))

      case Success(None) => Success(NotFound(s"UserInfo for id: {$id}, not found in the database."))

      case Failure(exception) =>
        logger.error("GetUserInfo API", exception)
        Success(InternalServerError(exception.getMessage))
    }
  }

  def getAll: Action[AnyContent] = Action.async {
    userInfoRepo.getAll transform {
      case Success(infos) => Success(Ok(Json.toJson(infos)))

      case Failure(exception) =>
        logger.error("GetAllUserInfo API", exception)
        Success(InternalServerError(exception.getMessage))
    }
  }

  def updateFName(id: String, fName: String): Action[AnyContent] = Action.async {
    userInfoRepo.updateFName(id, fName) transform {
      case Success(_) => Success(Ok("Updated first name."))

      case Failure(exception) =>
        logger.error("UpdateFName API", exception)
        Success(InternalServerError(exception.getMessage))
    }
  }

  def updateLName(id: String, lName: String): Action[AnyContent] = Action.async {
    userInfoRepo.updateLName(id, lName) transform {
      case Success(_) => Success(Ok("Updated last name."))

      case Failure(exception) =>
        logger.error("UpdateLName API", exception)
        Success(InternalServerError(exception.getMessage))
    }
  }

  def updateAge(id: String, age: String): Action[AnyContent] = Action.async {
    userInfoRepo.updateAge(id, LocalDate.parse(age)) transform {
      case Success(_) => Success(Ok("Updated age."))

      case Failure(exception) =>
        logger.error("UpdateAge API", exception)
        Success(InternalServerError(exception.getMessage))
    }
  }

  def delete(id: String): Action[AnyContent] = Action.async {
    userInfoRepo.delete(id) transform {
      case Success(_) => Success(Ok(s"Deleted user with id: {$id}."))

      case Failure(exception) =>
        logger.error("DeleteUserInfo API", exception)
        Success(InternalServerError(exception.getMessage))
    }
  }

  def deleteAll(): Action[AnyContent] = Action.async {
    userInfoRepo.deleteAll transform {
      case Success(_) => Success(Ok(s"Deleted all user info."))

      case Failure(exception) =>
        logger.error("DeleteAllUserInfo API", exception)
        Success(InternalServerError(exception.getMessage))
    }
  }
}
