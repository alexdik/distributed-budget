package cache

import akka.actor.{Actor, FSM}
import service.AerospikeServiceEvents

object DbCacheProps {
  trait CacheState
  case object CacheState extends CacheState

  //data
  case class CacheData(value: Map[Int, Int])

}

class DbCache extends Actor with FSM[DbCacheProps.CacheState, DbCacheProps.CacheData] {

  import AerospikeServiceEvents._
  import DbCacheProps._

  startWith(CacheState, CacheData(Map()))

  when(CacheState) {
    case Event(MakePurchase(id, incrementValue), CacheData(currentCache)) if incrementValue > 0 =>
      val newCacheValue = currentCache.get(id) match {
        case Some(cachedValue: Int) => id -> (cachedValue + incrementValue)
        case _ => id -> incrementValue
      }
      goto(CacheState) using CacheData(currentCache + newCacheValue)
    case Event(SynchronizeData, CacheData(currentCache)) =>
      sender() ! AddOrUpdateData(currentCache)
      goto(CacheState) using CacheData(Map())
  }
}
