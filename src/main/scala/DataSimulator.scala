import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import java.util.Properties
import scala.util.Random
import com.typesafe.config.ConfigFactory
import org.apache.log4j.Logger
import java.time.LocalDateTime



object DataSimulator extends App {
  @transient lazy val logger: Logger = Logger.getLogger(getClass.getName)


  if (args.length != 1) {
    println("Usage: DataSimulator <number_of_records>")
    System.exit(1)
  }



  val numRecords = args(0).toInt
  println(s"Starting DataSimulator with $numRecords records")

  val props = new Properties()
  val conf = ConfigFactory.load
  props.put("bootstrap.servers", conf.getString("kafka.bootstrap.servers"))
  props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
  props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")

  val producer = new KafkaProducer[String, String](props)
  val topic = conf.getString("kafka.topic.reports")

  val rand = new Random()

  val parisLatMin = 48.815573 // Minimum latitude for Paris
  val parisLatMax = 48.902145 // Maximum latitude for Paris
  val parisLonMin = 2.224122 // Minimum longitude for Paris
  val parisLonMax = 2.469136 // Maximum longitude for Paris


  for (i <- 1 to numRecords) {
    val latitude = rand.between(parisLatMin, parisLatMax)  // Range for Paris coordinates
    val longitude = rand.between(parisLonMin, parisLonMax) // Range for Paris coordinates
    val day =  LocalDateTime.now()
    val alcoholLevel = (math.round(rand.between(0.00, 3.00) * 100) / 100.0).toFloat // Random alcohol level between 0 and 3 g/L

    val report = new Report(latitude, longitude, day, alcoholLevel)
    val record = new ProducerRecord[String, String](topic, i.toString, report.toString)
    producer.send(record)
    println(s"Sent record: $report")

    Thread.sleep(1000)

  }

  producer.close()
  println("DataSimulator finished sending records")
}
