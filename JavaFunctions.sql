-- Step 1: Create LIBRARY
\set libTfile '\''`pwd`'/vertica-java-udtf-0.1-jar-with-dependencies.jar\''

CREATE LIBRARY JdbcDemoFunctions AS :libTfile LANGUAGE 'JAVA';
-- Step 2: Create Functions
CREATE TRANSFORM FUNCTION jdbcDemo AS LANGUAGE 'Java' NAME 'com.vertica.JavaLibs.JdbcDemoFactory' LIBRARY JdbcDemoFunctions;

-- Step 3: Use Functions
CREATE TABLE IF NOT EXISTS uDataInput (i1 int, i2 int, i3 int);
CREATE TABLE IF NOT EXISTS uDataOutput (i1 int, i2 int, v1 varchar);
CREATE TABLE IF NOT EXISTS uDataSrc (i1 IDENTITY, v1 VARCHAR);
CREATE TABLE IF NOT EXISTS uDataDst (i1 IDENTITY, v1 VARCHAR);

INSERT INTO uDataSrc (v1) VALUES ('from source table');

INSERT INTO uDataInput VALUES (1,2,3);
INSERT INTO uDataInput VALUES (2,3,4);
INSERT INTO uDataInput VALUES (3,4,5);

COMMIT;

SELECT 'uDataInput', * FROM uDataInput;

INSERT INTO uDataOutput
SELECT jdbcDemo(i1,i2,i3 USING PARAMETERS srcTable = 'uDataSrc', dstTable = 'uDataDst') OVER ()
FROM uDataInput;

SELECT 'uDataOutput', * FROM uDataOutput;

SELECT 'uDataDst', * FROM uDataDst;

-- Step 4: Clean up
DROP TABLE uDataInput;
DROP TABLE uDataOutput;
DROP TABLE uDataSrc;
DROP TABLE uDataDst;
DROP LIBRARY JdbcDemoFunctions CASCADE;
