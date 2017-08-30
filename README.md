# PlayFramework HBase Example

This sample shows how to connect to HBase in a PlayFramework application.

## Step by step guide

This considers that you already know how to install and start HBase. If not, take a look at [HBase Book](http://hbase.apache.org/book.html#quickstart) or check this [installation guide](https://www.tutorialspoint.com/hbase/hbase_installation.htm). Before continuing, check your installation by running:

```bash
hbase shell
```

You need to see something like:

```
HBase Shell; enter 'help<RETURN>' for list of supported commands.
Type "exit<RETURN>" to leave the HBase Shell
Version 1.3.1, r930b9a55528fe45d8edce7af42fef2d35e77677a, Thu Apr  6 19:36:54 PDT 2017

hbase(main):001:0>
```

#### Add HBase Client dependencies

Add the following dependencies to your `build.sbt` file:

```scala
libraryDependencies += "org.apache.hbase" % "hbase-client" % "1.3.1"
libraryDependencies += "org.apache.hbase" % "hbase-common" % "1.3.1"
libraryDependencies += "org.apache.hbase" % "hbase" % "1.3.1"
libraryDependencies += "org.apache.hadoop" % "hadoop-common" % "2.5.1"
```

#### Client configuration

Create a XML to configure HBase Client:

```xml
<configuration>
    <property>
        <name>hbase.zookeeper.quorum</name>
        <value>localhost</value>
    </property>
    <property>
        <name>hbase.zookeeper.property.clientPort</name>
        <value>2181</value>
    </property>
</configuration>
```

This should be created at `conf/hbase-config.xml`. We are using `localhost` here, but you need to use the proper host if have hbase running in another address.

#### HBase code

First, create a provider for HBase `Configuration`, like this:

```scala
import play.api.Environment
import javax.inject.{Inject, Provider}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.HBaseConfiguration

class HBaseConfigurationProvider @Inject()(environment: Environment) extends Provider[Configuration] {
  override def get() = {
    val conf = HBaseConfiguration.create()
    conf.addResource(environment.getFile("hbase-config.xml").getPath)
    conf
  }
}
```

Create a small helper class to easily access a HBase connection:

```scala
import org.apache.hadoop.conf.Configuration

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
```

This way we can ensure the connection will be closed after being used. We can then add a module to bind all things together:

```scala
class HBaseModule extends SimpleModule(
  bind[Configuration].toProvider[HBaseConfigurationProvider],
  bind[HBaseClient].toSelf
)
```

Enable this module by adding the following to your `application.conf`:

```HOCON
play.modules.enabled += "hbase.HBaseModule"
```

#### Using HBaseClient in a controller

Let's just have an `Action` that lists all the available tables:

```scala
@Singleton
class HomeController @Inject()(hbaseClient: HBaseClient, cc: ControllerComponents) extends AbstractController(cc) {

  def list = Action {
    hbaseClient.withConnection { conn =>
      val admin = conn.getAdmin
      Ok(admin.listTableNames().map(_.getNameAsString).mkString(", "))
    }
  }

}

```

## References

1. https://hbase.apache.org/book.html#quickstart
2. https://docs.microsoft.com/en-us/azure/hdinsight/hdinsight-hbase-build-java-maven-linux
3. https://cloud.google.com/bigtable/docs/samples-java-hello
4. http://www.baeldung.com/hbase