package com.Qiydaardev.systemcontrol.service;


import com.Qiydaardev.systemcontrol.dto.ReqRes;
import com.Qiydaardev.systemcontrol.dto.UserDTO;
import com.Qiydaardev.systemcontrol.entity.AccountUsers;
import com.Qiydaardev.systemcontrol.repository.UsersRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserManagementService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private JWTUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ReqRes register(ReqRes registrationRequest){
        ReqRes resp = new ReqRes();

        try{
            AccountUsers accountUser = new AccountUsers();
            accountUser.setEmail(registrationRequest.getEmail());
            accountUser.setCity(registrationRequest.getCity());
            accountUser.setRole(registrationRequest.getRole());
            accountUser.setName(registrationRequest.getName());
            accountUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            AccountUsers accountUsersResult = usersRepo.save(accountUser);

            if (accountUsersResult.getId() > 0 ){
                resp.setAccountUsers((accountUsersResult));
                resp.setMessage("User Saved Successfully");
                resp.setStausCode(200);
            }


        }catch (Exception e){
            resp.setStausCode(500);
            resp.setError(e.getMessage());
        }
        return resp;
    }

    public ReqRes login(ReqRes loginRequest,  HttpServletRequest request){
        System.out.println(loginRequest.getEmail() + " == " + loginRequest.getPassword());
        ReqRes response = new ReqRes();
        try {
            Authentication authentication= authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            System.out.println("Login auth request " + authentication);
            var user = usersRepo.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStausCode(200);

            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        }catch (Exception e){
            response.setStausCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    public ReqRes getMyInfo(String email) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<AccountUsers> userOptional = usersRepo.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setAccountUsers(userOptional.get());
                reqRes.setStausCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStausCode(404);
                reqRes.setMessage("User not found for update");
            }

        }catch (Exception e){
            reqRes.setStausCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes updateUser(Integer userId, AccountUsers updatedUser) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<AccountUsers> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                AccountUsers existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());

                // Check if password is present in the request
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    // Encode the password and update it
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                AccountUsers savedUser = usersRepo.save(existingUser);
                reqRes.setAccountUsers(savedUser);
                reqRes.setStausCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStausCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStausCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes getUserById(Integer id) {

        ReqRes reqRes = new ReqRes();
        try {
            AccountUsers usersById = usersRepo.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setAccountUsers(usersById);
            reqRes.setStausCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStausCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }

    public ReqRes getAllUsers() {
        ReqRes reqRes = new ReqRes();


        try {
            List<AccountUsers> result = usersRepo.findAll();
            if (!result.isEmpty()) {
                List<UserDTO> userDTOList = result.stream()
                        .map(user -> new UserDTO(
                                user.getId(),
                                user.getName(),
                                user.getEmail(),
                                user.getCity(),
                                user.getRole()
                        ))
                        .collect(Collectors.toList());


                reqRes.setAccountUsersList(userDTOList);
                reqRes.setStausCode(200);
                System.out.println("Get all user requet received");
                System.out.println("Result " + result);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStausCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStausCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }

    public ReqRes refreshToken(ReqRes refreshTokenReqiest) {
        ReqRes response = new ReqRes();
        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
            AccountUsers users = usersRepo.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStausCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenReqiest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStausCode(200);
            return response;

        }catch (Exception e){
            response.setStausCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }

    public ReqRes deleteUser(Integer userId) {
        ReqRes reqRes = new ReqRes();
        try {
            Optional<AccountUsers> userOptional = usersRepo.findById(userId);
            if (userOptional.isPresent()) {
                usersRepo.deleteById(userId);
                reqRes.setStausCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStausCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStausCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }
}
