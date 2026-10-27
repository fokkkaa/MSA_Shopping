CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at DATE,
    updated_at DATE,
    CONSTRAINT uq_shopping_users_email UNIQUE (email)
);

CREATE TABLE shopping_lists (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    author_id BIGINT NOT NULL,
    slug VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    created_at DATE,
    updated_at DATE,
    CONSTRAINT uq_shopping_lists_slug UNIQUE (slug),
    CONSTRAINT fk_shopping_lists_author FOREIGN KEY (author_id) REFERENCES users(id)
);

CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shopping_list_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    quantity INTEGER NOT NULL,
    unit VARCHAR(255) NOT NULL,
    purchased BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATE,
    updated_at DATE,
    CONSTRAINT fk_shopping_products_list FOREIGN KEY (shopping_list_id) REFERENCES shopping_lists(id),
    CONSTRAINT fk_shopping_products_author FOREIGN KEY (author_id) REFERENCES users(id)
);
