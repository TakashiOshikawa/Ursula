package com.redisconnect
import com.redis._

/**
 * Created by oshikawatakashi on 2015/08/31.
 */

object RedisClientSingleton {
  private val red = new RedisClient("127.0.0.1", 6379)
  def getInstance(): RedisClient = red
}


object RedisConnect {

  val red = RedisClientSingleton.getInstance

  def set(key: String, value: String): Boolean = red.set(key,value)
  def setex(key: String, time: Int, value: String): Boolean = red.setex(key, time, value)
  def get(key: String): String = red.get(key).getOrElse("")
  def keys = red.keys()

  def getKeysString: String = {
    val someKeys = RedisConnect.keys
    someKeys match {
      case Some(keyList) => keyList.filter( _.getOrElse("---bad") != "---bad" ).
                            foldLeft("")( (a: String, b: Option[String]) => a + "\n・" + b.get)
      case None          => "no keys"
    }
  }

}