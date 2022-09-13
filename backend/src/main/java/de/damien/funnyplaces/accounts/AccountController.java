package de.damien.funnyplaces.accounts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public String createAccount(@RequestBody Account account){
        String res = accountService.createAccount(account);
        if (res == null)
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        return res;

    }

    @GetMapping
    private boolean checkCredentials(@RequestBody Account account) {
        boolean res = accountService.checkCredentials(account);
        if (!res)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        return true;
    }


}
