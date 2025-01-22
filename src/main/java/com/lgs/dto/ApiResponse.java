package com.lgs.dto;

public class ApiResponse {

    private boolean success;
    private String message;
    private String token; // Adicionado para incluir o JWT
    private String userType;

    // Construtor sem token e userType
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Construtor com token e userType
    public ApiResponse(boolean success, String message, String token) {
        this.success = success;
        this.message = message;
        this.token = token;
    }

    // Construtor completo com token e userType
    public ApiResponse(boolean success, String message, String token, String userType) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.userType = userType;
    }

    // Getters e setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}
