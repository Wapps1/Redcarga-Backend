package com.app.redcarga.deals.domain.model.entities;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.List;

@Entity
@Table(schema = "deals", name = "checklist_instance", uniqueConstraints = @UniqueConstraint(columnNames = "quote_id"))
public class ChecklistInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instance_id")
    private Integer instanceId;

    @Column(name = "quote_id", nullable = false, unique = true)
    private Integer quoteId;

    @Column(name = "template_id", nullable = false)
    private Integer templateId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @OneToMany(mappedBy = "instance", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ChecklistInstanceItem> items;

    public Integer getInstanceId() { return instanceId; }
    public Integer getQuoteId() { return quoteId; }
    public void setQuoteId(Integer quoteId) { this.quoteId = quoteId; }
    public Integer getTemplateId() { return templateId; }
    public void setTemplateId(Integer templateId) { this.templateId = templateId; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<ChecklistInstanceItem> getItems() { return items; }
    public void setItems(List<ChecklistInstanceItem> items) { this.items = items; }
}
