ALTER TABLE users_tb
    ADD COLUMN IF NOT EXISTS cpf VARCHAR(11);

WITH numbered AS (
    SELECT
        user_id,
        ROW_NUMBER() OVER (ORDER BY user_id) AS rn
    FROM users_tb
    WHERE cpf IS NULL
)
UPDATE users_tb u
SET cpf = LPAD(numbered.rn::text, 11, '0')
    FROM numbered
WHERE u.user_id = numbered.id;

ALTER TABLE users_tb
    ADD CONSTRAINT u_cpf UNIQUE (cpf);