package com.example.udd_security_incidents.configuration;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@Configuration
@EnableElasticsearchRepositories(basePackages = "com.example.udd_security_incidents.indexrepository")
public class ElasticsearchConfiguration
        extends org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration {

    @Value("${elasticsearch.host:localhost}")
    private String host;

    @Value("${elasticsearch.port:9200}")
    private int port;

    @Value("${elasticsearch.username:}")
    private String userName;

    @Value("${elasticsearch.password:}")
    private String password;

    @NotNull
    @Override
    public ClientConfiguration clientConfiguration() {
        System.out.println("Connecting to Elasticsearch at " + host + ":" + port);

        var c= ClientConfiguration.builder()
                .connectedTo("localhost" + ":" + port)
                .withBasicAuth(userName, password)
                .build();
        System.out.println("U");
        return c;
    }
}