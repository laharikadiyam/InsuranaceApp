package com.insurance.app.auth.service;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

//my auth packages and config
import com.insurance.app.auth.entity.Roles;
import com.insurance.app.auth.entity.Users;
import com.insurance.app.auth.JWT_package.JwtTokenProvider;
import com.insurance.app.auth.repository.UserRepository;
import com.insurance.app.auth.dto.ChangePasswordRequest;
import com.insurance.app.auth.dto.ForgotPasswordRequest;
import com.insurance.app.auth.dto.LoginRequest;
import com.insurance.app.auth.dto.LoginResponse;
import com.insurance.app.auth.dto.RegistrationRequest;
import com.insurance.app.auth.dto.UserProfileResponse;
import com.insurance.app.auth.exceptionHandling.AccessForbiddenException;

import com.insurance.app.auth.exceptionHandling.UserAlreadyExistsException;
import com.insurance.app.auth.exceptionHandling.UserNotFoundException;
import com.insurance.app.auth.exceptionHandling.WrongStatusException;




@Service
public class AdminService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AdminService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    
    //login
    public LoginResponse loginAdmin(LoginRequest req)throws AccessForbiddenException{
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        Users user = userRepository.findByEmail(req.getEmail())
        		.orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRole() != Roles.ADMIN) {
            throw new AccessForbiddenException("Access Denied for Admin area");
        }
        if (!user.isActive()) {
            throw new WrongStatusException("Admin account not activated");
        }
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getRole());
        return new LoginResponse(user.getId(),user.getName(), user.getEmail(), user.getRole(), token);
    }
    
    //load profile
    public UserProfileResponse loadUserProfile(String email) {
        Users user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return new UserProfileResponse(user.getId(),user.getName(), user.getEmail(), user.getRole(),
                user.isActive(), user.getPanNumber());
    }
    
    
    //change password
    public void changePassword(ChangePasswordRequest req) {
    	
        Users user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        if (!passwordEncoder.matches(req.getOldPassword(), user.getPassword())) {
            throw new WrongStatusException("Old password incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }
    
    //reset password / forgot password
    public void forgotPassword(ForgotPasswordRequest req) {
        Users user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (!user.getPanNumber().equals(req.getPanNumber())) {
            throw new WrongStatusException("Invalid PAN Number");
        }
        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);
    }
    
    
    //activate new admin
    public String activateAdmin(Long adminId) {
        Users admin = userRepository.findById(adminId).orElseThrow(() -> new UserNotFoundException("Admin Not Found"));
        if (admin.getRole() != Roles.ADMIN) {
            throw new WrongStatusException("User is not admin");
        }
        if (admin.isActive()) {
            return "Admin already active";
        }
        admin.setActive(true);
        userRepository.save(admin);
        return "Admin account activated successfully";
    }
    
    //deactivate any user
    public String deactivateUser(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User Not Found"));
        user.setActive(false);
        userRepository.save(user);
        return "User account deactivated";
    }
    
    //get list of accounts that are inactive
    public List<UserProfileResponse> listInactiveUsersByRole(Roles role) {
        List<Users> users = userRepository.findAllByRoleAndIsActiveFalse(role);
        return users.stream()
                .map(u -> new UserProfileResponse(u.getId(),u.getName(), u.getEmail(), u.getRole(), u.isActive(), u.getPanNumber()))
                .collect(Collectors.toList());
    }
    
    //  find user by id or mail
    public UserProfileResponse findUserByEmailOrId(String email, Long id) {
        Users user;
        if(email != null && !email.isEmpty()) {
            user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User Not Found"));
            
        } 
        else if (id != null) {
            user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User Not Found"));
            
        } 
        else {
            throw new WrongStatusException("Email or ID must be provided");
            
        }
        return new UserProfileResponse(user.getId(),user.getName(), user.getEmail(), user.getRole(), user.isActive(), user.getPanNumber());
    }


    public String register(RegistrationRequest req) throws UserAlreadyExistsException{
		
    	Optional<Users> existingUser = userRepository.findByEmail(req.getEmail());
	    if (existingUser.isPresent()) {
	        throw new UserAlreadyExistsException("User Already Exists witht the mail ID");
	    }
		
		Users user = new Users();
	    user.setName(req.getName());
	    user.setEmail(req.getEmail());
	    user.setPassword(passwordEncoder.encode(req.getPassword()));
	    user.setPanNumber(req.getPanNumber());
	    user.setRole(req.getRole());
	   
	    user.setActive(req.getRole() == Roles.CUSTOMER);

	    userRepository.save(user);
	    return "User Registration Successful";
		
	}
    
    public String activateCustomer(Long id) {
        Users user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getRole() != Roles.CUSTOMER) {
            throw new RuntimeException("User is not a customer");
        }
        
        user.setActive(true);
        userRepository.save(user);
        return "Customer activated successfully";
    }
    public List<UserProfileResponse> getAllUsers() {
        List<Users> allUsers = userRepository.findAll();
        return allUsers.stream()
            .map(this::convertToUserProfileResponse)
            .collect(Collectors.toList());
    }
    private UserProfileResponse convertToUserProfileResponse(Users user) {
        return new UserProfileResponse(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole(),
            user.isActive(),
            user.getPanNumber()
        );
    }
 
    

}

