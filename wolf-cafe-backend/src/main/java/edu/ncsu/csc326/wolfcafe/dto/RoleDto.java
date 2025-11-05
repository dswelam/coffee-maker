package edu.ncsu.csc326.wolfcafe.dto;

import java.util.Set;

import edu.ncsu.csc326.wolfcafe.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data transfer object for Role and its permissions.
 *
 * @author Diya Patel
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleDto {
    /** id of the role */
    private Long            id;
    /** the role name */
    private String          name;
    /** the role permissions */
    private Set<Permission> permissions;
}
