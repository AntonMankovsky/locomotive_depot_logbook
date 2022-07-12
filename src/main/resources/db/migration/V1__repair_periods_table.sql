CREATE TABLE IF NOT EXISTS repair_periods(
	loco_model_name TEXT NOT NULL UNIQUE PRIMARY KEY,
	three_maintenance INTEGER NOT NULL,
	one_current_repair INTEGER NOT NULL,
	two_current_repair INTEGER NOT NULL,
	three_current_repair INTEGER NOT NULL,
	medium_repair INTEGER NOT NULL,
	overhaul INTEGER NOT NULL
	);

CREATE INDEX IF NOT EXISTS index_0 ON repair_periods (loco_model_name);