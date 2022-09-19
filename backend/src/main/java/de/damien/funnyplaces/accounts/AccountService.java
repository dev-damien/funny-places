package de.damien.funnyplaces.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import javax.persistence.EntityExistsException;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    /**
     * contains tuples that map a token to a specific user
     */
    private static final HashMap<String, String> tokens = new HashMap<String, String>();
    /**
     * repository for the database-connection
     */
    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * @param token the token of the user
     * @return the user mapped to that token
     *         or null if no user is mapped
     */
    public static String getAccountByToken(String token) {
        return tokens.get(token);
    }

    /**
     * @param userName name of the user to authenticate
     * @param token    token of the user
     * @return true if user is logged in (token maps to the username)
     *         false otherwise
     */
    public static boolean authenticateUser(String userName, String token) {
        return getAccountByToken(token).equals(userName);
    }

    /**
     * @param account the account details
     * @return the name of the created account if successful,
     *         null otherwise
     * @throws EntityExistsException if user(name) already exists
     */
    public String createAccount(Account account) {
        System.out.println("SIGNUP: received data " + account);
        if (getAccountByName(account.getName()) != null) {
            System.out.println("SIGNUP: Account already exists");
            throw new EntityExistsException();
        }
        accountRepository.save(account);
        System.out.println("SIGNUP: Account has been created successfully");
        return account.getName();
    }

    /**
     * @param account the credentials of the login try
     * @return auth-token if login was successful,
     *         null otherwise
     * @throws AuthenticationException if user does not exist or password is wrong
     */
    public String login(Account account) throws AuthenticationException {
        System.out.println("LOGIN: received data " + account);
        Account existing = getAccountByName(account.getName());
        if (existing == null) {
            //account does not exist
            System.out.println("LOGIN: Account does not exist");
            throw new AuthenticationException();
        }
        if (account.getPassword().equals(existing.getPassword())) {
            //login correct
            if (tokens.containsValue(account.getName())) {
                //user already logged in
                System.out.println("LOGIN: Logged in account tried to log in again");
                throw new AuthenticationException();
            }
            String token = generateNewToken();
            tokens.put(token, account.getName());
            System.out.println("LOGIN: Account logged in successfully. Token=" + token);
            return token;
        }
        //wrong password
        System.out.println("LOGIN: Account used wrong password for login");
        throw new AuthenticationException();
    }

    /**
     * @param token the token to authenticate the user
     * @return true, if logout was successful
     *         false otherwise
     * @throws AuthenticationException if token is invalid
     */
    public String logout(String token) throws AuthenticationException {
        System.out.println("LOGOUT: with token " + token);
        String res = tokens.remove(token);
        if (res == null) {
            System.out.println("LOGOUT: has been denied. Wrong token used");
            throw new AuthenticationException();
        }
        System.out.println("LOGOUT: User " + res + " has been logged out successfully");
        return res;
    }

    /**
     * @param name the name of the account
     * @return the account matching the name
     */
    private Account getAccountByName(String name) {
        Optional<Account> optAcc = accountRepository.findById(name);
        return optAcc.orElse(null);
    }

    /**
     * @return a new token with 32 characters
     */
    private String generateNewToken() {
        String token;
        do {
            token = UUID.randomUUID().toString();
        }
        while (tokens.containsKey(token));
        token = token.replaceAll("-", "");
        return token;
    }

}
