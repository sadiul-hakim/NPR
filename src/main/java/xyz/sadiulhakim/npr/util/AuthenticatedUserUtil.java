package xyz.sadiulhakim.npr.util;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import xyz.sadiulhakim.npr.user.User;

import java.util.Map;

public class AuthenticatedUserUtil {
    private AuthenticatedUserUtil() {
    }

    public final static String NAME = "name";
    public final static String VERIFIED = "email_verified";
    public final static String ISS = "iss";
    public final static String NONCE = "nonce";
    public final static String PICTURE = "picture";
    public final static String EMAIL = "email";
    public final static String FAMILY_NAME = "family_name";

    public static String get(String key) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof DefaultOidcUser user) {
            Map<String, Object> claims = user.getClaims();
            return String.valueOf(claims.get(key));
        }
        return "";
    }

    public static String getName() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken user) {
            var principal = (User) user.getPrincipal();
            return principal.getName();
        }
        return "";
    }

    public static String getPicture() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UsernamePasswordAuthenticationToken user) {
            var principal = (User) user.getPrincipal();
            return principal.getPicture();
        }
        return "";
    }
}
