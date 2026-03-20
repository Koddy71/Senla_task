ALTER TABLE room
ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

ALTER TABLE room
ADD CONSTRAINT chk_room_status CHECK (
    status IN ('ACTIVE', 'MAINTENANCE')
);


CREATE SEQUENCE IF NOT EXISTS service_id_seq;

SELECT setval(
        'service_id_seq', COALESCE(
            (
                SELECT MAX(id)
                FROM service
            ), 0
        )
    );

ALTER TABLE service
ALTER COLUMN id
SET DEFAULT nextval('service_id_seq');      --увеличение последовательности на 1

ALTER SEQUENCE service_id_seq OWNED BY service.id;      --закоепляем последовательность за столбцом



CREATE SEQUENCE IF NOT EXISTS guest_id_seq;

SELECT setval(
        'guest_id_seq', COALESCE(
            (
                SELECT MAX(id)
                FROM guest
            ), 0
        )
    );

ALTER TABLE guest
ALTER COLUMN id
SET DEFAULT nextval('guest_id_seq');

ALTER SEQUENCE guest_id_seq OWNED BY guest.id;