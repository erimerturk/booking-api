CREATE INDEX booking_date_propertyId_bookingType_date ON booking_date (property_id, booking_type, date);

CREATE INDEX booking_date_propertyId_date ON booking_date (property_id, date);