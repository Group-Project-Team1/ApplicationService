package com.beaconfire.applicationservice.config;

import com.beaconfire.applicationservice.domain.AwsSecrets;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

@Configuration
@PropertySource("classpath:application.properties")
public class HibernateProperty {

    private String url;

    @Value("${database.hibernate.driver}")
    private String driver;

    private String username;

    private String password;

    @Value("${database.hibernate.dialect}")
    private String dialect;

    @Value("${database.hibernate.showsql}")
    private String showsql;

    public static AwsSecrets getSecret() {

        String secretName = "Mysql";
        Region region = Region.of("us-east-2");

        // Create a Secrets Manager client
        SecretsManagerClient client = SecretsManagerClient.builder()
                .region(region)
                .build();

        GetSecretValueRequest getSecretValueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse getSecretValueResponse;

        try {
            getSecretValueResponse = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            // For a list of exceptions thrown, see
            // https://docs.aws.amazon.com/secretsmanager/latest/apireference/API_GetSecretValue.html
            throw e;
        }

        String secret = getSecretValueResponse.secretString();


        Gson gson = new Gson();
        return gson.fromJson(secret, AwsSecrets.class);

    }

    public HibernateProperty(){
        AwsSecrets awsSecrets = getSecret();
        this.username = awsSecrets.getUsername();
        this.password = awsSecrets.getPassword();
        // jdbc:mysql://team1-hrwebsite-database.cobx6uz1hjmg.us-east-2.rds.amazonaws.com:3306/ApplicationService?useSSL=false
        this.url = "jdbc:" + awsSecrets.getEngine() + "://" + awsSecrets.getHost() + ":" + awsSecrets.getPort() + "/HousingService?useSSL=false";
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public String getShowsql() {
        return showsql;
    }

    public void setShowsql(String showsql) {
        this.showsql = showsql;
    }
}

