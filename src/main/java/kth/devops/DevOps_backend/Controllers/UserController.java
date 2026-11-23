package kth.devops.DevOps_backend.Controllers;

import kth.devops.DevOps_backend.Entities.UserEntity;
import kth.devops.DevOps_backend.Entities.UserRole;
import kth.devops.DevOps_backend.Services.UserService;
import kth.devops.DevOps_backend.Repos.PatientRepo;
import kth.devops.DevOps_backend.Entities.PatientEntity;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;
    private final PatientRepo patientRepo;

    public UserController(UserService userService, PatientRepo patientRepo) {
        this.userService = userService;
        this.patientRepo = patientRepo;
    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody UserEntity user) {
        try {
            if (user.getUserRole() == UserRole.PATIENT) {
                return ResponseEntity.badRequest().body("Use the patient registration endpoint.");
            }
            userService.createAccount(user);

            return ResponseEntity.ok("Account created");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/patient")
    public ResponseEntity<?> createPatientAccount(@RequestBody PatientRegistrationRequest request) {
        try {
            UserEntity user = new UserEntity(request.username(), request.password(), UserRole.PATIENT);
            userService.createNewPatientAccount(user, request.patientName(), request.gender(), request.birthDate());

            return ResponseEntity.ok("Patient account created successfully!");
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record PatientRegistrationRequest(String username, String password, String patientName, String gender, String birthDate) {}

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            UserEntity user = userService.login(request.username(), request.password());
            Long patientId = null;

            if (user.getUserRole() == UserRole.PATIENT) {
                Optional<PatientEntity> patientResult = patientRepo.findByUserId(user.getId());

                if (patientResult.isPresent()) {
                    PatientEntity patient = patientResult.get();
                    patientId = patient.getId();
                }
                else {
                    throw new IllegalArgumentException("No patient profile found");
                }
            }
            LoginResponse response = new LoginResponse(user.getId(), user.getUsername(), user.getUserRole(), patientId);

            return ResponseEntity.ok(response);

        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public record LoginRequest(String username, String password) {}

    public record LoginResponse(Long userId, String username, UserRole userRole, Long patientId) {}
}
