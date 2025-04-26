package xyz.sadiulhakim.npr.util.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import xyz.sadiulhakim.npr.security.CustomUserDetails;

public class AuthenticatedUserUtil {
    private AuthenticatedUserUtil() {
    }

    public final static String NAME = "name";
    public final static String VERIFIED = "email_verified";
    public final static String ISS = "iss";
    public final static String SUB = "sub";
    public final static String NONCE = "nonce";
    public final static String PICTURE = "picture";
    public final static String EMAIL = "email";
    public final static String FAMILY_NAME = "family_name";

    public static String get(String key) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof OAuth2User user) {
            return user.getAttribute(key);
        }
        return "";
    }

    public static String getName() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken user) {
            var principal = (CustomUserDetails) user.getPrincipal();
            return principal.getName();
        } else if (authentication.getPrincipal() instanceof OAuth2User user) {
            return user.getAttribute(NAME);
        }
        return "";
    }

    public static String getPicture(String prefix) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof UsernamePasswordAuthenticationToken user) {
            var principal = (CustomUserDetails) user.getPrincipal();
            return prefix + principal.getPicture();
        } else if (authentication.getPrincipal() instanceof OAuth2User user) {
            return user.getAttribute(PICTURE);
        }
        return "";
    }
}
