package com.attachakki.components.staff;

import com.attachakki.components.shop.Shop;
import com.attachakki.components.permissions.StaffPermission;
import com.attachakki.entity.UserDetails;
import com.attachakki.entity.type.StaffRole;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "shop_staff")
public class ShopStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shop_id")
    private Shop shop;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_details_id")
    private UserDetails userDetail;

    @Enumerated(EnumType.STRING)
    @Column(name = "staff_role", nullable = false)
    private StaffRole staffRole; // OWNER or SHOPKEEPER or SUPPORTER

    @OneToMany(mappedBy = "shopStaff", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StaffPermission> permissions = new ArrayList<>();

    @Column(name = "active", nullable = false)
    private Boolean active = true;   // default true

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by", nullable = false)
    private UserDetails addedBy; // shopkeeper who added supporter

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ShopStaff() {}

    public ShopStaff(
            Shop shop,
            UserDetails userDetail,
            StaffRole staffRole
    ) {
        this.shop = shop;
        this.userDetail = userDetail;
        this.staffRole = staffRole;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Shop getShop() {
        return shop;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
        shop.addShopStaff(this);
    }

    public UserDetails getUserDetail() {
        return userDetail;
    }

    public void setUserDetail(UserDetails userDetail) {
        this.userDetail = userDetail;
    }

    public StaffRole getStaffRole() {
        return staffRole;
    }

    public void setStaffRole(StaffRole staffRole) {
        this.staffRole = staffRole;
    }

    public UserDetails getAddedBy() {
        return addedBy;
    }

    public void setAddedBy(UserDetails addedBy) {
        this.addedBy = addedBy;
    }

    public List<StaffPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<StaffPermission> permissions) {
        permissions.forEach(p -> p.setShopStaff(this));
        this.permissions = permissions;
    }

    public void addPermission(StaffPermission permission) {
        this.permissions.add(permission);
        permission.setShopStaff(this);
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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
