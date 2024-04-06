package com.lql.humanresourcedemo.model;

import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class LeaveRequest extends Auditable {

    @Id
    @SequenceGenerator(name = "leave_request_id_seq", sequenceName = "leave_request_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "leave_request_id_seq")
    private Long id;

    @OneToOne
    @JoinColumn(name = "employeeId")
    private Employee employee;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private LeaveType type;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    @OneToOne
    @JoinColumn(name = "approvedBy")
    private Employee approvedBy;

}
