package ru.practicum.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User implements Serializable {

    @Serial
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JsonIgnore
    @Column(nullable = false)
    String passwordHash;

    @Column(name = "first_name", nullable = false)
    String firstName;

    @Column(name = "last_name", nullable = false)
    String lastName;

    @Column(name = "personnel_number", unique = true, nullable = false)
    String personnelNumber;     // Табельный номер

    @Column(unique = true)
    String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    UserRole role = UserRole.MACHINIST;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    UserStatus status = UserStatus.ACTIVE;

    public enum UserRole {
        MACHINIST,
        ENGINEER,
        SHIFT_SUPERVISOR    // начальник смены
    }

    public enum UserStatus {
        ACTIVE,
        VACATION,       // В отпуске
        SICK_LEAVE,     // На больничном
        DISMISSED       // Уволен
    }

    public void updateName(String firstName, String lastName) {
        this.firstName = Objects.requireNonNull(firstName, "Имя не может быть null");
        this.lastName = Objects.requireNonNull(lastName, "Фамилия не может быть null");
    }

    public void updatePassword(String newPasswordHash) {
        this.passwordHash = Objects.requireNonNull(newPasswordHash);
    }

    public void changeRole(UserRole newRole) {
        this.role = Objects.requireNonNull(newRole);
    }

    public void changeStatus(UserStatus newState){
        this.status = Objects.requireNonNull(newState);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}