package fittering.mall.config;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithCustomMockUserSecurityContextFactory.class)
public @interface WithCustomMockUser {
    int id() default 1;
    String username() default "test";
    String password() default "testpassword";
    String role() default "USER";
    String email() default "test@email.com";
    String gender() default "T";
    int year() default 1990;
    int month() default 1;
    int day() default 1;
    int ageRange() default 0;
}
