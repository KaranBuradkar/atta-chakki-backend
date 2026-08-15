package com.attachakki.components.shop;

import com.attachakki.components.address.Address;
import com.attachakki.components.staff.ShopStaff;
import com.attachakki.entity.UserDetails;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shop")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shop_name", nullable = false)
    private String name;

    @Column(name = "phone_no", nullable = false, length = 100)
    private String phoneNo;

    @Column(name = "shop_email", length = 100)
    private String email;

    @Column(name = "shop_location_url")
    private String locationUrl;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "shop_status", nullable = false)
    private ShopStatus status;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false)
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserDetails owner;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ShopStaff> shopStaffs = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Shop() {}

    public Shop(
            String name,
            String phoneNo,
            String email,
            String locationUrl,
            Address address,
            UserDetails owner,
            ShopStatus status
    ) {
        this.name = name;
        this.phoneNo = phoneNo;
        this.email = email;
        this.locationUrl = locationUrl;
        this.address = address;
        this.owner = owner;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocationUrl() {
        return locationUrl;
    }

    public void setLocationUrl(String locationUrl) {
        this.locationUrl = locationUrl;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public UserDetails getOwner() {
        return owner;
    }

    public void setOwner(UserDetails owner) {
        this.owner = owner;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<ShopStaff> getShopStaffs() {
        return shopStaffs;
    }

    public void setShopStaffs(List<ShopStaff> shopStaffs) {
        this.shopStaffs = shopStaffs;
    }

    public void addShopStaff(ShopStaff staff) {
        this.shopStaffs.add(staff);
    }

    public ShopStatus getStatus() {
        return status;
    }

    public void setStatus(ShopStatus status) {
        this.status = status;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}