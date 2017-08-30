package hbase

import javax.inject.{Inject, Provider}

import org.apache.hadoop.hbase.client.{Connection, ConnectionFactory}
import play.api.Environment
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration
import play.api.inject.{ SimpleModule, bind }

class HBaseClient @Inject()(configuration: Configuration) {

  def withConnection[T](block: Connection => T): T = {
    val connection: Connection = ConnectionFactory.createConnection(configuration)
    try {
      block(connection)
    } finally {
      connection.close()
    }
  }
}

class HBaseConfigurationProvider @Inject()(environment: Environment) extends Provider[Configuration] {
  override def get() = {
    val conf = HBaseConfiguration.create()
    conf.addResource(environment.getFile("hbase-config.xml").getPath)
    conf
  }
}

class HBaseModule extends SimpleModule(
  bind[Configuration].toProvider[HBaseConfigurationProvider],
  bind[HBaseClient].toSelf
)