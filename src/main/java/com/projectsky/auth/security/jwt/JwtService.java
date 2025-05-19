package com.projectsky.auth.security.jwt;

import com.projectsky.auth.dto.JwtAuthenticationDto;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    /**
     * 1. Если пользователь заходит первый раз - оба токена == null
     * 2. Для пользователя после регистрации генерируются JwtToken и RefreshJwtToken
     * */
    public JwtAuthenticationDto generateAuthToken(String email) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email));
        jwtDto.setRefreshToken(generateRefreshToken(email));
        return jwtDto;
    }

    /**
     * 1. JwtToken == expired, JwtRefreshToken != null
     * 2. JwtDto (token, refreshToken) обновляется
     * 3. JwtToken = new JwtToken, JwtRefreshToken - Остается тем же
     * */
    public JwtAuthenticationDto refreshBaseToken(String email, String refreshToken) {
        JwtAuthenticationDto jwtDto = new JwtAuthenticationDto();
        jwtDto.setToken(generateJwtToken(email));
        jwtDto.setRefreshToken(refreshToken);
        return jwtDto;
    }

    /**
     *  verifyWith(getSignInKey()) - Верификация с подписью
     *  parseSignedClaims(token)
     *      1. Проверяется валидность подписи (SignInKey)
     *      2. Проверяется структура токена (header.payload.signature)
     *      3. Извлекается Claims - тело токена (payload)
     *  getPayload() - Возвращает тело токена (Расширенный Map<String,Object>
     *      По телу токена можно получить его поля (стандартные и кастомные)
     *  getSubject() - Вернет @email т.к в токен был зашит именно он (в данном случае)
     * */
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSignInKey()) // Устанавливается ключ для верификации токена
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateJwtToken(String token) {
        try{
            Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch (ExpiredJwtException e){
            log.error("Expired JwtException" , e);
        } catch (UnsupportedJwtException e){
            log.error("Unsupported JwtException" , e);
        } catch (MalformedJwtException e){
            log.error("Malformed JwtException" , e);
        } catch (SecurityException e){
            log.error("Security Exception" , e);
        } catch (Exception e){
            log.error("Invalid token", e);
        }
        return false;
    }

    private String generateJwtToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusHours(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                // .claim(n, n) - добавит кастом поле в payload
                .signWith(getSignInKey())
                .compact();
    }

    private String generateRefreshToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusDays(1).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts.builder()
                .subject(email)
                .expiration(date)
                // .claim(n, n) - добавит кастом поле в payload
                .signWith(getSignInKey())
                .compact();
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
