package com.ecommerce.sbecom.service;

import com.ecommerce.sbecom.model.AppRole;
import com.ecommerce.sbecom.model.Role;
import com.ecommerce.sbecom.model.User;
import com.ecommerce.sbecom.payload.AuthenticationResult;
import com.ecommerce.sbecom.payload.UserDTO;
import com.ecommerce.sbecom.payload.UserResponse;
import com.ecommerce.sbecom.repository.RoleRepository;
import com.ecommerce.sbecom.repository.UserRepository;
import com.ecommerce.sbecom.security.jwt.JWTUtils;
import com.ecommerce.sbecom.security.request.LoginRequest;
import com.ecommerce.sbecom.security.request.SignupRequest;
import com.ecommerce.sbecom.security.response.MessageResponse;
import com.ecommerce.sbecom.security.response.UserInfoResponse;
import com.ecommerce.sbecom.security.services.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ModelMapper modelMapper;
    @Override
    public ResponseEntity<?> register(SignupRequest signUpRequest) {
        if(userRepository.existsByUserName(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }
        if(userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email already exists!"));
        }
        User user= new User(signUpRequest.getUsername(),signUpRequest.getEmail(),passwordEncoder.encode(signUpRequest.getPassword()));
        Set<String> strRoles= signUpRequest.getRole();
        Set<Role> roles= new HashSet<>();
        if(strRoles==null) {
            Role userRole= roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        }else{
            strRoles.forEach(role->{
                switch (role){
                    case "admin":
                        Role adminRole= roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole= roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole= roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(()-> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


    @Override
    public AuthenticationResult login(LoginRequest loginRequest) {

        Authentication authentication;
        authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));


        //storing object in security context
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails= (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie= jwtUtils.generateJWTCookie(userDetails);
        List<String> roles= userDetails.getAuthorities().stream()
                .map(item-> item.getAuthority())
                .collect(Collectors.toList());
       UserInfoResponse response= new UserInfoResponse( userDetails.getId(),userDetails.getUsername(),jwtCookie.toString(), userDetails.getEmail(), roles);
       return new AuthenticationResult(response, jwtCookie);
    }
    @Override
    public UserInfoResponse getCurrentUserDetails(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        UserInfoResponse response = new UserInfoResponse(userDetails.getId(),
                userDetails.getUsername(), roles);

        return response;
    }
    @Override
    public ResponseCookie logOutUser() {
        return jwtUtils.getCleanJwtCookie();
    }

    @Override
    public UserResponse getAllSellers(Pageable pageable) {
        Page<User> allUsers = userRepository.findByRoleName(AppRole.ROLE_SELLER, pageable);
        List<UserDTO> userDTOs = allUsers.getContent()
                .stream()
                .map(p -> modelMapper.map(p,UserDTO.class))
                .toList();
        UserResponse response= new UserResponse();
        response.setContent(userDTOs);
        response.setPageNumber(allUsers.getNumber());
        response.setPageSize(allUsers.getSize());
        response.setTotalElements(allUsers.getTotalElements());
        response.setTotalPages(allUsers.getTotalPages());
        response.setLastPage(allUsers.isLast());
        return response;
    }

}
