-- EmailAR schema

-- !Ups

create table `emailAR` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `role_name` TEXT NOT NULL,
  `pillar` TEXT,
  `crew_name` TEXT,
  `email` TEXT NOT NULL
)