package kr.moonwalk.moonwalk_api.domain;

import static jakarta.persistence.FetchType.LAZY;

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
@Table(name = "inventories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int quantity;
    private int usedQuantity;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "module_id")
    private Module module;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public static Inventory createFromCart(Cart cart, Project project) {
        Inventory inventory = new Inventory();
        inventory.module = cart.getModule();
        inventory.project = project;
        inventory.quantity = cart.getQuantity();
        inventory.usedQuantity = 0;

        return inventory;
    }

    public void updateUsedQuantity(int amountUsed) {
        this.usedQuantity -= amountUsed;
    }
}