package edu.ncsu.csc326.wolfcafe.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * User role.
 *
 * @author Diya Patel
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table ( name = "roles" )
public class Role {

    /** Role id */
    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long            id;

    /** Role name */
    private String          name;

    /** Permissions assigned to this role */
    @ElementCollection ( fetch = FetchType.EAGER )
    @CollectionTable ( name = "role_permissions", joinColumns = @JoinColumn ( name = "role_id" ) )
    @Enumerated ( EnumType.STRING )
    @Column ( name = "permission" )
    private Set<Permission> permissions = new HashSet<>();

}
