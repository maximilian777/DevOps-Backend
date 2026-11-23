
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
public class DoctorService {

    private final UserService userService;
    private final DoctorRepo doctorRepo;
    private final StaffRepo staffRepo;
    private final OrganizationRepo organizationRepo;
    private final LocationRepo locationRepo;

    public DoctorService(UserService userService, DoctorRepo doctorRepo, StaffRepo staffRepo, OrganizationRepo organizationRepo, LocationRepo locationRepo) {
        this.userService = userService;
        this.doctorRepo = doctorRepo;
        this.staffRepo = staffRepo;
        this.organizationRepo = organizationRepo;
        this.locationRepo = locationRepo;
    }

    public List<Map<String, String>> getOrganizations() {
        List<Map<String, String>> organizations = new ArrayList<>();
        List<OrganizationEntity> organizationList = organizationRepo.findAll();

        for (OrganizationEntity organization : organizationList) {
            Map<String, String> organizationData = Map.of(
                    "id", organization.getId().toString(),
                    "name", organization.getName()
            );
            organizations.add(organizationData);
        }

        return organizations;
    }

    public List<Map<String, String>> getLocations() {
        List<Map<String, String>> locations = new ArrayList<>();
        List<LocationEntity> locationList = locationRepo.findAll();

        for (LocationEntity location : locationList) {
            Map<String, String> locationData = Map.of(
                    "id", location.getId().toString(),
                    "name", location.getName()
            );
            locations.add(locationData);
        }

        return locations;
    }

    public List<Map<String, String>> searchPractitioners(String name, UserRole role) {
        validateRole(role);

        String searchName = name.trim().toLowerCase();
        List<Map<String, String>> practitioners = new ArrayList<>();

        if (role == UserRole.DOCTOR) {
            List<DoctorEntity> doctors = doctorRepo.findAll();

            for (DoctorEntity doctor : doctors) {
                if (doctor.getName().toLowerCase().contains(searchName)) {
                    Map<String, String> doctorData = Map.of(
                            "id", doctor.getId().toString(),
                            "name", doctor.getName()
                    );
                    practitioners.add(doctorData);
                }
            }
        }
        else {
            List<StaffEntity> staffMembers = staffRepo.findAll();

            for (StaffEntity staff : staffMembers) {
                if (staff.getName().toLowerCase().contains(searchName)) {
                    Map<String, String> staffData = Map.of(
                            "id", staff.getId().toString(),
                            "name", staff.getName()
                    );
                    practitioners.add(staffData);
                }
            }
        }

        return practitioners;
    }

    private void validateRole(UserRole role) {
        if (role != UserRole.DOCTOR && role != UserRole.STAFF) {
            throw new IllegalArgumentException("Registration for Doctors and staff only");
        }
    }

    @Transactional
    public UserEntity createPractitioner(UserEntity user, String practitionerName, Long organizationId, Long locationId) {
        validateRole(user.getUserRole());

        if (practitionerName == null || practitionerName.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (organizationId == null || locationId == null) {
            throw new IllegalArgumentException("Please select an organization and location");
        }

        Optional<OrganizationEntity> organizationResult = organizationRepo.findById(organizationId);
        OrganizationEntity organization;

        if (organizationResult.isPresent()) {
            organization = organizationResult.get();
        }
        else {
            throw new IllegalArgumentException("Organization not found");
        }

        Optional<LocationEntity> locationResult = locationRepo.findById(locationId);
        LocationEntity location;

        if (locationResult.isPresent()) {
            location = locationResult.get();
        }
        else {
            throw new IllegalArgumentException("Location not found");
        }

        UserEntity savedUser = userService.createAccount(user);

        if (user.getUserRole() == UserRole.DOCTOR) {
            DoctorEntity doctor = new DoctorEntity(practitionerName, savedUser, organization, location);
            doctorRepo.save(doctor);
        }
        else {
            StaffEntity staff = new StaffEntity(practitionerName, savedUser, organization, location);
            staffRepo.save(staff);
        }

        return savedUser;
    }

    public Map<String, String> getMessagingRecipient(UserRole role, Long profileId) {
        validateRole(role);

        if (profileId == null) {
            throw new IllegalArgumentException("Invalid practitioner ID");
        }

        if (role == UserRole.DOCTOR) {
            Optional<DoctorEntity> doctorResult = doctorRepo.findById(profileId);

            if (doctorResult.isPresent()) {
                DoctorEntity doctor = doctorResult.get();
                return Map.of("userId", doctor.getUser().getId().toString());
            }
            else {
                throw new IllegalArgumentException("Doctor not found");
            }
        }
        else {
            Optional<StaffEntity> staffResult = staffRepo.findById(profileId);

            if (staffResult.isPresent()) {
                StaffEntity staff = staffResult.get();
                return Map.of("userId", staff.getUser().getId().toString());
            }
            else {
                throw new IllegalArgumentException("Staff member not found");
            }
        }
    }
}
