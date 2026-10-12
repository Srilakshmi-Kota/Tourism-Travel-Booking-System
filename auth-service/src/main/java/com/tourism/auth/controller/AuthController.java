package com.tourism.auth.controller;

import com.tourism.auth.model.User;
import com.tourism.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    private final AuthService service;
    public AuthController(AuthService service){ this.service = service; }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest r){
        try {
            User u = service.register(r.name(), r.email(), r.password(), r.phone());
            return ResponseEntity.ok(Map.of("id",u.getId(),"name",u.getName(),"email",u.getEmail(),"phone",u.getPhone()==null?"":u.getPhone()));
        } catch(Exception e){ return ResponseEntity.badRequest().body(Map.of("error", msg(e))); }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest r){
        try { return ResponseEntity.ok(service.login(r.email(), r.password())); }
        catch(Exception e){ return ResponseEntity.status(401).body(Map.of("error", msg(e))); }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader(value="X-Auth-Token", required=false) String token){
        try { service.logout(token); return ResponseEntity.ok(Map.of("message","Logged out")); }
        catch(Exception e){ return ResponseEntity.status(401).body(Map.of("error", msg(e))); }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader(value="X-Auth-Token", required=false) String token){
        try { return ResponseEntity.ok(service.response(service.validate(token))); }
        catch(Exception e){ return ResponseEntity.status(401).body(Map.of("error", msg(e))); }
    }

    private String msg(Exception e){ return e.getMessage()==null ? "Request failed" : e.getMessage(); }
    public record RegisterRequest(String name,String email,String password,String phone){}
    public record LoginRequest(String email,String password){}
}
