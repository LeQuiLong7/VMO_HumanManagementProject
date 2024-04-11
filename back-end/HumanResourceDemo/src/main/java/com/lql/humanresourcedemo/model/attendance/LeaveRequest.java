package com.lql.humanresourcedemo.model.attendance;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.model.Auditable;
import com.lql.humanresourcedemo.model.employee.Employee;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LeaveRequest extends Auditable {

    @Id
    @SequenceGenerator(name = "leave_request_id_seq", sequenceName = "leave_request_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_request_id_seq")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employeeId")
    private Employee employee;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private LeaveType type;

    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approvedBy")
    private Employee approvedBy;

}
