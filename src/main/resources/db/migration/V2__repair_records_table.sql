CREATE TABLE IF NOT EXISTS repair_records(
	id INTEGER NOT NULL PRIMARY KEY,
	loco_model_name TEXT NOT NULL,
	loco_number TEXT NOT NULL,
	
	last_three_maintenance TEXT,
	next_three_maintenance TEXT,
	
	last_one_current_repair TEXT,
	next_one_current_repair TEXT,
	
	last_two_current_repair TEXT,
	next_two_current_repair TEXT,
	
	last_three_current_repair TEXT,
	next_three_current_repair TEXT,
	
	last_medium_repair TEXT,
	next_medium_repair TEXT,
	
	last_overhaul TEXT,
	next_overhaul TEXT,
	
	notes TEXT,
	
	FOREIGN KEY(loco_model_name) REFERENCES repair_periods(loco_model_name)
	);