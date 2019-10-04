package com.home.mycicd.controller;

import com.home.mycicd.service.KubernetesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MyCiCdRestController {
    private KubernetesService kubernetesService;

    @Autowired
    public MyCiCdRestController(KubernetesService kubernetesService) {
        this.kubernetesService = kubernetesService;
    }

    @GetMapping("/createHelmPod")
    public ResponseEntity<Void> createHelmPod() {
        kubernetesService.createServiceAccount();
        kubernetesService.createHelmPod();

        return ResponseEntity.accepted().build();
    }
}
