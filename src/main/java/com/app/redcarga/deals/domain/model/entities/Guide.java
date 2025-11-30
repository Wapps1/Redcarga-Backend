package com.app.redcarga.deals.domain.model.entities;

import com.app.redcarga.deals.domain.model.valueobjects.GuideType;
import jakarta.persistence.*;

@Entity
@Table(schema = "deals", name = "guides")
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "guide_id")
    private Integer guideId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 100)
    private GuideType type;

    @Column(name = "quote_id")
    private Integer quoteId;

    @Column(name = "guide_url", length = 500)
    private String guideUrl;

    public Integer getGuideId() {
        return guideId;
    }

    public GuideType getType() {
        return type;
    }

    public void setType(GuideType type) {
        this.type = type;
    }

    public Integer getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Integer quoteId) {
        this.quoteId = quoteId;
    }

    public String getGuideUrl() {
        return guideUrl;
    }

    public void setGuideUrl(String guideUrl) {
        this.guideUrl = guideUrl;
    }
}
