package de.damien.funnyplaces.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public String createAccount(Account account) {
        if (getAccountByName(account.getName()) != null){
            System.out.println("Account with name " + account.getName() + " already exists");
            return null;
        }
        accountRepository.save(account);
        System.out.println("Account has been created: " + account.getName());
        return account.getName();
    }

    public boolean checkCredentials(Account account) {
        Account existing = getAccountByName(account.getName());
        if (existing == null) {
            return false;
        }
        return (account.getPassword().equals(existing.getPassword()));
    }

    private Account getAccountByName(String name) {
        Optional<Account> optAcc = accountRepository.findById(name);
        return optAcc.orElse(null);
    }
}
