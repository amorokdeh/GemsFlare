package com.gemsflare.gemsflare.permission.jpa;

import com.gemsflare.gemsflare.utils.UUIDListConverter;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "permission", schema = "public")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PermissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String route;

    @Convert(converter = UUIDListConverter.class)
    @Column(nullable = false)
    private List<UUID> admins;

    @Convert(converter = UUIDListConverter.class)
    @Column(nullable = false)
    private List<UUID> users;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public List<UUID> getUsers() {
        return users;
    }

    public void setUsers(List<UUID> users) {
        this.users = users;
    }

    public List<UUID> getAdmins() {
        return admins;
    }

    public void setAdmins(List<UUID> admins) {
        this.admins = admins;
    }
}
