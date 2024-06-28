import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import com.typesafe.config.ConfigFactory
import org.apache.log4j.Logger
import java.time.format.DateTimeFormatter
import java.time.LocalDate
import org.apache.spark.SparkConf


object SparkBatchJob extends App {
  @transient lazy val logger: Logger = Logger.getLogger(getClass.getName)

  val conf = ConfigFactory.load()


  val spark = SparkSession.builder
      .appName("KafkaToS3Batch")
      .master("local[*]")
      .config("spark.jars.packages", "org.apache.hadoop:hadoop-aws:3.3.4")
      .config("spark.hadoop.fs.s3a.aws.credentials.provider", "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
      .config("spark.hadoop.fs.s3a.access.key", sys.env.getOrElse("AWS_ACCESS_KEY_ID", ""))
      .config("spark.hadoop.fs.s3a.secret.key", sys.env.getOrElse("AWS_SECRET_ACCESS_KEY", ""))
      .getOrCreate()


  // Read data from Kafka
  val kafkaParams = Map(
    "kafka.bootstrap.servers" -> conf.getString("kafka.bootstrap.servers"),
    "subscribe" -> conf.getString("kafka.topic.reports"),
    "group.id" -> "batch-group-id",
    "auto.offset.reset" -> "latest"
  )

  import spark.implicits._

  val kafkaDF = spark
    .read
    .format("kafka")
    .options(kafkaParams)
    .load()

  val parsedDF = kafkaDF.selectExpr("CAST(value AS STRING) as json")
      .as[String]
      .flatMap(ReportParser.parseReport) // Parse each report string into a Report object
      .toDF()

  
  val currentDate = LocalDate.now
  val year = currentDate.getYear
  val month = currentDate.getMonthValue
  val day = currentDate.getDayOfMonth
  val outputPath = s"s3a://${conf.getString("s3.bucket.name")}/reports/$year/$month/$day/"
  parsedDF.write.mode("Overwrite").json(outputPath)
  logger.info(s"Data written to $outputPath")
  
  
  spark.stop()
}
