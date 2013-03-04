package controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import play.Logger;
import play.mvc.Controller;

import com.nitro.activity.account.AccountActivity;
import com.nitro.common.AccountConstants;
import com.stripe.Stripe;
import com.stripe.exception.APIConnectionException;
import com.stripe.exception.APIException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.CardException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.model.Charge;
import com.stripe.model.ChargeCollection;

/**
 * Account Controller which allows entry point for
 * various functions such as:
 *  1. Display Login Page
 *  2. Process Login
 *  3. Display Payment Page
 *  4. Process Payment
 *  5. Display Thank you page
 *  
 * @author mpujara
 *
 */
public class Account extends Controller implements AccountConstants {

    /**
     * Action Mapping for all GET requests
     * @param id the action Id
     */
    public static void show(String id) {
        if (id == null || id.trim().length() == 0) {
            render();
        } else {
            if (id.startsWith("payments")) {
                fetchCharges(id);
            }
            renderTemplate(ACCOUNT_CONTEXT + id);
        }
    }
    
    /**
     * Action Mapping for all POST requests
     * @param id the action Id
     */
    public static void process(String id) {
        if (id == null || id.trim().length() == 0) {
            render();
        } else {
            String username = params.get(USERNAME);
            String password = params.get(PASSWORD);

            if (username == null || password == null) {
                renderTemplate(LOGIN_URI);
            } else {
                Logger.info("processing login for username " + username);
                session.put(USERNAME, username);
                boolean valid = processLogin(username, password);
                if (valid) {
                    renderTemplate(PROCESS_URI);
                } else {
                    renderTemplate(LOGIN_URI);
                }
            }
        }
    }
    
    /**
     * Action Mapping for processing payment POST request
     * @param id the action Id
     */
    public static void payment(String id) {
        if (id == null || id.trim().length() == 0) {
            render();
        } else {
            String token = params.get(STRIPE_TOKEN);
            String amountStr = params.get(AMOUNT);
            if (amountStr == null) {
                Logger.error("invalid payment request");
                renderTemplate(PROCESS_URI);
            }
            // Stripe only supports Integer, however, UI
            // can collect amount in any form, remove
            // decimals to have full amount
            if (amountStr.indexOf(".") != -1) {
                amountStr = amountStr.replace(".", "");
            }
            Integer amount = Integer.parseInt(amountStr);

            if (token == null || amount == null) {
                Logger.error("invalid payment request");
                renderTemplate(PROCESS_URI);
            } else {
                Logger.info("processing payment for token " + token);
                boolean processed = processPayment(token, amount);
                if (processed) {
                    renderTemplate(THANKYOU_URI);
                } else {
                    Logger.error("problem processing payment, returning back to payment information page");
                    renderTemplate(PROCESS_URI);
                }
            }
        }
    }
    
    private static void fetchCharges(String id) {
        if (id == null || id.trim().length() == 0) {
            return;
        } else {
            Stripe.apiKey = STRIPE_PRIVATE_KEY;
            Map<String, Object> chargesParams = new HashMap<String, Object>();
            //chargesParams.put("count", 20);
            ChargeCollection chargeCollection = null;
            try {
                chargeCollection = Charge.all(chargesParams);
            } catch (AuthenticationException e) {
                Logger.error(e, "cannot authenticate request");
            } catch (InvalidRequestException e) {
                Logger.error(e, "invalid request");
            } catch (APIConnectionException e) {
                Logger.error(e, "could not connect to Stripe Gateway");
            } catch (CardException e) {
                Logger.error(e, "invalid credit card information");
            } catch (APIException e) {
                Logger.error(e, "unknown error occurred");
            } 
            if (chargeCollection != null) {
                List<Charge> chargeList = chargeCollection.getData();
                renderArgs.put("charges", chargeList);
            }
        }
    }
    
    /**
     * Validate User against database
     * @param username the username
     * @param password the password
     * @return true or false depending on valid user or not
     */
    private static boolean processLogin(String username, String password) {
        AccountActivity activity = new AccountActivity();
        return activity.isValidUser(username, password);
    }

    /**
     * Process Payment Information via Stripe Payment Gateway
     * @param token the token obtained on front end via Stripe.js
     * @param amount the amount
     * @return true if payment processed successfully else false
     */
    private static boolean processPayment(String token, Integer amount) {
        Stripe.apiKey = STRIPE_PRIVATE_KEY;
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put(AMOUNT, amount);
        chargeParams.put(CURRENCY, CURRENCY_USD);
        chargeParams.put(CARD, token); // obtained with Stripe.js
        String username = session.get(USERNAME);
        if (username != null) {
            chargeParams.put(DESCRIPTION, "Charge for " + username);
        }
        try {
            Charge.create(chargeParams);
            return true;
        } catch (AuthenticationException e) {
            Logger.error(e, "cannot authenticate request");
            return false;
        } catch (InvalidRequestException e) {
            Logger.error(e, "invalid request");
            return false;
        } catch (APIConnectionException e) {
            Logger.error(e, "could not connect to Stripe Gateway");
            return false;
        } catch (CardException e) {
            Logger.error(e, "invalid credit card information");
            return false;
        } catch (APIException e) {
            Logger.error(e, "unknown error occurred");
            return false;
        }
    }
    
}
