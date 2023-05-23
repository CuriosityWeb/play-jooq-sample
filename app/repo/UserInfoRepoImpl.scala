package repo

import akka.Done
import model.UserInfo
import org.jooq.DSLContext
import org.jooq.impl.DSL
import play.api.db.Database
import repo.Implicits._
import repo.jooq.tables.UserInfo.USER_INFO

import java.sql.SQLException
import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.jdk.CollectionConverters._
import scala.jdk.FutureConverters._
import scala.util.{Failure, Success}

final class UserInfoRepoImpl @Inject()(db: Database)(implicit ec: ExecutionContext) extends UserInfoRepo {

  private val SQLDialect: org.jooq.SQLDialect = org.jooq.SQLDialect.H2

  override def add(userInfo: UserInfo): Future[Done] = execute(_.insertInto(USER_INFO)
    .set(userInfo.toUserInfoRecord)
    .executeAsync()
    .asScala
    .transform {
      case Success(value) if value == 1 => Success(Done)
      case Success(value) => Failure(new SQLException(s"Unexpected value: {$value}"))
      case Failure(exception) => Failure(exception)
    })

  override def addBatch(usersInfo: Set[UserInfo]): Future[Done] = execute(ctx => ctx
    .batch(usersInfo.map(info => ctx.insertInto(USER_INFO).set(info.toUserInfoRecord)).asJava)
    .executeAsync()
    .asScala
    .map(_ => Done))

  override def get(id: String): Future[Option[UserInfo]] = execute(_.selectFrom(USER_INFO)
    .where(USER_INFO.ID.equal(id))
    .fetchAsync()
    .asScala
    .map(_.asScala)
    .map(_.find(_.getId == id))
    .map(_.map(_.toUserInfo)))

  override def getAll: Future[Set[UserInfo]] = execute(_.fetchAsync(USER_INFO)
    .asScala
    .map(_.asScala)
    .map(_.map(_.toUserInfo))
    .map(_.toSet))

  override def updateFName(id: String, fName: String): Future[Done] = execute(_.update(USER_INFO)
    .set(USER_INFO.F_NAME, fName)
    .where(USER_INFO.ID.equal(id))
    .executeAsync()
    .asScala
    .transform {
      case Success(value) if value == 1 => Success(Done)
      case Success(value) => Failure(new SQLException(s"Unexpected value: {$value}, possible reason: user with id: {$id} may not be present in database"))
      case Failure(exception) => Failure(exception)
    })

  override def updateLName(id: String, fName: String): Future[Done] = execute(_.update(USER_INFO)
    .set(USER_INFO.L_NAME, fName)
    .where(USER_INFO.ID.equal(id))
    .executeAsync()
    .asScala
    .transform {
      case Success(value) if value == 1 => Success(Done)
      case Success(value) => Failure(new SQLException(s"Unexpected value: {$value}, possible reason: user with id: {$id} may not be present in database"))
      case Failure(exception) => Failure(exception)
    })

  override def updateAge(id: String, date: LocalDate): Future[Done] = execute(_.update(USER_INFO)
    .set(USER_INFO.AGE, date)
    .where(USER_INFO.ID.equal(id))
    .executeAsync()
    .asScala
    .transform {
      case Success(value) if value == 1 => Success(Done)
      case Success(value) => Failure(new SQLException(s"Unexpected value: {$value}, possible reason: user with id: {$id} may not be present in database"))
      case Failure(exception) => Failure(exception)
    })

  override def delete(id: String): Future[Done] = execute(_.delete(USER_INFO)
    .where(USER_INFO.ID.equal(id))
    .executeAsync()
    .asScala
    .map(_ => Done))

  override def deleteAll: Future[Done] = execute(_.delete(USER_INFO)
    .executeAsync()
    .asScala
    .map(_ => Done))

  private def execute[T](fxn: DSLContext => Future[T]): Future[T] = {
    val conn = db.getConnection()
    val future = fxn(DSL.using(conn, SQLDialect))
    future.onComplete(_ => conn.close())
    future
  }
}
