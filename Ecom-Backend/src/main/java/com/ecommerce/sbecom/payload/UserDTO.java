package com.ecommerce.sbecom.payload;

import com.ecommerce.sbecom.model.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Long userId;
    private String userName;
    private String email;
    private String password;
    private Set<Role> roles = new HashSet<>();
    private AddressDTO address;
    private CartDTO cart;
}
