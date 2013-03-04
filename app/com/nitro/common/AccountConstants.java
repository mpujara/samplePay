package com.nitro.common;

/**
 * Constant Values for Account related functions
 * @author mpujara
 *
 */
public interface AccountConstants {
    
    // Render Templates Mapping
    String ACCOUNT_CONTEXT = "Account/";
    
    String LOGIN_URI = "Account/login.html";
    
    String SIGNUP_URI = "Account/signup.html";
    
    String PROCESS_URI= "Account/process.html";
    
    String THANKYOU_URI = "Account/thankyou.html";
    
    // Parameter Names
    String USERNAME = "username";
    
    String PASSWORD = "password";
    
    String STRIPE_TOKEN = "stripeToken";
    
    String AMOUNT = "amount";
    
    String CURRENCY = "currency";
    
    String CARD = "card";
    
    String DESCRIPTION = "description";
    
    // Constant Values
    // currently only domestic market is supported so no need to define global currencies
    String CURRENCY_USD = "usd";
    
    // TODO: extract apiKey in JNDI context
    String STRIPE_PRIVATE_KEY = "sk_test_WpwzsEjheO5h63ulKwrkLLna";
    

}
