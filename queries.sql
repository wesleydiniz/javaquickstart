/**
    Queries for testing
**/

-- Find IP by threshold and period


SELECT 
    l.ipAddress, COUNT(l.id) AS requests
FROM
    logparser.log l
WHERE
    logDate BETWEEN '2017-01-01 13:00:00' AND '2017-01-01 14:00:00'
GROUP BY l.ipAddress
HAVING requests > 100;

-- Find by IP
SELECT 
    l.*
FROM
    logparser.log l
WHERE
    l.ipAddress = '192.168.11.231';

-- Find "blocked" IP
SELECT 
    ip.*
FROM
    logparser.blockedip ip
WHERE
    ip.ipAddress = '192.168.228.188'
