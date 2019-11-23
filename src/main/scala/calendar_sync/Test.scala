package calendar_sync

import calendar_sync.application.coordinator.RefreshCredentialCoordinator
import com.google.inject.Guice


object Test extends App {
  val injector = Guice.createInjector(new CalendarSyncModule())
  val refreshCredentialCoordinator = injector.getInstance(classOf[RefreshCredentialCoordinator])

  val result = refreshCredentialCoordinator.refresh
  result.left.map(e => e.printStackTrace())
}
