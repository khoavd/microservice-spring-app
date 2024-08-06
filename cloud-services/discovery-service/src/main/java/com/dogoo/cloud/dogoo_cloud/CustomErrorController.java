package com.dogoo.cloud.dogoo_cloud;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController {
    private static final String ERROR_MAPPING = "/error";

    @GetMapping(ERROR_MAPPING)
    public ResponseEntity<Void> error() {
      return ResponseEntity.notFound().build();
    }

}
