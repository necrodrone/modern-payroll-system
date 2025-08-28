-- Add the new columns with a temporary default value to satisfy the NOT NULL constraint
ALTER TABLE employee ADD COLUMN date_of_birth DATE NOT NULL DEFAULT '1900-01-01';
ALTER TABLE employee ADD COLUMN address VARCHAR(255) NOT NULL DEFAULT 'N/A';
ALTER TABLE employee ADD COLUMN middle_name VARCHAR(255);
ALTER TABLE employee ADD COLUMN sss_number VARCHAR(10);
ALTER TABLE employee ADD COLUMN philhealth_number VARCHAR(12);
ALTER TABLE employee ADD COLUMN pagibig_number VARCHAR(12);
ALTER TABLE employee ADD COLUMN gsis_number VARCHAR(11);

-- The "NOT NULL" constraints on the optional fields can be added here, after the default values have been added,
-- but since your updated model has them as optional, no NOT NULL constraint is needed.