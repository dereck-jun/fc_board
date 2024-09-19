package com.fc_board.model.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Random;

@Entity
@Getter
@Table(name = "\"user\"")
@SQLDelete(sql = "update \"user\" set deleted_date_time = current_timestamp where user_id = ?")
@SQLRestriction("deleted_date_time is null")
@EqualsAndHashCode
public class UserEntity implements UserDetails {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long id;

    @Column
    @Setter
    private String username;

    @Column
    @Setter
    private String password;

    @Column
    @Setter
    private String profile;

    @Column
    private String description;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime createdDateTime;

    @Column
    private ZonedDateTime updatedDateTime;

    @Column(nullable = true)
    private ZonedDateTime deletedDateTime;

    public static UserEntity of(String username, String password) {
        var userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setPassword(password);
        userEntity.setProfile("https://avatar.iran.liara.run/public/" + (new Random().nextInt(100) + 1));
        return userEntity;
    }

    @PrePersist
    private void prePersist() {
        this.createdDateTime = ZonedDateTime.now();
        this.updatedDateTime = this.createdDateTime;
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedDateTime = ZonedDateTime.now();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
