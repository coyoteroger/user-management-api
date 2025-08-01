package com.nisum.app.domain.specification;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidationSpecification implements ValidationSpecification<String> {

    private final Pattern passwordPattern;

    public PasswordValidationSpecification(@Value("${app.password.regex}") String passwordRegex) {
        this.passwordPattern = Pattern.compile(passwordRegex);
    }

    @Override
    public boolean isSatisfiedBy(String password) {
        return password != null && passwordPattern.matcher(password).matches();
    }

    @Override
    public String getErrorMessage() {
        return "el password debe contener al menos 8 caracteres, incluyendo mayusculas, letras minisculas y numeros";
    }
}
