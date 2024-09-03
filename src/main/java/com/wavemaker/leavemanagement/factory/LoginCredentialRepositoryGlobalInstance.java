package com.wavemaker.leavemanagement.factory;

import com.wavemaker.leavemanagement.repository.LoginCredentialRepository;
import com.wavemaker.leavemanagement.repository.impl.LoginCredentialRepositoryImpl;

public class LoginCredentialRepositoryGlobalInstance {
    private static LoginCredentialRepository loginCredentialRepository = null;

    public static LoginCredentialRepository getLoginCredentialRepositoryInstance() {
        if (loginCredentialRepository == null) {
            synchronized (LoginCredentialRepositoryGlobalInstance.class) {
                if (loginCredentialRepository == null) {
                    loginCredentialRepository = new LoginCredentialRepositoryImpl();
                }

            }

        }
        return loginCredentialRepository;
    }


}
