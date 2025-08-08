package com.cnsu.common.dto;

public class LoginMessageDto {
    private String id;
    private String password;
    public LoginMessageDto(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public LoginMessageDto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
