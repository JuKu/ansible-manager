package com.jukusoft.anman.base.entity.general;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@MappedSuperclass
@SequenceGenerator(name = "abstract_entity_seq", initialValue = 1, allocationSize = 100)
public abstract class AbstractEntity implements Serializable {

    protected static final ZoneId UTC_ZONE = ZoneId.of("UTC");

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "abstract_entity_seq")
    private long id;

    @Column(name = "creation_date_time")
    private ZonedDateTime creationDateTime;

    @PrePersist
    public final void prePersist() {
        creationDateTime = ZonedDateTime.now(UTC_ZONE);
    }

    public final boolean isPersistent() {
        return this.id != 0;
    }

    public long getId() {
        return id;
    }

    public void forceID(long id) {
        this.id = id;
    }

    public ZonedDateTime getCreationDateTime() {
        return creationDateTime;
    }

    public void setCreationDateTime(ZonedDateTime creationDateTime) {
        this.creationDateTime = creationDateTime;
    }

}
