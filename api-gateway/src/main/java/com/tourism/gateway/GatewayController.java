package com.tourism.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class GatewayController {
    private final RestClient packages;
    private final RestClient bookings;
    private final RestClient payments;
    private final RestClient auth;

    public GatewayController(
            @Value("${package.service.url}") String packageUrl,
            @Value("${booking.service.url}") String bookingUrl,
            @Value("${payment.service.url}") String paymentUrl,
            @Value("${auth.service.url}") String authUrl) {
        packages = RestClient.builder().baseUrl(packageUrl).build();
        bookings = RestClient.builder().baseUrl(bookingUrl).build();
        payments = RestClient.builder().baseUrl(paymentUrl).build();
        auth = RestClient.builder().baseUrl(authUrl).build();
    }

    @PostMapping("/auth/register")
    public Object register(@RequestBody Object request) {
        return auth.post().uri("/api/auth/register").body(request).retrieve().body(Object.class);
    }

    @PostMapping("/auth/login")
    public Object login(@RequestBody Object request) {
        return auth.post().uri("/api/auth/login").body(request).retrieve().body(Object.class);
    }

    @PostMapping("/auth/logout")
    public Object logout(@RequestHeader(value="X-Auth-Token", required=false) String token) {
        return auth.post().uri("/api/auth/logout").header("X-Auth-Token", safeToken(token)).retrieve().body(Object.class);
    }

    @GetMapping("/auth/me")
    public Object me(@RequestHeader(value="X-Auth-Token", required=false) String token) {
        return validate(token);
    }

    @GetMapping("/packages")
    public Object packages() {
        return packages.get().uri("/api/packages").retrieve().body(Object.class);
    }

    @GetMapping("/packages/{id}")
    public Object packageOne(@PathVariable Long id) {
        return packages.get().uri("/api/packages/{id}", id).retrieve().body(Object.class);
    }

    @PostMapping("/bookings")
    public Object booking(@RequestHeader(value="X-Auth-Token", required=false) String token,
                          @RequestBody BookingRequest request) {
        Map<String,Object> user = validate(token);
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("userId", ((Number) user.get("id")).longValue());
        body.put("packageId", request.packageId());
        body.put("bookingType", request.bookingType());
        body.put("travellers", request.travellers());
        return bookings.post().uri("/api/bookings").body(body).retrieve().body(Object.class);
    }

    @GetMapping("/bookings")
    public Object bookings(@RequestHeader(value="X-Auth-Token", required=false) String token) {
        Map<String,Object> user = validate(token);
        Long userId = ((Number) user.get("id")).longValue();
        return bookings.get().uri("/api/bookings/user/{userId}", userId).retrieve().body(Object.class);
    }

    @PutMapping("/bookings/{id}/cancel")
    public Object cancel(@RequestHeader(value="X-Auth-Token", required=false) String token, @PathVariable Long id) {
        Map<String,Object> user = validate(token);
        Long userId = ((Number) user.get("id")).longValue();
        List<?> mine = bookings.get().uri("/api/bookings/user/{userId}", userId).retrieve().body(List.class);
        boolean owns = mine != null && mine.stream().anyMatch(x -> x instanceof Map<?,?> m && Objects.equals(((Number)m.get("id")).longValue(), id));
        if (!owns) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only cancel your own bookings.");
        return bookings.put().uri("/api/bookings/{id}/cancel", id).retrieve().body(Object.class);
    }

    @PostMapping("/payments")
    public Object payment(@RequestHeader(value="X-Auth-Token", required=false) String token,
                          @RequestBody PaymentRequest request) {
        validate(token);
        return payments.post().uri("/api/payments").body(request).retrieve().body(Object.class);
    }

    private Map<String,Object> validate(String token) {
        return auth.get().uri("/api/auth/validate").header("X-Auth-Token", safeToken(token)).retrieve().body(Map.class);
    }

    private String safeToken(String token) { return token == null ? "" : token; }
    public record BookingRequest(Long packageId, String bookingType, int travellers) {}
    public record PaymentRequest(Long bookingId, double amount, String method) {}
}
