package kth.devops.DevOps_backend.Services;

import kth.devops.DevOps_backend.Entities.UserEntity;
import kth.devops.DevOps_backend.Entities.UserRole;
import kth.devops.DevOps_backend.Entities.PatientEntity;
import kth.devops.DevOps_backend.Repos.UserRepo;
import kth.devops.DevOps_backend.Repos.PatientRepo;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final PatientRepo patientRepo;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepo userRepo, PatientRepo patientRepo) {
        this.userRepo = userRepo;
        this.patientRepo = patientRepo;
    }

    public UserEntity createAccount(UserEntity user) {
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getUserRole() == null) {
            throw new IllegalArgumentException("User role is required");
        }
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        UserEntity newUser = new UserEntity(
                user.getUsername(),
                hashedPassword,
                user.getUserRole()
        );
        return userRepo.save(newUser);
    }

    @Transactional
    public UserEntity createNewPatientAccount(UserEntity user, String patientName, String gender, String birthDate) {
        if (user.getUserRole() != UserRole.PATIENT) {
            throw new IllegalArgumentException("Patient Registration only");
        }
        if (user.getUsername() == null || user.getUsername().isBlank()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (userRepo.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username is taken");
        }
        if (patientName == null || patientName.isBlank() || gender == null || birthDate == null || birthDate.isBlank()) {
            throw new IllegalArgumentException("Please provide fill in all fields");
        }
        if (!gender.equals("male") && !gender.equals("female")) {
            throw new IllegalArgumentException("Invalid sex");
        }
        LocalDate parsedBirthDate;

        try {
            parsedBirthDate = LocalDate.parse(birthDate);
        }
        catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date of birth. Use YYYY-MM-DD");
        }

        String hashedPassword = passwordEncoder.encode(user.getPassword());
        UserEntity newUser = new UserEntity(
                user.getUsername(),
                hashedPassword,
                UserRole.PATIENT
        );

        UserEntity savedUser = userRepo.save(newUser);
        PatientEntity newPatient = new PatientEntity(
                patientName,
                gender,
                parsedBirthDate
        );
        newPatient.setUser(savedUser);
        patientRepo.save(newPatient);

        return savedUser;
    }

    public UserEntity login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Username and password are required");
        }
        Optional<UserEntity> userResult = userRepo.findByUsername(username);
        UserEntity user;

        if (userResult.isPresent()) {
            user = userResult.get();
        }
        else {
            throw new IllegalArgumentException("Invalid username or password");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return user;
    }
}
