INSERT INTO customer (username, password, role) VALUES ('admin', 'admin123', 'ADMIN');
INSERT INTO customer (username, password, role) VALUES ('testCustomer', 'password', 'USER');

INSERT INTO asset (customer_id, asset_name, size, usable_size)
VALUES (1, 'TRY', 1000000, 1000000);

INSERT INTO asset (customer_id, asset_name, size, usable_size)
VALUES (2, 'TRY', 50000, 50000);