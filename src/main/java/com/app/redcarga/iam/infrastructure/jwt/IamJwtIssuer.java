package com.app.redcarga.iam.infrastructure.jwt;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class IamJwtIssuer {

    private final IamJwtProperties props;
    private final RSAPrivateKey privateKey;

    public IamJwtIssuer(IamJwtProperties props, RsaKeyLoader loader) {
        this.props = props;
        this.privateKey = loader.loadPrivate(props.privateKeyLocation());
    }

    public IssuedToken issueAccessToken(
            int accountId,
            int sessionId,
            List<String> sysRoles
    ) {
        try {
            Instant now = Instant.now();
            Instant exp = now.plus(props.ttl());

            // Header con kid para rotación
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .keyID(props.kid())
                    .build();

            // Claims
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .issuer(props.issuer())
                    .audience(props.audience())
                    .subject(String.valueOf(accountId))   // sub = accountId (clave)
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(exp))
                    .claim("sessionId", sessionId)
                    .claim("sys_roles", sysRoles)
                    .build();

            SignedJWT jwt = new SignedJWT(header, claims);
            jwt.sign(new RSASSASigner(privateKey));

            String token = jwt.serialize();
            long expiresIn = props.ttl().toSeconds();

            return new IssuedToken(token, "Bearer", expiresIn);
        } catch (Exception e) {
            throw new IllegalStateException("No pude firmar el JWT de IAM", e);
        }
    }

    public record IssuedToken(String accessToken, String tokenType, long expiresIn) { }
}
