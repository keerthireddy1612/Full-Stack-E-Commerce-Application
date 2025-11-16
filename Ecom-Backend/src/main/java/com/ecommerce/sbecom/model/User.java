package com.ecommerce.sbecom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@ToString
@NoArgsConstructor
@Table(name="Users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "userName"),
        @UniqueConstraint(columnNames = "email")
})
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long userId;

    @NotBlank
    @Size(max=20)
    @Column(name="userName")
    private String userName;

    @NotBlank
    @Size(max=50)
    @Email
    @Column(name="email")
    private String email;

    @NotBlank
    @Size(max=120)
    @Column(name="password")
    private String password;

    public User(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    @Getter
    @Setter
    @ManyToMany(cascade={CascadeType.PERSIST,CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name="user_role",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id"))
    private Set<Role> roles=new HashSet<>();

    @Getter
    @Setter
    @OneToMany(mappedBy="user" ,cascade={CascadeType.PERSIST,CascadeType.MERGE}, orphanRemoval = true)
    private List<Address> addresses=new ArrayList<>();

    @ToString.Exclude
    @OneToOne(mappedBy = "user", cascade ={CascadeType.PERSIST,CascadeType.REMOVE}, orphanRemoval = true)
    private Cart cart;

    @ToString.Exclude
    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL, CascadeType.MERGE}, orphanRemoval = true)//in this scenario user is seller of the product
    private Set<Product>products=new HashSet<>();



}
