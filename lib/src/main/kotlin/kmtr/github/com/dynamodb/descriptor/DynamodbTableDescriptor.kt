package kmtr.github.com.dynamodb.descriptor

import kotlinx.serialization.Serializable

@Serializable
data class TableDescriptor(
    val TableName: String,
    val AttributeDefinitions: List<AttributeDefinitionDescriptor>,
    val KeySchema: List<KeySchemaDescriptor>,
    val GlobalSecondaryIndexes: List<GlobalSecondaryIndexDescriptor> = arrayListOf(),
    val LocalSecondaryIndexes: List<LocalSecondaryIndexDescriptor> = arrayListOf(),
    val ProvisionedThroughput: ProvisionedThroughputDescriptor,
    val BillingMode: String = "",
    val StreamSpecification: StreamSpecificationDescriptor = StreamSpecificationDescriptor(),
    val SSESpecification: SSESpecificationDescriptor = SSESpecificationDescriptor(),
)

@Serializable
data class AttributeDefinitionDescriptor(
    val AttributeName: String,
    val AttributeType: String,
)

@Serializable
data class GlobalSecondaryIndexDescriptor(
    val IndexName: String,
    val KeySchema: List<KeySchemaDescriptor>,
    val Projection: ProjectionDescriptor,
    val ProvisionedThroughput: ProvisionedThroughputDescriptor,
)

@Serializable
data class KeySchemaDescriptor(
    val AttributeName: String,
    val KeyType: String,
)

@Serializable
data class ProjectionDescriptor(
    val ProjectionType: String,
    val NonKeyAttributes: List<String> = listOf(),
)

@Serializable
data class ProvisionedThroughputDescriptor(
    val ReadCapacityUnits: String = "1",
    val WriteCapacityUnits: String = "1",
)

@Serializable
class LocalSecondaryIndexDescriptor(
    val IndexName: String,
    val KeySchema: List<KeySchemaDescriptor>,
    val Projection: ProjectionDescriptor,
)

@Serializable
class StreamSpecificationDescriptor(
    val StreamEnabled: Boolean = false,
    val StreamViewType: String = "",
)

@Serializable
class SSESpecificationDescriptor(
    val Enabled: Boolean = false,
    val SSEType: String = "",
    val KMSMasterKeyId: String = "",
)