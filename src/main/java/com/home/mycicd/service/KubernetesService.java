package com.home.mycicd.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.AppsV1beta1Api;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.apis.RbacAuthorizationV1Api;
import io.kubernetes.client.models.V1ClusterRoleBinding;
import io.kubernetes.client.models.V1Container;
import io.kubernetes.client.models.V1EnvVar;
import io.kubernetes.client.models.V1HostPathVolumeSource;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodSpec;
import io.kubernetes.client.models.V1RoleRef;
import io.kubernetes.client.models.V1Subject;
import io.kubernetes.client.models.V1Volume;
import io.kubernetes.client.models.V1VolumeMount;

@Service
public class KubernetesService {
    private static final String DEFAULT_NAMESPACE = "default";
    private CoreV1Api coreApi;
    private RbacAuthorizationV1Api rbacApi;
    private AppsV1beta1Api appsV1beta1Api;

    @Autowired
    public KubernetesService(CoreV1Api coreApi,
            RbacAuthorizationV1Api rbacApi,
            AppsV1beta1Api appsV1beta1Api) {
        this.coreApi = coreApi;
        this.rbacApi = rbacApi;
        this.appsV1beta1Api = appsV1beta1Api;
    }

    public void createHelmPod() {

        V1ObjectMeta meta = new V1ObjectMeta()
                .name("my-helm-pod")
                .labels(Collections.singletonMap("role", "myrole"));

        V1EnvVar envVar = new V1EnvVar()
                .name("KUBECONFIG")
                .value("/apps/kube_config.yaml");

        V1HostPathVolumeSource hostPathVolumeSource = new V1HostPathVolumeSource()
                .path("/Users/amyrgorod/");

        V1Volume volume = new V1Volume()
                .name("host-volume")
                .hostPath(hostPathVolumeSource);

        V1VolumeMount volumeMount = new V1VolumeMount()
                .name("host-volume")
                .mountPath("/apps");

        V1Container container = new V1Container()
                .name("my-helm")
                .image("alpine/helm")
                .env(Collections.singletonList(envVar))
                .volumeMounts(Collections.singletonList(volumeMount))
                .command(Arrays.asList("/bin/sh", "-c"))
                .args(Collections.singletonList("helm init; helm install stable/concourse;"));
//                .args(Collections.singletonList("helm version"));


        V1PodSpec podSpec = new V1PodSpec()
                .containers(Collections.singletonList(container))
                .restartPolicy("OnFailure")
                .volumes(Collections.singletonList(volume));

        V1Pod body = new V1Pod()
                .apiVersion("v1")
                .kind("Pod")
                .metadata(meta)
                .spec(podSpec);

        try {
            V1Pod result = coreApi.createNamespacedPod(DEFAULT_NAMESPACE, body, null,
                    "pretty-print", null);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception when calling CoreV1Api#createNamespacedPod");
            e.printStackTrace();
        }
    }

    public void createServiceAccount() {
        V1ObjectMeta meta = new V1ObjectMeta()
                .name("serviceaccounts-cluster-admin");
//                .labels(Collections.singletonMap("role", "myrole"));

        V1RoleRef roleRef = new V1RoleRef()
                .apiGroup("rbac.authorization.k8s.io")
                .kind("ClusterRole")
                .name("cluster-admin");

        V1Subject subject = new V1Subject()
                .apiGroup("rbac.authorization.k8s.io")
                .kind("Group")
                .name("system:serviceaccounts");

        V1ClusterRoleBinding clusterRoleBinding = new V1ClusterRoleBinding()
                .apiVersion("rbac.authorization.k8s.io/v1")
                .kind("ClusterRoleBinding")
                .metadata(meta)
                .roleRef(roleRef)
                .subjects(Collections.singletonList(subject));


        try {
            V1ClusterRoleBinding result = rbacApi.createClusterRoleBinding(clusterRoleBinding, null,
                    "pretty", null);
            System.out.println(result);
        } catch (ApiException e) {
            System.err.println("Exception");
            e.printStackTrace();
        }
    }
}
