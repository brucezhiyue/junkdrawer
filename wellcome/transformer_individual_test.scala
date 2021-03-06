// This is a snippet for dropping into SierraTransformableTransformerTest
// to run when you have a specific JSON string that's failing.

it("transforms this record") {
  val jsonString = """<...>"""
  import SierraTransformable._
  val transformable = fromJson[SierraTransformable](jsonString).get

  transformToWork(transformable)
}

// Or if you have the notification message from the SQS Console, you can do
// the whole thing like this:

it("transforms this record") {
    import SierraTransformable._
    import com.amazonaws.services.s3.AmazonS3ClientBuilder
    import uk.ac.wellcome.messaging.sns.NotificationMessage
    import uk.ac.wellcome.storage.vhs.HybridRecord

    val queueJsonString = """<...>"""

    val notification: NotificationMessage = fromJson[NotificationMessage](queueJsonString).get
    val hybridRecord = fromJson[HybridRecord](notification.Message).get

    val s3client = AmazonS3ClientBuilder.standard.withRegion("eu-west-1").build()

    val jsonString =
      scala.io.Source
        .fromInputStream(
          s3client.getObject(hybridRecord.location.namespace, hybridRecord.location.key).getObjectContent
        )
        .mkString

    val transformable = fromJson[SierraTransformable](jsonString).get

    transformToWork(transformable)
  }

// For finding out why a Miro record won't transform:

it("transforms this ID") {
  import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder
  import com.amazonaws.services.s3.AmazonS3ClientBuilder
  import com.gu.scanamo._
  import com.gu.scanamo.syntax._
  import uk.ac.wellcome.json.JsonUtil._
  import uk.ac.wellcome.platform.transformer.miro.models.MiroTransformable
  import uk.ac.wellcome.storage.vhs.HybridRecord

  val miroId = "B0003989"

  val dynamoDbClient = AmazonDynamoDBClientBuilder
    .standard()
    .withRegion("eu-west-1")
    .build()

  val s3client = AmazonS3ClientBuilder
    .standard
    .withRegion("eu-west-1")
    .build()

  val hybridRecord: HybridRecord = Scanamo.get[HybridRecord](dynamoDbClient)("vhs-sourcedata-miro")('id -> miroId).get.right.get

  val data =
    scala.io.Source
      .fromInputStream(
        s3client.getObject(hybridRecord.location.namespace, hybridRecord.location.key).getObjectContent
      )
      .mkString

  println(s"data = <<$data>>")

  val miroTransformable = fromJson[MiroTransformable](data).get
  transformToWork(miroTransformable).asInstanceOf[UnidentifiedWork]
}
