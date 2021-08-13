package com.jukusoft.anman.base.entity.general;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

/**
 * global settings entity
 */
@Entity
@Table(name = "global_settings", indexes = {
        //@Index(columnList = "email", name = "email_idx"),
}, uniqueConstraints = {
        //@UniqueConstraint(columnNames = "username", name = "username_uqn")
})
@Cacheable//use second level cache
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GlobalSettingEntity {

    @Id
    @Size(min = 2, max = 45)
    @Column(name = "setting_key", unique = true, nullable = false, updatable = false)
    @NotEmpty(message = "key is required")
    private String key;

    @Size(min = 1, max = 900)
    @Column(name = "value", unique = false, nullable = false, updatable = true)
    @NotEmpty(message = "value is required")
    private String value;

    @Size(min = 2, max = 255)
    @Column(name = "title", unique = false, nullable = false, updatable = true)
    @NotEmpty(message = "title is required")
    private String title;

    public GlobalSettingEntity(@Size(min = 2, max = 45) @NotEmpty(message = "key is required") String key, @Size(min = 1, max = 900) @NotEmpty(message = "value is required") String value, @Size(min = 2, max = 255) @NotEmpty(message = "title is required") String title) {
        this.key = key;
        this.value = value;
        this.title = title;
    }

    public GlobalSettingEntity() {
        //
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIntValue() {
        return Integer.parseInt(getValue());
    }

    public void setValue(int value) {
        this.value = Integer.toString(value);
    }

    public long getLongValue() {
        return Long.parseLong(getValue());
    }

    public void setValue(long value) {
        this.value = Long.toString(value);
    }

    public double getDoubleValue() {
        return Double.parseDouble(getValue());
    }

    public void setValue(double value) {
        this.value = Double.toString(value);
    }

    public boolean getBoolValue() {
        return Boolean.parseBoolean(getValue());
    }

    public void setValue(boolean value) {
        this.value = Boolean.toString(value);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

}
