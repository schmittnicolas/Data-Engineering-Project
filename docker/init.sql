-- Connect to the default database
\c mydatabase

-- Create a new table
CREATE TABLE reports (
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    date TIMESTAMP,
    alcoholLevel DOUBLE PRECISION
);