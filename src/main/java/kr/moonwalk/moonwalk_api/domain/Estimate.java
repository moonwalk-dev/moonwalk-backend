package kr.moonwalk.moonwalk_api.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Estimate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "estimate_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "mood_id")
    private Mood mood;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    private int totalPrice;

    private LocalDateTime createAt;

    @OneToMany(mappedBy = "estimate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> carts = new ArrayList<>();

    public void addModuleToEstimate(Module module, int quantity) {
        Cart cart = new Cart(this, module, quantity);
        carts.add(cart);
        totalPrice += module.getPrice() * quantity;
    }

    public Estimate(User user, String title) {
        this.user = user;
        this.title = title;
        this.totalPrice = 0;
        this.createAt = LocalDateTime.now();
    }
}
