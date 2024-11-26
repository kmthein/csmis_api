package com.team2.csmis_api.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@Table(name = "user")
@Entity
public class User extends Base implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "staff_id")
    private String staffId;

    @Column(name = "door_log_no")
    private Integer doorLogNo;

    @Column(name = "has_default_password", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean hasDefaultPassword = true;

    @Column(name = "received_mail", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean receivedMail = false;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @Column(name = "is_vegan", columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isVegan = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @ManyToOne
    @JoinColumn(name="division_id")
    private Division division;

    @ManyToOne
    @JoinColumn(name="department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name="team_id")
    private Team team;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_avoid_meat",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "meat_id")
    )
    private List<Meat> meats;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return staffId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
