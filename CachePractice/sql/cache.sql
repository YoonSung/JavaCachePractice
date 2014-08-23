DELIMITER $$
 DROP PROCEDURE IF EXISTS makeRandString$$
 CREATE PROCEDURE makeRandString()
       BEGIN
               DECLARE x  INT;
		declare y int default 1;
               DECLARE str varchar(10000);
	
		WHILE y <= 10000 do
              		 SET x = 1;
        	       SET str =  '';
              		 WHILE x  <= 10000 DO
                           SET  str = concat(str,char(round(rand() * 25) + 97), '');
                           SET  x = x + 1; 
              		 END WHILE;
			insert into ctest (v) values (str);
			set y = y + 1;
		end while;
       END$$
   DELIMITER ;