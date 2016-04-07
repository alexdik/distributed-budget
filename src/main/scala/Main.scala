import java.util.concurrent.TimeUnit

import akka.actor.{Actor, ActorSystem, Props}
import dao.AerospikeDao
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import service.{AerospikeServiceProps, AerospikeService}
import service.AerospikeServiceEvents.{GetData, Stop, MakePurchase}


object Main {

  import system.dispatcher

  val system = ActorSystem("AppActorSystem")

  def main(args: Array[String]) {
    val dao = new AerospikeDao(ConfigFactory.load(), AerospikeServiceProps)
    println("client2 before=" + dao.getDataSync(2))
    val aerospikeActor = system.actorOf(Props(new AerospikeService(ConfigFactory.load())))

    val client1 = system.scheduler.schedule(70 milliseconds, 70 milliseconds, aerospikeActor, MakePurchase(1, 10))
    val client2 = system.scheduler.schedule(50 milliseconds, 50 milliseconds, aerospikeActor, MakePurchase(2, 2))

    TimeUnit.SECONDS.sleep(10)

    client1.cancel()
    client2.cancel()

    TimeUnit.SECONDS.sleep(10)
    aerospikeActor ! Stop
    system.stop(aerospikeActor)
    system.shutdown()

    println("client2 after=" + dao.getDataSync(2))
  }
}