import javax.inject._

import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport
import actors.ProcessRqActor
import scala.concurrent.ExecutionContext

class Module extends AbstractModule with AkkaGuiceSupport {
    override def configure = {
        bindActor[ProcessRqActor]("process-rq-actor")
      }
}