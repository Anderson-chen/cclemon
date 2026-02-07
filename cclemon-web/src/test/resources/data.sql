-- Set default for deleted column to match production MySQL schema
ALTER TABLE cclemon_user ALTER COLUMN deleted SET DEFAULT FALSE;
ALTER TABLE user_weight_logs ALTER COLUMN deleted SET DEFAULT FALSE;
