package com.Qiydaardev.systemcontrol.controller;

import com.Qiydaardev.systemcontrol.dto.ReqRes;
import com.Qiydaardev.systemcontrol.entity.AccountUsers;
import com.Qiydaardev.systemcontrol.service.JWTUtils;
import com.Qiydaardev.systemcontrol.service.UserManagementService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserManagementController {
    private final JWTUtils jwtUtils;

    @Autowired
    private UserManagementService userManagementService;

    public UserManagementController(JWTUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/auth/register")
    public ResponseEntity<ReqRes> register(@RequestBody ReqRes reg){
        return ResponseEntity.ok(userManagementService.register(reg));
    }
    @PostMapping("/auth/login")
    public ResponseEntity<ReqRes> login(@RequestBody ReqRes req, HttpServletRequest request){
        System.out.println(req.getEmail() + " -- " +  req.getPassword());
        return ResponseEntity.ok(userManagementService.login(req, request));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req){
        return ResponseEntity.ok(userManagementService.refreshToken(req));
    }

    @GetMapping("/admin/get-all-users")
    public ResponseEntity<ReqRes> getAllUsers(){
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @GetMapping("/admin/get-all-users/{id}")
    public ResponseEntity<ReqRes> getUserById(@PathVariable Integer id){
        System.out.println("id "+id);
        System.out.println("get request received get all users");
        return ResponseEntity.ok(userManagementService.getUserById(id));
    }
    @PutMapping("/admin/update/{id}")
    public ResponseEntity<ReqRes> updateUser(@PathVariable Integer id, @RequestBody AccountUsers reqres){
        System.out.println("get request received with id");
        return ResponseEntity.ok(userManagementService.updateUser(id, reqres));
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<ReqRes> getMyProfile(HttpServletRequest request){
        System.out.println("get request received");
        System.out.println("Request" + request.getHeader("Authorization"));


        String email = jwtUtils.extractUsername(request.getHeader("Authorization").split(" ")[1].trim());
        System.out.println(email);
        ReqRes reponse = userManagementService.getMyInfo(email);
        return ResponseEntity.status(reponse.getStausCode()).body(reponse);
    }
    @DeleteMapping("admin/delete/{userId}")
    public ResponseEntity<ReqRes> deleteUser(@PathVariable Integer userId){
        return ResponseEntity.ok(userManagementService.deleteUser(userId));
    }



}
