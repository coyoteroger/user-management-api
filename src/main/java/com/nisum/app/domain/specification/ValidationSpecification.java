package com.nisum.app.domain.specification;

public interface ValidationSpecification<T> {
    boolean isSatisfiedBy(T candidate);
    String getErrorMessage();
}

