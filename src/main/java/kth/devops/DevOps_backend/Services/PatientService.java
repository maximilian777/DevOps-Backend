package kth.devops.DevOps_backend.Services;

import kth.devops.DevOps_backend.Entities.*;
import kth.devops.DevOps_backend.Repos.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class PatientService {

    private final PatientRepo patientRepo;
    private final ConditionRepo conditionRepo;
    private final ObservationRepo observationRepo;
    private final EncounterRepo encounterRepo;
    private final UserRepo userRepo;

    public PatientService(PatientRepo patientRepo, ConditionRepo conditionRepo, ObservationRepo observationRepo, EncounterRepo encounterRepo, UserRepo userRepo) {
        this.patientRepo = patientRepo;
        this.conditionRepo = conditionRepo;
        this.observationRepo = observationRepo;
        this.encounterRepo = encounterRepo;
        this.userRepo = userRepo;
    }

    public List<Map<String, String>> getPatients() {
        List<PatientEntity> patients = patientRepo.findAll();
        List<Map<String, String>> patientList = new ArrayList<>();

        for (PatientEntity patient : patients) {
            patientList.add(convertPatientToMap(patient));
        }

        return patientList;
    }

    public List<Map<String, String>> searchPatients(String name) {
        List<PatientEntity> patients = patientRepo.findByNameContainingIgnoreCase(name);
        List<Map<String, String>> patientList = new ArrayList<>();

        for (PatientEntity patient : patients) {
            patientList.add(convertPatientToMap(patient));
        }

        return patientList;
    }

    public Map<String, String> getPatientById(String id) {
        Long patientId;

        try {
            patientId = Long.parseLong(id);
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid patient ID.");
        }

        Optional<PatientEntity> patientResult = patientRepo.findById(patientId);

        if (patientResult.isPresent()) {
            PatientEntity patient = patientResult.get();
            return convertPatientToMap(patient);
        }
        else {
            throw new IllegalArgumentException("Patient not found.");
        }
    }

    public Map<String, Object> getPatientRecord(Long patientId) {
        Optional<PatientEntity> patientResult = patientRepo.findById(patientId);

        if (patientResult.isPresent()) {
            PatientEntity patient = patientResult.get();
            List<ConditionEntity> conditions = conditionRepo.findByPatientId(patientId);
            List<Map<String, Object>> conditionList = new ArrayList<>();

            for (ConditionEntity condition : conditions) {
                String description = condition.getDescription();

                if (description == null) {
                    description = "";
                }

                Map<String, Object> conditionData = Map.of(
                        "id", condition.getId(),
                        "name", condition.getName(),
                        "description", description,
                        "status", condition.getStatus(),
                        "diagnosedAt", condition.getDiagnosedAt().toString(),
                        "diagnosedBy", condition.getDiagnosedBy().getUsername()
                );
                conditionList.add(conditionData);
            }
            List<ObservationEntity> observations = observationRepo.findByPatientId(patientId);
            List<Map<String, Object>> observationList = new ArrayList<>();

            for (ObservationEntity observation : observations) {
                Map<String, Object> observationData = Map.of(
                        "id", observation.getId(),
                        "content", observation.getContent(),
                        "recordedAt", observation.getRecordedAt().toString(),
                        "createdBy", observation.getCreatedBy().getUsername()
                );

                observationList.add(observationData);
            }
            List<EncounterEntity> encounters = encounterRepo.findByPatientId(patientId);
            List<Map<String, Object>> encounterList = new ArrayList<>();

            for (EncounterEntity encounter : encounters) {
                String reason = encounter.getReason();

                if (reason == null) {
                    reason = "";
                }

                Map<String, Object> encounterData = Map.of(
                        "id", encounter.getId(),
                        "encounterDate", encounter.getEncounterDate().toString(),
                        "reason", reason,
                        "organization", encounter.getOrganization().getName(),
                        "location", encounter.getLocation().getName(),
                        "createdBy", encounter.getCreatedBy().getUsername()
                );
                encounterList.add(encounterData);
            }
            return Map.of(
                    "patient", convertPatientToMap(patient),
                    "conditions", conditionList,
                    "observations", observationList,
                    "encounters", encounterList
            );
        }
        else {
            throw new IllegalArgumentException("Patient not found.");
        }
    }

    @Transactional
    public void createObservation(Long patientId, Long userId, String content) {
        Optional<PatientEntity> patientResult = patientRepo.findById(patientId);
        PatientEntity patient;

        if (patientResult.isPresent()) {
            patient = patientResult.get();
        }
        else {
            throw new IllegalArgumentException("Patient not found.");
        }
        UserEntity user = getAuthorizedPractitioner(userId);

        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Observation content cannot be empty.");
        }
        ObservationEntity observation = new ObservationEntity(patient, null, user, content.trim());
        observationRepo.save(observation);
    }

    @Transactional
    public void createCondition(Long patientId, Long userId, String name, String description, String status) {
        Optional<PatientEntity> patientResult = patientRepo.findById(patientId);
        PatientEntity patient;

        if (patientResult.isPresent()) {
            patient = patientResult.get();
        }
        else {
            throw new IllegalArgumentException("Patient not found.");
        }
        UserEntity user = getAuthorizedPractitioner(userId);

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Condition name cannot be empty.");
        }

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("Condition status cannot be empty.");
        }
        String conditionDescription = description;

        if (conditionDescription == null) {
            conditionDescription = "";
        }
        else {
            conditionDescription = conditionDescription.trim();
        }
        ConditionEntity condition = new ConditionEntity(patient, null, user, name.trim(), conditionDescription, status.trim());
        conditionRepo.save(condition);
    }

    private UserEntity getAuthorizedPractitioner(Long userId) {
        Optional<UserEntity> userResult = userRepo.findById(userId);
        UserEntity user;

        if (userResult.isPresent()) {
            user = userResult.get();
        }
        else {
            throw new IllegalArgumentException("User not found.");
        }

        if (user.getUserRole() != UserRole.DOCTOR && user.getUserRole() != UserRole.STAFF) {
            throw new IllegalArgumentException("Only doctors and staff can modify medical journals.");
        }
        return user;
    }

    private Map<String, String> convertPatientToMap(PatientEntity patient) {
        return Map.of(
                "id", patient.getId().toString(),
                "name", patient.getName(),
                "gender", patient.getGender(),
                "birthDate", patient.getDateOfBirth().toString()
        );
    }
}
