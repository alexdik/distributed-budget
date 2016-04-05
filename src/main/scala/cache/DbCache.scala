package cache

import akka.actor.{Actor, FSM}
import service.AerospikeServiceEvents

object DbCacheProps {

  trait CacheState

  case object EmptyCache extends CacheState

  case object NonEmptyCache extends CacheState

  //data
  case class CacheData(value: Map[Int, Int])

}

class DbCache extends Actor with FSM[DbCacheProps.CacheState, DbCacheProps.CacheData] {

  import AerospikeServiceEvents._
  import DbCacheProps._

  startWith(EmptyCache, CacheData(Map()))

  when(EmptyCache) {
    case Event(MakePurchase(id, incrementValue), CacheData(currentCache)) if incrementValue > 0 =>
      val newCacheValue = currentCache.get(id) match {
        case Some(cachedValue: Int) => id -> (cachedValue + incrementValue)
        case _ => id -> incrementValue
      }
      goto(NonEmptyCache) using CacheData(currentCache + newCacheValue)
    case Event(SynchronizeData, CacheData(currentValue)) => stay
  }

  when(NonEmptyCache) {
    case Event(MakePurchase(id, incrementValue), CacheData(currentCache)) if incrementValue > 0 =>
      val newCacheValue = currentCache.get(id) match {
        case Some(cachedValue: Int) => id -> (cachedValue + incrementValue)
        case _ => id -> incrementValue
      }
      goto(NonEmptyCache) using CacheData(currentCache + newCacheValue)
    case Event(SynchronizeData, CacheData(currentCache)) => goto(EmptyCache) using CacheData(Map())
  }

  onTransition {
    case NonEmptyCache -> EmptyCache => stateData match {
      case CacheData(currentCache) => sender() ! AddOrUpdateData(currentCache)
    }
  }
}