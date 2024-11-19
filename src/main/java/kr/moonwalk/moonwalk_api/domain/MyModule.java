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
public class MyModule {

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

    public static MyModule createFromCart(Cart cart, Project project) {
        MyModule myModule = new MyModule();
        myModule.module = cart.getModule();
        myModule.project = project;
        myModule.quantity = cart.getQuantity();
        myModule.usedQuantity = 0;

        return myModule;
    }

    public MyModule(Project project, Module module, int quantity) {
        this.project = project;
        this.module = module;
        this.quantity = quantity;
        this.usedQuantity = 0;

        project.getMyModules().add(this);
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void updateUsedQuantity(int amountUsed) {
        this.usedQuantity -= amountUsed;
    }
}