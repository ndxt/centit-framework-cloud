package com.centit.framework.oauth.domain.entity;

import lombok.*;

import java.util.List;

/**
 * @author
 */
@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long id;
    private String username;
    private String password;
    private Integer status;
    private List<String> roles;
}
