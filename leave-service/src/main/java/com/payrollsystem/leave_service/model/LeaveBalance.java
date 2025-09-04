package com.payrollsystem.leave_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
public class LeaveBalance {
    @EmbeddedId
    private LeaveBalanceId id;

    @Column(nullable = false)
    private Double balanceDays;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaveBalanceId implements Serializable {

        @Column(name = "employee_id", nullable = false)
        private Long employeeId;

        @Enumerated(EnumType.STRING)
        @Column(name = "leave_type", nullable = false)
        private LeaveType leaveType;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LeaveBalanceId that = (LeaveBalanceId) o;
            return Objects.equals(employeeId, that.employeeId) && leaveType == that.leaveType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(employeeId, leaveType);
        }
    }
}
