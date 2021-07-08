package kmtr.github.com.dynamodb.operator

import kmtr.github.com.dynamodb.descriptor.*
import software.amazon.awssdk.services.dynamodb.model.*

fun buildCreateTableRequest(td: TableDescriptor, tableName: String): CreateTableRequest {
    return CreateTableRequest.builder()
        .tableName(tableName)
        .attributeDefinitions(td.AttributeDefinitions.map { buildAttributeDefinition(it) })
        .keySchema(td.KeySchema.map { buildKeySchemaElement(it) })
        .provisionedThroughput(buildProvisionedThroughput(td.ProvisionedThroughput)).also { it ->
            if (td.GlobalSecondaryIndexes.isNotEmpty()) {
                it.globalSecondaryIndexes(td.GlobalSecondaryIndexes.map { gsid -> buildGlobalSecondaryIndex(gsid) })
            }
            if (td.LocalSecondaryIndexes.isNotEmpty()) {
                it.localSecondaryIndexes(td.LocalSecondaryIndexes.map { lsid -> buildLocalSecondaryIndex(lsid) })
            }
        }
        .also {
            if (td.BillingMode.isNotEmpty()) {
                it.billingMode(td.BillingMode)
            }
        }
        .streamSpecification(buildStreamSpecification(td.StreamSpecification))
        .sseSpecification(buildSSESpecification(td.SSESpecification))
        .build()
}

private fun buildAttributeDefinition(ad: AttributeDefinitionDescriptor): AttributeDefinition {
    return AttributeDefinition.builder()
        .attributeName(ad.AttributeName)
        .attributeType(ad.AttributeType)
        .build()
}

private fun buildGlobalSecondaryIndex(gsid: GlobalSecondaryIndexDescriptor): GlobalSecondaryIndex {
    return GlobalSecondaryIndex.builder()
        .indexName(gsid.IndexName)
        .keySchema(gsid.KeySchema.map { buildKeySchemaElement(it) })
        .projection(buildProjection(gsid.Projection))
        .provisionedThroughput(buildProvisionedThroughput(gsid.ProvisionedThroughput))
        .build()
}

private fun buildKeySchemaElement(ksd: KeySchemaDescriptor): KeySchemaElement {
    return KeySchemaElement.builder().attributeName(ksd.AttributeName).keyType(ksd.KeyType).build()
}

private fun buildProjection(pd: ProjectionDescriptor): Projection {
    return Projection.builder().projectionType(pd.ProjectionType).also {
        if (pd.NonKeyAttributes.isNotEmpty()) {
            it.nonKeyAttributes(pd.NonKeyAttributes)
        }
    }.build()
}

private fun buildProvisionedThroughput(ptd: ProvisionedThroughputDescriptor): ProvisionedThroughput {
    try {
        val rcu = ptd.ReadCapacityUnits.toLong()
        val wcu = ptd.WriteCapacityUnits.toLong()

        return ProvisionedThroughput.builder()
            .readCapacityUnits(rcu)
            .writeCapacityUnits(wcu)
            .build()
    } catch (e: NumberFormatException) {
        throw e
    }
}

private fun buildLocalSecondaryIndex(lsid: LocalSecondaryIndexDescriptor): LocalSecondaryIndex {
    return LocalSecondaryIndex.builder()
        .indexName(lsid.IndexName)
        .keySchema(lsid.KeySchema.map { buildKeySchemaElement(it) })
        .projection(buildProjection(lsid.Projection))
        .build()
}

private fun buildStreamSpecification(ssd: StreamSpecificationDescriptor): StreamSpecification {
    return StreamSpecification.builder()
        .streamEnabled(ssd.StreamEnabled)
        .also {
            if (ssd.StreamEnabled) {
                it.streamViewType(ssd.StreamViewType)
            }
        }
        .build()
}

private fun buildSSESpecification(ssesd: SSESpecificationDescriptor): SSESpecification {
    return SSESpecification.builder()
        .enabled(ssesd.Enabled)
        .also {
            if (ssesd.Enabled) {
                it.sseType(ssesd.SSEType)
                it.kmsMasterKeyId(ssesd.KMSMasterKeyId)
            }
        }
        .build()
}