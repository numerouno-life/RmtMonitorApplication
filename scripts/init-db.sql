CREATE SCHEMA IF NOT EXISTS schema_auth;
CREATE SCHEMA IF NOT EXISTS schema_control;
CREATE SCHEMA IF NOT EXISTS schema_monitoring;
CREATE SCHEMA IF NOT EXISTS schema_journal;

- Пользователь только для чтения (для Grafana)
CREATE USER grafana_reader WITH PASSWORD 'user';
GRANT CONNECT ON DATABASE rmt-platform TO grafana_reader;
GRANT USAGE ON SCHEMA schema_monitoring TO "grafana_reader";
GRANT SELECT ON ALL TABLES IN SCHEMA schema_monitoring TO grafana_reader;
-- В будущем предоставить доступ к schema_journal для отчетов

-- при создании новых таблиц нужно будет делать следующие команды:
-- GRANT SELECT ON TABLE schema_monitoring.table_name TO grafana_reader;