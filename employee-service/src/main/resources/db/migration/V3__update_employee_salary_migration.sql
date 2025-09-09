-- V3__update_employee_salary_migration.sql

-- This migration script updates the 'employee' table to change the salary model.
-- It renames the 'salary' column to 'daily_rate' and adds a new 'hourly_rate' column.
-- It then populates the new 'hourly_rate' column by calculating its value from the 'daily_rate'.

-- 1. Rename the 'salary' column to 'daily_rate'.
ALTER TABLE employee RENAME COLUMN salary TO daily_rate;

-- 2. Update the existing data by dividing the daily_rate by 30 to get the new calculated daily rate.
UPDATE employee SET daily_rate = daily_rate / 30;

-- 3. Add the new 'hourly_rate' column to the 'employee' table.
-- We use a DECIMAL type to ensure accurate monetary values.
ALTER TABLE employee ADD COLUMN hourly_rate DECIMAL(10, 2);

-- 4. Update the existing data by calculating the hourly rate.
-- The hourly rate is calculated by dividing the new 'daily_rate' by 8 (assuming an 8-hour workday).
-- This ensures that all existing employee records have a value for the new 'hourly_rate' column.
UPDATE employee SET hourly_rate = daily_rate / 8;
