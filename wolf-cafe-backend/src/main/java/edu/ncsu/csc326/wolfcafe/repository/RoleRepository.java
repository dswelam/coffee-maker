package edu.ncsu.csc326.wolfcafe.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import edu.ncsu.csc326.wolfcafe.entity.Role;

/**
 * Repository interface for Roles.
 *
 * @author Dania Swelam
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds the role by name.
     *
     * @param name role's name
     * @return Role object
     */
    Optional<Role> findByName ( String name );
}
