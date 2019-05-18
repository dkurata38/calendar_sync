package calendar_sync

import calendar_sync.batch.CalendarSyncExecutor
import com.amazonaws.services.lambda.runtime.Context
import com.google.inject.Guice

class Handler {
  def handleRequest(input: String, context: Context): Unit = {
    val injector = Guice.createInjector(new CalendarSyncModule)
    val executor = injector.getInstance(classOf[CalendarSyncExecutor])
    executor.execute
  }
}
