package de.damien.funnyplaces.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;

@RestController
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/signup")
    public String createAccount(@RequestBody Account account) {
        String res = accountService.createAccount(account);
        if (res == null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        return res;

    }

    @PostMapping("/login")
    public String login(@RequestBody Account account) {
        String res = accountService.login(account);
        if (res == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return res;
    }

    @PostMapping("/logout")
    public String logout(@PathVariable String token) throws ResponseStatusException {
        try {
            return accountService.logout(token);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

}
