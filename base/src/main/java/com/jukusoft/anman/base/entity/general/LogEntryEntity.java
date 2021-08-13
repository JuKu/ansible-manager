package com.jukusoft.anman.base.entity.general;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Entity
@Table(name = "logs", indexes = {
        //@Index(columnList = "email", name = "email_idx"),
}, uniqueConstraints = {
        //@UniqueConstraint(columnNames = "username", name = "username_uqn")
})
@Cacheable(value = false)//use second level cache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.NONE)
public class LogEntryEntity extends com.jukusoft.anman.base.entity.general.AbstractEntity {

    //@Size(min = 2, max = 45)
    @Column(name = "title", unique = false, nullable = false, updatable = true)
    @NotEmpty(message = "title is required")
    private String title;

    //@Size(min = 2, max = 45)
    @Column(name = "message", unique = false, nullable = true, updatable = true)
    //@NotEmpty(message = "message is required")
    private String message;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "log_type", nullable = false, updatable = false)
    private LogEntryType type;

    public LogEntryEntity() {
        //
    }

    public LogEntryEntity(@Size(min = 2, max = 45) @NotEmpty(message = "title is required") String title, @Size(min = 2, max = 45) @NotEmpty(message = "message is required") String message) {
        this.title = title;
        this.message = message;
        this.type = LogEntryType.INFO;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

}
