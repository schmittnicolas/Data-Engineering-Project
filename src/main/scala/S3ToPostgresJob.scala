import org.apache.spark.sql.{SparkSession, DataFrame}
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import java.time.LocalDate
import com.typesafe.config.ConfigFactory

object S3ToPostgresJob extends App{

    val spark = SparkSession.builder()
      .appName("S3ToPostgresJob")
      .master("local[*]")
      .config("spark.jars.packages", "org.apache.hadoop:hadoop-aws:3.3.4")
      .config("spark.hadoop.fs.s3a.aws.credentials.provider", "org.apache.hadoop.fs.s3a.SimpleAWSCredentialsProvider")
      .config("spark.hadoop.fs.s3a.access.key", sys.env.getOrElse("AWS_ACCESS_KEY_ID", ""))
      .config("spark.hadoop.fs.s3a.secret.key", sys.env.getOrElse("AWS_SECRET_ACCESS_KEY", ""))
      .getOrCreate()


    
    import spark.implicits._

    val conf = ConfigFactory.load()

    // Get current date
    val now = LocalDate.now()
    val year = now.getYear
    val month = f"${now.getMonthValue}%02d"
    val day = f"${now.getDayOfMonth}%02d"

    // Construct S3 path
    val s3Path = f"s3a://dataingproject/reports/${year}/$month/$day/*.json"


    
    // Define schema
    val schema = StructType(Seq(
      StructField("latitude", DoubleType, false),
      StructField("longitude", DoubleType, false),
      StructField("date", TimestampType, false),
      StructField("alcoholLevel", DoubleType, false)
    ))

    // Read data from S3
    val rawDF = spark.read
      .schema(schema)
      .json(s3Path)

    // Validate data
    val validatedDF = rawDF.filter(
      col("latitude").isNotNull &&
      col("longitude").isNotNull &&
      col("date").isNotNull &&
      col("alcoholLevel").isNotNull
    )

    // PostgreSQL connection properties
    val jdbcUrl = "jdbc:postgresql://localhost:5432/mydatabase"
    val connectionProperties = new java.util.Properties()
    connectionProperties.put("user", "myuser")
    connectionProperties.put("password", "mypassword")
    connectionProperties.put("driver", "org.postgresql.Driver")

    // Write Dataset to PostgreSQL table
    validatedDF.write
      .mode("append")
      .jdbc(jdbcUrl, "reports", connectionProperties)

    spark.stop()

}