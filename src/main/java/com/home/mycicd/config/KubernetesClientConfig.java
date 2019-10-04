package com.home.mycicd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.apis.RbacAuthorizationV1Api;
import io.kubernetes.client.util.Config;

@Configuration
public class KubernetesClientConfig {
    public KubernetesClientConfig() throws IOException {
        ApiClient client = Config.defaultClient();
        io.kubernetes.client.Configuration.setDefaultApiClient(client);
    }

    @Bean
    public CoreV1Api coreV1Api() {
        return new CoreV1Api();
    }

    @Bean
    public RbacAuthorizationV1Api rbacAuthorizationV1Api() {
        return new RbacAuthorizationV1Api();
    }

    @Bean
    public AppsV1beta1Api appsV1beta1Api() {
        return new AppsV1beta1Api();
    }
}
