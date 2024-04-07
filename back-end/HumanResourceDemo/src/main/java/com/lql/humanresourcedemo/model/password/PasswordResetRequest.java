package com.lql.humanresourcedemo.model.password;

import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class PasswordResetRequest {
    @EmbeddedId
    private PasswordResetRequestId id;
    private LocalDateTime validUntil;

    @Embeddable
    @NoArgsConstructor
    @Getter
    @Setter
    public static class PasswordResetRequestId implements Serializable {
        @OneToOne
        @JoinColumn(name = "employeeId")
        private Employee employee;
        private String token;



        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PasswordResetRequestId that = (PasswordResetRequestId) o;


            return employee.getId().equals(that.employee.getId())
                    && token.equals(that.token);
        }

        @Override
        public int hashCode() {
            int result = employee != null ? employee.hashCode() : 0;
            result = 31 * result + (token != null ? token.hashCode() : 0);
            return result;
        }
    }

}
