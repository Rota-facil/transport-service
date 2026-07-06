ALTER TABLE users_tb
ALTER COLUMN cpf TYPE VARCHAR(14);

UPDATE users_tb
SET cpf =
        SUBSTRING(cpf, 1, 3) || '.' ||
        SUBSTRING(cpf, 4, 3) || '.' ||
        SUBSTRING(cpf, 7, 3) || '-' ||
        SUBSTRING(cpf, 10, 2)
WHERE cpf NOT LIKE '%.%';