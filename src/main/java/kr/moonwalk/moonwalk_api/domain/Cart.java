package kr.moonwalk.moonwalk_api.domain;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "carts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    private int quantity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "estimate_id")
    private Estimate estimate;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    public Cart(Estimate estimate, Module module, int quantity) {
        this.estimate = estimate;
        this.module = module;
        this.quantity = quantity;

        estimate.getCarts().add(this);
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
