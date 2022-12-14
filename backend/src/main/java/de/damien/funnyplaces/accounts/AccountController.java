package de.damien.funnyplaces.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.naming.AuthenticationException;
import javax.persistence.EntityExistsException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/signup")
    public String createAccount(@RequestBody Account account) throws ResponseStatusException {
        try {
            return accountService.createAccount(account);
        } catch (EntityExistsException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody Account account) throws ResponseStatusException {
        try {
            return accountService.login(account);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping("/logout")
    public String logout(@RequestHeader("token") String token) throws ResponseStatusException {
        try {
            return accountService.logout(token);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/accounts/{name}")
    public String delete(@PathVariable("name") String name, @RequestHeader("password") String password) throws ResponseStatusException {
        try {
            return accountService.delete(name, password);
        } catch (AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } catch (NoSuchElementException ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/status")
    public String status() {
        return "Server is up";
    }

}
