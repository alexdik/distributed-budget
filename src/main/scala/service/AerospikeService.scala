package service

import akka.actor.{Actor, ActorSystem, Props}
import cache.DbCache
import com.typesafe.config.Config
import com.typesafe.scalalogging.StrictLogging
import dao.AerospikeDao
import service.AerospikeServiceEvents._
import scala.concurrent.duration._

object AerospikeServiceProps extends ServiceProps {
  override def namespace: String = "test"

  override def statisticsSet: String = "statistics"
}

object AerospikeServiceEvents {

  //events
  case class GetData(clientId: Int)

  case object SynchronizeData

  case class MakePurchase(id: Int, value: Int)

  case class AddOrUpdateData(updateData: Map[Int, Int])

  case object Stop

}

class AerospikeService(config: Config, system: ActorSystem) extends Actor with StrictLogging {

  import system.dispatcher

  val dao = new AerospikeDao(config, AerospikeServiceProps)
  val dbCache = system.actorOf(Props(new DbCache))
  val synchronizer = context.system.scheduler.schedule(1 seconds, 1 seconds, this.self, SynchronizeData)

  def receive: Receive = {
    case GetData(clientId) => sender ! dao.getData(clientId)
    case purchase: MakePurchase => dbCache ! purchase
    case SynchronizeData => dbCache ! SynchronizeData
    case AddOrUpdateData(updateData) => dao.updateData(updateData)
    case Stop =>
      synchronizer.cancel()
      dbCache ! SynchronizeData
      context.stop(dbCache)
  }
}


