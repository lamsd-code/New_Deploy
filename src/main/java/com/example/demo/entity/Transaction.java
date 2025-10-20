package com.example.demo.entity;


import lombok.Getter;
import lombok.Setter;
import com.example.demo.enums.TransactionType;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction{
    @Column(name = "code")
    private String code;

    @Column(name = "note")
    private String note;

    @ManyToOne
    @JoinColumn(name = "customerid")
    private Customer customer;
    
    @ManyToOne
    @JoinColumn(name = "buildingid")
    private Building building;
    
    @Column(name = "type")
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "amount")
    private Long amount;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "createddate")
    private Date createdDate = new Date();

    @Column(name = "createdby")
    private String createdBy;

    @Column(name = "modifieddate")
    private Date modifiedDate;

    @Column(name = "modifiedby")
    private String modifiedBy;
    
    @ManyToOne
    @JoinColumn(name = "staffid")
    private User staff;
}

