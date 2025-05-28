package http.tasktracksystem.domain.controllers;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomePage {

    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello from home page.");
    }
}
