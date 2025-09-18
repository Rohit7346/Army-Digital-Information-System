-- Insert default admin if not exists
INSERT INTO admins (username, password)
SELECT 'admin', '$2a$10$8.UnVuG9HpHF7pOGuQcZ/uL9f6J4q1q1q1q1q1q1q1q1q1q1q1q1q1q'
WHERE NOT EXISTS (SELECT 1 FROM admins WHERE username = 'admin');