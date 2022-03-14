-- mariaDB 기준
-- DROP TABLE exchange_rate;

CREATE TABLE exchange_rate (
	dt DATE NOT NULL,
	cur_unit CHAR(3) NOT NULL,
	cur_nm VARCHAR(30),
	ttb FLOAT,
	tts FLOAT,
	deal_bas_r FLOAT,
	bkpr INT,
	yy_efee_r INT,
	ten_dd_efee_r INT,
	kftc_deal_bas_r FLOAT,
	kftc_bkpr INT,
	created_datetime DATETIME NOT NULL,
	
	PRIMARY KEY (dt, cur_unit)
);

-- DESCRIBE exchange_rate;