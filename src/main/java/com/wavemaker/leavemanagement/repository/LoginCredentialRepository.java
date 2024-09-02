package com.wavemaker.leavemanagement.repository;

import com.wavemaker.leavemanagement.model.LoginCredential;

public interface LoginCredentialRepository {
    public int isValidate(LoginCredential loginCredential);
    public LoginCredential addEmployeeLogin (LoginCredential loginCredential);
}
