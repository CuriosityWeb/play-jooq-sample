package repo

import model.UserInfo
import repo.jooq.tables.records.UserInfoRecord

object Implicits {

  implicit class UserInfoToUserInfoRecord(userInfo: UserInfo) {

    def toUserInfoRecord: UserInfoRecord = userInfo match {
      case UserInfo(id, fName, lName, age) => new UserInfoRecord(id, fName, lName, age)
    }
  }

  implicit class UserInfoRecordToUserInfo(record: UserInfoRecord) {

    def toUserInfo: UserInfo = UserInfo(record.getId, record.getFName, record.getLName, record.getAge)
  }
}
