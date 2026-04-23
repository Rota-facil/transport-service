package com.rota.facil.transport_service.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Getter
@RequiredArgsConstructor
public enum DaysOfWeek {
    MONDAY("Segunda", 1),
    TUESDAY("Terça", 2),
    WEDNESDAY("Quarta", 3),
    THURSDAY("Quinta", 4),
    FRIDAY("Sexta", 5),
    SATURDAY("Sábado", 6),
    SUNDAY("Domingo", 7);

    private final String description;
    private final Integer value;

    private static final Map<Integer, DaysOfWeek> MAP_DAYS_OF_WEEK = new HashMap<>();

    static {
        for (DaysOfWeek daysOfWeek : DaysOfWeek.values()) {
            MAP_DAYS_OF_WEEK.put(daysOfWeek.getValue(), daysOfWeek);
        }
    }

    public static DaysOfWeek getFromValueDay(Integer valueDay) {
        return MAP_DAYS_OF_WEEK.get(valueDay);
    }

    public boolean inSequenceDays(Set<DaysOfWeek> daysOfWeek) {
        return daysOfWeek.contains(this);
    }
}
