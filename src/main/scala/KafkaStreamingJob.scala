import org.apache.kafka.streams.scala._
import org.apache.kafka.streams.scala.kstream._
import org.apache.kafka.streams.{KafkaStreams, StreamsConfig}
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.serialization.Serdes._
import org.apache.kafka.streams.kstream.Printed
import com.typesafe.config.ConfigFactory
import java.util.Properties

object KafkaStreamingJob extends App {
  val conf = ConfigFactory.load()

  val kafkaBootstrapServers = conf.getString("kafka.bootstrap.servers")
  val reportsTopic = conf.getString("kafka.topic.reports")
  val alertsTopic = conf.getString("kafka.topic.alerts")

  val props: Properties = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "kafka-streams-job")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers)
    p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String.getClass.getName)
    p.put("auto.offset.reset", "earliest") // Lire depuis le début si aucun offset n'est trouvé
    p
  }

  val builder = new StreamsBuilder()

  // Read from Kafka topic
  val reportsStream: KStream[String, String] = builder.stream[String, String](reportsTopic)

  // Define your filtering logic
  val filteredReportsStream: KStream[String, String] = reportsStream.filter { (_, reportString) =>
    val report = ReportParser.parseReport(reportString)
    report.exists(_.alcoholLevel > 1.5)
  }

  // Print the filtered stream to console (for debugging purposes)
  filteredReportsStream.print(Printed.toSysOut[String, String].withLabel("Filtered Reports"))

  // Write the filtered data to another Kafka topic
  filteredReportsStream.to(alertsTopic)

  val streams: KafkaStreams = new KafkaStreams(builder.build(), props)
  streams.start()

  sys.ShutdownHookThread {
    streams.close()
  }
}
