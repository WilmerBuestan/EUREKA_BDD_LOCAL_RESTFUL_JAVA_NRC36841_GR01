-- ============================================================
--  EUREKABANK - AJUSTES PARA ECUADOR (Grupo 1 - NRC 36841)
--  Ejecutar DESPUES de Crea_BD.sql y Carga_Datos.sql.
--  * Cedula ecuatoriana de 10 digitos (ALTER TABLE)
--  * Moneda unica: Dolares (se eliminan los Soles)
--  * Sucursales, ciudades, direcciones y telefonos de Ecuador
-- ============================================================
USE eurekabank;

-- 1. Cedula de 10 digitos (antes DNI de 8)
ALTER TABLE cliente MODIFY chr_cliedni CHAR(10) NOT NULL;

-- 2. Clientes con cedulas validas (algoritmo modulo 10) y datos de Ecuador
UPDATE cliente SET chr_cliedni = '1722601810', vch_clieciudad = 'QUITO', vch_cliedireccion = 'AV. AMAZONAS N24-120', vch_clietelefono = '0981234567' WHERE chr_cliecodigo = '00001';
UPDATE cliente SET chr_cliedni = '0929083012', vch_clieciudad = 'GUAYAQUIL', vch_cliedireccion = 'AV. 9 DE OCTUBRE 1520', vch_clietelefono = '0981235298' WHERE chr_cliecodigo = '00002';
UPDATE cliente SET chr_cliedni = '0136131869', vch_clieciudad = 'CUENCA', vch_cliedireccion = 'CALLE LARGA 7-45', vch_clietelefono = '0981236029' WHERE chr_cliecodigo = '00003';
UPDATE cliente SET chr_cliedni = '1709139099', vch_clieciudad = 'QUITO', vch_cliedireccion = 'AV. SHYRIS N35-17', vch_clietelefono = '0981236760' WHERE chr_cliecodigo = '00004';
UPDATE cliente SET chr_cliedni = '1846030821', vch_clieciudad = 'AMBATO', vch_cliedireccion = 'AV. CEVALLOS 03-48', vch_clietelefono = '0981237491' WHERE chr_cliecodigo = '00005';
UPDATE cliente SET chr_cliedni = '1126281946', vch_clieciudad = 'LOJA', vch_cliedireccion = 'AV. UNIVERSITARIA 12-30', vch_clietelefono = '0981238222' WHERE chr_cliecodigo = '00006';
UPDATE cliente SET chr_cliedni = '1342199351', vch_clieciudad = 'MANTA', vch_cliedireccion = 'AV. FLAVIO REYES 1102', vch_clietelefono = '0981238953' WHERE chr_cliecodigo = '00007';
UPDATE cliente SET chr_cliedni = '1708190937', vch_clieciudad = 'SANGOLQUI', vch_cliedireccion = 'AV. GENERAL RUMIÑAHUI S/N', vch_clietelefono = '0981239684' WHERE chr_cliecodigo = '00008';
UPDATE cliente SET chr_cliedni = '0938657970', vch_clieciudad = 'GUAYAQUIL', vch_cliedireccion = 'URDESA CENTRAL CALLE 5', vch_clietelefono = '0981240415' WHERE chr_cliecodigo = '00009';
UPDATE cliente SET chr_cliedni = '1724323199', vch_clieciudad = 'QUITO', vch_cliedireccion = 'AV. 6 DE DICIEMBRE N31-50', vch_clietelefono = '0981241146' WHERE chr_cliecodigo = '00010';
UPDATE cliente SET chr_cliedni = '0128757499', vch_clieciudad = 'CUENCA', vch_cliedireccion = 'AV. REMIGIO CRESPO 4-80', vch_clietelefono = '0981241877' WHERE chr_cliecodigo = '00011';
UPDATE cliente SET chr_cliedni = '1701862524', vch_clieciudad = 'QUITO', vch_cliedireccion = 'LA CAROLINA, AV. REPUBLICA E7-61', vch_clietelefono = '0981242608' WHERE chr_cliecodigo = '00012';
UPDATE cliente SET chr_cliedni = '2336018953', vch_clieciudad = 'SANTO DOMINGO', vch_cliedireccion = 'AV. QUITO 815', vch_clietelefono = '0981243339' WHERE chr_cliecodigo = '00013';
UPDATE cliente SET chr_cliedni = '1725979718', vch_clieciudad = 'SANGOLQUI', vch_cliedireccion = 'SAN RAFAEL, AV. ILALO 220', vch_clietelefono = '0981244070' WHERE chr_cliecodigo = '00014';
UPDATE cliente SET chr_cliedni = '0904710498', vch_clieciudad = 'GUAYAQUIL', vch_cliedireccion = 'SAMBORONDON KM 2.5', vch_clietelefono = '0981244801' WHERE chr_cliecodigo = '00015';
UPDATE cliente SET chr_cliedni = '1857465072', vch_clieciudad = 'AMBATO', vch_cliedireccion = 'FICOA, AV. LOS GUAYTAMBOS', vch_clietelefono = '0981245532' WHERE chr_cliecodigo = '00016';
UPDATE cliente SET chr_cliedni = '1722917034', vch_clieciudad = 'QUITO', vch_cliedireccion = 'CUMBAYA, AV. INTEROCEANICA', vch_clietelefono = '0981246263' WHERE chr_cliecodigo = '00017';
UPDATE cliente SET chr_cliedni = '0522366715', vch_clieciudad = 'RIOBAMBA', vch_cliedireccion = 'AV. DANIEL LEON BORJA 26-10', vch_clietelefono = '0981246994' WHERE chr_cliecodigo = '00018';
UPDATE cliente SET chr_cliedni = '1717684268', vch_clieciudad = 'QUITO', vch_cliedireccion = 'EL INCA, AV. ELOY ALFARO N47', vch_clietelefono = '0981247725' WHERE chr_cliecodigo = '00019';
UPDATE cliente SET chr_cliedni = '1044656328', vch_clieciudad = 'IBARRA', vch_cliedireccion = 'AV. MARIANO ACOSTA 2-14', vch_clietelefono = '0981248456' WHERE chr_cliecodigo = '00020';

-- 3. Moneda unica: Dolares (codigo 02). Todas las cuentas pasan a Dolares.
UPDATE cuenta SET chr_monecodigo = '02';
DELETE FROM cargomantenimiento WHERE chr_monecodigo = '01';
DELETE FROM costomovimiento WHERE chr_monecodigo = '01';
DELETE FROM interesmensual WHERE chr_monecodigo = '01';
DELETE FROM moneda WHERE chr_monecodigo = '01';
UPDATE moneda SET vch_monedescripcion = 'Dólares' WHERE chr_monecodigo = '02';

-- 4. Sucursales en Ecuador (se conservan los codigos y contadores)
UPDATE sucursal SET vch_sucunombre = 'Matriz Quito', vch_sucuciudad = 'Quito', vch_sucudireccion = 'Av. Amazonas N24-120' WHERE chr_sucucodigo = '001';
UPDATE sucursal SET vch_sucunombre = 'Guayaquil Centro', vch_sucuciudad = 'Guayaquil', vch_sucudireccion = 'Av. 9 de Octubre 1520' WHERE chr_sucucodigo = '002';
UPDATE sucursal SET vch_sucunombre = 'Cuenca', vch_sucuciudad = 'Cuenca', vch_sucudireccion = 'Calle Larga 7-45' WHERE chr_sucucodigo = '003';
UPDATE sucursal SET vch_sucunombre = 'Sangolquí ESPE', vch_sucuciudad = 'Sangolquí', vch_sucudireccion = 'Av. General Rumiñahui s/n' WHERE chr_sucucodigo = '004';
UPDATE sucursal SET vch_sucunombre = 'Ambato', vch_sucuciudad = 'Ambato', vch_sucudireccion = 'Av. Cevallos 03-48' WHERE chr_sucucodigo = '005';
UPDATE sucursal SET vch_sucunombre = 'Loja', vch_sucuciudad = 'Loja', vch_sucudireccion = 'Av. Universitaria 12-30' WHERE chr_sucucodigo = '006';
UPDATE sucursal SET vch_sucunombre = 'Manta', vch_sucuciudad = 'Manta', vch_sucudireccion = 'Av. Flavio Reyes 1102' WHERE chr_sucucodigo = '007';

-- 5. Ciudades de los empleados
UPDATE empleado SET vch_emplciudad = 'Quito' WHERE vch_emplciudad IN ('Lima', 'Huancayo');
UPDATE empleado SET vch_emplciudad = 'Guayaquil' WHERE vch_emplciudad = 'Trujillo';
UPDATE empleado SET vch_emplciudad = 'Cuenca' WHERE vch_emplciudad = 'Chiclayo';
UPDATE empleado SET vch_emplciudad = 'Ambato' WHERE vch_emplciudad = 'Cusco';
UPDATE empleado SET vch_emplciudad = 'Loja' WHERE vch_emplciudad = 'Areguipa';

SELECT 'Ajustes para Ecuador aplicados' AS resultado;
