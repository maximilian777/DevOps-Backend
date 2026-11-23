package kth.devops.DevOps_backend.Controllers;

import kth.devops.DevOps_backend.Services.DoctorService;
import kth.devops.DevOps_backend.Entities.UserEntity;
import kth.devops.DevOps_backend.Entities.UserRole;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "http://localhost:5173")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/organizations")
    public List<Map<String, String>> getOrganizations() {
        return doctorService.getOrganizations();
    }

    @GetMapping("/locations")
    public List<Map<String, String>> getLocations() {
        return doctorService.getLocations();
    }

    @GetMapping("/search")
    public List<Map<String, String>> searchPractitioners(@RequestParam String name, @RequestParam UserRole role) {
        return doctorService.searchPractitioners(name, role);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPractitioner(@RequestBody DoctorRegistrationRequest request) {

        try {
            UserEntity user = new UserEntity(request.username(), request.password(), request.userRole());
            doctorService.createPractitioner(user, request.practitionerName(), request.organizationId(), request.locationId());

            return ResponseEntity.ok("Doctor/Staff account created");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record DoctorRegistrationRequest(String username, String password, UserRole userRole, String practitionerName, Long organizationId, Long locationId) {}

    @GetMapping("/messaging-recipient/{role}/{profileId}")
    public ResponseEntity<?> getMessagingRecipient(@PathVariable UserRole role, @PathVariable Long profileId) {
        try {
            return ResponseEntity.ok(doctorService.getMessagingRecipient(role, profileId));
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
