package com.nitro.activity.account;

import java.util.List;

import models.com.nitro.activity.account.model.Account;
import play.Logger;

/**
 * Account related activities
 * @author mpujara
 *
 */
public class AccountActivity {
    
    /**
     * validate user credentials
     * 
     * @param username the username
     * @param password the password
     * @return true if credentials matches else false
     */
    public boolean isValidUser(String username, String password) {
        List<Account> users = Account.find("username", username).fetch();
        if (users == null || users.size() == 0) {
            Logger.info("cannot find any user for username " + username);
            return false;
        } else {
            Account user = users.get(0);
            if (user.getPassword().equals(password)) {
                Logger.info("valid credentials for user " + username);
                return true;
            } 
        }
        return false;
    }

}
