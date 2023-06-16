DROP TABLE IF EXISTS booking;
CREATE TABLE booking (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         property_id BIGINT NOT NULL,
                         start_date DATE NOT NULL,
                         end_date DATE NOT NULL,
                         guest_id BIGINT,
                         status VARCHAR(32) NOT NULL,
                         booking_type VARCHAR(32),
                         PRIMARY KEY (id)
);

CREATE TABLE booking_date (
                              id BIGINT NOT NULL AUTO_INCREMENT,
                              date DATE NOT NULL,
                              booking_id BIGINT NOT NULL,
                              booking_type VARCHAR(255),
                              property_id BIGINT NOT NULL,
                              PRIMARY KEY (id),
                              FOREIGN KEY (booking_id) REFERENCES booking (id)
);