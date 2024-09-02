package com.wavemaker.leavemanagement.service;

import com.wavemaker.leavemanagement.model.LoginCredential;

public interface LoginCredentialService {
    public int isValidate(LoginCredential loginCredential);
    public LoginCredential addEmployeeLogin (LoginCredential loginCredential);
}
