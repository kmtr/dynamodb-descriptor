# Dynamodb Descriptor

This is a utility library for dynamodb in Kotlin.
It can read a YAML template of a dynamodb table and build a `CreateTableRequest` instance.

It depends on kaml.  
https://github.com/charleskorn/kaml

## How to use

```kotlin
import com.charleskorn.kaml.Yaml
import kmtr.github.com.dynamodb.descriptor.TableDescriptor
import kmtr.github.com.dynamodb.operator.buildCreateTableRequest
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import java.io.File

val yamlText = File("/Music.yaml").readText(Charsets.UTF_8)
val descriptor = Yaml.default.decodeFromString(TableDescriptor.serializer(), yamlText)
val cli: DynamoDbClient = /* create DynamoDbClient */
val result = cli.createTable(buildCreateTableRequest(descriptor, "Music"))
```

## Appendix

You can create a same table represented in `lib/src/test/resources/Music_GlobalIndex_LocalIndex.yaml` with CLI.

```shell
aws dynamodb --endpoint-url http://localhost:8000 create-table \
    --table-name Music \
    --attribute-definitions AttributeName=Artist,AttributeType=S AttributeName=SongTitle,AttributeType=S \
        AttributeName=AlbumTitle,AttributeType=S  \
    --key-schema AttributeName=Artist,KeyType=HASH AttributeName=SongTitle,KeyType=RANGE \
    --provisioned-throughput \
        ReadCapacityUnits=10,WriteCapacityUnits=5 \
    --local-secondary-indexes \
        "[{\"IndexName\": \"AlbumTitleIndex\",
        \"KeySchema\":[{\"AttributeName\":\"Artist\",\"KeyType\":\"HASH\"},
                      {\"AttributeName\":\"AlbumTitle\",\"KeyType\":\"RANGE\"}],
        \"Projection\":{\"ProjectionType\":\"INCLUDE\",  \"NonKeyAttributes\":[\"Genre\", \"Year\"]}}]" \
    --global-secondary-indexes \
        "[
            {
                \"IndexName\": \"SongTitle-index\",
                \"KeySchema\": [{\"AttributeName\":\"SongTitle\",\"KeyType\":\"HASH\"},
                                {\"AttributeName\":\"Artist\",\"KeyType\":\"RANGE\"}],
                \"Projection\":{
                    \"ProjectionType\":\"INCLUDE\",
                    \"NonKeyAttributes\":[\"AlbumTitle\"]
                },
                \"ProvisionedThroughput\":{
                    \"ReadCapacityUnits\":10,
                    \"WriteCapacityUnits\":5
                }
            }
        ]"
```