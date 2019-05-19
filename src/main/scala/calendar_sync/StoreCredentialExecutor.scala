package calendar_sync

import calendar_sync.application.coordinator.StoreCredentialCoordinator
import com.google.inject.Guice

object StoreCredentialExecutor extends App {
  val injector = Guice.createInjector(new CalendarSyncModule)
  val coordinator = injector.getInstance(classOf[StoreCredentialCoordinator])

  coordinator.store()
}
