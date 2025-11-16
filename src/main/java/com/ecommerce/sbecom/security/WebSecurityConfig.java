package com.ecommerce.sbecom.security;

import com.ecommerce.sbecom.model.AppRole;
import com.ecommerce.sbecom.model.Role;
import com.ecommerce.sbecom.repository.RoleRepository;
import com.ecommerce.sbecom.repository.UserRepository;
import com.ecommerce.sbecom.security.jwt.AuthEntryPointJWT;
import com.ecommerce.sbecom.security.jwt.AuthTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import com.ecommerce.sbecom.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

import java.util.Set;

@Configuration
@EnableWebSecurity
//@EnableMethodSecurity
public class WebSecurityConfig {
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    private AuthEntryPointJWT unauthorizedHandler;

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig ) throws Exception{
       return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain FilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//to make session management stateless
                .authorizeHttpRequests((requests) ->
                requests.requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/test/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        http.headers(headers -> {
            headers.frameOptions(frameOptions -> frameOptions.sameOrigin());//to allow h2-console to be displayed in a frame
        });
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow frontend origin
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));

        // Allow all HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // Allow all headers
        configuration.setAllowedHeaders(Arrays.asList("*"));

        // Allow credentials (cookies, authorization headers)
        configuration.setAllowCredentials(true);

        // How long the response from a pre-flight request can be cached (in seconds)
        configuration.setMaxAge(3600L);

        // Expose Authorization header to frontend
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/webjars/**",
                "/swagger-ui.html");
    }

    @Bean
    public CommandLineRunner initData(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args->{
            Role userRole= roleRepository.findByRoleName((AppRole.ROLE_USER))
                    .orElseGet(()->{
                        Role newRole= new Role(AppRole.ROLE_USER);
                        return roleRepository.save(newRole);
                    });
            Role adminRole= roleRepository.findByRoleName((AppRole.ROLE_ADMIN))
                    .orElseGet(()->{
                        Role newRole= new Role(AppRole.ROLE_ADMIN);
                        return roleRepository.save(newRole);
                    });
            Role sellerRole= roleRepository.findByRoleName((AppRole.ROLE_SELLER))
                    .orElseGet(()->{
                        Role newRole= new Role(AppRole.ROLE_SELLER);
                        return roleRepository.save(newRole);
                    });

            Set<Role> userRoles= Set.of(userRole);
            Set<Role> sellerRoles= Set.of(sellerRole);
            Set<Role> adminRoles= Set.of(userRole, sellerRole, adminRole);

            if(!userRepository.existsByUserName("user1")){
                User user1= new User("user1","user1@exmaple.com", passwordEncoder.encode("password1"));
                userRepository.save(user1);}

            if(!userRepository.existsByUserName("seller1")){
                User seller1= new User("seller1","seller1@example.com", passwordEncoder.encode("password1"));
                userRepository.save(seller1);}
            if(!userRepository.existsByUserName("admin")){
                User admin= new User("admin","admin@example.com", passwordEncoder.encode("adminpass"));
                userRepository.save(admin);}

            userRepository.findByUserName("user1").ifPresent(user -> {
                user.setRoles(userRoles);
                userRepository.save(user);
            });
            userRepository.findByUserName("seller1").ifPresent(user -> {
                user.setRoles(sellerRoles);
                userRepository.save(user);
            });
            userRepository.findByUserName("admin").ifPresent(user -> {
                user.setRoles(adminRoles);
                userRepository.save(user);
            });
        };
    }

}
