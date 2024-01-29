package com.ssafy.relpl.db.postgre.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ruser")
public class User {
    @Id
    @GeneratedValue
    private Integer id;
    //TODO
}
