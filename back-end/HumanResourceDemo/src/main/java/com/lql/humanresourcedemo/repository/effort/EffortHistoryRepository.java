package com.lql.humanresourcedemo.repository.effort;

import com.lql.humanresourcedemo.dto.response.effort.MonthlyAverageResult;
import com.lql.humanresourcedemo.model.effort.EffortHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EffortHistoryRepository extends JpaRepository<EffortHistory, EffortHistory.EffortHistoryId>, JpaSpecificationExecutor<EffortHistory> {


    @Query("""
            SELECT new com.lql.humanresourcedemo.dto.response.effort.MonthlyAverageResult(MONTH(e.id.date), AVG(e.effort)) 
                        FROM EffortHistory e 
                        WHERE e.id.employeeId = :employeeId AND e.id.date between :startDate AND :endDate
                        GROUP BY MONTH(e.id.date)
                        ORDER BY MONTH(e.id.date) ASC
            """)
    List<MonthlyAverageResult> findMonthlyAverageEffort(Long employeeId, LocalDate startDate, LocalDate endDate);
}
