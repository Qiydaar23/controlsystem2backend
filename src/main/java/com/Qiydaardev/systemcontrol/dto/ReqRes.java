package com.Qiydaardev.systemcontrol.dto;

import com.Qiydaardev.systemcontrol.entity.AccountUsers;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes {

    private int stausCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String name;
    private String city;
    private String role;
    private String password;
    private String email;
    private AccountUsers accountUsers;
    private List<UserDTO> accountUsersList;

}
