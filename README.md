[![License](https://img.shields.io/github/license/mashape/apistatus.svg?maxAge=2592000)]()
[![](https://jitpack.io/v/ZrdJ/java-properties.svg)](https://jitpack.io/#ZrdJ/java-properties)
![GitHub Workflow Status (branch)](https://github.com/zrdj/java-properties/actions/workflows/maven.yml/badge.svg)

# java-properties

## Maven

Add the Jitpack repository to your build file

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Release artifact

```xml

<dependency>
    <groupId>com.github.zrdj</groupId>
    <artifactId>java-properties</artifactId>
    <version>0.1.0</version>
</dependency>
```

## Usage

### Configure it the way you like it

```java

@Component
public class ApplicationPropertyStoreProvider implements Provider<ApplicationPropertyStore> {

    @Inject
    private LoggerFacadeProvider _logProvider;

    @Override
    public ApplicationPropertyStore get() {
        // read from configured vm otions
        final ApplicationPropertyStore systemPropertyStore = new SystemPropertyStore(_logProvider::get).decorate(
                s -> new RetryStore(_logProvider::get, ChangeDotToUnderscoreProperty::new, s));
        // read from configured environment variables
        final ApplicationPropertyStore systemEnvironmentStore = new SystemEnvironmentStore(_logProvider::get).decorate(
                s -> new RetryStore(_logProvider::get, ChangeDotToUnderscoreProperty::new, s));

        // define a application environment of your choice
        final String environment = systemPropertyStore.or(systemEnvironmentStore).get(new ApplicationProperty.Immutable("application.environment"))
                .map(ApplicationPropertyValue::asString)
                .orElse("default");
        // read from properties files based on the environment
        final ApplicationPropertyStore propertiesFileStore = new PropertiesFileStore(_logProvider::get, environment).decorate(
                s -> new RetryStore(_logProvider::get, ChangeDotToUnderscoreProperty::new, s));

        // compose every store in the order you want
        return new ComposedApplcationPropertyStore(_logProvider::get, propertiesFileStore //
                .or(systemPropertyStore) //
                .or(systemEnvironmentStore) //
        );
    }
}
```

### Define your properties in an enum for typed access

```java
public enum DynamoDbApplicationProperties implements ApplicationProperty {
    AwsDynamoRegion("aws.dynamo.region", false),
    AwsDynamoAccessKey("aws.dynamo.access.key", true),
    AwsDynamoSecretKey("aws.dynamo.secret.key", true),
    AwsDynamoEndpointOverride("aws.dynamo.endpoint.override", false);

    private final String _key;
    private final boolean _secret;

    DynamoDbApplicationProperties(final String key, final boolean secret) {
        _key = key;
        _secret = secret;
    }

    @Override
    public String key() {
        return _key;
    }

    @Override
    public boolean isSecret() {
        return _secret;
    }
}
```

### Inject / use the store as you like and access the properties.

```java

@Component
public class DynamoDbClientProvider implements Provider<DynamoDbEnhancedClient> {

    @Inject
    private ApplicationPropertyStore _store;

    private final String _dynamoDbAccessKey = _store.get(DynamoDbApplicationProperties.AwsDynamoAccessKey)
            .map(ApplicationPropertyValue::asString)
            .orElseThrow(DynamoDbApplicationProperties.AwsDynamoAccessKey::exception);
    private final String _dynamoDbSecretKey = _store.get(DynamoDbApplicationProperties.AwsDynamoSecretKey)
            .map(ApplicationPropertyValue::asString)
            .orElseThrow(DynamoDbApplicationProperties.AwsDynamoSecretKey::exception);
    private final Region _dynamoDbRegion = _store.get(DynamoDbApplicationProperties.AwsDynamoRegion)
            .map(p -> Region.of(p.asString()))
            .orElseThrow(DynamoDbApplicationProperties.AwsDynamoRegion::exception);


    @Override
    public DynamoDbEnhancedClient get() {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(overrideEndpoint(DynamoDbClient.builder())
                        .region(_dynamoDbRegion)
                        .credentialsProvider(() -> AwsBasicCredentials.create(_dynamoDbAccessKey, _dynamoDbSecretKey))
                        .build())
                .build();
    }

    private DynamoDbClientBuilder overrideEndpoint(final DynamoDbClientBuilder builder) {
        _store.get(DynamoDbApplicationProperties.AwsDynamoEndpointOverride)
                .map(ApplicationPropertyValue::asString)
                .ifPresent(endpoint -> builder.endpointOverride(URI.create(endpoint)));

        return builder;
    }
}

```