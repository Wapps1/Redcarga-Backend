package com.app.redcarga.deals.domain.model.entities;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(schema = "deals", name = "checklist_instance_item", indexes = {@Index(name = "ix_checkitem_instance", columnList = "instance_id, status_code")})
public class ChecklistInstanceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instance_item_id")
    private Integer instanceItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instance_id", nullable = false)
    private ChecklistInstance instance;

    @Column(name = "code", length = 64, nullable = false)
    private String code;

    @Column(name = "status_code", length = 16, nullable = false)
    private String statusCode = "PENDING";

    @Column(name = "completed_by")
    private Integer completedBy;

    @Column(name = "completed_at")
    private Instant completedAt;

    public Integer getInstanceItemId() { return instanceItemId; }
    public ChecklistInstance getInstance() { return instance; }
    public void setInstance(ChecklistInstance instance) { this.instance = instance; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStatusCode() { return statusCode; }
    public void setStatusCode(String statusCode) { this.statusCode = statusCode; }
    public Integer getCompletedBy() { return completedBy; }
    public void setCompletedBy(Integer completedBy) { this.completedBy = completedBy; }
    public Instant getCompletedAt() { return completedAt; }
    public void setCompletedAt(Instant completedAt) { this.completedAt = completedAt; }
}
