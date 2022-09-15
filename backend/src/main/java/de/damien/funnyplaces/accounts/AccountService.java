package de.damien.funnyplaces.accounts;

import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    private final HashMap<String, String> tokens = new HashMap<String, String>();

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    /**
     *
     * @param   account the account details
     * @return  the name of the created account if successful,
     *          null otherwise
     */
    public String createAccount(Account account) {
        if (getAccountByName(account.getName()) != null){
            System.out.println("Account with name " + account.getName() + " already exists");
            return null;
        }
        accountRepository.save(account);
        System.out.println("Account has been created: " + account.getName());
        return account.getName();
    }

    /**
     *
     * @param   account the credentials of the login try
     * @return  auth-token if login was successful,
     *          null otherwise
     */
    public String login(Account account) {
        Account existing = getAccountByName(account.getName());
        if (existing == null) {
            //account does not exist
            return null;
        }
        if (account.getPassword().equals(existing.getPassword())){
            //login correct
            String token = generateNewToken();
            tokens.put(token, account.getName());
            return token;
        }
        //wrong password
        return null;
    }

    /**
     *
     * @param   token the token to authenticate the user
     * @return  true, if logout was successful
     *          false otherwise
     */
    public boolean logout(String token){
        String res = tokens.remove(token);
        return res != null;
    }

    /**
     *
     * @param name the name of the account
     * @return the account matching the name
     */
    private Account getAccountByName(String name) {
        Optional<Account> optAcc = accountRepository.findById(name);
        return optAcc.orElse(null);
    }

    /**
     *
     * @return  a new token with 32 characters
     */
    private String generateNewToken(){
        String token;
        do {
            token = UUID.randomUUID().toString();
        }
        while (tokens.containsKey(token));
        return token;
    }

}
