package com.lql.humanresourcedemo.model.password;

import com.lql.humanresourcedemo.model.employee.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PasswordResetRequest {
    @EmbeddedId
    private PasswordResetRequestId id;
    private LocalDateTime validUntil;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    private Employee employee;

    public PasswordResetRequest(Employee employee, String token,LocalDateTime validUntil) {
        this.id = new PasswordResetRequestId(employee.getId(), token);
        this.validUntil = validUntil;
        this.employee = employee;
    }

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class PasswordResetRequestId implements Serializable {
        private Long employeeId;
        private String token;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PasswordResetRequestId that = (PasswordResetRequestId) o;

            if (!Objects.equals(employeeId, that.employeeId)) return false;
            return Objects.equals(token, that.token);
        }

        @Override
        public int hashCode() {
            int result = employeeId != null ? employeeId.hashCode() : 0;
            result = 31 * result + (token != null ? token.hashCode() : 0);
            return result;
        }
    }

}
