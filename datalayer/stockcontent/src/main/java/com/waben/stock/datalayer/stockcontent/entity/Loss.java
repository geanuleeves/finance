package com.waben.stock.datalayer.stockcontent.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Set;

/**
 * @author Created by yuyidi on 2017/11/22.
 * @desc 止损
 */
@Entity
@Table(name = "loss")
public class Loss {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private BigDecimal point;

    @JsonBackReference
    @ManyToMany(targetEntity = StraegyType.class,mappedBy = "losses")
    private Set<StraegyType> straegyTypes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPoint() {
        return point;
    }

    public void setPoint(BigDecimal point) {
        this.point = point;
    }

    public Set<StraegyType> getStraegyTypes() {
        return straegyTypes;
    }

    public void setStraegyTypes(Set<StraegyType> straegyTypes) {
        this.straegyTypes = straegyTypes;
    }
}
