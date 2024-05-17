package com.lql.humanresourcedemo.dto.response.effort;

import com.lql.humanresourcedemo.model.effort.EffortHistory;

import java.time.LocalDate;


    public  record EffortHistoryRecord(LocalDate date, Double effort){
        public static EffortHistoryRecord of(EffortHistory eh) {
            return new EffortHistoryRecord(eh.getId().getDate(), eh.getEffort().doubleValue());
        }
    }
