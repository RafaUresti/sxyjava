SELECT r1.`status`,
CONCAT(r1.RIN_TYPE, "-", r1.PRODUCTION_YEAR, "-", r1.COMPANY_REGISTRATION_ID, "-", r1.FACILITY_REGISTRATION_ID, "-", r1.BATCH_NBR, "-", r1.EQUIVALENCE_VALUE, "-", r1.RENEWABLE_ENERGY_TYPE) AS uni_rin,
r1.starting_gallon_nbr,
r1.ending_gallon_nbr
FROM rin r1 WHERE r1.uni_rin IN
(SELECT r.uni_rin FROM rin r WHERE r.`status` = 'AVAILABLE')
ORDER BY r1.uni_rin, starting_gallon_nbr

SELECT r1.`status`, r1.rin_id, r1.uni_rin,
CONCAT(r1.RIN_TYPE, "-", r1.PRODUCTION_YEAR, "-", r1.COMPANY_REGISTRATION_ID, "-", r1.FACILITY_REGISTRATION_ID, "-", r1.BATCH_NBR, "-", r1.EQUIVALENCE_VALUE, "-", r1.RENEWABLE_ENERGY_TYPE) AS uni_rin,
r1.starting_gallon_nbr,
r1.ending_gallon_nbr
FROM rin r1 WHERE r1.uni_rin IN
(SELECT r.uni_rin FROM rin r WHERE r.`status` = 'AVAILABLE')
ORDER BY r1.uni_rin, starting_gallon_nbr