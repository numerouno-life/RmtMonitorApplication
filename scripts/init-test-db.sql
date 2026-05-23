CREATE SCHEMA IF NOT EXISTS schema_control;
CREATE SCHEMA IF NOT EXISTS schema_auth;
CREATE SCHEMA IF NOT EXISTS schema_monitoring;
CREATE SCHEMA IF NOT EXISTS schema_journal;

-- Устанавливаем схему по умолчанию для тестов
SET search_path TO schema_control, public;