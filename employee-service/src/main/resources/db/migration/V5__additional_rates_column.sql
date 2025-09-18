-- Add new rate columns to the employee table to support different pay frequencies.
-- These columns are nullable to accommodate the 'at least one rate' validation logic.
ALTER TABLE employee
ADD COLUMN weekly_rate DECIMAL(10, 2),
ADD COLUMN monthly_rate DECIMAL(10, 2),
ADD COLUMN yearly_rate DECIMAL(10, 2);
