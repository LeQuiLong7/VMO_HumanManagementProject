package com.lql.humanresourcedemo.model.effort;

import com.lql.humanresourcedemo.model.employee.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class EffortHistory {
    @EmbeddedId
    private EffortHistoryId id = new EffortHistoryId();

    private Integer effort;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    private Employee employee;

    public EffortHistory(Long employeeId,LocalDate date, Integer effort) {
        this.id.employeeId = employeeId;
        this.id.date = date;
        this.effort = effort;
    }

    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class EffortHistoryId implements Serializable {

        private Long employeeId;
        private LocalDate date;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            EffortHistoryId that = (EffortHistoryId) o;

            if (!Objects.equals(employeeId, that.employeeId)) return false;
            return Objects.equals(date, that.date);
        }

        @Override
        public int hashCode() {
            int result = employeeId != null ? employeeId.hashCode() : 0;
            result = 31 * result + (date != null ? date.hashCode() : 0);
            return result;
        }
    }
}
