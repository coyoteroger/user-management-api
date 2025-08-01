-- Tabla USERS
CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created TIMESTAMP NOT NULL,
    modified TIMESTAMP,
    last_login TIMESTAMP,
    token VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- Tabla PHONES
CREATE TABLE phones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    number VARCHAR(255) NOT NULL,
    city_code VARCHAR(255) NOT NULL,
    country_code VARCHAR(255) NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
