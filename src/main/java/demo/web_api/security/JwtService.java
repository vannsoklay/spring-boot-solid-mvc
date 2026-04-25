package demo.web_api.security;

import demo.web_api.model.entity.UserEntity;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Slf4j
@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.access-expiration}")
    private long accessExpiration;

    @Value("${security.jwt.refresh-expiration}")
    private long refreshExpiration;

    // Store only JTI instead of full token (better memory usage)
    private final Set<String> blacklist = ConcurrentHashMap.newKeySet();

    // ========================
    // TOKEN GENERATION
    // ========================

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(userDetails, accessExpiration, new HashMap<>());
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(userDetails, refreshExpiration, new HashMap<>());
    }

    public String generateTokenForUser(UserEntity user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        return buildToken(user, accessExpiration, claims);
    }

    private String buildToken(UserDetails userDetails, long expiration, Map<String, Object> extraClaims) {

        String jti = UUID.randomUUID().toString();

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setId(jti)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ========================
    // CLAIM EXTRACTION
    // ========================

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractJti(String token) {
        return extractAllClaims(token).getId();
    }

    public Long extractUserId(String token) {
        return extractClaim(token, claims -> claims.get("userId", Long.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    // ========================
    // VALIDATION
    // ========================

    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);

            return username.equals(userDetails.getUsername())
                    && !isTokenExpired(token)
                    && !isBlacklisted(token);

        } catch (JwtException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token) && !isBlacklisted(token);
        } catch (JwtException e) {
            log.error("JWT validation error: {}", e.getMessage());
            return false;
        }
    }

    // ========================
    // BLACKLIST (LOGOUT)
    // ========================

    public void blacklistToken(String token) {
        String jti = extractJti(token);
        blacklist.add(jti);
        log.info("Token blacklisted: {}", jti);
    }

    public boolean isBlacklisted(String token) {
        String jti = extractJti(token);
        return blacklist.contains(jti);
    }

    // ========================
    // EXPIRATION
    // ========================

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ========================
    // INTERNAL
    // ========================

    private Claims extractAllClaims(String token) {
        return parser().parseClaimsJws(token).getBody();
    }

    private JwtParser parser() {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}