-- User schema

-- !Ups

create table `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  `role_name` TEXT NOT NULL,
  `crew_name` TEXT,
  `email` TEXT NOT NULL
)