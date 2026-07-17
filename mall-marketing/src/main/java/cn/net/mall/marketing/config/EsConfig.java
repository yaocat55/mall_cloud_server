package cn.net.mall.marketing.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * ES 连接配置.
 */
@Slf4j
@Configuration
public class EsConfig {

    @Value("${spring.elasticsearch.host:}")
    private String host;

    @Value("${spring.elasticsearch.port:9200}")
    private int port;

    @Value("${spring.elasticsearch.username:}")
    private String username;

    @Value("${spring.elasticsearch.password:}")
    private String password;

    @Value("${spring.elasticsearch.connect-timeout-ms:60000}")
    private int connectTimeoutMs;

    @Value("${spring.elasticsearch.socket-timeout-ms:180000}")
    private int socketTimeoutMs;

    @Bean(destroyMethod = "close")
    public RestClient restClient() {
        HttpHost[] hosts = Arrays.stream(host.split(","))
                .map(s -> new HttpHost(s.trim(), port))
                .toArray(HttpHost[]::new);
        log.info("初始化ES连接: hosts={}, port={}", Arrays.toString(hosts), port);

        RestClientBuilder clientBuilder = RestClient.builder(hosts);
        clientBuilder.setRequestConfigCallback(requestConfigBuilder ->
                requestConfigBuilder
                        .setConnectTimeout(connectTimeoutMs)
                        .setSocketTimeout(socketTimeoutMs)
        );
        if (StringUtils.hasText(username)) {
            log.info("ES启用认证: username={}", username);
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
            clientBuilder.setHttpClientConfigCallback(
                    httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credsProvider));
        }
        return clientBuilder.build();
    }

    @Bean
    public ElasticsearchOperations elasticsearchOperations(RestClient restClient) {
        return new ElasticsearchTemplate(
                new ElasticsearchClient(new RestClientTransport(restClient, new JacksonJsonpMapper())));
    }
}
