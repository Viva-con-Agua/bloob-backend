-- EmailAccessRight schema

-- !Ups

CREATE TABLE `email_access_rights` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `role_name` VARCHAR(255) NOT NULL,
  `pillar` VARCHAR(255),
  `crew_name` VARCHAR(255),
  `email` VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

-- Email schema

-- !Ups

CREATE TABLE `email` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `sender_uuid` VARCHAR(255) NOT NULL,
  `sender_role` VARCHAR(255) NOT NULL,
  `sender_mail` VARCHAR(255) NOT NULL,
  `sender_crew_name` VARCHAR(255) NOT NULL,
  `sender_crew_id` VARCHAR(255) NOT NULL,
  `subject` VARCHAR(255) NOT NULL,
  `message_data` VARCHAR(255) NOT NULL,
  `date` DATE NOT NULL,
  `status` VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE `email_recipient` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `email_id` BIGINT(20) NOT NULL,
  `recipient_uuid`VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT FK_email_recipients_email FOREIGN KEY (email_id) REFERENCES email(id) 
);