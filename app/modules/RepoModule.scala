package modules

import com.google.inject.AbstractModule
import repo.{UserInfoRepo, UserInfoRepoImpl}

final class RepoModule extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[UserInfoRepo]).to(classOf[UserInfoRepoImpl])
  }
}
