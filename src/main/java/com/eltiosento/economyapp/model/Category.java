package com.eltiosento.economyapp.model;

import java.math.BigDecimal;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "Categories")
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon")
    private String icon;

    @Column(name = "is_saving", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean isSaving; // Indica si la categoria és d'estalvi

    @Column(name = "goal", precision = 10, scale = 2)
    private BigDecimal goal; // Marcar objectiu d'estalvi o despesa per a la categoria

    // Relació recursiva: una categoria pot tenir una categoria pare
    @ManyToOne
    @JoinColumn(name = "parent_category_id", foreignKey = @ForeignKey(name = "FK_ExpenseSubcategory_ParentCategory"))
    private Category parentCategory;

    // Relació recursiva inversa: una categoria pot tenir múltiples subcategories
    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Category> subcategories;

}
