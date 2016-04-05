import java.util.concurrent.TimeUnit

import akka.actor.{ActorSystem, Props}
import scala.concurrent.duration._
import com.typesafe.config.ConfigFactory
import service.AerospikeService
import service.AerospikeServiceEvents.{GetData, Stop, MakePurchase}


object Main {

  import system.dispatcher

  val system = ActorSystem("AppActorSystem")

  def main(args: Array[String]) {

    val aerospikeActor = system.actorOf(Props(new AerospikeService(ConfigFactory.load(), system)))

    val client1 = system.scheduler.schedule(70 milliseconds, 70 milliseconds, aerospikeActor, MakePurchase(1, 10))
    val client2 = system.scheduler.schedule(50 milliseconds, 50 milliseconds, aerospikeActor, MakePurchase(2, 2))
    val client2rec = system.scheduler.schedule(500 milliseconds, 500 milliseconds, aerospikeActor, GetData(2))

    TimeUnit.SECONDS.sleep(10)

    client1.cancel()
    client2.cancel()
    client2rec.cancel()
    aerospikeActor ! Stop
    TimeUnit.SECONDS.sleep(10)
    system.stop(aerospikeActor)
    system.shutdown()
  }
}