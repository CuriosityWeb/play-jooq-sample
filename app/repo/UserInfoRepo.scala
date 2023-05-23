package repo

import akka.Done
import model.UserInfo

import java.time.LocalDate
import scala.concurrent.Future

trait UserInfoRepo {

  def add(userInfo: UserInfo): Future[Done]

  def addBatch(usersInfo: Set[UserInfo]): Future[Done]

  def get(id: String): Future[Option[UserInfo]]

  def getAll: Future[Set[UserInfo]]

  def updateFName(id: String, fName: String): Future[Done]

  def updateLName(id: String, fName: String): Future[Done]

  def updateAge(id: String, date: LocalDate): Future[Done]

  def delete(id: String): Future[Done]

  def deleteAll: Future[Done]
}
