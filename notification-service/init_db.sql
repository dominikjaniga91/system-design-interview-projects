CREATE SEQUENCE notification_id_seq START 1;

CREATE TABLE notifications (
                               id INTEGER PRIMARY KEY DEFAULT nextval('notification_id_seq'),
                               service_name  VARCHAR NOT NULL,
                               types TEXT[] NOT NULL,
                               sender VARCHAR NOT NULL,
                               recipients_ids TEXT[] NOT NULL,
                               title TEXT NOT NULL,
                               message VARCHAR NOT NULL
);