-- Drop table if exists (for fresh start)
DROP TABLE IF EXISTS projects CASCADE;
DROP TABLE IF EXISTS schedule CASCADE;
DROP SEQUENCE IF EXISTS project_id_seq;

-- Create sequence for auto-generating project IDs
CREATE SEQUENCE project_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create projects table
CREATE TABLE projects (
                          project_id INTEGER PRIMARY KEY DEFAULT nextval('project_id_seq'),
                          title VARCHAR(200) NOT NULL,
                          deadline INTEGER NOT NULL CHECK (deadline > 0 AND deadline <= 5),
                          revenue DECIMAL(10, 2) NOT NULL CHECK (revenue > 0),
                          status VARCHAR(20) DEFAULT 'PENDING',
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT chk_status CHECK (status IN ('PENDING', 'SCHEDULED', 'COMPLETED'))
);

-- Create schedule table
CREATE TABLE schedule (
                          schedule_id SERIAL PRIMARY KEY,
                          project_id INTEGER NOT NULL,
                          day_number INTEGER NOT NULL CHECK (day_number >= 1 AND day_number <= 5),
                          day_name VARCHAR(10) NOT NULL,
                          week_start_date DATE NOT NULL,
                          scheduled_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES projects(project_id) ON DELETE CASCADE,
                          CONSTRAINT unique_day_per_week UNIQUE (week_start_date, day_number),
                          CONSTRAINT chk_day_name CHECK (day_name IN ('Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'))
);

-- Create indexes for better query performance
CREATE INDEX idx_projects_status ON projects(status);
CREATE INDEX idx_projects_deadline ON projects(deadline);
CREATE INDEX idx_schedule_week ON schedule(week_start_date);

-- Display table structures
\d projects
\d schedule