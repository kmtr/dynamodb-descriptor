package kmtr.github.com.dynamodb.descriptor

import kotlinx.serialization.Serializable
import software.amazon.awssdk.services.dynamodb.model.*

@Serializable
class TableDescriptor(
    val TableName: String,
    val AttributeDefinitions: List<AttributeDefinitionDescriptor>,
    val KeySchema: List<KeySchemaDescriptor>,
    val GlobalSecondaryIndexes: List<GlobalSecondaryIndexDescriptor> = arrayListOf(),
    val LocalSecondaryIndexes: List<LocalSecondaryIndexDescriptor> = arrayListOf(),
    val ProvisionedThroughput: ProvisionedThroughputDescriptor,
    val BillingMode: String = "",
    val StreamSpecification: StreamSpecificationDescriptor = StreamSpecificationDescriptor(),
    val SSESpecification: SSESpecificationDescriptor = SSESpecificationDescriptor(),
) {
    fun build(tableName: String = TableName): CreateTableRequest {
        return CreateTableRequest.builder()
            .tableName(tableName)
            .attributeDefinitions(AttributeDefinitions.map { it.build() })
            .keySchema(KeySchema.map { it.build() })
            .provisionedThroughput(ProvisionedThroughput.build()).also {
                if (GlobalSecondaryIndexes.isNotEmpty()) {
                    it.globalSecondaryIndexes(GlobalSecondaryIndexes.map { it.build() })
                }
                if (LocalSecondaryIndexes.isNotEmpty()) {
                    it.localSecondaryIndexes(LocalSecondaryIndexes.map { it.build() })
                }
            }
            .also {
                if (BillingMode.isNotEmpty()) {
                    it.billingMode(BillingMode)
                }
            }
            .streamSpecification(StreamSpecification.build())
            .sseSpecification(SSESpecification.build())
            .build()
    }
}

@Serializable
class AttributeDefinitionDescriptor(
    val AttributeName: String,
    val AttributeType: String,
) {
    fun build(): AttributeDefinition {
        return AttributeDefinition.builder()
            .attributeName(AttributeName)
            .attributeType(AttributeType)
            .build()
    }
}

@Serializable
class GlobalSecondaryIndexDescriptor(
    val IndexName: String,
    val KeySchema: List<KeySchemaDescriptor>,
    val Projection: ProjectionDescriptor,
    val ProvisionedThroughput: ProvisionedThroughputDescriptor,
) {
    fun build(): GlobalSecondaryIndex {
        return GlobalSecondaryIndex.builder()
            .indexName(IndexName)
            .keySchema(KeySchema.map { it.build() })
            .projection(Projection.build())
            .provisionedThroughput(ProvisionedThroughput.build())
            .build()
    }
}

@Serializable
class KeySchemaDescriptor(
    val AttributeName: String,
    val KeyType: String,
) {
    fun build(): KeySchemaElement {
        return KeySchemaElement.builder().attributeName(AttributeName).keyType(KeyType).build()
    }
}

@Serializable
class ProjectionDescriptor(
    val ProjectionType: String,
    val NonKeyAttributes: List<String> = listOf(),
) {
    fun build(): Projection {
        return Projection.builder().projectionType(ProjectionType).also {
            if (NonKeyAttributes.isNotEmpty()) {
                it.nonKeyAttributes(NonKeyAttributes)
            }
        }.build()
    }
}

@Serializable
class ProvisionedThroughputDescriptor(
    val ReadCapacityUnits: String = "1",
    val WriteCapacityUnits: String = "1",
) {
    fun build(): ProvisionedThroughput {
        try {
            val rcu = ReadCapacityUnits.toLong()
            val wcu = WriteCapacityUnits.toLong()

            return ProvisionedThroughput.builder()
                .readCapacityUnits(rcu)
                .writeCapacityUnits(wcu)
                .build()
        } catch (e: NumberFormatException) {
            throw e
        }
    }
}

@Serializable
class LocalSecondaryIndexDescriptor(
    val IndexName: String,
    val KeySchema: List<KeySchemaDescriptor>,
    val Projection: ProjectionDescriptor,
) {
    fun build(): LocalSecondaryIndex {
        return LocalSecondaryIndex.builder()
            .indexName(IndexName)
            .keySchema(KeySchema.map { it.build() })
            .projection(Projection.build())
            .build()
    }
}

@Serializable
class StreamSpecificationDescriptor(
    val StreamEnabled: Boolean = false,
    val StreamViewType: String = "",
) {
    fun build(): StreamSpecification {
        return StreamSpecification.builder()
            .streamEnabled(StreamEnabled)
            .also {
                if (StreamEnabled) {
                    it.streamViewType(StreamViewType)
                }
            }
            .build()
    }
}

@Serializable
class SSESpecificationDescriptor(
    val Enabled: Boolean = false,
    val SSEType: String = "",
    val KMSMasterKeyId: String = "",
) {
    fun build(): SSESpecification {
        return SSESpecification.builder()
            .enabled(Enabled)
            .also {
                if (Enabled) {
                    it.sseType(SSEType)
                    it.kmsMasterKeyId(KMSMasterKeyId)
                }
            }
            .build()
    }
}