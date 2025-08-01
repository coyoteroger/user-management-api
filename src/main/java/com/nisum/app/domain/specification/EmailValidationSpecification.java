package com.nisum.app.domain.specification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class EmailValidationSpecification implements ValidationSpecification<String> {

    private final Pattern emailPattern;

    public EmailValidationSpecification(@Value("${app.email.regex}") String emailRegex) {
        this.emailPattern = Pattern.compile(emailRegex);
    }

    @Override
    public boolean isSatisfiedBy(String email) {
        return email != null && emailPattern.matcher(email).matches();
    }

    @Override
    public String getErrorMessage() {
        return "formato invalido de email";
    }
}
