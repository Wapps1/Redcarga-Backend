package com.app.redcarga.requests.domain.model.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(schema = "requests", name = "request_item_images",
        indexes = @Index(name="ix_request_item_images_item_pos", columnList = "item_id, image_position"))
public class RequestItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private RequestItem item;

    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    private String imageUrl;

    @Column(name = "image_position", nullable = false)
    private Integer imagePosition;

    public static RequestItemImage of(String imageUrl) {
        RequestItemImage img = new RequestItemImage();
        img.imageUrl = requireUrl(imageUrl);
        return img;
    }

    public void attachTo(RequestItem item, int position) {
        this.item = item;
        this.imagePosition = position;
    }

    private static String requireUrl(String url) {
        if (url == null || (url = url.trim()).isEmpty())
            throw new IllegalArgumentException("image_url_required");
        if (url.length() > 2048) throw new IllegalArgumentException("image_url_too_long");
        return url;
    }
}
