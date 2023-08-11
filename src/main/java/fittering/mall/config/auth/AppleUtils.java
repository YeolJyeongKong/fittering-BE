package fittering.mall.config.auth;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppleUtils {

    @Value("${APPLE.AUD}")
    private String AUD;

    @Value("${APPLE.WEBSITE.URL}")
    private String APPLE_WEBSITE_URL;

    @Value("${APPLE.NONCE}")
    private String NONCE;

    public Map<String, String> getMetaInfo() {
        Map<String, String> metaInfo = new HashMap<>();
        metaInfo.put("CLIENT_ID", AUD);
        metaInfo.put("REDIRECT_URI", APPLE_WEBSITE_URL);
        metaInfo.put("NONCE", NONCE);
        return metaInfo;
    }

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
