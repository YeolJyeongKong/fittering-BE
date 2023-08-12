package fittering.mall.config.auth;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

@Component
public class AppleUtils {

    public String getEmailFromIdToken(String id_token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(id_token);
            JWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();
            return getPayload.toJSONObject().get("email").toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
