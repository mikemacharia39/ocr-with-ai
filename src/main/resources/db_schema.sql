-- data for temperature table in postgres
CREATE TABLE temperature
(
    id          BIGSERIAL PRIMARY KEY,
    sensor_ref  VARCHAR(50)      NOT NULL,
    temp        DOUBLE PRECISION NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    location    VARCHAR(100)
);

INSERT INTO temperature (sensor_ref, temp, recorded_at, location)
VALUES ('sensor-A1', 22.5, '2025-07-08T08:30:00+00', 'Nairobi, Kenya'),
       ('sensor-B3', 28.1, '2025-07-08T08:45:00+00', 'Kikuyu, Kenya'),
       ('sensor-C2', 19.3, '2025-07-08T09:00:00+00', 'Nyeri, Kenya'),
       ('sensor-A1', 21.7, '2025-07-08T09:15:00+00', 'Nairobi, Kenya'),
       ('sensor-D4', 35.2, '2025-07-08T09:30:00+00', 'Mombasa, Kenya'),
       ('sensor-B3', 27.8, '2025-07-08T09:45:00+00', 'Kikuyu, Kenya'),
       ('sensor-E5', 17.4, '2025-07-08T10:00:00+00', 'Nakuru, Kenya'),
       ('sensor-F6', 23.9, '2025-07-08T10:15:00+00', 'Machakos, Kenya'),
       ('sensor-G7', 25.0, '2025-07-08T10:30:00+00', 'Kisumu, Kenya'),
       ('sensor-H8', 29.6, '2025-07-08T10:45:00+00', 'Eldoret, Kenya'),
       ('sensor-A1', 25.5, '2025-07-10T08:30:00+00', 'Nairobi, Kenya'),
       ('sensor-B3', 27.1, '2025-07-10T08:45:00+00', 'Kikuyu, Kenya'),
       ('sensor-C2', 17.3, '2025-07-10T09:00:00+00', 'Nyeri, Kenya'),
       ('sensor-A1', 24.7, '2025-07-10T09:15:00+00', 'Nairobi, Kenya'),
       ('sensor-D4', 36.2, '2025-07-10T09:30:00+00', 'Mombasa, Kenya'),
       ('sensor-B3', 28.8, '2025-07-10T09:45:00+00', 'Kikuyu, Kenya'),
       ('sensor-E5', 19.4, '2025-07-10T10:00:00+00', 'Nakuru, Kenya'),
       ('sensor-F6', 20.9, '2025-07-10T10:15:00+00', 'Machakos, Kenya'),
       ('sensor-G7', 22.0, '2025-07-10T10:30:00+00', 'Kisumu, Kenya'),
       ('sensor-H8', 27.6, '2025-07-10T10:45:00+00', 'Eldoret, Kenya');
