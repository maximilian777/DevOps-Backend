package kth.devops.DevOps_backend.Controllers;

import kth.devops.DevOps_backend.Services.PatientService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:5173")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    public List<Map<String, String>> getPatients() {
        return patientService.getPatients();
    }

    @GetMapping("/search")
    public List<Map<String, String>> searchPatients(@RequestParam String name) {
        return patientService.searchPatients(name);
    }

    @GetMapping("/{id}")
    public Map<String, String> getPatientById(@PathVariable String id) {
        return patientService.getPatientById(id);
    }

    @GetMapping("/{id}/record")
    public Map<String, Object> getPatientRecord(@PathVariable Long id) {
        return patientService.getPatientRecord(id);
    }

    @PostMapping("/{id}/observations")
    public ResponseEntity<?> createObservation(@PathVariable Long id, @RequestBody CreateObservationRequest request) {
        try {
            patientService.createObservation(id, request.userId(), request.content());

            return ResponseEntity.ok(Map.of("message", "Observation created successfully."));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/conditions")
    public ResponseEntity<?> createCondition(@PathVariable Long id, @RequestBody CreateConditionRequest request) {
        try {
            patientService.createCondition(id, request.userId(), request.name(), request.description(), request.status());
            return ResponseEntity.ok(Map.of("message", "Condition created"));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record CreateObservationRequest(Long userId, String content) {}

    public record CreateConditionRequest(Long userId, String name, String description, String status) {}
}
