package kr.moonwalk.moonwalk_api.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_id")
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "guide_id", nullable = true)
    private Guide guide;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "mood_id", nullable = true)
    private Mood mood;

    public Image(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setGuide(Guide guide) {
        if (this.mood != null) {
            throw new IllegalStateException("이미지는 Guide 또는 Mood 중 하나만 가질 수 있습니다.");
        }
        this.guide = guide;
    }

    public void setMood(Mood mood) {
        if (this.guide != null) {
            throw new IllegalStateException("이미지는 Guide 또는 Mood 중 하나만 가질 수 있습니다.");
        }
        this.mood = mood;
    }
}
