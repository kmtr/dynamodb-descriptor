package kmtr.github.com.dynamodb.descriptor

import com.amazonaws.services.dynamodbv2.local.embedded.DynamoDBEmbedded
import com.amazonaws.services.dynamodbv2.local.shared.access.AmazonDynamoDBLocal
import com.charleskorn.kaml.Yaml
import kmtr.github.com.dynamodb.operator.buildCreateTableRequest
import kotlinx.serialization.decodeFromString
import java.util.logging.Logger
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

val logger: Logger = Logger.getLogger(TableDescriptorTest::class.qualifiedName)

class TableDescriptorTest {

    lateinit var dynamodb: AmazonDynamoDBLocal

    @BeforeTest
    fun beforeTest() {
        dynamodb = DynamoDBEmbedded.create()
    }

    @AfterTest
    fun afterTest() {
        dynamodb.shutdown()
    }

    @Test
    fun decodeYamlAndCreateTable() {
        val fileNames = mapOf(
            "Music" to "/Music.yaml",
            "MusicGlobalIndex" to "/Music_GlobalIndex.yaml",
            "MusicGlobalIndexLocalIndex" to "/Music_GlobalIndex_LocalIndex.yaml"
        )

        fileNames.forEach {
            logger.info("Load Dynamodb Schema File: ${it.value}")
            val yamlText = this::class.java.getResource(it.value)!!.readText(Charsets.UTF_8)
            val decoded = Yaml.default.decodeFromString<TableDescriptor>(yamlText)
            val cli = dynamodb.dynamoDbClient()
            cli.createTable(buildCreateTableRequest(decoded, it.key))
        }
    }
}