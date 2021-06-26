# Dynamodb Descriptor

This is a utility library for dynamodb in Kotlin.
It can read a YAML template of a dynamodb table and build a `CreateTableRequest` instance.

It depends on kaml.  
https://github.com/charleskorn/kaml

## How to use

```kotlin
val yamlText = this::class.java.getResource(it.value).readText(Charsets.UTF_8)
val decoded = Yaml.default.decodeFromString(TableDescriptor.serializer(), yamlText)
val cli = dynamodb.dynamoDbClient()
cli.createTable(decoded.build(it.key))
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