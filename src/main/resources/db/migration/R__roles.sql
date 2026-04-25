INSERT INTO bank.roles (name, created_by, updated_by)
VALUES 
  ('ADMIN', 'system', 'system'),
  ('USER', 'system', 'system')
ON CONFLICT (name) DO UPDATE 
SET updated_at = NOW();