package com.casino.betting.entity;

import com.casino.common.domain.base.BaseAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "betting_events")
@Getter
@Setter
@NoArgsConstructor
public class BettingEvent extends BaseAuditableEntity {

    @Column(nullable = false, length = 256)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_status", nullable = false, length = 32)
    private EventStatus eventStatus = EventStatus.ACTIVE;

    public BettingEvent(String title, EventStatus eventStatus) {
        this.title = title;
        this.eventStatus = eventStatus;
    }
}
