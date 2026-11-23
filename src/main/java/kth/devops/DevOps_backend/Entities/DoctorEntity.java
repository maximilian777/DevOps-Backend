package kth.devops.DevOps_backend.Entities;

import jakarta.persistence.*;

@Entity
@Table(name = "doctors")
public class DoctorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "organization_id", nullable = false)
    private OrganizationEntity organization;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private LocationEntity location;

    protected DoctorEntity() {
    }

    public DoctorEntity(String name, UserEntity user, OrganizationEntity organization, LocationEntity location) {

        this.name = name;
        this.user = user;
        this.organization = organization;
        this.location = location;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UserEntity getUser() {
        return user;
    }

    public OrganizationEntity getOrganization() {
        return organization;
    }

    public LocationEntity getLocation() {
        return location;
    }
}
