-- Natural keys avoid overwriting records when adopting the Lab 3 database.
INSERT INTO users (first_name, last_name, email, password, created_at, updated_at)
SELECT 'Angelina', 'Zanko', 'shopping.demo@example.com', 'demo-shopping', CURRENT_DATE, CURRENT_DATE
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'shopping.demo@example.com');

INSERT INTO shopping_lists (author_id, slug, name, description, created_at, updated_at)
SELECT id, 'weekly-shopping-demo', 'Weekly groceries', 'Sample list created by Flyway', CURRENT_DATE, CURRENT_DATE
FROM users WHERE email = 'shopping.demo@example.com'
AND NOT EXISTS (SELECT 1 FROM shopping_lists WHERE slug = 'weekly-shopping-demo');

INSERT INTO products (shopping_list_id, author_id, name, quantity, unit, purchased, created_at, updated_at)
SELECT id, author_id, 'Milk', 2, 'l', FALSE, CURRENT_DATE, CURRENT_DATE
FROM shopping_lists WHERE slug = 'weekly-shopping-demo'
AND NOT EXISTS (SELECT 1 FROM products WHERE shopping_list_id = shopping_lists.id AND name = 'Milk');

INSERT INTO products (shopping_list_id, author_id, name, quantity, unit, purchased, created_at, updated_at)
SELECT id, author_id, 'Bread', 1, 'pcs', FALSE, CURRENT_DATE, CURRENT_DATE
FROM shopping_lists WHERE slug = 'weekly-shopping-demo'
AND NOT EXISTS (SELECT 1 FROM products WHERE shopping_list_id = shopping_lists.id AND name = 'Bread');

INSERT INTO products (shopping_list_id, author_id, name, quantity, unit, purchased, created_at, updated_at)
SELECT id, author_id, 'Apples', 3, 'kg', TRUE, CURRENT_DATE, CURRENT_DATE
FROM shopping_lists WHERE slug = 'weekly-shopping-demo'
AND NOT EXISTS (SELECT 1 FROM products WHERE shopping_list_id = shopping_lists.id AND name = 'Apples');
