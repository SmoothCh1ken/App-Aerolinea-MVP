-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 09-11-2026 a las 18:51:30
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `aerolinea_app_db`
--
CREATE DATABASE IF NOT EXISTS `aerolinea_app_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `aerolinea_app_db`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `aeronave`
--

DROP TABLE IF EXISTS `aeronave`;
CREATE TABLE `aeronave` (
  `id_aeronave` int(11) NOT NULL,
  `matricula` varchar(20) NOT NULL,
  `modelo` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `aeronave`:
--

--
-- Volcado de datos para la tabla `aeronave`
--

INSERT INTO `aeronave` (`id_aeronave`, `matricula`, `modelo`) VALUES
(1, 'HK-1001', 'Airbus A320'),
(2, 'HK-1002', 'Airbus A320'),
(3, 'HK-1003', 'Boeing 737 MAX'),
(4, 'HK-1004', 'Boeing 737 MAX'),
(5, 'HK-1005', 'Boeing 787 Dreamliner'),
(6, 'HK-1006', 'Boeing 787 Dreamliner'),
(7, 'HK-1007', 'Airbus A330'),
(8, 'HK-1008', 'ATR 72'),
(9, 'HK-1009', 'ATR 72'),
(10, 'HK-1010', 'Airbus A320neo');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `aeropuerto`
--

DROP TABLE IF EXISTS `aeropuerto`;
CREATE TABLE `aeropuerto` (
  `id_aeropuerto` int(11) NOT NULL,
  `codigo_iata` char(3) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `id_ciudad` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `aeropuerto`:
--   `id_ciudad`
--       `ciudad` -> `id_ciudad`
--

--
-- Volcado de datos para la tabla `aeropuerto`
--

INSERT INTO `aeropuerto` (`id_aeropuerto`, `codigo_iata`, `nombre`, `id_ciudad`) VALUES
(1, 'BOG', 'El Dorado', 1),
(2, 'MDE', 'José María Córdova', 2),
(3, 'CLO', 'Alfonso Bonilla Aragón', 3),
(4, 'CTG', 'Rafael Núñez', 4),
(5, 'BAQ', 'Ernesto Cortissoz', 5),
(6, 'MIA', 'Miami International', 6),
(7, 'JFK', 'John F. Kennedy', 7),
(8, 'MAD', 'Adolfo Suárez', 8),
(9, 'LIM', 'Jorge Chávez', 9),
(10, 'EZE', 'Ministro Pistarini', 10);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asiento`
--

DROP TABLE IF EXISTS `asiento`;
CREATE TABLE `asiento` (
  `id_asiento` int(11) NOT NULL,
  `id_aeronave` int(11) NOT NULL,
  `numero_asiento` varchar(10) NOT NULL,
  `id_clase` int(11) NOT NULL,
  `es_preferencial` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `asiento`:
--   `id_aeronave`
--       `aeronave` -> `id_aeronave`
--   `id_clase`
--       `clase_servicio` -> `id_clase`
--

--
-- Volcado de datos para la tabla `asiento`
--

INSERT INTO `asiento` (`id_asiento`, `id_aeronave`, `numero_asiento`, `id_clase`, `es_preferencial`) VALUES
(1, 1, '1A', 2, 1),
(2, 1, '1B', 2, 1),
(3, 1, '1C', 2, 1),
(4, 1, '1D', 2, 1),
(5, 1, '2A', 2, 0),
(6, 1, '2B', 2, 0),
(7, 1, '2C', 2, 0),
(8, 1, '2D', 2, 0),
(9, 1, '3A', 1, 1),
(10, 1, '3B', 1, 0),
(11, 1, '3C', 1, 0),
(12, 1, '3D', 1, 1),
(13, 1, '4A', 1, 0),
(14, 1, '4B', 1, 0),
(15, 1, '4C', 1, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `auditoria_seguridad`
--

DROP TABLE IF EXISTS `auditoria_seguridad`;
CREATE TABLE `auditoria_seguridad` (
  `id_auditoria` bigint(20) NOT NULL,
  `fecha_hora` datetime NOT NULL DEFAULT current_timestamp(),
  `id_usuario` int(11) DEFAULT NULL,
  `usuario_texto` varchar(50) DEFAULT NULL,
  `evento` varchar(40) NOT NULL,
  `resultado` enum('Exitoso','Fallido') NOT NULL,
  `detalle` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- RELACIONES PARA LA TABLA `auditoria_seguridad`:
--   `id_usuario`
--       `usuario_sistema` -> `id_usuario`
--

--
-- Volcado de datos para la tabla `auditoria_seguridad`
--

INSERT INTO `auditoria_seguridad` (`id_auditoria`, `fecha_hora`, `id_usuario`, `usuario_texto`, `evento`, `resultado`, `detalle`) VALUES
(1, '2026-11-09 08:56:05', 1, 'admin.torres', 'LOGIN_FALLIDO', 'Fallido', 'Contraseña incorrecta (intento 1 de 3)'),
(2, '2026-11-09 08:56:46', 1, 'admin.torres', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(3, '2026-11-09 09:59:23', 3, 'directivo.gomez', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(4, '2026-11-09 10:00:10', 3, 'directivo.gomez', 'MFA_OK', 'Exitoso', 'Autenticación multifactor completada'),
(5, '2026-11-09 10:00:16', 3, 'directivo.gomez', 'ACCESO_DENEGADO', 'Fallido', 'Acceso denegado a Operaciones de aerolínea (rol Directivo)'),
(6, '2026-11-09 10:00:19', 3, 'directivo.gomez', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Reportes gerenciales (rol Directivo)'),
(7, '2026-11-09 10:00:37', 3, 'directivo.gomez', 'CIERRE_SESION', 'Exitoso', 'El usuario cerró la sesión'),
(8, '2026-11-09 10:03:12', 2, 'tecnologia.ruiz', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(9, '2026-11-09 10:03:52', 2, 'tecnologia.ruiz', 'MFA_OK', 'Exitoso', 'Autenticación multifactor completada'),
(10, '2026-11-09 10:04:03', 2, 'tecnologia.ruiz', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Auditoría de seguridad (rol Administrador de tecnología)'),
(11, '2026-11-09 10:05:23', 2, 'tecnologia.ruiz', 'CIERRE_SESION', 'Exitoso', 'El usuario cerró la sesión'),
(12, '2026-11-09 10:05:58', 5, 'auditor.pardo', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(13, '2026-11-09 10:06:20', 5, 'auditor.pardo', 'MFA_OK', 'Exitoso', 'Autenticación multifactor completada'),
(14, '2026-11-09 10:06:26', 5, 'auditor.pardo', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Auditoría de seguridad (rol Auditor interno)'),
(15, '2026-11-09 10:06:32', 5, 'auditor.pardo', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Auditoría de seguridad (rol Auditor interno)'),
(16, '2026-11-09 10:06:36', 5, 'auditor.pardo', 'ACCESO_DENEGADO', 'Fallido', 'Acceso denegado a Operaciones de aerolínea (rol Auditor interno)'),
(17, '2026-11-09 10:06:37', 5, 'auditor.pardo', 'ACCESO_DENEGADO', 'Fallido', 'Acceso denegado a Reportes gerenciales (rol Auditor interno)'),
(18, '2026-11-09 10:07:37', 5, 'auditor.pardo', 'CIERRE_SESION', 'Exitoso', 'El usuario cerró la sesión'),
(19, '2026-11-09 10:07:46', 1, 'admin.torres', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(20, '2026-11-09 10:08:27', 1, 'admin.torres', 'MFA_OK', 'Exitoso', 'Autenticación multifactor completada'),
(21, '2026-11-09 10:08:34', 1, 'admin.torres', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Gestión de usuarios (rol Administrador general)'),
(22, '2026-11-09 10:10:04', 1, 'admin.torres', 'CIERRE_SESION', 'Exitoso', 'El usuario cerró la sesión'),
(23, '2026-11-09 10:23:12', 1, 'admin.torres', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(24, '2026-11-09 10:23:37', 1, 'admin.torres', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(25, '2026-11-09 10:27:26', 1, 'admin.torres', 'LOGIN_FALLIDO', 'Fallido', 'Contraseña incorrecta (intento 1 de 3)'),
(26, '2026-11-09 10:27:33', 1, 'admin.torres', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(27, '2026-11-09 10:58:30', 1, 'admin.torres', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(28, '2026-11-09 10:59:05', 1, 'admin.torres', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (App TOTP) completada'),
(29, '2026-11-09 10:59:13', 1, 'admin.torres', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Gestión de usuarios (rol Administrador general)'),
(30, '2026-11-09 11:00:33', 1, 'admin.torres', 'USUARIO_CREADO', 'Exitoso', 'Cuenta \"admin.bascur\" creada con el rol \"Administrador general\"'),
(31, '2026-11-09 11:01:35', 1, 'admin.torres', 'CIERRE_SESION', 'Exitoso', 'El usuario cerró la sesión'),
(32, '2026-11-09 11:01:52', 6, 'admin.bascur', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(33, '2026-11-09 11:02:12', 6, 'admin.bascur', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (App TOTP) completada'),
(34, '2026-11-09 11:02:17', 6, 'admin.bascur', 'CIERRE_SESION', 'Exitoso', 'El usuario cerró la sesión'),
(35, '2026-11-09 11:02:30', 6, 'admin.bascur', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(36, '2026-11-09 11:09:53', 6, 'admin.bascur', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(37, '2026-11-09 11:10:37', 6, 'admin.bascur', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (Correo) completada'),
(38, '2026-11-09 11:17:55', 5, 'auditor.pardo', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(39, '2026-11-09 11:18:12', 5, 'auditor.pardo', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (App TOTP) completada'),
(40, '2026-11-09 11:18:16', 5, 'auditor.pardo', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Auditoría de seguridad (rol Auditor interno)'),
(41, '2026-11-09 11:19:04', 5, 'auditor.pardo', 'LOGIN_FALLIDO', 'Fallido', 'Contraseña incorrecta (intento 1 de 3)'),
(42, '2026-11-09 11:19:15', 5, 'auditor.pardo', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(43, '2026-11-09 11:19:23', 5, 'auditor.pardo', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (App TOTP) completada'),
(44, '2026-11-09 11:19:32', 5, 'auditor.pardo', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Auditoría de seguridad (rol Auditor interno)'),
(45, '2026-11-09 11:20:17', 5, 'auditor.pardo', 'LOGIN_FALLIDO', 'Fallido', 'Contraseña incorrecta (intento 1 de 3)'),
(46, '2026-11-09 11:20:27', 5, 'auditor.pardo', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(47, '2026-11-09 11:20:39', 5, 'auditor.pardo', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (App TOTP) completada'),
(48, '2026-11-09 11:20:46', 5, 'auditor.pardo', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Auditoría de seguridad (rol Auditor interno)'),
(49, '2026-11-09 11:21:55', 5, 'auditor.pardo', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(50, '2026-11-09 11:22:07', 5, 'auditor.pardo', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (App TOTP) completada'),
(51, '2026-11-09 11:22:12', 5, 'auditor.pardo', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Auditoría de seguridad (rol Auditor interno)'),
(52, '2026-11-09 11:31:05', 6, 'admin.bascur', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(53, '2026-11-09 11:31:26', 6, 'admin.bascur', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (Correo) completada'),
(54, '2026-11-09 11:31:40', 6, 'admin.bascur', 'CIERRE_SESION', 'Exitoso', 'El usuario cerró la sesión'),
(55, '2026-11-09 11:33:47', 6, 'admin.bascur', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(56, '2026-11-09 11:34:16', 6, 'admin.bascur', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(57, '2026-11-09 11:34:28', 6, 'admin.bascur', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (App TOTP) completada'),
(58, '2026-11-09 11:34:32', 6, 'admin.bascur', 'CIERRE_SESION', 'Exitoso', 'El usuario cerró la sesión'),
(59, '2026-11-09 11:34:43', 6, 'admin.bascur', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(60, '2026-11-09 11:34:49', 6, 'admin.bascur', 'MFA_FALLIDO', 'Fallido', 'Código de App Autenticadora inválido (intento 1 de 3)'),
(61, '2026-11-09 11:34:52', 6, 'admin.bascur', 'MFA_FALLIDO', 'Fallido', 'Código de App Autenticadora inválido (intento 2 de 3)'),
(62, '2026-11-09 11:34:52', 6, 'admin.bascur', 'MFA_FALLIDO', 'Fallido', 'Código de App Autenticadora inválido (intento 3 de 3)'),
(63, '2026-11-09 11:34:52', 6, 'admin.bascur', 'CUENTA_BLOQUEADA', 'Exitoso', '3 intentos fallidos consecutivos; cuenta bloqueada 5 minuto(s).'),
(64, '2026-11-09 11:35:25', 1, 'admin.torres', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(65, '2026-11-09 11:35:54', 1, 'admin.torres', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (App TOTP) completada'),
(66, '2026-11-09 11:35:57', 1, 'admin.torres', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Gestión de usuarios (rol Administrador general)'),
(67, '2026-11-09 11:36:11', 1, 'admin.torres', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Gestión de usuarios (rol Administrador general)'),
(68, '2026-11-09 11:36:18', 1, 'admin.torres', 'CUENTA_DESBLOQUEADA', 'Exitoso', 'Desbloqueo manual de \"admin.bascur\"'),
(69, '2026-11-09 11:36:24', 1, 'admin.torres', 'USUARIO_DESACTIVADO', 'Exitoso', 'Cuenta \"admin.bascur\" desactivada'),
(70, '2026-11-09 11:36:26', 1, 'admin.torres', 'USUARIO_ACTIVADO', 'Exitoso', 'Cuenta \"admin.bascur\" activada'),
(71, '2026-11-09 11:36:41', 1, 'admin.torres', 'CIERRE_SESION', 'Exitoso', 'El usuario cerró la sesión'),
(72, '2026-11-09 11:36:56', 6, 'admin.bascur', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(73, '2026-11-09 11:37:15', 6, 'admin.bascur', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (App TOTP) completada'),
(74, '2026-11-09 11:37:19', 6, 'admin.bascur', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Reportes gerenciales (rol Administrador general)'),
(75, '2026-11-09 12:16:10', 6, 'admin.bascur', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(76, '2026-11-09 12:16:52', 6, 'admin.bascur', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (App TOTP) completada'),
(77, '2026-11-09 12:17:02', 6, 'admin.bascur', 'CIERRE_SESION', 'Exitoso', 'El usuario cerró la sesión'),
(78, '2026-11-09 12:17:21', 6, 'admin.bascur', 'LOGIN_CLAVE_OK', 'Exitoso', 'Primer factor validado'),
(79, '2026-11-09 12:18:01', 6, 'admin.bascur', 'MFA_OK', 'Exitoso', 'Autenticación multifactor (Correo) completada'),
(80, '2026-11-09 12:18:12', 6, 'admin.bascur', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Operaciones de aerolínea (rol Administrador general)'),
(81, '2026-11-09 12:18:16', 6, 'admin.bascur', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Reportes gerenciales (rol Administrador general)'),
(82, '2026-11-09 12:18:31', 6, 'admin.bascur', 'ACCESO_MODULO', 'Exitoso', 'Acceso a Gestión de usuarios (rol Administrador general)'),
(83, '2026-11-09 12:20:36', 6, 'admin.bascur', 'CIERRE_SESION', 'Exitoso', 'El usuario cerró la sesión');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `bono_corporativo`
--

DROP TABLE IF EXISTS `bono_corporativo`;
CREATE TABLE `bono_corporativo` (
  `id_bono` int(11) NOT NULL,
  `codigo` varchar(50) NOT NULL,
  `empresa` varchar(100) NOT NULL,
  `saldo` decimal(10,2) NOT NULL,
  `fecha_vencimiento` date NOT NULL,
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `bono_corporativo`:
--

--
-- Volcado de datos para la tabla `bono_corporativo`
--

INSERT INTO `bono_corporativo` (`id_bono`, `codigo`, `empresa`, `saldo`, `fecha_vencimiento`, `activo`) VALUES
(1, 'BONO-001', 'Empresa X', 500000.00, '2027-12-31', 1),
(2, 'BONO-002', 'Empresa Y', 250000.00, '2027-12-31', 1),
(3, 'BONO-003', 'Globant', 1000000.00, '2026-11-30', 1),
(4, 'BONO-004', 'Bancolombia', 150000.00, '2027-01-15', 1),
(5, 'BONO-005', 'Ecopetrol', 800000.00, '2027-06-30', 1),
(6, 'BONO-006', 'Avianca', 300000.00, '2026-10-31', 1),
(7, 'BONO-007', 'Nutresa', 450000.00, '2027-03-20', 1),
(8, 'BONO-008', 'Rappi', 100000.00, '2027-05-10', 1),
(9, 'BONO-009', 'Sura', 600000.00, '2027-08-25', 1),
(10, 'BONO-010', 'Tigo', 200000.00, '2025-12-31', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cambio_reserva`
--

DROP TABLE IF EXISTS `cambio_reserva`;
CREATE TABLE `cambio_reserva` (
  `id_cambio` int(11) NOT NULL,
  `id_reserva` int(11) NOT NULL,
  `sentido` enum('Ida','Regreso') NOT NULL,
  `id_vuelo_anterior` int(11) NOT NULL,
  `id_tarifa_anterior` int(11) NOT NULL,
  `id_vuelo_nuevo` int(11) NOT NULL,
  `id_tarifa_nuevo` int(11) NOT NULL,
  `motivo` enum('Voluntario','Operacional') NOT NULL,
  `cargo_cambio` decimal(10,2) DEFAULT 0.00,
  `diferencia_valor` decimal(10,2) NOT NULL,
  `estado` enum('Pendiente de pago','Aplicado','Cancelado') DEFAULT 'Pendiente de pago',
  `fecha_hora` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `cambio_reserva`:
--   `id_reserva`
--       `reserva` -> `id_reserva`
--   `id_vuelo_anterior`
--       `vuelo_tarifa` -> `id_vuelo`
--   `id_tarifa_anterior`
--       `vuelo_tarifa` -> `id_tarifa`
--   `id_vuelo_nuevo`
--       `vuelo_tarifa` -> `id_vuelo`
--   `id_tarifa_nuevo`
--       `vuelo_tarifa` -> `id_tarifa`
--

--
-- Volcado de datos para la tabla `cambio_reserva`
--

INSERT INTO `cambio_reserva` (`id_cambio`, `id_reserva`, `sentido`, `id_vuelo_anterior`, `id_tarifa_anterior`, `id_vuelo_nuevo`, `id_tarifa_nuevo`, `motivo`, `cargo_cambio`, `diferencia_valor`, `estado`, `fecha_hora`) VALUES
(1, 1, 'Ida', 1, 3, 2, 4, 'Voluntario', 80000.00, 70000.00, 'Aplicado', '2026-10-15 11:59:38'),
(2, 3, 'Ida', 3, 6, 4, 8, 'Voluntario', 0.00, 4000000.00, 'Pendiente de pago', '2026-10-15 11:59:38'),
(3, 4, 'Ida', 5, 1, 6, 3, 'Operacional', 0.00, 0.00, 'Aplicado', '2026-10-15 11:59:38'),
(4, 6, 'Ida', 7, 1, 8, 3, 'Voluntario', 150000.00, 60000.00, 'Cancelado', '2026-10-15 11:59:38'),
(5, 7, 'Ida', 8, 3, 4, 3, 'Voluntario', 80000.00, 60000.00, 'Aplicado', '2026-10-15 11:59:38'),
(6, 8, 'Ida', 2, 2, 1, 1, 'Voluntario', 150000.00, -40000.00, 'Aplicado', '2026-10-15 11:59:38'),
(7, 10, 'Ida', 4, 3, 3, 6, 'Voluntario', 80000.00, 400000.00, 'Pendiente de pago', '2026-10-15 11:59:38'),
(8, 2, 'Ida', 6, 3, 5, 1, 'Operacional', 0.00, 0.00, 'Aplicado', '2026-10-15 11:59:38'),
(9, 5, 'Ida', 8, 3, 7, 1, 'Operacional', 0.00, -60000.00, 'Aplicado', '2026-10-15 11:59:38'),
(10, 9, 'Ida', 1, 3, 1, 5, 'Voluntario', 80000.00, 270000.00, 'Cancelado', '2026-10-15 11:59:38');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria_pasajero`
--

DROP TABLE IF EXISTS `categoria_pasajero`;
CREATE TABLE `categoria_pasajero` (
  `id_categoria` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `edad_min` int(11) NOT NULL,
  `edad_max` int(11) NOT NULL,
  `factor_tarifa` decimal(5,2) DEFAULT 1.00,
  `ocupa_cupo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `categoria_pasajero`:
--

--
-- Volcado de datos para la tabla `categoria_pasajero`
--

INSERT INTO `categoria_pasajero` (`id_categoria`, `nombre`, `edad_min`, `edad_max`, `factor_tarifa`, `ocupa_cupo`) VALUES
(1, 'Adulto', 12, 120, 1.00, 1),
(2, 'Niño', 2, 11, 0.75, 1),
(3, 'Menor de 2 años', 0, 1, 0.10, 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ciudad`
--

DROP TABLE IF EXISTS `ciudad`;
CREATE TABLE `ciudad` (
  `id_ciudad` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `pais` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `ciudad`:
--

--
-- Volcado de datos para la tabla `ciudad`
--

INSERT INTO `ciudad` (`id_ciudad`, `nombre`, `pais`) VALUES
(5, 'Barranquilla', 'Colombia'),
(1, 'Bogotá', 'Colombia'),
(10, 'Buenos Aires', 'Argentina'),
(3, 'Cali', 'Colombia'),
(4, 'Cartagena', 'Colombia'),
(9, 'Lima', 'Perú'),
(8, 'Madrid', 'España'),
(2, 'Medellín', 'Colombia'),
(6, 'Miami', 'Estados Unidos'),
(7, 'Nueva York', 'Estados Unidos');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `clase_servicio`
--

DROP TABLE IF EXISTS `clase_servicio`;
CREATE TABLE `clase_servicio` (
  `id_clase` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `clase_servicio`:
--

--
-- Volcado de datos para la tabla `clase_servicio`
--

INSERT INTO `clase_servicio` (`id_clase`, `nombre`) VALUES
(1, 'Económica'),
(2, 'Ejecutiva'),
(3, 'Primera Clase');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cuenta_millas`
--

DROP TABLE IF EXISTS `cuenta_millas`;
CREATE TABLE `cuenta_millas` (
  `id_persona` int(11) NOT NULL,
  `saldo_millas` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `cuenta_millas`:
--   `id_persona`
--       `persona` -> `id_persona`
--

--
-- Volcado de datos para la tabla `cuenta_millas`
--

INSERT INTO `cuenta_millas` (`id_persona`, `saldo_millas`) VALUES
(1, 15000),
(2, 5000),
(3, 45000),
(5, 1200),
(6, 0),
(7, 8500),
(8, 0),
(9, 25000),
(10, 300),
(12, 10785),
(13, 38410),
(17, 14691),
(18, 13882),
(20, 41544),
(21, 31059),
(22, 8719),
(23, 35824),
(24, 43999),
(25, 31206),
(28, 24357),
(29, 12157),
(30, 44372),
(32, 42851),
(33, 30908),
(36, 37809);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medio_pago`
--

DROP TABLE IF EXISTS `medio_pago`;
CREATE TABLE `medio_pago` (
  `id_medio_pago` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `medio_pago`:
--

--
-- Volcado de datos para la tabla `medio_pago`
--

INSERT INTO `medio_pago` (`id_medio_pago`, `nombre`, `activo`) VALUES
(1, 'PSE', 1),
(2, 'Tarjeta de Crédito', 1),
(3, 'Transferencia Bancaria', 1),
(4, 'Bono corporativo', 1),
(5, 'Millas', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `notificacion`
--

DROP TABLE IF EXISTS `notificacion`;
CREATE TABLE `notificacion` (
  `id_notificacion` int(11) NOT NULL,
  `id_reserva` int(11) NOT NULL,
  `id_persona` int(11) NOT NULL,
  `id_vuelo` int(11) DEFAULT NULL,
  `tipo` enum('Confirmación de compra','Vuelo cancelado','Vuelo reprogramado','Cambio de vuelo','Cancelación de reserva') NOT NULL,
  `asunto` varchar(150) NOT NULL,
  `mensaje` text NOT NULL,
  `correo_destino` varchar(150) NOT NULL,
  `estado` enum('Pendiente','Enviada','Fallida') DEFAULT 'Pendiente',
  `fecha_hora` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `notificacion`:
--   `id_reserva`
--       `reserva` -> `id_reserva`
--   `id_persona`
--       `persona` -> `id_persona`
--   `id_vuelo`
--       `vuelo` -> `id_vuelo`
--

--
-- Volcado de datos para la tabla `notificacion`
--

INSERT INTO `notificacion` (`id_notificacion`, `id_reserva`, `id_persona`, `id_vuelo`, `tipo`, `asunto`, `mensaje`, `correo_destino`, `estado`, `fecha_hora`) VALUES
(1, 1, 1, 1, 'Confirmación de compra', 'Reserva Confirmada RES-001', 'Su vuelo está confirmado.', 'carlos@mail.com', 'Enviada', '2026-10-01 10:06:00'),
(2, 2, 2, NULL, 'Confirmación de compra', 'Pago Pendiente RES-002', 'Por favor complete su pago por PSE.', 'maria@mail.com', 'Enviada', '2026-10-12 15:06:00'),
(3, 3, 3, 3, 'Confirmación de compra', 'Reserva Confirmada RES-003', 'Su vuelo está confirmado.', 'john@mail.com', 'Enviada', '2026-10-05 08:36:00'),
(4, 4, 5, 5, 'Vuelo reprogramado', 'Cambio Operacional Vuelo AV104', 'Su vuelo fue reprogramado.', 'andres@mail.com', 'Enviada', '2026-10-06 14:00:00'),
(5, 5, 6, 8, 'Vuelo cancelado', 'Cancelación de Vuelo AV108', 'Lamentamos informar que su vuelo fue cancelado.', 'lucia@mail.com', 'Enviada', '2026-10-08 10:00:00'),
(6, 6, 7, 7, 'Confirmación de compra', 'Reserva Confirmada RES-006', 'Su vuelo está confirmado.', 'diego@mail.com', 'Enviada', '2026-10-10 11:26:00'),
(7, 7, 9, 9, 'Confirmación de compra', 'Reserva Confirmada RES-007', 'Su vuelo está confirmado.', 'javier@mail.com', 'Enviada', '2026-10-11 16:51:00'),
(8, 8, 10, NULL, 'Confirmación de compra', 'Error de Pago RES-008', 'Su transacción fue cancelada.', 'martin@mail.com', 'Fallida', '2026-10-12 17:02:00'),
(9, 9, 1, 10, 'Confirmación de compra', 'Reserva Confirmada RES-009', 'Su vuelo está confirmado.', 'carlos@mail.com', 'Enviada', '2026-09-15 10:06:00'),
(10, 10, 2, 4, 'Vuelo reprogramado', 'Cambio Operacional Vuelo AV103', 'Su vuelo fue reprogramado.', 'maria@mail.com', 'Enviada', '2026-09-20 08:06:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pago`
--

DROP TABLE IF EXISTS `pago`;
CREATE TABLE `pago` (
  `id_pago` int(11) NOT NULL,
  `id_reserva` int(11) NOT NULL,
  `id_medio_pago` int(11) NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `estado` enum('Pendiente','Aprobado','Rechazado','Cancelado','Expirado','Error de comunicación') DEFAULT 'Pendiente',
  `referencia_externa` varchar(100) DEFAULT NULL,
  `id_bono` int(11) DEFAULT NULL,
  `millas_usadas` int(11) DEFAULT NULL,
  `id_cambio` int(11) DEFAULT NULL,
  `fecha_hora` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `pago`:
--   `id_reserva`
--       `reserva` -> `id_reserva`
--   `id_medio_pago`
--       `medio_pago` -> `id_medio_pago`
--   `id_bono`
--       `bono_corporativo` -> `id_bono`
--   `id_cambio`
--       `cambio_reserva` -> `id_cambio`
--

--
-- Volcado de datos para la tabla `pago`
--

INSERT INTO `pago` (`id_pago`, `id_reserva`, `id_medio_pago`, `valor`, `estado`, `referencia_externa`, `id_bono`, `millas_usadas`, `id_cambio`, `fecha_hora`) VALUES
(1, 1, 2, 384850.00, 'Aprobado', 'TRX-9981', NULL, NULL, NULL, '2026-10-01 10:02:00'),
(2, 2, 1, 346040.00, 'Expirado', 'PSE-1029', NULL, NULL, NULL, '2026-10-12 15:05:00'),
(3, 3, 2, 2475000.00, 'Aprobado', 'TRX-1092', NULL, NULL, NULL, '2026-10-05 08:32:00'),
(4, 6, 4, 892500.00, 'Aprobado', 'BONO-USE-001', NULL, NULL, NULL, '2026-10-10 11:22:00'),
(5, 7, 5, 142800.00, 'Aprobado', 'MILLAS-5000', NULL, NULL, NULL, '2026-10-11 16:48:00'),
(6, 4, 2, 2205000.00, 'Rechazado', 'TRX-3331', NULL, NULL, NULL, '2026-10-06 14:02:00'),
(7, 5, 3, 892500.00, 'Expirado', 'TRANS-9901', NULL, NULL, NULL, '2026-10-08 09:05:00'),
(8, 8, 1, 997500.00, 'Cancelado', 'PSE-1090', NULL, NULL, NULL, '2026-10-12 17:01:00'),
(9, 10, 2, 142800.00, 'Aprobado', 'TRX-7772', NULL, NULL, NULL, '2026-09-20 08:02:00'),
(10, 9, 2, 214200.00, 'Aprobado', 'TRX-1115', NULL, NULL, NULL, '2026-09-15 10:02:00'),
(11, 11, 3, 512890.00, 'Aprobado', 'TRX-1011', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(12, 12, 1, 1262590.00, 'Aprobado', 'TRX-1012', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(13, 14, 3, 459340.00, 'Aprobado', 'TRX-1014', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(14, 15, 2, 207060.00, 'Aprobado', 'TRX-1015', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(15, 16, 3, 205870.00, 'Aprobado', 'TRX-1016', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(16, 17, 2, 229670.00, 'Aprobado', 'TRX-1017', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(17, 18, 3, 273700.00, 'Aprobado', 'TRX-1018', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(18, 19, 1, 126140.00, 'Aprobado', 'TRX-1019', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(19, 20, 2, 291550.00, 'Aprobado', 'TRX-1020', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(20, 21, 2, 217770.00, 'Aprobado', 'TRX-1021', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(21, 22, 3, 565250.00, 'Aprobado', 'TRX-1022', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(22, 24, 1, 559300.00, 'Aprobado', 'TRX-1024', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(23, 25, 2, 102340.00, 'Aprobado', 'TRX-1025', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(24, 27, 1, 402220.00, 'Aprobado', 'TRX-1027', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(25, 28, 3, 1531530.00, 'Aprobado', 'TRX-1028', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(26, 29, 3, 235620.00, 'Aprobado', 'TRX-1029', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(27, 30, 3, 523600.00, 'Aprobado', 'TRX-1030', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(28, 31, 2, 109480.00, 'Aprobado', 'TRX-1031', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(29, 32, 1, 1666000.00, 'Aprobado', 'TRX-1032', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(30, 33, 2, 262990.00, 'Aprobado', 'TRX-1033', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(31, 34, 3, 254660.00, 'Aprobado', 'TRX-1034', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(32, 35, 1, 600950.00, 'Aprobado', 'TRX-1035', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(33, 36, 2, 110670.00, 'Aprobado', 'TRX-1036', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(34, 37, 3, 108290.00, 'Aprobado', 'TRX-1037', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(35, 38, 1, 146370.00, 'Aprobado', 'TRX-1038', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(36, 39, 3, 149940.00, 'Aprobado', 'TRX-1039', NULL, NULL, NULL, '2026-10-15 10:02:00'),
(37, 40, 2, 641410.00, 'Aprobado', 'TRX-1040', NULL, NULL, NULL, '2026-10-15 10:02:00');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `persona`
--

DROP TABLE IF EXISTS `persona`;
CREATE TABLE `persona` (
  `id_persona` int(11) NOT NULL,
  `tipo_documento` varchar(20) NOT NULL,
  `numero_documento` varchar(50) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `fecha_nacimiento` date NOT NULL,
  `nacionalidad` varchar(50) NOT NULL,
  `id_ciudad_residencia` int(11) NOT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `correo` varchar(150) DEFAULT NULL,
  `telefono` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `persona`:
--   `id_ciudad_residencia`
--       `ciudad` -> `id_ciudad`
--

--
-- Volcado de datos para la tabla `persona`
--

INSERT INTO `persona` (`id_persona`, `tipo_documento`, `numero_documento`, `nombres`, `apellidos`, `fecha_nacimiento`, `nacionalidad`, `id_ciudad_residencia`, `direccion`, `correo`, `telefono`) VALUES
(1, 'CC', '100100100', 'Carlos', 'Pérez', '1985-05-15', 'Colombiana', 1, 'Calle 123', 'carlos@mail.com', '3001112233'),
(2, 'CC', '100200200', 'María', 'Gómez', '1990-08-22', 'Colombiana', 2, 'Cra 45', 'maria@mail.com', '3102223344'),
(3, 'CE', '100300300', 'John', 'Smith', '1982-11-10', 'Estadounidense', 6, 'Avenue 5', 'john@mail.com', '15551234'),
(4, 'CC', '100400400', 'Laura', 'Díaz', '2015-02-14', 'Colombiana', 1, 'Calle 123', NULL, NULL),
(5, 'CC', '100500500', 'Andrés', 'López', '1975-09-30', 'Colombiana', 3, 'Cll 10', 'andres@mail.com', '3203334455'),
(6, 'Pasaporte', 'P123456', 'Lucía', 'Martínez', '1995-04-18', 'Española', 8, 'Gran Vía', 'lucia@mail.com', '600123456'),
(7, 'CC', '100600600', 'Diego', 'Ramírez', '1988-12-05', 'Colombiana', 4, 'Bocagrande', 'diego@mail.com', '3154445566'),
(8, 'CC', '100700700', 'Sofía', 'Castro', '2025-01-10', 'Colombiana', 2, 'Cra 45', NULL, NULL),
(9, 'CC', '100800800', 'Javier', 'Ruiz', '1992-07-07', 'Colombiana', 1, 'Suba', 'javier@mail.com', '3015556677'),
(10, 'DNI', '99887766', 'Martín', 'Rossi', '1980-03-25', 'Argentina', 10, 'Palermo', 'martin@mail.com', '1144445555'),
(11, 'CC', '10011001100', 'Daniela', 'López', '1991-05-15', 'Colombiana', 5, 'Dir 11', 'daniela.lópez11@mail.com', '300000011'),
(12, 'CC', '10012001200', 'David', 'Torres', '1988-05-15', 'Colombiana', 5, 'Dir 12', 'david.torres12@mail.com', '300000012'),
(13, 'CC', '10013001300', 'Nicolás', 'Morales', '1988-05-15', 'Colombiana', 6, 'Dir 13', 'nicolás.morales13@mail.com', '300000013'),
(14, 'CC', '10014001400', 'Gabriela', 'López', '1976-05-15', 'Colombiana', 8, 'Dir 14', 'gabriela.lópez14@mail.com', '300000014'),
(15, 'CC', '10015001500', 'Camila', 'Hernández', '1978-05-15', 'Colombiana', 3, 'Dir 15', 'camila.hernández15@mail.com', '300000015'),
(16, 'CC', '10016001600', 'Luis', 'Rodríguez', '2000-05-15', 'Colombiana', 10, 'Dir 16', 'luis.rodríguez16@mail.com', '300000016'),
(17, 'CC', '10017001700', 'David', 'Sánchez', '1980-05-15', 'Colombiana', 4, 'Dir 17', 'david.sánchez17@mail.com', '300000017'),
(18, 'CC', '10018001800', 'Sebastián', 'Rivera', '1964-05-15', 'Colombiana', 1, 'Dir 18', 'sebastián.rivera18@mail.com', '300000018'),
(19, 'CC', '10019001900', 'Daniela', 'Rodríguez', '1978-05-15', 'Colombiana', 6, 'Dir 19', 'daniela.rodríguez19@mail.com', '300000019'),
(20, 'CC', '10020002000', 'Ana', 'López', '1982-05-15', 'Colombiana', 1, 'Dir 20', 'ana.lópez20@mail.com', '300000020'),
(21, 'CC', '10021002100', 'Valentina', 'Hernández', '1980-05-15', 'Colombiana', 10, 'Dir 21', 'valentina.hernández21@mail.com', '300000021'),
(22, 'CC', '10022002200', 'Nicolás', 'Hernández', '1983-05-15', 'Colombiana', 7, 'Dir 22', 'nicolás.hernández22@mail.com', '300000022'),
(23, 'CC', '10023002300', 'Gabriela', 'Pérez', '1964-05-15', 'Colombiana', 3, 'Dir 23', 'gabriela.pérez23@mail.com', '300000023'),
(24, 'CC', '10024002400', 'Luis', 'Pérez', '1964-05-15', 'Colombiana', 7, 'Dir 24', 'luis.pérez24@mail.com', '300000024'),
(25, 'CC', '10025002500', 'Mariana', 'Flores', '1990-05-15', 'Colombiana', 5, 'Dir 25', 'mariana.flores25@mail.com', '300000025'),
(26, 'CC', '10026002600', 'Alejandro', 'Rivera', '1977-05-15', 'Colombiana', 10, 'Dir 26', 'alejandro.rivera26@mail.com', '300000026'),
(27, 'CC', '10027002700', 'Camila', 'Gómez', '1968-05-15', 'Colombiana', 6, 'Dir 27', 'camila.gómez27@mail.com', '300000027'),
(28, 'CC', '10028002800', 'Juliana', 'Sánchez', '1981-05-15', 'Colombiana', 1, 'Dir 28', 'juliana.sánchez28@mail.com', '300000028'),
(29, 'CC', '10029002900', 'Daniela', 'Ramírez', '1977-05-15', 'Colombiana', 10, 'Dir 29', 'daniela.ramírez29@mail.com', '300000029'),
(30, 'CC', '10030003000', 'Ana', 'Díaz', '2002-05-15', 'Colombiana', 2, 'Dir 30', 'ana.díaz30@mail.com', '300000030'),
(31, 'CC', '10031003100', 'Daniela', 'Torres', '1985-05-15', 'Colombiana', 2, 'Dir 31', 'daniela.torres31@mail.com', '300000031'),
(32, 'CC', '10032003200', 'Sebastián', 'Flores', '1989-05-15', 'Colombiana', 2, 'Dir 32', 'sebastián.flores32@mail.com', '300000032'),
(33, 'CC', '10033003300', 'Daniela', 'Martínez', '1975-05-15', 'Colombiana', 8, 'Dir 33', 'daniela.martínez33@mail.com', '300000033'),
(34, 'CC', '10034003400', 'Felipe', 'Morales', '1989-05-15', 'Colombiana', 8, 'Dir 34', 'felipe.morales34@mail.com', '300000034'),
(35, 'CC', '10035003500', 'Juliana', 'Díaz', '1998-05-15', 'Colombiana', 5, 'Dir 35', 'juliana.díaz35@mail.com', '300000035'),
(36, 'CC', '10036003600', 'Camila', 'García', '1976-05-15', 'Colombiana', 5, 'Dir 36', 'camila.garcía36@mail.com', '300000036'),
(37, 'CC', '10037003700', 'Juliana', 'Díaz', '1989-05-15', 'Colombiana', 2, 'Dir 37', 'juliana.díaz37@mail.com', '300000037'),
(38, 'CC', '10038003800', 'Camila', 'Cruz', '1976-05-15', 'Colombiana', 1, 'Dir 38', 'camila.cruz38@mail.com', '300000038'),
(39, 'CC', '10039003900', 'Sebastián', 'Flores', '2003-05-15', 'Colombiana', 2, 'Dir 39', 'sebastián.flores39@mail.com', '300000039'),
(40, 'CC', '10040004000', 'Samuel', 'Díaz', '1979-05-15', 'Colombiana', 1, 'Dir 40', 'samuel.díaz40@mail.com', '300000040');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `politica_seguridad`
--

DROP TABLE IF EXISTS `politica_seguridad`;
CREATE TABLE `politica_seguridad` (
  `clave` varchar(50) NOT NULL,
  `valor` varchar(100) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- RELACIONES PARA LA TABLA `politica_seguridad`:
--

--
-- Volcado de datos para la tabla `politica_seguridad`
--

INSERT INTO `politica_seguridad` (`clave`, `valor`, `descripcion`) VALUES
('MAX_INTENTOS', '3', 'Intentos fallidos permitidos antes de bloquear la cuenta'),
('MINUTOS_BLOQUEO', '5', 'Minutos que dura el bloqueo temporal de una cuenta'),
('MINUTOS_INACTIVIDAD', '10', 'Minutos sin actividad para cerrar la sesión automáticamente');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reembolso`
--

DROP TABLE IF EXISTS `reembolso`;
CREATE TABLE `reembolso` (
  `id_reembolso` int(11) NOT NULL,
  `id_reserva` int(11) NOT NULL,
  `id_cambio` int(11) DEFAULT NULL,
  `motivo` enum('Cancelación voluntaria','Cancelación operacional','Saldo a favor') NOT NULL,
  `valor_penalidad` decimal(10,2) DEFAULT 0.00,
  `valor_reembolso` decimal(10,2) NOT NULL,
  `estado` enum('Solicitado','Aprobado','Pagado') DEFAULT 'Solicitado',
  `fecha_solicitud` datetime NOT NULL DEFAULT current_timestamp(),
  `fecha_resolucion` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `reembolso`:
--   `id_reserva`
--       `reserva` -> `id_reserva`
--   `id_cambio`
--       `cambio_reserva` -> `id_cambio`
--

--
-- Volcado de datos para la tabla `reembolso`
--

INSERT INTO `reembolso` (`id_reembolso`, `id_reserva`, `id_cambio`, `motivo`, `valor_penalidad`, `valor_reembolso`, `estado`, `fecha_solicitud`, `fecha_resolucion`) VALUES
(1, 4, NULL, 'Cancelación voluntaria', 220500.00, 1984500.00, 'Pagado', '2026-10-06 18:00:00', NULL),
(2, 9, NULL, 'Cancelación voluntaria', 64260.00, 149940.00, 'Aprobado', '2026-09-15 20:00:00', NULL),
(3, 8, 6, 'Saldo a favor', 0.00, 40000.00, 'Solicitado', '2026-10-12 18:00:00', NULL),
(4, 5, 9, 'Saldo a favor', 0.00, 60000.00, 'Pagado', '2026-10-09 10:00:00', NULL),
(5, 1, NULL, 'Cancelación operacional', 0.00, 384850.00, 'Pagado', '2026-10-03 10:00:00', NULL),
(6, 2, NULL, 'Cancelación voluntaria', 34604.00, 311436.00, 'Solicitado', '2026-10-13 15:00:00', NULL),
(7, 3, NULL, 'Cancelación operacional', 0.00, 2475000.00, 'Aprobado', '2026-10-06 08:30:00', NULL),
(8, 6, NULL, 'Cancelación voluntaria', 89250.00, 803250.00, 'Pagado', '2026-10-11 11:20:00', NULL),
(9, 7, NULL, 'Cancelación operacional', 0.00, 142800.00, 'Solicitado', '2026-10-12 16:45:00', NULL),
(10, 10, NULL, 'Cancelación voluntaria', 14280.00, 128520.00, 'Aprobado', '2026-09-21 08:00:00', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reserva`
--

DROP TABLE IF EXISTS `reserva`;
CREATE TABLE `reserva` (
  `id_reserva` int(11) NOT NULL,
  `codigo_reserva` varchar(10) NOT NULL,
  `id_comprador` int(11) NOT NULL,
  `estado` enum('Pendiente de pago','Confirmada','Expirada','Cancelada') DEFAULT 'Pendiente de pago',
  `fecha_creacion` datetime NOT NULL DEFAULT current_timestamp(),
  `fecha_expiracion` datetime NOT NULL,
  `fecha_cancelacion` datetime DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `reserva`:
--   `id_comprador`
--       `persona` -> `id_persona`
--

--
-- Volcado de datos para la tabla `reserva`
--

INSERT INTO `reserva` (`id_reserva`, `codigo_reserva`, `id_comprador`, `estado`, `fecha_creacion`, `fecha_expiracion`, `fecha_cancelacion`) VALUES
(1, 'RES-001', 1, 'Confirmada', '2026-10-01 10:00:00', '2026-10-02 10:00:00', NULL),
(2, 'RES-002', 2, 'Expirada', '2026-10-12 15:00:00', '2026-10-13 15:00:00', NULL),
(3, 'RES-003', 3, 'Confirmada', '2026-10-05 08:30:00', '2026-10-06 08:30:00', NULL),
(4, 'RES-004', 5, 'Cancelada', '2026-10-06 14:00:00', '2026-10-07 14:00:00', '2026-10-06 18:00:00'),
(5, 'RES-005', 6, 'Expirada', '2026-10-08 09:00:00', '2026-10-09 09:00:00', NULL),
(6, 'RES-006', 7, 'Confirmada', '2026-10-10 11:20:00', '2026-10-11 11:20:00', NULL),
(7, 'RES-007', 9, 'Confirmada', '2026-10-11 16:45:00', '2026-10-12 16:45:00', NULL),
(8, 'RES-008', 10, 'Expirada', '2026-10-12 17:00:00', '2026-10-13 17:00:00', NULL),
(9, 'RES-009', 1, 'Cancelada', '2026-09-15 10:00:00', '2026-09-16 10:00:00', '2026-09-15 20:00:00'),
(10, 'RES-010', 2, 'Confirmada', '2026-09-20 08:00:00', '2026-09-21 08:00:00', NULL),
(11, 'RES-011', 11, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(12, 'RES-012', 12, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(13, 'RES-013', 13, 'Expirada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(14, 'RES-014', 14, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(15, 'RES-015', 15, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(16, 'RES-016', 16, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(17, 'RES-017', 17, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(18, 'RES-018', 18, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(19, 'RES-019', 19, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(20, 'RES-020', 20, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(21, 'RES-021', 21, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(22, 'RES-022', 22, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(23, 'RES-023', 23, 'Expirada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(24, 'RES-024', 24, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(25, 'RES-025', 25, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(26, 'RES-026', 26, 'Expirada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(27, 'RES-027', 27, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(28, 'RES-028', 28, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(29, 'RES-029', 29, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(30, 'RES-030', 30, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(31, 'RES-031', 31, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(32, 'RES-032', 32, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(33, 'RES-033', 33, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(34, 'RES-034', 34, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(35, 'RES-035', 35, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(36, 'RES-036', 36, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(37, 'RES-037', 37, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(38, 'RES-038', 38, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(39, 'RES-039', 39, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL),
(40, 'RES-040', 40, 'Confirmada', '2026-10-15 10:00:00', '2026-10-16 10:00:00', NULL);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `reserva_pasajero`
--

DROP TABLE IF EXISTS `reserva_pasajero`;
CREATE TABLE `reserva_pasajero` (
  `id_reserva_pasajero` int(11) NOT NULL,
  `id_reserva` int(11) NOT NULL,
  `id_persona` int(11) NOT NULL,
  `id_categoria` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `reserva_pasajero`:
--   `id_reserva`
--       `reserva` -> `id_reserva`
--   `id_persona`
--       `persona` -> `id_persona`
--   `id_categoria`
--       `categoria_pasajero` -> `id_categoria`
--

--
-- Volcado de datos para la tabla `reserva_pasajero`
--

INSERT INTO `reserva_pasajero` (`id_reserva_pasajero`, `id_reserva`, `id_persona`, `id_categoria`) VALUES
(1, 1, 1, 1),
(2, 1, 4, 2),
(3, 2, 2, 1),
(4, 2, 8, 3),
(5, 3, 3, 1),
(6, 4, 5, 1),
(7, 5, 6, 1),
(8, 6, 7, 1),
(9, 7, 9, 1),
(10, 8, 10, 1),
(11, 11, 11, 1),
(12, 12, 12, 1),
(13, 13, 13, 1),
(14, 14, 14, 1),
(15, 15, 15, 1),
(16, 16, 16, 1),
(17, 17, 17, 1),
(18, 18, 18, 1),
(19, 19, 19, 1),
(20, 20, 20, 1),
(21, 21, 21, 1),
(22, 22, 22, 1),
(23, 23, 23, 1),
(24, 24, 24, 1),
(25, 25, 25, 1),
(26, 26, 26, 1),
(27, 27, 27, 1),
(28, 28, 28, 1),
(29, 29, 29, 1),
(30, 30, 30, 1),
(31, 31, 31, 1),
(32, 32, 32, 1),
(33, 33, 33, 1),
(34, 34, 34, 1),
(35, 35, 35, 1),
(36, 36, 36, 1),
(37, 37, 37, 1),
(38, 38, 38, 1),
(39, 39, 39, 1),
(40, 40, 40, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol`
--

DROP TABLE IF EXISTS `rol`;
CREATE TABLE `rol` (
  `id_rol` int(11) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- RELACIONES PARA LA TABLA `rol`:
--

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`id_rol`, `nombre`, `descripcion`) VALUES
(1, 'Administrador general', 'Administra cuentas y roles; opera reportes y operaciones'),
(2, 'Administrador de tecnología', 'Atiende incidentes de seguridad y consulta la auditoría'),
(3, 'Directivo', 'Consulta reportes gerenciales y el tablero ejecutivo'),
(4, 'Operaciones', 'Gestiona vuelos: reprogramación y cancelación'),
(5, 'Auditor interno', 'Consulta la trazabilidad de accesos y administración');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol_modulo`
--

DROP TABLE IF EXISTS `rol_modulo`;
CREATE TABLE `rol_modulo` (
  `id_rol` int(11) NOT NULL,
  `modulo` enum('USUARIOS','OPERACIONES','REPORTES','AUDITORIA','INCIDENTES') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- RELACIONES PARA LA TABLA `rol_modulo`:
--   `id_rol`
--       `rol` -> `id_rol`
--

--
-- Volcado de datos para la tabla `rol_modulo`
--

INSERT INTO `rol_modulo` (`id_rol`, `modulo`) VALUES
(1, 'USUARIOS'),
(1, 'OPERACIONES'),
(1, 'REPORTES'),
(2, 'AUDITORIA'),
(2, 'INCIDENTES'),
(3, 'REPORTES'),
(4, 'OPERACIONES'),
(5, 'AUDITORIA');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `ruta`
--

DROP TABLE IF EXISTS `ruta`;
CREATE TABLE `ruta` (
  `id_ruta` int(11) NOT NULL,
  `id_aeropuerto_origen` int(11) NOT NULL,
  `id_aeropuerto_destino` int(11) NOT NULL,
  `escalas` int(11) DEFAULT 0,
  `detalle_escala` varchar(255) DEFAULT NULL,
  `porcentaje_impuesto` decimal(5,2) NOT NULL DEFAULT 0.00
) ;

--
-- RELACIONES PARA LA TABLA `ruta`:
--   `id_aeropuerto_origen`
--       `aeropuerto` -> `id_aeropuerto`
--   `id_aeropuerto_destino`
--       `aeropuerto` -> `id_aeropuerto`
--

--
-- Volcado de datos para la tabla `ruta`
--

INSERT INTO `ruta` (`id_ruta`, `id_aeropuerto_origen`, `id_aeropuerto_destino`, `escalas`, `detalle_escala`, `porcentaje_impuesto`) VALUES
(1, 1, 2, 0, NULL, 19.00),
(2, 1, 4, 0, NULL, 19.00),
(3, 1, 6, 0, NULL, 5.00),
(4, 1, 8, 0, NULL, 5.00),
(5, 2, 6, 1, 'Conexión en BOG', 5.00),
(6, 1, 10, 0, NULL, 5.00),
(7, 6, 1, 0, NULL, 5.00),
(8, 8, 1, 0, NULL, 5.00),
(9, 3, 4, 1, 'Conexión en BOG', 19.00),
(10, 4, 1, 0, NULL, 19.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `servicio`
--

DROP TABLE IF EXISTS `servicio`;
CREATE TABLE `servicio` (
  `id_servicio` int(11) NOT NULL,
  `tipo` enum('Asiento preferencial','Equipaje adicional','Mascotas','Seguro','Alimentación especial','Embarque prioritario','Sala VIP','Transporte terrestre') NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `servicio`:
--

--
-- Volcado de datos para la tabla `servicio`
--

INSERT INTO `servicio` (`id_servicio`, `tipo`, `nombre`, `descripcion`, `activo`) VALUES
(1, 'Asiento preferencial', 'Silla VIP 1A-4C', 'Asientos en las primeras filas con más espacio', 1),
(2, 'Equipaje adicional', 'Maleta en bodega 23kg', 'Pieza de equipaje extra registrada en bodega', 1),
(3, 'Mascotas', 'Mascota en cabina', 'Mascota pequeña menor a 10kg en guacal', 1),
(4, 'Seguro', 'Seguro de viaje Plus', 'Cobertura médica y pérdida de equipaje', 1),
(5, 'Alimentación especial', 'Menú Vegetariano', 'Comida libre de carne', 1),
(6, 'Embarque prioritario', 'Abordaje Grupo 1', 'Ingreso anticipado a la aeronave', 1),
(7, 'Sala VIP', 'Acceso Lounge Avianca', 'Acceso a salas VIP con snacks y bebidas', 1),
(8, 'Transporte terrestre', 'Traslado Hotel-Aeropuerto', 'Servicio de transfer privado', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `servicio_contratado`
--

DROP TABLE IF EXISTS `servicio_contratado`;
CREATE TABLE `servicio_contratado` (
  `id_servicio_contratado` int(11) NOT NULL,
  `id_tramo_pasajero` int(11) NOT NULL,
  `id_servicio` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL DEFAULT 1,
  `valor_unitario` decimal(10,2) NOT NULL,
  `detalle` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `servicio_contratado`:
--   `id_tramo_pasajero`
--       `tramo_pasajero` -> `id_tramo_pasajero`
--   `id_servicio`
--       `servicio` -> `id_servicio`
--

--
-- Volcado de datos para la tabla `servicio_contratado`
--

INSERT INTO `servicio_contratado` (`id_servicio_contratado`, `id_tramo_pasajero`, `id_servicio`, `cantidad`, `valor_unitario`, `detalle`) VALUES
(1, 1, 2, 1, 0.00, 'Incluido en tarifa Classic'),
(2, 1, 4, 1, 45000.00, 'Seguro básico'),
(3, 3, 3, 1, 150000.00, 'Gato en guacal flexible'),
(4, 5, 7, 1, 0.00, 'Beneficio Business'),
(5, 5, 5, 1, 35000.00, 'Menú Vegetariano'),
(6, 6, 2, 2, 90000.00, '2 Maletas extra'),
(7, 7, 8, 1, 80000.00, 'Taxi al Poblado'),
(8, 8, 6, 1, 20000.00, 'Grupo 1'),
(9, 9, 2, 1, 90000.00, 'Maleta de bodega'),
(10, 10, 4, 1, 250000.00, 'Seguro USA');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `servicio_precio`
--

DROP TABLE IF EXISTS `servicio_precio`;
CREATE TABLE `servicio_precio` (
  `id_servicio_precio` int(11) NOT NULL,
  `id_servicio` int(11) NOT NULL,
  `id_ciudad_destino` int(11) DEFAULT NULL,
  `id_tarifa` int(11) DEFAULT NULL,
  `valor` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `servicio_precio`:
--   `id_servicio`
--       `servicio` -> `id_servicio`
--   `id_ciudad_destino`
--       `ciudad` -> `id_ciudad`
--   `id_tarifa`
--       `tarifa` -> `id_tarifa`
--

--
-- Volcado de datos para la tabla `servicio_precio`
--

INSERT INTO `servicio_precio` (`id_servicio_precio`, `id_servicio`, `id_ciudad_destino`, `id_tarifa`, `valor`) VALUES
(1, 2, NULL, 1, 90000.00),
(2, 2, NULL, 3, 0.00),
(3, 3, NULL, NULL, 150000.00),
(4, 4, 6, NULL, 250000.00),
(5, 4, 1, NULL, 45000.00),
(6, 5, NULL, NULL, 35000.00),
(7, 6, NULL, 1, 20000.00),
(8, 7, NULL, 6, 0.00),
(9, 7, NULL, 1, 120000.00),
(10, 8, 2, NULL, 80000.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tarifa`
--

DROP TABLE IF EXISTS `tarifa`;
CREATE TABLE `tarifa` (
  `id_tarifa` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `id_clase` int(11) NOT NULL,
  `permite_cambios` tinyint(1) DEFAULT 0,
  `cargo_cambio` decimal(10,2) DEFAULT 0.00,
  `permite_reembolso` tinyint(1) DEFAULT 0,
  `porcentaje_penalidad_reembolso` decimal(5,2) DEFAULT 0.00,
  `horas_minimas_antelacion` int(11) DEFAULT 0,
  `equipaje_incluido_kg` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `tarifa`:
--   `id_clase`
--       `clase_servicio` -> `id_clase`
--

--
-- Volcado de datos para la tabla `tarifa`
--

INSERT INTO `tarifa` (`id_tarifa`, `nombre`, `id_clase`, `permite_cambios`, `cargo_cambio`, `permite_reembolso`, `porcentaje_penalidad_reembolso`, `horas_minimas_antelacion`, `equipaje_incluido_kg`) VALUES
(1, 'XS (Basic)', 1, 0, 0.00, 0, 0.00, 0, 0),
(2, 'S (Light)', 1, 1, 150000.00, 0, 0.00, 48, 10),
(3, 'M (Classic)', 1, 1, 80000.00, 1, 30.00, 24, 23),
(4, 'L (Flex)', 1, 1, 0.00, 1, 10.00, 12, 23),
(5, 'XL (Business Prom)', 2, 1, 200000.00, 1, 20.00, 24, 32),
(6, 'XXL (Business Flex)', 2, 1, 0.00, 1, 0.00, 2, 46),
(7, 'First Saver', 3, 1, 300000.00, 1, 15.00, 48, 46),
(8, 'First Flex', 3, 1, 0.00, 1, 0.00, 1, 69),
(9, 'Promo Flash', 1, 0, 0.00, 0, 0.00, 0, 0),
(10, 'Tarifa Empleado', 1, 1, 0.00, 1, 0.00, 1, 23);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tiquete`
--

DROP TABLE IF EXISTS `tiquete`;
CREATE TABLE `tiquete` (
  `id_tiquete` int(11) NOT NULL,
  `id_reserva_pasajero` int(11) NOT NULL,
  `numero_tiquete` varchar(50) NOT NULL,
  `fecha_emision` datetime NOT NULL DEFAULT current_timestamp(),
  `estado` enum('Emitido','Anulado') DEFAULT 'Emitido'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `tiquete`:
--   `id_reserva_pasajero`
--       `reserva_pasajero` -> `id_reserva_pasajero`
--

--
-- Volcado de datos para la tabla `tiquete`
--

INSERT INTO `tiquete` (`id_tiquete`, `id_reserva_pasajero`, `numero_tiquete`, `fecha_emision`, `estado`) VALUES
(1, 1, 'TKT-1001', '2026-10-01 10:05:00', 'Emitido'),
(2, 2, 'TKT-1002', '2026-10-01 10:05:00', 'Emitido'),
(3, 5, 'TKT-1003', '2026-10-05 08:35:00', 'Emitido'),
(4, 8, 'TKT-1004', '2026-10-10 11:25:00', 'Emitido'),
(5, 9, 'TKT-1005', '2026-10-11 16:50:00', 'Emitido'),
(6, 6, 'TKT-1006', '2026-10-06 14:05:00', 'Anulado'),
(7, 7, 'TKT-1007', '2026-10-08 09:05:00', 'Anulado'),
(8, 10, 'TKT-1008', '2026-09-20 08:05:00', 'Emitido'),
(9, 3, 'TKT-1009', '2026-10-12 16:00:00', 'Emitido'),
(10, 4, 'TKT-1010', '2026-10-12 16:00:00', 'Emitido'),
(11, 11, 'TKT-1011', '2026-10-15 10:05:00', 'Emitido'),
(12, 12, 'TKT-1012', '2026-10-15 10:05:00', 'Emitido'),
(13, 14, 'TKT-1014', '2026-10-15 10:05:00', 'Emitido'),
(14, 15, 'TKT-1015', '2026-10-15 10:05:00', 'Emitido'),
(15, 16, 'TKT-1016', '2026-10-15 10:05:00', 'Emitido'),
(16, 17, 'TKT-1017', '2026-10-15 10:05:00', 'Emitido'),
(17, 18, 'TKT-1018', '2026-10-15 10:05:00', 'Emitido'),
(18, 19, 'TKT-1019', '2026-10-15 10:05:00', 'Emitido'),
(19, 20, 'TKT-1020', '2026-10-15 10:05:00', 'Emitido'),
(20, 21, 'TKT-1021', '2026-10-15 10:05:00', 'Emitido'),
(21, 22, 'TKT-1022', '2026-10-15 10:05:00', 'Emitido'),
(22, 24, 'TKT-1024', '2026-10-15 10:05:00', 'Emitido'),
(23, 25, 'TKT-1025', '2026-10-15 10:05:00', 'Emitido'),
(24, 27, 'TKT-1027', '2026-10-15 10:05:00', 'Emitido'),
(25, 28, 'TKT-1028', '2026-10-15 10:05:00', 'Emitido'),
(26, 29, 'TKT-1029', '2026-10-15 10:05:00', 'Emitido'),
(27, 30, 'TKT-1030', '2026-10-15 10:05:00', 'Emitido'),
(28, 31, 'TKT-1031', '2026-10-15 10:05:00', 'Emitido'),
(29, 32, 'TKT-1032', '2026-10-15 10:05:00', 'Emitido'),
(30, 33, 'TKT-1033', '2026-10-15 10:05:00', 'Emitido'),
(31, 34, 'TKT-1034', '2026-10-15 10:05:00', 'Emitido'),
(32, 35, 'TKT-1035', '2026-10-15 10:05:00', 'Emitido'),
(33, 36, 'TKT-1036', '2026-10-15 10:05:00', 'Emitido'),
(34, 37, 'TKT-1037', '2026-10-15 10:05:00', 'Emitido'),
(35, 38, 'TKT-1038', '2026-10-15 10:05:00', 'Emitido'),
(36, 39, 'TKT-1039', '2026-10-15 10:05:00', 'Emitido'),
(37, 40, 'TKT-1040', '2026-10-15 10:05:00', 'Emitido');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tramo_pasajero`
--

DROP TABLE IF EXISTS `tramo_pasajero`;
CREATE TABLE `tramo_pasajero` (
  `id_tramo_pasajero` int(11) NOT NULL,
  `id_reserva_pasajero` int(11) NOT NULL,
  `sentido` enum('Ida','Regreso') NOT NULL,
  `id_vuelo` int(11) NOT NULL,
  `id_tarifa` int(11) NOT NULL,
  `id_asiento` int(11) DEFAULT NULL,
  `precio_base` decimal(10,2) NOT NULL,
  `descuento` decimal(10,2) DEFAULT 0.00,
  `impuestos` decimal(10,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `tramo_pasajero`:
--   `id_reserva_pasajero`
--       `reserva_pasajero` -> `id_reserva_pasajero`
--   `id_vuelo`
--       `vuelo_tarifa` -> `id_vuelo`
--   `id_tarifa`
--       `vuelo_tarifa` -> `id_tarifa`
--   `id_asiento`
--       `asiento` -> `id_asiento`
--

--
-- Volcado de datos para la tabla `tramo_pasajero`
--

INSERT INTO `tramo_pasajero` (`id_tramo_pasajero`, `id_reserva_pasajero`, `sentido`, `id_vuelo`, `id_tarifa`, `id_asiento`, `precio_base`, `descuento`, `impuestos`) VALUES
(1, 1, 'Ida', 1, 1, 9, 180000.00, 0.00, 34200.00),
(2, 2, 'Ida', 1, 3, 10, 135000.00, 0.00, 25650.00),
(3, 3, 'Ida', 2, 2, NULL, 160000.00, 0.00, 30400.00),
(4, 4, 'Ida', 2, 2, NULL, 16000.00, 0.00, 3040.00),
(5, 5, 'Ida', 3, 6, NULL, 2500000.00, 100000.00, 125000.00),
(6, 6, 'Ida', 4, 3, NULL, 2100000.00, 0.00, 105000.00),
(7, 7, 'Ida', 5, 1, NULL, 850000.00, 0.00, 42500.00),
(8, 8, 'Ida', 6, 3, NULL, 950000.00, 0.00, 47500.00),
(9, 9, 'Ida', 7, 1, NULL, 120000.00, 0.00, 22800.00),
(10, 10, 'Ida', 8, 3, NULL, 180000.00, 0.00, 34200.00),
(11, 11, 'Ida', 276, 6, NULL, 431000.00, 0.00, 81890.00),
(12, 12, 'Ida', 645, 8, NULL, 1061000.00, 0.00, 201590.00),
(13, 13, 'Ida', 80, 4, NULL, 219000.00, 0.00, 41610.00),
(14, 14, 'Ida', 193, 5, NULL, 386000.00, 0.00, 73340.00),
(15, 15, 'Ida', 620, 10, NULL, 174000.00, 0.00, 33060.00),
(16, 16, 'Ida', 439, 1, NULL, 173000.00, 0.00, 32870.00),
(17, 17, 'Ida', 299, 4, NULL, 193000.00, 0.00, 36670.00),
(18, 18, 'Ida', 425, 2, NULL, 230000.00, 0.00, 43700.00),
(19, 19, 'Ida', 542, 4, NULL, 106000.00, 0.00, 20140.00),
(20, 20, 'Ida', 55, 4, NULL, 245000.00, 0.00, 46550.00),
(21, 21, 'Ida', 608, 9, NULL, 183000.00, 0.00, 34770.00),
(22, 22, 'Ida', 532, 6, NULL, 475000.00, 0.00, 90250.00),
(23, 23, 'Ida', 624, 3, NULL, 237000.00, 0.00, 45030.00),
(24, 24, 'Ida', 406, 5, NULL, 470000.00, 0.00, 89300.00),
(25, 25, 'Ida', 353, 9, NULL, 86000.00, 0.00, 16340.00),
(26, 26, 'Ida', 443, 10, NULL, 116000.00, 0.00, 22040.00),
(27, 27, 'Ida', 47, 6, NULL, 338000.00, 0.00, 64220.00),
(28, 28, 'Ida', 648, 7, NULL, 1287000.00, 0.00, 244530.00),
(29, 29, 'Ida', 416, 10, NULL, 198000.00, 0.00, 37620.00),
(30, 30, 'Ida', 527, 6, NULL, 440000.00, 0.00, 83600.00),
(31, 31, 'Ida', 511, 1, NULL, 92000.00, 0.00, 17480.00),
(32, 32, 'Ida', 343, 7, NULL, 1400000.00, 0.00, 266000.00),
(33, 33, 'Ida', 157, 4, NULL, 221000.00, 0.00, 41990.00),
(34, 34, 'Ida', 125, 10, NULL, 214000.00, 0.00, 40660.00),
(35, 35, 'Ida', 156, 5, NULL, 505000.00, 0.00, 95950.00),
(36, 36, 'Ida', 423, 4, NULL, 93000.00, 0.00, 17670.00),
(37, 37, 'Ida', 279, 10, NULL, 91000.00, 0.00, 17290.00),
(38, 38, 'Ida', 272, 1, NULL, 123000.00, 0.00, 23370.00),
(39, 39, 'Ida', 58, 3, NULL, 126000.00, 0.00, 23940.00),
(40, 40, 'Ida', 294, 6, NULL, 539000.00, 0.00, 102410.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario_sistema`
--

DROP TABLE IF EXISTS `usuario_sistema`;
CREATE TABLE `usuario_sistema` (
  `id_usuario` int(11) NOT NULL,
  `usuario` varchar(50) NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `numero_documento` varchar(50) NOT NULL,
  `correo_institucional` varchar(150) NOT NULL,
  `cargo` varchar(100) NOT NULL,
  `id_rol` int(11) NOT NULL,
  `sal_clave` varchar(32) NOT NULL,
  `hash_clave` varchar(64) NOT NULL,
  `secreto_mfa` varchar(32) NOT NULL,
  `estado` enum('Activo','Inactivo','Bloqueado') NOT NULL DEFAULT 'Activo',
  `intentos_fallidos` int(11) NOT NULL DEFAULT 0,
  `bloqueado_hasta` datetime DEFAULT NULL,
  `ultimo_acceso` datetime DEFAULT NULL,
  `fecha_creacion` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- RELACIONES PARA LA TABLA `usuario_sistema`:
--   `id_rol`
--       `rol` -> `id_rol`
--

--
-- Volcado de datos para la tabla `usuario_sistema`
--

INSERT INTO `usuario_sistema` (`id_usuario`, `usuario`, `nombres`, `apellidos`, `numero_documento`, `correo_institucional`, `cargo`, `id_rol`, `sal_clave`, `hash_clave`, `secreto_mfa`, `estado`, `intentos_fallidos`, `bloqueado_hasta`, `ultimo_acceso`, `fecha_creacion`) VALUES
(1, 'admin.torres', 'Ana', 'Torres', '1000000001', 'ana.torres@aerolinea.com', 'Administradora general del sistema', 1, 'RW92Z9W+2Lcxt4AziLcp9g==', 'JcsjsVDF5BO3OsR16Wic2Eh4jzk3gn60E9EwXwvXLCA=', 'QSBEQED2PCBBQXAY', 'Activo', 0, NULL, '2026-11-09 11:35:54', '2026-11-08 19:40:31'),
(2, 'tecnologia.ruiz', 'Carlos', 'Ruiz', '1000000002', 'carlos.ruiz@aerolinea.com', 'Jefe de Tecnologia', 2, 'OHkYaIltCO4yEVwwk43Q5A==', 'z9iHA5jwSP+N77UTENoYpVGrSVjqLEXTiafGXHuN/kg=', '3YDVZWCQK7LSLIPL', 'Activo', 0, NULL, '2026-11-09 10:03:52', '2026-11-08 19:40:31'),
(3, 'directivo.gomez', 'Laura', 'Gomez', '1000000003', 'laura.gomez@aerolinea.com', 'Directora de Operaciones Comerciales', 3, 'LbndSYDell7vxbXAYBUyAw==', 'U1JRYhcgNfeg7DqeqiSt5yhVgzgZ+9jgcl4I0Z9YHkE=', '6HU55ZUBQVHKBGNS', 'Activo', 0, NULL, '2026-11-09 10:00:10', '2026-11-08 19:40:31'),
(4, 'operaciones.diaz', 'Mateo', 'Diaz', '1000000004', 'mateo.diaz@aerolinea.com', 'Coordinador de Operaciones de Vuelo', 4, 'VW9GhvM0SbwUS0rNgtdcSw==', 'AlRhgat2Ws6jmAkh9hpXDhj9IUT2SNXgOG8GEnNKg6A=', 'DHL5KJOMJQUTXLRA', 'Activo', 0, NULL, NULL, '2026-11-08 19:40:31'),
(5, 'auditor.pardo', 'Sofia', 'Pardo', '1000000005', 'sofia.pardo@aerolinea.com', 'Auditora interna', 5, 'D7mGzRzim8w+ue+wi21xEA==', '4i8+ZELgzBfAIy/u0/Ke/Cvj5hmzH1p6IsCVrdAzQ2g=', 'LYEELZY3KQHFYCOA', 'Activo', 0, NULL, '2026-11-09 11:22:07', '2026-11-08 19:40:31'),
(6, 'admin.bascur', 'Luciano', 'Bascur', '6367083', 'luciano.bascur28@gmail.com', 'Administrador general del sistema', 1, 'yjYzxv2qcH+4+SSmZ5+ZWw==', 'T2lyoma6lhkvz+cQvoSDx+Bzb7MWjToETDq2azAWrjQ=', 'EXMAGPANQSU5U6IA', 'Activo', 0, NULL, '2026-11-09 12:18:01', '2026-11-09 11:00:33');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vuelo`
--

DROP TABLE IF EXISTS `vuelo`;
CREATE TABLE `vuelo` (
  `id_vuelo` int(11) NOT NULL,
  `numero_vuelo` varchar(20) NOT NULL,
  `id_ruta` int(11) NOT NULL,
  `id_aeronave` int(11) NOT NULL,
  `fecha_hora_salida` datetime NOT NULL,
  `fecha_hora_llegada` datetime NOT NULL,
  `estado` enum('Programado','Reprogramado','Cancelado') DEFAULT 'Programado'
) ;

--
-- RELACIONES PARA LA TABLA `vuelo`:
--   `id_ruta`
--       `ruta` -> `id_ruta`
--   `id_aeronave`
--       `aeronave` -> `id_aeronave`
--

--
-- Volcado de datos para la tabla `vuelo`
--

INSERT INTO `vuelo` (`id_vuelo`, `numero_vuelo`, `id_ruta`, `id_aeronave`, `fecha_hora_salida`, `fecha_hora_llegada`, `estado`) VALUES
(1, 'AV100', 1, 1, '2026-11-20 08:00:00', '2026-11-20 09:00:00', 'Programado'),
(2, 'AV101', 2, 2, '2026-11-21 10:30:00', '2026-11-21 12:00:00', 'Programado'),
(3, 'AV102', 3, 5, '2026-11-22 14:00:00', '2026-11-22 18:30:00', 'Programado'),
(4, 'AV103', 4, 6, '2026-11-23 20:00:00', '2026-11-24 06:00:00', 'Programado'),
(5, 'AV104', 5, 3, '2026-11-24 09:00:00', '2026-11-24 14:00:00', 'Reprogramado'),
(6, 'AV105', 6, 7, '2026-11-25 22:00:00', '2026-11-26 05:30:00', 'Programado'),
(7, 'AV106', 7, 5, '2026-11-26 15:00:00', '2026-11-26 18:30:00', 'Programado'),
(8, 'AV107', 8, 6, '2026-11-27 10:00:00', '2026-11-27 15:00:00', 'Programado'),
(9, 'AV108', 9, 8, '2026-11-28 07:00:00', '2026-11-28 10:00:00', 'Cancelado'),
(10, 'AV109', 10, 2, '2026-11-29 18:00:00', '2026-11-29 19:30:00', 'Programado'),
(11, 'AV110', 3, 1, '2026-10-16 06:00:00', '2026-10-16 09:30:00', 'Programado'),
(12, 'AV111', 5, 3, '2026-10-16 08:26:00', '2026-10-16 12:26:00', 'Programado'),
(13, 'AV112', 5, 1, '2026-10-16 09:14:00', '2026-10-16 13:14:00', 'Programado'),
(14, 'AV113', 1, 6, '2026-10-16 11:42:00', '2026-10-16 12:42:00', 'Programado'),
(15, 'AV114', 3, 7, '2026-10-16 14:05:00', '2026-10-16 17:35:00', 'Programado'),
(16, 'AV115', 6, 7, '2026-10-16 14:57:00', '2026-10-16 20:57:00', 'Programado'),
(17, 'AV116', 2, 3, '2026-10-16 16:22:00', '2026-10-16 17:52:00', 'Reprogramado'),
(18, 'AV117', 6, 1, '2026-10-16 18:40:00', '2026-10-17 00:40:00', 'Programado'),
(19, 'AV118', 7, 5, '2026-10-16 19:32:00', '2026-10-16 23:02:00', 'Programado'),
(20, 'AV119', 7, 10, '2026-10-16 21:44:00', '2026-10-17 01:14:00', 'Programado'),
(21, 'AV120', 9, 7, '2026-10-16 23:27:00', '2026-10-17 01:12:00', 'Reprogramado'),
(22, 'AV121', 3, 3, '2026-10-17 00:58:00', '2026-10-17 04:28:00', 'Programado'),
(23, 'AV122', 8, 3, '2026-10-17 02:10:00', '2026-10-17 12:10:00', 'Programado'),
(24, 'AV123', 4, 2, '2026-10-17 04:00:00', '2026-10-17 14:00:00', 'Programado'),
(25, 'AV124', 8, 1, '2026-10-17 05:43:00', '2026-10-17 15:43:00', 'Programado'),
(26, 'AV125', 2, 1, '2026-10-17 07:25:00', '2026-10-17 08:55:00', 'Programado'),
(27, 'AV126', 8, 6, '2026-10-17 08:29:00', '2026-10-17 18:29:00', 'Programado'),
(28, 'AV127', 1, 4, '2026-10-17 10:04:00', '2026-10-17 11:04:00', 'Programado'),
(29, 'AV128', 6, 10, '2026-10-17 11:37:00', '2026-10-17 17:37:00', 'Programado'),
(30, 'AV129', 4, 4, '2026-10-17 13:40:00', '2026-10-17 23:40:00', 'Programado'),
(31, 'AV130', 7, 2, '2026-10-17 14:45:00', '2026-10-17 18:15:00', 'Programado'),
(32, 'AV131', 4, 4, '2026-10-17 16:28:00', '2026-10-18 02:28:00', 'Reprogramado'),
(33, 'AV132', 10, 6, '2026-10-17 17:58:00', '2026-10-17 19:28:00', 'Programado'),
(34, 'AV133', 10, 10, '2026-10-17 20:13:00', '2026-10-17 21:43:00', 'Programado'),
(35, 'AV134', 7, 7, '2026-10-17 21:54:00', '2026-10-18 01:24:00', 'Programado'),
(36, 'AV135', 8, 4, '2026-10-17 23:09:00', '2026-10-18 09:09:00', 'Programado'),
(37, 'AV136', 9, 10, '2026-10-18 00:35:00', '2026-10-18 02:20:00', 'Reprogramado'),
(38, 'AV137', 1, 8, '2026-10-18 01:52:00', '2026-10-18 02:52:00', 'Programado'),
(39, 'AV138', 7, 9, '2026-10-18 03:36:00', '2026-10-18 07:06:00', 'Reprogramado'),
(40, 'AV139', 6, 8, '2026-10-18 05:30:00', '2026-10-18 11:30:00', 'Programado'),
(41, 'AV140', 8, 9, '2026-10-18 07:42:00', '2026-10-18 17:42:00', 'Programado'),
(42, 'AV141', 6, 6, '2026-10-18 09:19:00', '2026-10-18 15:19:00', 'Programado'),
(43, 'AV142', 4, 7, '2026-10-18 10:37:00', '2026-10-18 20:37:00', 'Cancelado'),
(44, 'AV143', 7, 2, '2026-10-18 12:50:00', '2026-10-18 16:20:00', 'Programado'),
(45, 'AV144', 9, 5, '2026-10-18 14:12:00', '2026-10-18 15:57:00', 'Programado'),
(46, 'AV145', 2, 7, '2026-10-18 15:43:00', '2026-10-18 17:13:00', 'Programado'),
(47, 'AV146', 2, 10, '2026-10-18 17:13:00', '2026-10-18 18:43:00', 'Programado'),
(48, 'AV147', 6, 2, '2026-10-18 18:36:00', '2026-10-19 00:36:00', 'Programado'),
(49, 'AV148', 10, 2, '2026-10-18 19:38:00', '2026-10-18 21:08:00', 'Programado'),
(50, 'AV149', 2, 2, '2026-10-18 21:47:00', '2026-10-18 23:17:00', 'Programado'),
(51, 'AV150', 4, 5, '2026-10-18 23:46:00', '2026-10-19 09:46:00', 'Programado'),
(52, 'AV151', 3, 8, '2026-10-19 02:12:00', '2026-10-19 05:42:00', 'Programado'),
(53, 'AV152', 9, 4, '2026-10-19 04:32:00', '2026-10-19 06:17:00', 'Programado'),
(54, 'AV153', 10, 8, '2026-10-19 05:25:00', '2026-10-19 06:55:00', 'Cancelado'),
(55, 'AV154', 3, 9, '2026-10-19 07:27:00', '2026-10-19 10:57:00', 'Programado'),
(56, 'AV155', 4, 2, '2026-10-19 09:44:00', '2026-10-19 19:44:00', 'Programado'),
(57, 'AV156', 9, 6, '2026-10-19 10:50:00', '2026-10-19 12:35:00', 'Programado'),
(58, 'AV157', 9, 3, '2026-10-19 11:53:00', '2026-10-19 13:38:00', 'Reprogramado'),
(59, 'AV158', 6, 1, '2026-10-19 12:41:00', '2026-10-19 18:41:00', 'Programado'),
(60, 'AV159', 1, 9, '2026-10-19 13:52:00', '2026-10-19 14:52:00', 'Programado'),
(61, 'AV160', 9, 5, '2026-10-19 15:51:00', '2026-10-19 17:36:00', 'Programado'),
(62, 'AV161', 9, 3, '2026-10-19 17:06:00', '2026-10-19 18:51:00', 'Reprogramado'),
(63, 'AV162', 5, 2, '2026-10-19 18:05:00', '2026-10-19 22:05:00', 'Programado'),
(64, 'AV163', 3, 7, '2026-10-19 20:35:00', '2026-10-20 00:05:00', 'Programado'),
(65, 'AV164', 7, 8, '2026-10-19 21:51:00', '2026-10-20 01:21:00', 'Programado'),
(66, 'AV165', 9, 1, '2026-10-19 23:35:00', '2026-10-20 01:20:00', 'Programado'),
(67, 'AV166', 3, 8, '2026-10-20 01:16:00', '2026-10-20 04:46:00', 'Programado'),
(68, 'AV167', 7, 4, '2026-10-20 02:48:00', '2026-10-20 06:18:00', 'Programado'),
(69, 'AV168', 10, 7, '2026-10-20 04:23:00', '2026-10-20 05:53:00', 'Programado'),
(70, 'AV169', 8, 4, '2026-10-20 08:50:00', '2026-10-20 18:50:00', 'Cancelado'),
(71, 'AV170', 6, 8, '2026-10-20 08:15:00', '2026-10-20 14:15:00', 'Programado'),
(72, 'AV171', 8, 2, '2026-10-20 09:38:00', '2026-10-20 19:38:00', 'Programado'),
(73, 'AV172', 5, 3, '2026-10-20 11:31:00', '2026-10-20 15:31:00', 'Programado'),
(74, 'AV173', 6, 6, '2026-10-20 12:18:00', '2026-10-20 18:18:00', 'Programado'),
(75, 'AV174', 4, 10, '2026-10-20 14:11:00', '2026-10-21 00:11:00', 'Programado'),
(76, 'AV175', 5, 3, '2026-10-20 15:23:00', '2026-10-20 19:23:00', 'Cancelado'),
(77, 'AV176', 8, 5, '2026-10-20 16:50:00', '2026-10-21 02:50:00', 'Programado'),
(78, 'AV177', 4, 9, '2026-10-20 17:36:00', '2026-10-21 03:36:00', 'Programado'),
(79, 'AV178', 8, 6, '2026-10-20 19:10:00', '2026-10-21 05:10:00', 'Programado'),
(80, 'AV179', 3, 3, '2026-10-20 20:24:00', '2026-10-20 23:54:00', 'Programado'),
(81, 'AV180', 3, 4, '2026-10-20 21:22:00', '2026-10-21 00:52:00', 'Programado'),
(82, 'AV181', 7, 4, '2026-10-20 22:19:00', '2026-10-21 01:49:00', 'Programado'),
(83, 'AV182', 6, 1, '2026-10-20 23:25:00', '2026-10-21 05:25:00', 'Programado'),
(84, 'AV183', 8, 7, '2026-10-21 00:29:00', '2026-10-21 10:29:00', 'Programado'),
(85, 'AV184', 5, 1, '2026-10-21 01:40:00', '2026-10-21 05:40:00', 'Programado'),
(86, 'AV185', 4, 5, '2026-10-21 04:00:00', '2026-10-21 14:00:00', 'Programado'),
(87, 'AV186', 9, 3, '2026-10-21 05:32:00', '2026-10-21 07:17:00', 'Programado'),
(88, 'AV187', 6, 9, '2026-10-21 06:23:00', '2026-10-21 12:23:00', 'Programado'),
(89, 'AV188', 7, 10, '2026-10-21 07:42:00', '2026-10-21 11:12:00', 'Programado'),
(90, 'AV189', 8, 8, '2026-10-21 08:40:00', '2026-10-21 18:40:00', 'Programado'),
(91, 'AV190', 4, 10, '2026-10-21 10:26:00', '2026-10-21 20:26:00', 'Programado'),
(92, 'AV191', 8, 3, '2026-10-21 11:43:00', '2026-10-21 21:43:00', 'Programado'),
(93, 'AV192', 7, 10, '2026-10-21 12:38:00', '2026-10-21 16:08:00', 'Programado'),
(94, 'AV193', 9, 4, '2026-10-21 14:04:00', '2026-10-21 15:49:00', 'Programado'),
(95, 'AV194', 7, 9, '2026-10-21 14:55:00', '2026-10-21 18:25:00', 'Programado'),
(96, 'AV195', 1, 7, '2026-10-21 16:35:00', '2026-10-21 17:35:00', 'Cancelado'),
(97, 'AV196', 7, 5, '2026-10-21 18:42:00', '2026-10-21 22:12:00', 'Programado'),
(98, 'AV197', 1, 9, '2026-10-21 20:09:00', '2026-10-21 21:09:00', 'Programado'),
(99, 'AV198', 10, 1, '2026-10-21 20:57:00', '2026-10-21 22:27:00', 'Programado'),
(100, 'AV199', 10, 1, '2026-10-21 23:00:00', '2026-10-22 00:30:00', 'Cancelado'),
(101, 'AV200', 10, 3, '2026-10-22 01:30:00', '2026-10-22 03:00:00', 'Reprogramado'),
(102, 'AV201', 7, 1, '2026-10-22 02:29:00', '2026-10-22 05:59:00', 'Programado'),
(103, 'AV202', 5, 5, '2026-10-22 03:58:00', '2026-10-22 07:58:00', 'Programado'),
(104, 'AV203', 5, 10, '2026-10-22 06:27:00', '2026-10-22 10:27:00', 'Programado'),
(105, 'AV204', 8, 9, '2026-10-22 08:57:00', '2026-10-22 18:57:00', 'Programado'),
(106, 'AV205', 5, 6, '2026-10-22 09:45:00', '2026-10-22 13:45:00', 'Programado'),
(107, 'AV206', 5, 2, '2026-10-22 11:43:00', '2026-10-22 15:43:00', 'Programado'),
(108, 'AV207', 6, 8, '2026-10-22 14:10:00', '2026-10-22 20:10:00', 'Programado'),
(109, 'AV208', 2, 8, '2026-10-22 15:23:00', '2026-10-22 16:53:00', 'Programado'),
(110, 'AV209', 8, 4, '2026-10-22 16:24:00', '2026-10-23 02:24:00', 'Programado'),
(111, 'AV210', 2, 9, '2026-10-22 18:09:00', '2026-10-22 19:39:00', 'Programado'),
(112, 'AV211', 5, 3, '2026-10-22 20:05:00', '2026-10-23 00:05:00', 'Programado'),
(113, 'AV212', 7, 2, '2026-10-22 21:00:00', '2026-10-23 00:30:00', 'Programado'),
(114, 'AV213', 7, 8, '2026-10-22 23:18:00', '2026-10-23 02:48:00', 'Programado'),
(115, 'AV214', 2, 1, '2026-10-23 01:07:00', '2026-10-23 02:37:00', 'Programado'),
(116, 'AV215', 10, 3, '2026-10-23 03:28:00', '2026-10-23 04:58:00', 'Programado'),
(117, 'AV216', 5, 9, '2026-10-23 05:46:00', '2026-10-23 09:46:00', 'Programado'),
(118, 'AV217', 5, 4, '2026-10-23 07:36:00', '2026-10-23 11:36:00', 'Programado'),
(119, 'AV218', 4, 10, '2026-10-23 09:24:00', '2026-10-23 19:24:00', 'Programado'),
(120, 'AV219', 5, 3, '2026-10-23 10:47:00', '2026-10-23 14:47:00', 'Programado'),
(121, 'AV220', 2, 1, '2026-10-23 13:08:00', '2026-10-23 14:38:00', 'Programado'),
(122, 'AV221', 8, 3, '2026-10-23 15:36:00', '2026-10-24 01:36:00', 'Programado'),
(123, 'AV222', 10, 6, '2026-10-23 16:35:00', '2026-10-23 18:05:00', 'Programado'),
(124, 'AV223', 4, 4, '2026-10-23 18:04:00', '2026-10-24 04:04:00', 'Cancelado'),
(125, 'AV224', 4, 3, '2026-10-23 19:28:00', '2026-10-24 05:28:00', 'Programado'),
(126, 'AV225', 10, 8, '2026-10-23 21:35:00', '2026-10-23 23:05:00', 'Programado'),
(127, 'AV226', 5, 10, '2026-10-23 23:11:00', '2026-10-24 03:11:00', 'Programado'),
(128, 'AV227', 1, 4, '2026-10-23 23:59:00', '2026-10-24 00:59:00', 'Programado'),
(129, 'AV228', 10, 7, '2026-10-24 01:14:00', '2026-10-24 02:44:00', 'Programado'),
(130, 'AV229', 8, 8, '2026-10-24 01:59:00', '2026-10-24 11:59:00', 'Programado'),
(131, 'AV230', 6, 3, '2026-10-24 02:52:00', '2026-10-24 08:52:00', 'Programado'),
(132, 'AV231', 7, 7, '2026-10-24 05:10:00', '2026-10-24 08:40:00', 'Reprogramado'),
(133, 'AV232', 1, 4, '2026-10-24 07:35:00', '2026-10-24 08:35:00', 'Programado'),
(134, 'AV233', 3, 3, '2026-10-24 10:04:00', '2026-10-24 13:34:00', 'Reprogramado'),
(135, 'AV234', 9, 1, '2026-10-24 12:30:00', '2026-10-24 14:15:00', 'Programado'),
(136, 'AV235', 5, 2, '2026-10-24 13:35:00', '2026-10-24 17:35:00', 'Reprogramado'),
(137, 'AV236', 10, 6, '2026-10-24 15:40:00', '2026-10-24 17:10:00', 'Programado'),
(138, 'AV237', 6, 2, '2026-10-24 17:36:00', '2026-10-24 23:36:00', 'Programado'),
(139, 'AV238', 2, 4, '2026-10-24 19:08:00', '2026-10-24 20:38:00', 'Programado'),
(140, 'AV239', 8, 4, '2026-10-24 21:12:00', '2026-10-25 07:12:00', 'Programado'),
(141, 'AV240', 9, 10, '2026-10-24 23:15:00', '2026-10-25 01:00:00', 'Programado'),
(142, 'AV241', 7, 9, '2026-10-25 01:22:00', '2026-10-25 04:52:00', 'Cancelado'),
(143, 'AV242', 7, 6, '2026-10-25 03:51:00', '2026-10-25 07:21:00', 'Programado'),
(144, 'AV243', 3, 5, '2026-10-25 04:58:00', '2026-10-25 08:28:00', 'Programado'),
(145, 'AV244', 1, 10, '2026-10-25 06:31:00', '2026-10-25 07:31:00', 'Cancelado'),
(146, 'AV245', 6, 5, '2026-10-25 08:15:00', '2026-10-25 14:15:00', 'Programado'),
(147, 'AV246', 4, 1, '2026-10-25 09:21:00', '2026-10-25 19:21:00', 'Programado'),
(148, 'AV247', 2, 2, '2026-10-25 10:33:00', '2026-10-25 12:03:00', 'Programado'),
(149, 'AV248', 7, 7, '2026-10-25 11:28:00', '2026-10-25 14:58:00', 'Programado'),
(150, 'AV249', 6, 4, '2026-10-25 12:25:00', '2026-10-25 18:25:00', 'Programado'),
(151, 'AV250', 7, 8, '2026-10-25 14:36:00', '2026-10-25 18:06:00', 'Programado'),
(152, 'AV251', 9, 9, '2026-10-25 16:39:00', '2026-10-25 18:24:00', 'Reprogramado'),
(153, 'AV252', 7, 1, '2026-10-25 17:29:00', '2026-10-25 20:59:00', 'Programado'),
(154, 'AV253', 6, 9, '2026-10-25 18:21:00', '2026-10-26 00:21:00', 'Programado'),
(155, 'AV254', 2, 6, '2026-10-25 20:30:00', '2026-10-25 22:00:00', 'Programado'),
(156, 'AV255', 5, 1, '2026-10-25 23:00:00', '2026-10-26 03:00:00', 'Programado'),
(157, 'AV256', 9, 8, '2026-10-26 00:03:00', '2026-10-26 01:48:00', 'Programado'),
(158, 'AV257', 2, 2, '2026-10-26 01:59:00', '2026-10-26 03:29:00', 'Programado'),
(159, 'AV258', 3, 5, '2026-10-26 04:23:00', '2026-10-26 07:53:00', 'Programado'),
(160, 'AV259', 5, 7, '2026-10-26 05:59:00', '2026-10-26 09:59:00', 'Programado'),
(161, 'AV260', 7, 3, '2026-10-26 07:47:00', '2026-10-26 11:17:00', 'Reprogramado'),
(162, 'AV261', 10, 6, '2026-10-26 10:05:00', '2026-10-26 11:35:00', 'Programado'),
(163, 'AV262', 1, 1, '2026-10-26 11:10:00', '2026-10-26 12:10:00', 'Programado'),
(164, 'AV263', 8, 3, '2026-10-26 12:16:00', '2026-10-26 22:16:00', 'Programado'),
(165, 'AV264', 10, 1, '2026-10-26 14:19:00', '2026-10-26 15:49:00', 'Programado'),
(166, 'AV265', 5, 9, '2026-10-26 16:18:00', '2026-10-26 20:18:00', 'Programado'),
(167, 'AV266', 2, 4, '2026-10-26 18:34:00', '2026-10-26 20:04:00', 'Programado'),
(168, 'AV267', 10, 2, '2026-10-26 20:24:00', '2026-10-26 21:54:00', 'Programado'),
(169, 'AV268', 3, 2, '2026-10-26 22:53:00', '2026-10-27 02:23:00', 'Reprogramado'),
(170, 'AV269', 10, 1, '2026-10-27 01:20:00', '2026-10-27 02:50:00', 'Reprogramado'),
(171, 'AV270', 10, 6, '2026-10-27 02:18:00', '2026-10-27 03:48:00', 'Programado'),
(172, 'AV271', 10, 1, '2026-10-27 04:14:00', '2026-10-27 05:44:00', 'Programado'),
(173, 'AV272', 9, 6, '2026-10-27 05:39:00', '2026-10-27 07:24:00', 'Programado'),
(174, 'AV273', 4, 1, '2026-10-27 07:05:00', '2026-10-27 17:05:00', 'Programado'),
(175, 'AV274', 8, 1, '2026-10-27 07:58:00', '2026-10-27 17:58:00', 'Programado'),
(176, 'AV275', 7, 4, '2026-10-27 08:44:00', '2026-10-27 12:14:00', 'Programado'),
(177, 'AV276', 2, 8, '2026-10-27 10:06:00', '2026-10-27 11:36:00', 'Programado'),
(178, 'AV277', 9, 3, '2026-10-27 11:54:00', '2026-10-27 13:39:00', 'Programado'),
(179, 'AV278', 5, 3, '2026-10-27 12:54:00', '2026-10-27 16:54:00', 'Programado'),
(180, 'AV279', 6, 10, '2026-10-27 15:05:00', '2026-10-27 21:05:00', 'Cancelado'),
(181, 'AV280', 10, 7, '2026-10-27 17:23:00', '2026-10-27 18:53:00', 'Programado'),
(182, 'AV281', 1, 2, '2026-10-27 18:36:00', '2026-10-27 19:36:00', 'Programado'),
(183, 'AV282', 3, 1, '2026-10-27 20:23:00', '2026-10-27 23:53:00', 'Programado'),
(184, 'AV283', 7, 8, '2026-10-27 22:11:00', '2026-10-28 01:41:00', 'Cancelado'),
(185, 'AV284', 6, 4, '2026-10-27 23:34:00', '2026-10-28 05:34:00', 'Programado'),
(186, 'AV285', 8, 2, '2026-10-28 00:56:00', '2026-10-28 10:56:00', 'Programado'),
(187, 'AV286', 8, 10, '2026-10-28 03:05:00', '2026-10-28 13:05:00', 'Programado'),
(188, 'AV287', 9, 6, '2026-10-28 04:28:00', '2026-10-28 06:13:00', 'Programado'),
(189, 'AV288', 4, 8, '2026-10-28 06:21:00', '2026-10-28 16:21:00', 'Programado'),
(190, 'AV289', 4, 3, '2026-10-28 09:21:00', '2026-10-28 19:21:00', 'Reprogramado'),
(191, 'AV290', 3, 7, '2026-10-28 08:31:00', '2026-10-28 12:01:00', 'Programado'),
(192, 'AV291', 2, 7, '2026-10-28 09:36:00', '2026-10-28 11:06:00', 'Programado'),
(193, 'AV292', 1, 9, '2026-10-28 10:32:00', '2026-10-28 11:32:00', 'Programado'),
(194, 'AV293', 2, 4, '2026-10-28 12:22:00', '2026-10-28 13:52:00', 'Programado'),
(195, 'AV294', 5, 6, '2026-10-28 14:36:00', '2026-10-28 18:36:00', 'Programado'),
(196, 'AV295', 1, 9, '2026-10-28 15:21:00', '2026-10-28 16:21:00', 'Programado'),
(197, 'AV296', 10, 8, '2026-10-28 16:30:00', '2026-10-28 18:00:00', 'Programado'),
(198, 'AV297', 10, 5, '2026-10-28 17:54:00', '2026-10-28 19:24:00', 'Programado'),
(199, 'AV298', 6, 9, '2026-10-28 19:29:00', '2026-10-29 01:29:00', 'Programado'),
(200, 'AV299', 6, 2, '2026-10-28 21:45:00', '2026-10-29 03:45:00', 'Programado'),
(201, 'AV300', 8, 1, '2026-10-28 23:27:00', '2026-10-29 09:27:00', 'Programado'),
(202, 'AV301', 8, 3, '2026-10-29 01:14:00', '2026-10-29 11:14:00', 'Programado'),
(203, 'AV302', 9, 1, '2026-10-29 02:27:00', '2026-10-29 04:12:00', 'Programado'),
(204, 'AV303', 5, 9, '2026-10-29 04:36:00', '2026-10-29 08:36:00', 'Reprogramado'),
(205, 'AV304', 10, 4, '2026-10-29 06:41:00', '2026-10-29 08:11:00', 'Programado'),
(206, 'AV305', 5, 6, '2026-10-29 09:01:00', '2026-10-29 13:01:00', 'Programado'),
(207, 'AV306', 6, 6, '2026-10-29 11:30:00', '2026-10-29 17:30:00', 'Reprogramado'),
(208, 'AV307', 2, 3, '2026-10-29 13:32:00', '2026-10-29 15:02:00', 'Programado'),
(209, 'AV308', 9, 4, '2026-10-29 15:55:00', '2026-10-29 17:40:00', 'Programado'),
(210, 'AV309', 8, 6, '2026-10-29 17:31:00', '2026-10-30 03:31:00', 'Programado'),
(211, 'AV310', 4, 2, '2026-10-29 19:00:00', '2026-10-30 05:00:00', 'Programado'),
(212, 'AV311', 2, 7, '2026-10-29 20:20:00', '2026-10-29 21:50:00', 'Programado'),
(213, 'AV312', 7, 2, '2026-10-29 22:19:00', '2026-10-30 01:49:00', 'Programado'),
(214, 'AV313', 4, 9, '2026-10-30 00:02:00', '2026-10-30 10:02:00', 'Programado'),
(215, 'AV314', 9, 1, '2026-10-30 02:03:00', '2026-10-30 03:48:00', 'Programado'),
(216, 'AV315', 2, 9, '2026-10-30 03:30:00', '2026-10-30 05:00:00', 'Reprogramado'),
(217, 'AV316', 4, 10, '2026-10-30 04:17:00', '2026-10-30 14:17:00', 'Programado'),
(218, 'AV317', 8, 10, '2026-10-30 05:28:00', '2026-10-30 15:28:00', 'Programado'),
(219, 'AV318', 4, 6, '2026-10-30 06:13:00', '2026-10-30 16:13:00', 'Programado'),
(220, 'AV319', 5, 9, '2026-10-30 08:06:00', '2026-10-30 12:06:00', 'Programado'),
(221, 'AV320', 8, 3, '2026-10-30 11:57:00', '2026-10-30 21:57:00', 'Reprogramado'),
(222, 'AV321', 8, 10, '2026-10-30 12:08:00', '2026-10-30 22:08:00', 'Programado'),
(223, 'AV322', 1, 6, '2026-10-30 14:15:00', '2026-10-30 15:15:00', 'Programado'),
(224, 'AV323', 8, 3, '2026-10-30 15:30:00', '2026-10-31 01:30:00', 'Programado'),
(225, 'AV324', 7, 8, '2026-10-30 16:33:00', '2026-10-30 20:03:00', 'Programado'),
(226, 'AV325', 4, 8, '2026-10-30 18:25:00', '2026-10-31 04:25:00', 'Programado'),
(227, 'AV326', 7, 9, '2026-10-30 20:18:00', '2026-10-30 23:48:00', 'Programado'),
(228, 'AV327', 10, 9, '2026-10-30 21:16:00', '2026-10-30 22:46:00', 'Programado'),
(229, 'AV328', 6, 2, '2026-10-30 22:51:00', '2026-10-31 04:51:00', 'Programado'),
(230, 'AV329', 6, 2, '2026-10-31 01:15:00', '2026-10-31 07:15:00', 'Programado'),
(231, 'AV330', 1, 10, '2026-10-31 02:30:00', '2026-10-31 03:30:00', 'Reprogramado'),
(232, 'AV331', 6, 10, '2026-10-31 03:38:00', '2026-10-31 09:38:00', 'Programado'),
(233, 'AV332', 9, 9, '2026-10-31 04:32:00', '2026-10-31 06:17:00', 'Cancelado'),
(234, 'AV333', 1, 10, '2026-10-31 06:01:00', '2026-10-31 07:01:00', 'Programado'),
(235, 'AV334', 4, 10, '2026-10-31 08:12:00', '2026-10-31 18:12:00', 'Programado'),
(236, 'AV335', 9, 7, '2026-10-31 09:33:00', '2026-10-31 11:18:00', 'Reprogramado'),
(237, 'AV336', 3, 5, '2026-10-31 10:20:00', '2026-10-31 13:50:00', 'Programado'),
(238, 'AV337', 1, 5, '2026-10-31 12:22:00', '2026-10-31 13:22:00', 'Cancelado'),
(239, 'AV338', 2, 6, '2026-10-31 14:25:00', '2026-10-31 15:55:00', 'Programado'),
(240, 'AV339', 6, 10, '2026-10-31 15:39:00', '2026-10-31 21:39:00', 'Programado'),
(241, 'AV340', 1, 3, '2026-10-31 17:47:00', '2026-10-31 18:47:00', 'Programado'),
(242, 'AV341', 10, 9, '2026-10-31 19:47:00', '2026-10-31 21:17:00', 'Programado'),
(243, 'AV342', 3, 3, '2026-10-31 21:46:00', '2026-11-01 01:16:00', 'Programado'),
(244, 'AV343', 5, 8, '2026-10-31 23:32:00', '2026-11-01 03:32:00', 'Reprogramado'),
(245, 'AV344', 3, 2, '2026-11-01 01:45:00', '2026-11-01 05:15:00', 'Programado'),
(246, 'AV345', 3, 6, '2026-11-01 04:04:00', '2026-11-01 07:34:00', 'Programado'),
(247, 'AV346', 10, 9, '2026-11-01 05:10:00', '2026-11-01 06:40:00', 'Programado'),
(248, 'AV347', 1, 3, '2026-11-01 06:18:00', '2026-11-01 07:18:00', 'Programado'),
(249, 'AV348', 9, 6, '2026-11-01 07:32:00', '2026-11-01 09:17:00', 'Programado'),
(250, 'AV349', 3, 5, '2026-11-01 09:59:00', '2026-11-01 13:29:00', 'Programado'),
(251, 'AV350', 9, 9, '2026-11-01 11:54:00', '2026-11-01 13:39:00', 'Programado'),
(252, 'AV351', 10, 9, '2026-11-01 13:35:00', '2026-11-01 15:05:00', 'Reprogramado'),
(253, 'AV352', 9, 7, '2026-11-01 15:56:00', '2026-11-01 17:41:00', 'Programado'),
(254, 'AV353', 9, 1, '2026-11-01 16:46:00', '2026-11-01 18:31:00', 'Programado'),
(255, 'AV354', 10, 6, '2026-11-01 17:35:00', '2026-11-01 19:05:00', 'Programado'),
(256, 'AV355', 10, 8, '2026-11-01 19:27:00', '2026-11-01 20:57:00', 'Programado'),
(257, 'AV356', 5, 10, '2026-11-01 21:50:00', '2026-11-02 01:50:00', 'Cancelado'),
(258, 'AV357', 4, 10, '2026-11-01 23:29:00', '2026-11-02 09:29:00', 'Programado'),
(259, 'AV358', 9, 9, '2026-11-02 00:31:00', '2026-11-02 02:16:00', 'Programado'),
(260, 'AV359', 9, 3, '2026-11-02 02:34:00', '2026-11-02 04:19:00', 'Programado'),
(261, 'AV360', 8, 3, '2026-11-02 03:22:00', '2026-11-02 13:22:00', 'Cancelado'),
(262, 'AV361', 2, 4, '2026-11-02 04:58:00', '2026-11-02 06:28:00', 'Programado'),
(263, 'AV362', 3, 7, '2026-11-02 07:09:00', '2026-11-02 10:39:00', 'Programado'),
(264, 'AV363', 4, 2, '2026-11-02 08:14:00', '2026-11-02 18:14:00', 'Programado'),
(265, 'AV364', 5, 3, '2026-11-02 10:43:00', '2026-11-02 14:43:00', 'Reprogramado'),
(266, 'AV365', 2, 4, '2026-11-02 11:46:00', '2026-11-02 13:16:00', 'Programado'),
(267, 'AV366', 7, 8, '2026-11-02 12:49:00', '2026-11-02 16:19:00', 'Programado'),
(268, 'AV367', 2, 6, '2026-11-02 13:54:00', '2026-11-02 15:24:00', 'Programado'),
(269, 'AV368', 3, 3, '2026-11-02 15:18:00', '2026-11-02 18:48:00', 'Programado'),
(270, 'AV369', 7, 9, '2026-11-02 16:33:00', '2026-11-02 20:03:00', 'Programado'),
(271, 'AV370', 6, 1, '2026-11-02 17:22:00', '2026-11-02 23:22:00', 'Programado'),
(272, 'AV371', 4, 7, '2026-11-02 19:32:00', '2026-11-03 05:32:00', 'Programado'),
(273, 'AV372', 5, 7, '2026-11-02 21:20:00', '2026-11-03 01:20:00', 'Programado'),
(274, 'AV373', 1, 2, '2026-11-02 23:34:00', '2026-11-03 00:34:00', 'Programado'),
(275, 'AV374', 4, 2, '2026-11-03 01:12:00', '2026-11-03 11:12:00', 'Reprogramado'),
(276, 'AV375', 10, 1, '2026-11-03 03:31:00', '2026-11-03 05:01:00', 'Programado'),
(277, 'AV376', 4, 7, '2026-11-03 05:51:00', '2026-11-03 15:51:00', 'Programado'),
(278, 'AV377', 8, 2, '2026-11-03 08:04:00', '2026-11-03 18:04:00', 'Programado'),
(279, 'AV378', 7, 8, '2026-11-03 10:00:00', '2026-11-03 13:30:00', 'Programado'),
(280, 'AV379', 5, 3, '2026-11-03 12:23:00', '2026-11-03 16:23:00', 'Reprogramado'),
(281, 'AV380', 4, 9, '2026-11-03 14:17:00', '2026-11-04 00:17:00', 'Reprogramado'),
(282, 'AV381', 10, 10, '2026-11-03 16:43:00', '2026-11-03 18:13:00', 'Programado'),
(283, 'AV382', 10, 4, '2026-11-03 18:21:00', '2026-11-03 19:51:00', 'Programado'),
(284, 'AV383', 1, 3, '2026-11-03 19:21:00', '2026-11-03 20:21:00', 'Programado'),
(285, 'AV384', 3, 7, '2026-11-03 20:53:00', '2026-11-04 00:23:00', 'Programado'),
(286, 'AV385', 7, 5, '2026-11-03 22:40:00', '2026-11-04 02:10:00', 'Programado'),
(287, 'AV386', 1, 3, '2026-11-03 23:37:00', '2026-11-04 00:37:00', 'Programado'),
(288, 'AV387', 2, 9, '2026-11-04 01:35:00', '2026-11-04 03:05:00', 'Programado'),
(289, 'AV388', 6, 6, '2026-11-04 03:49:00', '2026-11-04 09:49:00', 'Cancelado'),
(290, 'AV389', 7, 3, '2026-11-04 05:53:00', '2026-11-04 09:23:00', 'Programado'),
(291, 'AV390', 5, 6, '2026-11-04 06:59:00', '2026-11-04 10:59:00', 'Programado'),
(292, 'AV391', 1, 10, '2026-11-04 09:10:00', '2026-11-04 10:10:00', 'Programado'),
(293, 'AV392', 2, 5, '2026-11-04 11:20:00', '2026-11-04 12:50:00', 'Programado'),
(294, 'AV393', 8, 9, '2026-11-04 12:51:00', '2026-11-04 22:51:00', 'Programado'),
(295, 'AV394', 2, 2, '2026-11-04 14:07:00', '2026-11-04 15:37:00', 'Programado'),
(296, 'AV395', 7, 10, '2026-11-04 16:28:00', '2026-11-04 19:58:00', 'Programado'),
(297, 'AV396', 3, 8, '2026-11-04 18:32:00', '2026-11-04 22:02:00', 'Cancelado'),
(298, 'AV397', 3, 9, '2026-11-04 20:59:00', '2026-11-05 00:29:00', 'Programado'),
(299, 'AV398', 3, 10, '2026-11-04 23:03:00', '2026-11-05 02:33:00', 'Programado'),
(300, 'AV399', 7, 1, '2026-11-05 01:01:00', '2026-11-05 04:31:00', 'Programado'),
(301, 'AV400', 1, 10, '2026-11-05 02:14:00', '2026-11-05 03:14:00', 'Reprogramado'),
(302, 'AV401', 3, 6, '2026-11-05 04:41:00', '2026-11-05 08:11:00', 'Programado'),
(303, 'AV402', 5, 10, '2026-11-05 05:47:00', '2026-11-05 09:47:00', 'Reprogramado'),
(304, 'AV403', 9, 7, '2026-11-05 08:12:00', '2026-11-05 09:57:00', 'Programado'),
(305, 'AV404', 9, 3, '2026-11-05 10:00:00', '2026-11-05 11:45:00', 'Programado'),
(306, 'AV405', 2, 4, '2026-11-05 12:22:00', '2026-11-05 13:52:00', 'Programado'),
(307, 'AV406', 8, 5, '2026-11-05 14:32:00', '2026-11-06 00:32:00', 'Programado'),
(308, 'AV407', 2, 5, '2026-11-05 15:34:00', '2026-11-05 17:04:00', 'Programado'),
(309, 'AV408', 8, 3, '2026-11-05 17:47:00', '2026-11-06 03:47:00', 'Reprogramado'),
(310, 'AV409', 7, 10, '2026-11-05 19:25:00', '2026-11-05 22:55:00', 'Programado'),
(311, 'AV410', 4, 7, '2026-11-05 20:20:00', '2026-11-06 06:20:00', 'Reprogramado'),
(312, 'AV411', 9, 1, '2026-11-05 22:29:00', '2026-11-06 00:14:00', 'Reprogramado'),
(313, 'AV412', 2, 6, '2026-11-06 00:32:00', '2026-11-06 02:02:00', 'Programado'),
(314, 'AV413', 4, 7, '2026-11-06 01:29:00', '2026-11-06 11:29:00', 'Programado'),
(315, 'AV414', 2, 10, '2026-11-06 03:39:00', '2026-11-06 05:09:00', 'Programado'),
(316, 'AV415', 1, 7, '2026-11-06 05:27:00', '2026-11-06 06:27:00', 'Programado'),
(317, 'AV416', 4, 4, '2026-11-06 06:44:00', '2026-11-06 16:44:00', 'Reprogramado'),
(318, 'AV417', 2, 2, '2026-11-06 08:15:00', '2026-11-06 09:45:00', 'Programado'),
(319, 'AV418', 3, 2, '2026-11-06 09:00:00', '2026-11-06 12:30:00', 'Programado'),
(320, 'AV419', 8, 3, '2026-11-06 10:40:00', '2026-11-06 20:40:00', 'Cancelado'),
(321, 'AV420', 7, 1, '2026-11-06 12:56:00', '2026-11-06 16:26:00', 'Programado'),
(322, 'AV421', 1, 6, '2026-11-06 14:30:00', '2026-11-06 15:30:00', 'Programado'),
(323, 'AV422', 7, 4, '2026-11-06 16:42:00', '2026-11-06 20:12:00', 'Programado'),
(324, 'AV423', 1, 7, '2026-11-06 17:45:00', '2026-11-06 18:45:00', 'Cancelado'),
(325, 'AV424', 8, 3, '2026-11-06 18:43:00', '2026-11-07 04:43:00', 'Programado'),
(326, 'AV425', 3, 5, '2026-11-06 19:49:00', '2026-11-06 23:19:00', 'Programado'),
(327, 'AV426', 10, 10, '2026-11-06 21:27:00', '2026-11-06 22:57:00', 'Programado'),
(328, 'AV427', 9, 1, '2026-11-06 23:54:00', '2026-11-07 01:39:00', 'Programado'),
(329, 'AV428', 6, 4, '2026-11-07 01:22:00', '2026-11-07 07:22:00', 'Reprogramado'),
(330, 'AV429', 5, 3, '2026-11-07 03:19:00', '2026-11-07 07:19:00', 'Cancelado'),
(331, 'AV430', 7, 10, '2026-11-07 05:20:00', '2026-11-07 08:50:00', 'Programado'),
(332, 'AV431', 9, 9, '2026-11-07 07:11:00', '2026-11-07 08:56:00', 'Cancelado'),
(333, 'AV432', 9, 4, '2026-11-07 09:15:00', '2026-11-07 11:00:00', 'Programado'),
(334, 'AV433', 7, 9, '2026-11-07 10:36:00', '2026-11-07 14:06:00', 'Programado'),
(335, 'AV434', 3, 8, '2026-11-07 13:06:00', '2026-11-07 16:36:00', 'Reprogramado'),
(336, 'AV435', 2, 3, '2026-11-07 14:29:00', '2026-11-07 15:59:00', 'Programado'),
(337, 'AV436', 7, 6, '2026-11-07 15:24:00', '2026-11-07 18:54:00', 'Programado'),
(338, 'AV437', 1, 10, '2026-11-07 16:43:00', '2026-11-07 17:43:00', 'Programado'),
(339, 'AV438', 8, 4, '2026-11-07 17:35:00', '2026-11-08 03:35:00', 'Programado'),
(340, 'AV439', 1, 10, '2026-11-07 19:05:00', '2026-11-07 20:05:00', 'Programado'),
(341, 'AV440', 8, 1, '2026-11-07 20:49:00', '2026-11-08 06:49:00', 'Programado'),
(342, 'AV441', 9, 9, '2026-11-07 22:19:00', '2026-11-08 00:04:00', 'Programado'),
(343, 'AV442', 9, 10, '2026-11-08 00:17:00', '2026-11-08 02:02:00', 'Programado'),
(344, 'AV443', 4, 4, '2026-11-08 01:37:00', '2026-11-08 11:37:00', 'Programado'),
(345, 'AV444', 9, 3, '2026-11-08 03:28:00', '2026-11-08 05:13:00', 'Programado'),
(346, 'AV445', 10, 10, '2026-11-08 04:24:00', '2026-11-08 05:54:00', 'Programado'),
(347, 'AV446', 1, 2, '2026-11-08 06:31:00', '2026-11-08 07:31:00', 'Reprogramado'),
(348, 'AV447', 8, 8, '2026-11-08 07:39:00', '2026-11-08 17:39:00', 'Programado'),
(349, 'AV448', 5, 9, '2026-11-08 09:18:00', '2026-11-08 13:18:00', 'Programado'),
(350, 'AV449', 4, 1, '2026-11-08 10:58:00', '2026-11-08 20:58:00', 'Programado'),
(351, 'AV450', 4, 1, '2026-11-08 12:40:00', '2026-11-08 22:40:00', 'Programado'),
(352, 'AV451', 1, 6, '2026-11-08 14:23:00', '2026-11-08 15:23:00', 'Programado'),
(353, 'AV452', 9, 7, '2026-11-08 16:41:00', '2026-11-08 18:26:00', 'Cancelado'),
(354, 'AV453', 1, 1, '2026-11-08 17:41:00', '2026-11-08 18:41:00', 'Programado'),
(355, 'AV454', 10, 2, '2026-11-08 19:17:00', '2026-11-08 20:47:00', 'Reprogramado'),
(356, 'AV455', 6, 4, '2026-11-08 21:36:00', '2026-11-09 03:36:00', 'Programado'),
(357, 'AV456', 10, 3, '2026-11-08 22:38:00', '2026-11-09 00:08:00', 'Programado'),
(358, 'AV457', 10, 3, '2026-11-09 01:05:00', '2026-11-09 02:35:00', 'Programado'),
(359, 'AV458', 8, 5, '2026-11-09 02:13:00', '2026-11-09 12:13:00', 'Programado'),
(360, 'AV459', 5, 6, '2026-11-09 04:02:00', '2026-11-09 08:02:00', 'Reprogramado'),
(361, 'AV460', 8, 4, '2026-11-09 05:56:00', '2026-11-09 15:56:00', 'Reprogramado'),
(362, 'AV461', 6, 3, '2026-11-09 08:03:00', '2026-11-09 14:03:00', 'Programado'),
(363, 'AV462', 3, 4, '2026-11-09 09:06:00', '2026-11-09 12:36:00', 'Programado'),
(364, 'AV463', 1, 8, '2026-11-09 11:08:00', '2026-11-09 12:08:00', 'Programado'),
(365, 'AV464', 5, 9, '2026-11-09 11:56:00', '2026-11-09 15:56:00', 'Programado'),
(366, 'AV465', 10, 3, '2026-11-09 12:49:00', '2026-11-09 14:19:00', 'Programado'),
(367, 'AV466', 1, 8, '2026-11-09 15:11:00', '2026-11-09 16:11:00', 'Programado'),
(368, 'AV467', 2, 6, '2026-11-09 16:52:00', '2026-11-09 18:22:00', 'Programado'),
(369, 'AV468', 4, 7, '2026-11-09 19:07:00', '2026-11-10 05:07:00', 'Programado'),
(370, 'AV469', 2, 5, '2026-11-09 20:03:00', '2026-11-09 21:33:00', 'Programado'),
(371, 'AV470', 2, 1, '2026-11-09 21:49:00', '2026-11-09 23:19:00', 'Programado'),
(372, 'AV471', 3, 3, '2026-11-09 23:58:00', '2026-11-10 03:28:00', 'Programado'),
(373, 'AV472', 9, 7, '2026-11-10 01:55:00', '2026-11-10 03:40:00', 'Programado'),
(374, 'AV473', 10, 10, '2026-11-10 03:32:00', '2026-11-10 05:02:00', 'Programado'),
(375, 'AV474', 4, 9, '2026-11-10 04:41:00', '2026-11-10 14:41:00', 'Reprogramado'),
(376, 'AV475', 8, 10, '2026-11-10 06:44:00', '2026-11-10 16:44:00', 'Programado'),
(377, 'AV476', 8, 2, '2026-11-10 07:33:00', '2026-11-10 17:33:00', 'Programado'),
(378, 'AV477', 3, 10, '2026-11-10 08:20:00', '2026-11-10 11:50:00', 'Programado'),
(379, 'AV478', 3, 2, '2026-11-10 09:23:00', '2026-11-10 12:53:00', 'Reprogramado'),
(380, 'AV479', 1, 7, '2026-11-10 10:46:00', '2026-11-10 11:46:00', 'Reprogramado'),
(381, 'AV480', 10, 7, '2026-11-10 13:03:00', '2026-11-10 14:33:00', 'Programado'),
(382, 'AV481', 9, 5, '2026-11-10 14:37:00', '2026-11-10 16:22:00', 'Programado'),
(383, 'AV482', 9, 10, '2026-11-10 15:41:00', '2026-11-10 17:26:00', 'Programado'),
(384, 'AV483', 2, 6, '2026-11-10 16:44:00', '2026-11-10 18:14:00', 'Cancelado'),
(385, 'AV484', 8, 4, '2026-11-10 19:02:00', '2026-11-11 05:02:00', 'Reprogramado'),
(386, 'AV485', 9, 7, '2026-11-10 21:00:00', '2026-11-10 22:45:00', 'Programado'),
(387, 'AV486', 8, 6, '2026-11-10 22:05:00', '2026-11-11 08:05:00', 'Programado'),
(388, 'AV487', 4, 7, '2026-11-10 23:13:00', '2026-11-11 09:13:00', 'Programado'),
(389, 'AV488', 10, 5, '2026-11-11 01:12:00', '2026-11-11 02:42:00', 'Programado'),
(390, 'AV489', 2, 6, '2026-11-11 02:49:00', '2026-11-11 04:19:00', 'Programado'),
(391, 'AV490', 2, 6, '2026-11-11 04:14:00', '2026-11-11 05:44:00', 'Programado'),
(392, 'AV491', 2, 1, '2026-11-11 05:51:00', '2026-11-11 07:21:00', 'Programado'),
(393, 'AV492', 3, 4, '2026-11-11 06:39:00', '2026-11-11 10:09:00', 'Programado'),
(394, 'AV493', 7, 9, '2026-11-11 08:29:00', '2026-11-11 11:59:00', 'Programado'),
(395, 'AV494', 7, 8, '2026-11-11 09:51:00', '2026-11-11 13:21:00', 'Programado'),
(396, 'AV495', 10, 9, '2026-11-11 11:15:00', '2026-11-11 12:45:00', 'Programado'),
(397, 'AV496', 4, 2, '2026-11-11 13:38:00', '2026-11-11 23:38:00', 'Programado'),
(398, 'AV497', 4, 2, '2026-11-11 15:33:00', '2026-11-12 01:33:00', 'Reprogramado'),
(399, 'AV498', 3, 8, '2026-11-11 17:53:00', '2026-11-11 21:23:00', 'Programado'),
(400, 'AV499', 5, 6, '2026-11-11 18:57:00', '2026-11-11 22:57:00', 'Reprogramado'),
(401, 'AV500', 1, 4, '2026-11-11 20:05:00', '2026-11-11 21:05:00', 'Programado'),
(402, 'AV501', 1, 4, '2026-11-11 22:22:00', '2026-11-11 23:22:00', 'Programado'),
(403, 'AV502', 7, 4, '2026-11-11 23:44:00', '2026-11-12 03:14:00', 'Programado'),
(404, 'AV503', 6, 5, '2026-11-12 01:31:00', '2026-11-12 07:31:00', 'Programado'),
(405, 'AV504', 2, 6, '2026-11-12 03:49:00', '2026-11-12 05:19:00', 'Programado'),
(406, 'AV505', 8, 2, '2026-11-12 05:25:00', '2026-11-12 15:25:00', 'Reprogramado'),
(407, 'AV506', 3, 6, '2026-11-12 06:30:00', '2026-11-12 10:00:00', 'Programado'),
(408, 'AV507', 9, 8, '2026-11-12 08:35:00', '2026-11-12 10:20:00', 'Programado'),
(409, 'AV508', 10, 6, '2026-11-12 11:05:00', '2026-11-12 12:35:00', 'Programado'),
(410, 'AV509', 4, 5, '2026-11-12 12:43:00', '2026-11-12 22:43:00', 'Programado'),
(411, 'AV510', 2, 2, '2026-11-12 13:32:00', '2026-11-12 15:02:00', 'Reprogramado'),
(412, 'AV511', 9, 8, '2026-11-12 15:30:00', '2026-11-12 17:15:00', 'Programado'),
(413, 'AV512', 1, 5, '2026-11-12 16:22:00', '2026-11-12 17:22:00', 'Programado'),
(414, 'AV513', 2, 8, '2026-11-12 17:45:00', '2026-11-12 19:15:00', 'Programado'),
(415, 'AV514', 8, 4, '2026-11-12 18:37:00', '2026-11-13 04:37:00', 'Programado'),
(416, 'AV515', 3, 5, '2026-11-12 20:17:00', '2026-11-12 23:47:00', 'Programado'),
(417, 'AV516', 4, 7, '2026-11-12 21:56:00', '2026-11-13 07:56:00', 'Programado'),
(418, 'AV517', 7, 9, '2026-11-12 22:57:00', '2026-11-13 02:27:00', 'Programado'),
(419, 'AV518', 4, 4, '2026-11-13 00:15:00', '2026-11-13 10:15:00', 'Programado'),
(420, 'AV519', 3, 2, '2026-11-13 01:35:00', '2026-11-13 05:05:00', 'Programado'),
(421, 'AV520', 9, 9, '2026-11-13 03:41:00', '2026-11-13 05:26:00', 'Cancelado'),
(422, 'AV521', 1, 8, '2026-11-13 05:28:00', '2026-11-13 06:28:00', 'Programado'),
(423, 'AV522', 10, 1, '2026-11-13 06:17:00', '2026-11-13 07:47:00', 'Reprogramado'),
(424, 'AV523', 1, 8, '2026-11-13 08:26:00', '2026-11-13 09:26:00', 'Programado'),
(425, 'AV524', 9, 6, '2026-11-13 09:54:00', '2026-11-13 11:39:00', 'Programado'),
(426, 'AV525', 2, 1, '2026-11-13 11:01:00', '2026-11-13 12:31:00', 'Programado'),
(427, 'AV526', 1, 9, '2026-11-13 12:28:00', '2026-11-13 13:28:00', 'Programado'),
(428, 'AV527', 6, 6, '2026-11-13 13:33:00', '2026-11-13 19:33:00', 'Programado'),
(429, 'AV528', 1, 8, '2026-11-13 14:56:00', '2026-11-13 15:56:00', 'Programado'),
(430, 'AV529', 3, 9, '2026-11-13 17:10:00', '2026-11-13 20:40:00', 'Programado'),
(431, 'AV530', 4, 2, '2026-11-13 18:47:00', '2026-11-14 04:47:00', 'Reprogramado'),
(432, 'AV531', 3, 1, '2026-11-13 20:30:00', '2026-11-14 00:00:00', 'Programado'),
(433, 'AV532', 3, 3, '2026-11-13 21:52:00', '2026-11-14 01:22:00', 'Programado'),
(434, 'AV533', 4, 7, '2026-11-13 23:31:00', '2026-11-14 09:31:00', 'Cancelado'),
(435, 'AV534', 1, 9, '2026-11-14 01:59:00', '2026-11-14 02:59:00', 'Reprogramado'),
(436, 'AV535', 9, 8, '2026-11-14 04:03:00', '2026-11-14 05:48:00', 'Programado'),
(437, 'AV536', 5, 10, '2026-11-14 05:24:00', '2026-11-14 09:24:00', 'Programado'),
(438, 'AV537', 7, 5, '2026-11-14 06:33:00', '2026-11-14 10:03:00', 'Programado'),
(439, 'AV538', 2, 4, '2026-11-14 08:27:00', '2026-11-14 09:57:00', 'Programado'),
(440, 'AV539', 2, 10, '2026-11-14 10:26:00', '2026-11-14 11:56:00', 'Programado'),
(441, 'AV540', 7, 4, '2026-11-14 11:45:00', '2026-11-14 15:15:00', 'Programado'),
(442, 'AV541', 7, 5, '2026-11-14 13:47:00', '2026-11-14 17:17:00', 'Cancelado'),
(443, 'AV542', 5, 7, '2026-11-14 14:35:00', '2026-11-14 18:35:00', 'Programado'),
(444, 'AV543', 7, 6, '2026-11-14 16:17:00', '2026-11-14 19:47:00', 'Programado'),
(445, 'AV544', 9, 3, '2026-11-14 18:12:00', '2026-11-14 19:57:00', 'Programado'),
(446, 'AV545', 6, 1, '2026-11-14 19:30:00', '2026-11-15 01:30:00', 'Reprogramado'),
(447, 'AV546', 8, 1, '2026-11-14 21:50:00', '2026-11-15 07:50:00', 'Programado'),
(448, 'AV547', 5, 9, '2026-11-14 23:10:00', '2026-11-15 03:10:00', 'Programado'),
(449, 'AV548', 5, 9, '2026-11-15 00:58:00', '2026-11-15 04:58:00', 'Programado'),
(450, 'AV549', 6, 1, '2026-11-15 03:21:00', '2026-11-15 09:21:00', 'Programado'),
(451, 'AV550', 4, 9, '2026-11-15 04:39:00', '2026-11-15 14:39:00', 'Cancelado'),
(452, 'AV551', 5, 4, '2026-11-15 06:33:00', '2026-11-15 10:33:00', 'Programado'),
(453, 'AV552', 2, 3, '2026-11-15 07:27:00', '2026-11-15 08:57:00', 'Reprogramado'),
(454, 'AV553', 1, 4, '2026-11-15 08:28:00', '2026-11-15 09:28:00', 'Programado'),
(455, 'AV554', 8, 10, '2026-11-15 09:13:00', '2026-11-15 19:13:00', 'Programado'),
(456, 'AV555', 8, 8, '2026-11-15 10:52:00', '2026-11-15 20:52:00', 'Programado'),
(457, 'AV556', 5, 2, '2026-11-15 13:00:00', '2026-11-15 17:00:00', 'Programado'),
(458, 'AV557', 5, 5, '2026-11-15 14:31:00', '2026-11-15 18:31:00', 'Programado'),
(459, 'AV558', 5, 5, '2026-11-15 16:29:00', '2026-11-15 20:29:00', 'Programado'),
(460, 'AV559', 10, 1, '2026-11-15 18:27:00', '2026-11-15 19:57:00', 'Programado'),
(461, 'AV560', 3, 7, '2026-11-15 20:27:00', '2026-11-15 23:57:00', 'Programado'),
(462, 'AV561', 5, 7, '2026-11-15 21:42:00', '2026-11-16 01:42:00', 'Programado'),
(463, 'AV562', 2, 7, '2026-11-15 22:30:00', '2026-11-16 00:00:00', 'Programado'),
(464, 'AV563', 2, 2, '2026-11-16 00:10:00', '2026-11-16 01:40:00', 'Programado'),
(465, 'AV564', 2, 3, '2026-11-16 02:21:00', '2026-11-16 03:51:00', 'Programado'),
(466, 'AV565', 8, 3, '2026-11-16 04:42:00', '2026-11-16 14:42:00', 'Programado'),
(467, 'AV566', 3, 10, '2026-11-16 06:51:00', '2026-11-16 10:21:00', 'Programado'),
(468, 'AV567', 6, 9, '2026-11-16 08:40:00', '2026-11-16 14:40:00', 'Programado'),
(469, 'AV568', 3, 2, '2026-11-16 11:00:00', '2026-11-16 14:30:00', 'Programado'),
(470, 'AV569', 8, 10, '2026-11-16 13:13:00', '2026-11-16 23:13:00', 'Programado'),
(471, 'AV570', 5, 7, '2026-11-16 14:11:00', '2026-11-16 18:11:00', 'Programado'),
(472, 'AV571', 8, 2, '2026-11-16 15:23:00', '2026-11-17 01:23:00', 'Programado'),
(473, 'AV572', 2, 8, '2026-11-16 16:54:00', '2026-11-16 18:24:00', 'Programado'),
(474, 'AV573', 10, 8, '2026-11-16 19:20:00', '2026-11-16 20:50:00', 'Programado'),
(475, 'AV574', 8, 8, '2026-11-16 21:17:00', '2026-11-17 07:17:00', 'Programado'),
(476, 'AV575', 3, 5, '2026-11-16 23:08:00', '2026-11-17 02:38:00', 'Programado'),
(477, 'AV576', 1, 10, '2026-11-17 01:11:00', '2026-11-17 02:11:00', 'Programado'),
(478, 'AV577', 7, 10, '2026-11-17 02:30:00', '2026-11-17 06:00:00', 'Programado'),
(479, 'AV578', 10, 5, '2026-11-17 04:49:00', '2026-11-17 06:19:00', 'Programado'),
(480, 'AV579', 5, 10, '2026-11-17 06:23:00', '2026-11-17 10:23:00', 'Programado'),
(481, 'AV580', 7, 4, '2026-11-17 07:46:00', '2026-11-17 11:16:00', 'Programado'),
(482, 'AV581', 10, 2, '2026-11-17 09:34:00', '2026-11-17 11:04:00', 'Programado'),
(483, 'AV582', 5, 9, '2026-11-17 11:40:00', '2026-11-17 15:40:00', 'Programado'),
(484, 'AV583', 1, 5, '2026-11-17 12:59:00', '2026-11-17 13:59:00', 'Programado'),
(485, 'AV584', 8, 6, '2026-11-17 15:20:00', '2026-11-18 01:20:00', 'Programado'),
(486, 'AV585', 10, 9, '2026-11-17 17:17:00', '2026-11-17 18:47:00', 'Programado'),
(487, 'AV586', 1, 3, '2026-11-17 19:12:00', '2026-11-17 20:12:00', 'Programado'),
(488, 'AV587', 5, 10, '2026-11-17 20:14:00', '2026-11-18 00:14:00', 'Programado'),
(489, 'AV588', 4, 8, '2026-11-17 21:27:00', '2026-11-18 07:27:00', 'Programado'),
(490, 'AV589', 3, 7, '2026-11-17 22:47:00', '2026-11-18 02:17:00', 'Programado'),
(491, 'AV590', 1, 2, '2026-11-18 00:25:00', '2026-11-18 01:25:00', 'Programado'),
(492, 'AV591', 9, 5, '2026-11-18 02:44:00', '2026-11-18 04:29:00', 'Programado'),
(493, 'AV592', 7, 8, '2026-11-18 04:15:00', '2026-11-18 07:45:00', 'Programado'),
(494, 'AV593', 3, 3, '2026-11-18 05:59:00', '2026-11-18 09:29:00', 'Programado'),
(495, 'AV594', 7, 1, '2026-11-18 06:53:00', '2026-11-18 10:23:00', 'Programado'),
(496, 'AV595', 9, 4, '2026-11-18 08:18:00', '2026-11-18 10:03:00', 'Programado'),
(497, 'AV596', 10, 7, '2026-11-18 09:05:00', '2026-11-18 10:35:00', 'Programado'),
(498, 'AV597', 10, 2, '2026-11-18 11:00:00', '2026-11-18 12:30:00', 'Programado'),
(499, 'AV598', 9, 4, '2026-11-18 12:19:00', '2026-11-18 14:04:00', 'Reprogramado'),
(500, 'AV599', 8, 3, '2026-11-18 14:12:00', '2026-11-19 00:12:00', 'Programado'),
(501, 'AV600', 7, 3, '2026-11-18 15:26:00', '2026-11-18 18:56:00', 'Programado'),
(502, 'AV601', 4, 10, '2026-11-18 16:31:00', '2026-11-19 02:31:00', 'Programado'),
(503, 'AV602', 2, 10, '2026-11-18 18:34:00', '2026-11-18 20:04:00', 'Reprogramado'),
(504, 'AV603', 6, 10, '2026-11-18 19:56:00', '2026-11-19 01:56:00', 'Programado'),
(505, 'AV604', 9, 7, '2026-11-18 21:03:00', '2026-11-18 22:48:00', 'Programado'),
(506, 'AV605', 10, 1, '2026-11-18 22:14:00', '2026-11-18 23:44:00', 'Programado'),
(507, 'AV606', 1, 1, '2026-11-18 23:49:00', '2026-11-19 00:49:00', 'Programado'),
(508, 'AV607', 7, 5, '2026-11-19 01:49:00', '2026-11-19 05:19:00', 'Reprogramado'),
(509, 'AV608', 5, 6, '2026-11-19 03:37:00', '2026-11-19 07:37:00', 'Programado'),
(510, 'AV609', 9, 7, '2026-11-19 05:04:00', '2026-11-19 06:49:00', 'Programado'),
(511, 'AV610', 6, 2, '2026-11-19 07:13:00', '2026-11-19 13:13:00', 'Programado'),
(512, 'AV611', 2, 3, '2026-11-19 09:24:00', '2026-11-19 10:54:00', 'Programado'),
(513, 'AV612', 5, 4, '2026-11-19 10:24:00', '2026-11-19 14:24:00', 'Programado'),
(514, 'AV613', 8, 7, '2026-11-19 11:17:00', '2026-11-19 21:17:00', 'Programado'),
(515, 'AV614', 1, 3, '2026-11-19 12:21:00', '2026-11-19 13:21:00', 'Programado'),
(516, 'AV615', 2, 8, '2026-11-19 14:51:00', '2026-11-19 16:21:00', 'Programado'),
(517, 'AV616', 5, 6, '2026-11-19 17:00:00', '2026-11-19 21:00:00', 'Cancelado'),
(518, 'AV617', 7, 9, '2026-11-19 19:11:00', '2026-11-19 22:41:00', 'Cancelado'),
(519, 'AV618', 7, 4, '2026-11-19 21:11:00', '2026-11-20 00:41:00', 'Programado'),
(520, 'AV619', 6, 10, '2026-11-19 23:14:00', '2026-11-20 05:14:00', 'Programado'),
(521, 'AV620', 4, 5, '2026-11-20 00:51:00', '2026-11-20 10:51:00', 'Reprogramado'),
(522, 'AV621', 10, 5, '2026-11-20 01:47:00', '2026-11-20 03:17:00', 'Reprogramado'),
(523, 'AV622', 5, 10, '2026-11-20 03:09:00', '2026-11-20 07:09:00', 'Reprogramado'),
(524, 'AV623', 6, 5, '2026-11-20 05:07:00', '2026-11-20 11:07:00', 'Programado'),
(525, 'AV624', 6, 9, '2026-11-20 07:06:00', '2026-11-20 13:06:00', 'Programado'),
(526, 'AV625', 1, 3, '2026-11-20 08:51:00', '2026-11-20 09:51:00', 'Programado'),
(527, 'AV626', 3, 6, '2026-11-20 10:47:00', '2026-11-20 14:17:00', 'Programado'),
(528, 'AV627', 4, 9, '2026-11-20 13:05:00', '2026-11-20 23:05:00', 'Programado'),
(529, 'AV628', 3, 6, '2026-11-20 13:57:00', '2026-11-20 17:27:00', 'Programado'),
(530, 'AV629', 10, 2, '2026-11-20 15:32:00', '2026-11-20 17:02:00', 'Programado'),
(531, 'AV630', 4, 4, '2026-11-20 16:29:00', '2026-11-21 02:29:00', 'Programado'),
(532, 'AV631', 10, 9, '2026-11-20 18:23:00', '2026-11-20 19:53:00', 'Programado'),
(533, 'AV632', 6, 9, '2026-11-20 20:35:00', '2026-11-21 02:35:00', 'Programado'),
(534, 'AV633', 9, 8, '2026-11-20 21:42:00', '2026-11-20 23:27:00', 'Reprogramado'),
(535, 'AV634', 1, 1, '2026-11-21 00:06:00', '2026-11-21 01:06:00', 'Reprogramado'),
(536, 'AV635', 4, 2, '2026-11-21 02:12:00', '2026-11-21 12:12:00', 'Cancelado'),
(537, 'AV636', 5, 5, '2026-11-21 04:16:00', '2026-11-21 08:16:00', 'Programado'),
(538, 'AV637', 10, 2, '2026-11-21 05:27:00', '2026-11-21 06:57:00', 'Reprogramado'),
(539, 'AV638', 8, 5, '2026-11-21 06:41:00', '2026-11-21 16:41:00', 'Programado'),
(540, 'AV639', 7, 8, '2026-11-21 07:30:00', '2026-11-21 11:00:00', 'Programado'),
(541, 'AV640', 10, 2, '2026-11-21 09:08:00', '2026-11-21 10:38:00', 'Programado'),
(542, 'AV641', 2, 4, '2026-11-21 10:33:00', '2026-11-21 12:03:00', 'Programado'),
(543, 'AV642', 10, 5, '2026-11-21 12:23:00', '2026-11-21 13:53:00', 'Programado'),
(544, 'AV643', 8, 2, '2026-11-21 14:12:00', '2026-11-22 00:12:00', 'Programado'),
(545, 'AV644', 4, 6, '2026-11-21 16:31:00', '2026-11-22 02:31:00', 'Programado'),
(546, 'AV645', 9, 7, '2026-11-21 17:22:00', '2026-11-21 19:07:00', 'Reprogramado'),
(547, 'AV646', 2, 6, '2026-11-21 18:57:00', '2026-11-21 20:27:00', 'Programado'),
(548, 'AV647', 6, 6, '2026-11-21 20:48:00', '2026-11-22 02:48:00', 'Programado'),
(549, 'AV648', 1, 4, '2026-11-21 22:01:00', '2026-11-21 23:01:00', 'Programado'),
(550, 'AV649', 4, 6, '2026-11-21 22:51:00', '2026-11-22 08:51:00', 'Programado'),
(551, 'AV650', 5, 1, '2026-11-22 00:37:00', '2026-11-22 04:37:00', 'Programado'),
(552, 'AV651', 8, 7, '2026-11-22 01:29:00', '2026-11-22 11:29:00', 'Programado'),
(553, 'AV652', 9, 5, '2026-11-22 03:02:00', '2026-11-22 04:47:00', 'Programado'),
(554, 'AV653', 3, 1, '2026-11-22 04:21:00', '2026-11-22 07:51:00', 'Programado'),
(555, 'AV654', 6, 7, '2026-11-22 05:36:00', '2026-11-22 11:36:00', 'Programado'),
(556, 'AV655', 10, 7, '2026-11-22 06:35:00', '2026-11-22 08:05:00', 'Reprogramado'),
(557, 'AV656', 10, 3, '2026-11-22 08:09:00', '2026-11-22 09:39:00', 'Programado'),
(558, 'AV657', 8, 8, '2026-11-22 09:13:00', '2026-11-22 19:13:00', 'Programado'),
(559, 'AV658', 10, 9, '2026-11-22 10:47:00', '2026-11-22 12:17:00', 'Programado'),
(560, 'AV659', 10, 2, '2026-11-22 12:22:00', '2026-11-22 13:52:00', 'Reprogramado'),
(561, 'AV660', 10, 10, '2026-11-22 14:31:00', '2026-11-22 16:01:00', 'Programado'),
(562, 'AV661', 9, 9, '2026-11-22 16:27:00', '2026-11-22 18:12:00', 'Programado'),
(563, 'AV662', 9, 5, '2026-11-22 18:47:00', '2026-11-22 20:32:00', 'Programado'),
(564, 'AV663', 10, 4, '2026-11-22 19:51:00', '2026-11-22 21:21:00', 'Programado'),
(565, 'AV664', 2, 10, '2026-11-22 22:01:00', '2026-11-22 23:31:00', 'Programado'),
(566, 'AV665', 6, 9, '2026-11-23 00:21:00', '2026-11-23 06:21:00', 'Programado'),
(567, 'AV666', 6, 9, '2026-11-23 01:15:00', '2026-11-23 07:15:00', 'Programado'),
(568, 'AV667', 8, 1, '2026-11-23 03:08:00', '2026-11-23 13:08:00', 'Programado'),
(569, 'AV668', 1, 7, '2026-11-23 05:33:00', '2026-11-23 06:33:00', 'Programado'),
(570, 'AV669', 10, 7, '2026-11-23 07:52:00', '2026-11-23 09:22:00', 'Programado'),
(571, 'AV670', 8, 4, '2026-11-23 09:03:00', '2026-11-23 19:03:00', 'Programado'),
(572, 'AV671', 6, 3, '2026-11-23 11:07:00', '2026-11-23 17:07:00', 'Programado'),
(573, 'AV672', 8, 1, '2026-11-23 12:57:00', '2026-11-23 22:57:00', 'Programado'),
(574, 'AV673', 10, 3, '2026-11-23 15:14:00', '2026-11-23 16:44:00', 'Programado'),
(575, 'AV674', 8, 2, '2026-11-23 16:59:00', '2026-11-24 02:59:00', 'Reprogramado'),
(576, 'AV675', 5, 10, '2026-11-23 18:15:00', '2026-11-23 22:15:00', 'Programado'),
(577, 'AV676', 7, 5, '2026-11-23 20:44:00', '2026-11-24 00:14:00', 'Programado'),
(578, 'AV677', 2, 3, '2026-11-23 21:31:00', '2026-11-23 23:01:00', 'Programado'),
(579, 'AV678', 8, 8, '2026-11-23 22:32:00', '2026-11-24 08:32:00', 'Programado'),
(580, 'AV679', 8, 4, '2026-11-24 00:48:00', '2026-11-24 10:48:00', 'Programado'),
(581, 'AV680', 5, 8, '2026-11-24 02:08:00', '2026-11-24 06:08:00', 'Programado'),
(582, 'AV681', 2, 4, '2026-11-24 03:13:00', '2026-11-24 04:43:00', 'Reprogramado'),
(583, 'AV682', 2, 9, '2026-11-24 05:27:00', '2026-11-24 06:57:00', 'Programado'),
(584, 'AV683', 9, 2, '2026-11-24 06:47:00', '2026-11-24 08:32:00', 'Programado'),
(585, 'AV684', 10, 2, '2026-11-24 08:17:00', '2026-11-24 09:47:00', 'Programado'),
(586, 'AV685', 6, 2, '2026-11-24 09:54:00', '2026-11-24 15:54:00', 'Programado'),
(587, 'AV686', 9, 4, '2026-11-24 12:18:00', '2026-11-24 14:03:00', 'Programado'),
(588, 'AV687', 9, 6, '2026-11-24 14:36:00', '2026-11-24 16:21:00', 'Programado'),
(589, 'AV688', 2, 1, '2026-11-24 15:42:00', '2026-11-24 17:12:00', 'Programado'),
(590, 'AV689', 5, 5, '2026-11-24 16:28:00', '2026-11-24 20:28:00', 'Programado'),
(591, 'AV690', 8, 2, '2026-11-24 18:16:00', '2026-11-25 04:16:00', 'Reprogramado'),
(592, 'AV691', 3, 1, '2026-11-24 20:22:00', '2026-11-24 23:52:00', 'Cancelado'),
(593, 'AV692', 9, 3, '2026-11-24 21:57:00', '2026-11-24 23:42:00', 'Programado'),
(594, 'AV693', 10, 7, '2026-11-24 23:15:00', '2026-11-25 00:45:00', 'Programado'),
(595, 'AV694', 5, 5, '2026-11-25 00:27:00', '2026-11-25 04:27:00', 'Programado'),
(596, 'AV695', 8, 2, '2026-11-25 02:35:00', '2026-11-25 12:35:00', 'Programado'),
(597, 'AV696', 4, 9, '2026-11-25 03:42:00', '2026-11-25 13:42:00', 'Programado'),
(598, 'AV697', 10, 9, '2026-11-25 05:49:00', '2026-11-25 07:19:00', 'Programado'),
(599, 'AV698', 1, 2, '2026-11-25 07:15:00', '2026-11-25 08:15:00', 'Programado'),
(600, 'AV699', 5, 4, '2026-11-25 08:47:00', '2026-11-25 12:47:00', 'Cancelado'),
(601, 'AV700', 5, 9, '2026-11-25 10:20:00', '2026-11-25 14:20:00', 'Programado'),
(602, 'AV701', 4, 8, '2026-11-25 11:07:00', '2026-11-25 21:07:00', 'Programado'),
(603, 'AV702', 8, 5, '2026-11-25 13:36:00', '2026-11-25 23:36:00', 'Programado'),
(604, 'AV703', 5, 9, '2026-11-25 14:29:00', '2026-11-25 18:29:00', 'Programado'),
(605, 'AV704', 7, 9, '2026-11-25 15:33:00', '2026-11-25 19:03:00', 'Reprogramado'),
(606, 'AV705', 10, 9, '2026-11-25 17:40:00', '2026-11-25 19:10:00', 'Programado'),
(607, 'AV706', 9, 4, '2026-11-25 18:39:00', '2026-11-25 20:24:00', 'Programado'),
(608, 'AV707', 2, 7, '2026-11-25 20:17:00', '2026-11-25 21:47:00', 'Programado'),
(609, 'AV708', 4, 4, '2026-11-25 21:18:00', '2026-11-26 07:18:00', 'Programado'),
(610, 'AV709', 8, 7, '2026-11-25 22:49:00', '2026-11-26 08:49:00', 'Programado'),
(611, 'AV710', 6, 1, '2026-11-25 23:56:00', '2026-11-26 05:56:00', 'Programado'),
(612, 'AV711', 9, 10, '2026-11-26 02:05:00', '2026-11-26 03:50:00', 'Programado'),
(613, 'AV712', 9, 3, '2026-11-26 03:08:00', '2026-11-26 04:53:00', 'Programado'),
(614, 'AV713', 10, 10, '2026-11-26 04:39:00', '2026-11-26 06:09:00', 'Reprogramado'),
(615, 'AV714', 6, 6, '2026-11-26 06:37:00', '2026-11-26 12:37:00', 'Programado'),
(616, 'AV715', 4, 8, '2026-11-26 07:57:00', '2026-11-26 17:57:00', 'Programado'),
(617, 'AV716', 7, 9, '2026-11-26 08:55:00', '2026-11-26 12:25:00', 'Programado'),
(618, 'AV717', 6, 9, '2026-11-26 10:59:00', '2026-11-26 16:59:00', 'Programado'),
(619, 'AV718', 7, 1, '2026-11-26 11:47:00', '2026-11-26 15:17:00', 'Programado'),
(620, 'AV719', 5, 4, '2026-11-26 12:41:00', '2026-11-26 16:41:00', 'Programado'),
(621, 'AV720', 9, 5, '2026-11-26 14:39:00', '2026-11-26 16:24:00', 'Programado');
INSERT INTO `vuelo` (`id_vuelo`, `numero_vuelo`, `id_ruta`, `id_aeronave`, `fecha_hora_salida`, `fecha_hora_llegada`, `estado`) VALUES
(622, 'AV721', 5, 2, '2026-11-26 16:11:00', '2026-11-26 20:11:00', 'Programado'),
(623, 'AV722', 2, 8, '2026-11-26 17:25:00', '2026-11-26 18:55:00', 'Programado'),
(624, 'AV723', 10, 2, '2026-11-26 19:38:00', '2026-11-26 21:08:00', 'Programado'),
(625, 'AV724', 2, 1, '2026-11-26 22:00:00', '2026-11-26 23:30:00', 'Programado'),
(626, 'AV725', 10, 6, '2026-11-26 23:21:00', '2026-11-27 00:51:00', 'Programado'),
(627, 'AV726', 5, 3, '2026-11-27 00:43:00', '2026-11-27 04:43:00', 'Programado'),
(628, 'AV727', 1, 1, '2026-11-27 02:59:00', '2026-11-27 03:59:00', 'Programado'),
(629, 'AV728', 8, 1, '2026-11-27 05:29:00', '2026-11-27 15:29:00', 'Cancelado'),
(630, 'AV729', 2, 10, '2026-11-27 06:29:00', '2026-11-27 07:59:00', 'Programado'),
(631, 'AV730', 6, 1, '2026-11-27 07:54:00', '2026-11-27 13:54:00', 'Programado'),
(632, 'AV731', 10, 7, '2026-11-27 08:59:00', '2026-11-27 10:29:00', 'Programado'),
(633, 'AV732', 2, 4, '2026-11-27 10:08:00', '2026-11-27 11:38:00', 'Programado'),
(634, 'AV733', 5, 7, '2026-11-27 11:36:00', '2026-11-27 15:36:00', 'Programado'),
(635, 'AV734', 6, 9, '2026-11-27 12:49:00', '2026-11-27 18:49:00', 'Programado'),
(636, 'AV735', 10, 3, '2026-11-27 14:01:00', '2026-11-27 15:31:00', 'Programado'),
(637, 'AV736', 3, 5, '2026-11-27 15:47:00', '2026-11-27 19:17:00', 'Programado'),
(638, 'AV737', 4, 3, '2026-11-27 17:44:00', '2026-11-28 03:44:00', 'Reprogramado'),
(639, 'AV738', 8, 7, '2026-11-27 18:51:00', '2026-11-28 04:51:00', 'Programado'),
(640, 'AV739', 8, 9, '2026-11-27 19:59:00', '2026-11-28 05:59:00', 'Programado'),
(641, 'AV740', 3, 4, '2026-11-27 21:48:00', '2026-11-28 01:18:00', 'Programado'),
(642, 'AV741', 2, 5, '2026-11-27 22:51:00', '2026-11-28 00:21:00', 'Programado'),
(643, 'AV742', 6, 4, '2026-11-28 01:01:00', '2026-11-28 07:01:00', 'Programado'),
(644, 'AV743', 5, 9, '2026-11-28 03:23:00', '2026-11-28 07:23:00', 'Cancelado'),
(645, 'AV744', 10, 8, '2026-11-28 05:28:00', '2026-11-28 06:58:00', 'Programado'),
(646, 'AV745', 7, 1, '2026-11-28 07:02:00', '2026-11-28 10:32:00', 'Programado'),
(647, 'AV746', 9, 5, '2026-11-28 08:24:00', '2026-11-28 10:09:00', 'Programado'),
(648, 'AV747', 3, 2, '2026-11-28 09:50:00', '2026-11-28 13:20:00', 'Programado'),
(649, 'AV748', 5, 2, '2026-11-28 11:07:00', '2026-11-28 15:07:00', 'Programado'),
(650, 'AV749', 8, 7, '2026-11-28 12:40:00', '2026-11-28 22:40:00', 'Programado');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `vuelo_tarifa`
--

DROP TABLE IF EXISTS `vuelo_tarifa`;
CREATE TABLE `vuelo_tarifa` (
  `id_vuelo` int(11) NOT NULL,
  `id_tarifa` int(11) NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `cupos_totales` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- RELACIONES PARA LA TABLA `vuelo_tarifa`:
--   `id_vuelo`
--       `vuelo` -> `id_vuelo`
--   `id_tarifa`
--       `tarifa` -> `id_tarifa`
--

--
-- Volcado de datos para la tabla `vuelo_tarifa`
--

INSERT INTO `vuelo_tarifa` (`id_vuelo`, `id_tarifa`, `precio`, `cupos_totales`) VALUES
(1, 1, 120000.00, 50),
(1, 3, 180000.00, 50),
(1, 5, 450000.00, 12),
(2, 2, 160000.00, 60),
(2, 4, 250000.00, 40),
(3, 3, 950000.00, 100),
(3, 6, 2500000.00, 20),
(4, 3, 2100000.00, 150),
(4, 8, 6500000.00, 10),
(5, 1, 850000.00, 40),
(6, 3, 950000.00, 100),
(7, 1, 120000.00, 50),
(8, 1, 120000.00, 20),
(8, 3, 180000.00, 50),
(9, 1, 100000.00, 100),
(9, 5, 400000.00, 20),
(10, 3, 150000.00, 100),
(10, 6, 500000.00, 20),
(11, 2, 181000.00, 51),
(11, 3, 248000.00, 11),
(12, 1, 127000.00, 49),
(12, 7, 1105000.00, 17),
(13, 1, 193000.00, 27),
(13, 2, 153000.00, 23),
(13, 10, 235000.00, 14),
(14, 4, 248000.00, 44),
(14, 6, 534000.00, 44),
(14, 7, 1373000.00, 30),
(14, 9, 153000.00, 26),
(15, 5, 498000.00, 48),
(15, 7, 1168000.00, 51),
(16, 1, 110000.00, 44),
(16, 7, 1009000.00, 41),
(17, 1, 200000.00, 25),
(17, 2, 185000.00, 54),
(17, 4, 148000.00, 51),
(17, 8, 1468000.00, 23),
(18, 2, 243000.00, 44),
(18, 7, 1318000.00, 40),
(18, 10, 109000.00, 44),
(19, 6, 454000.00, 25),
(19, 7, 1314000.00, 41),
(20, 1, 106000.00, 21),
(20, 6, 325000.00, 58),
(20, 9, 146000.00, 37),
(20, 10, 114000.00, 25),
(21, 1, 138000.00, 21),
(21, 6, 420000.00, 53),
(21, 9, 216000.00, 27),
(21, 10, 226000.00, 26),
(22, 2, 165000.00, 16),
(22, 3, 236000.00, 32),
(22, 7, 877000.00, 43),
(22, 9, 165000.00, 25),
(23, 3, 123000.00, 23),
(23, 6, 358000.00, 54),
(23, 7, 1053000.00, 10),
(24, 8, 821000.00, 13),
(24, 10, 107000.00, 36),
(25, 4, 109000.00, 41),
(25, 5, 589000.00, 57),
(25, 6, 355000.00, 23),
(25, 7, 918000.00, 41),
(26, 1, 149000.00, 22),
(26, 8, 1383000.00, 28),
(26, 9, 194000.00, 30),
(27, 4, 161000.00, 31),
(27, 9, 205000.00, 53),
(28, 2, 82000.00, 50),
(28, 6, 468000.00, 21),
(28, 7, 1336000.00, 59),
(28, 8, 1485000.00, 12),
(29, 4, 130000.00, 33),
(29, 6, 371000.00, 52),
(30, 3, 173000.00, 19),
(30, 6, 358000.00, 56),
(31, 1, 217000.00, 39),
(31, 5, 308000.00, 60),
(31, 6, 547000.00, 12),
(31, 8, 1035000.00, 58),
(32, 1, 129000.00, 33),
(32, 3, 87000.00, 54),
(32, 8, 1124000.00, 56),
(32, 9, 164000.00, 29),
(33, 2, 230000.00, 54),
(33, 3, 230000.00, 38),
(33, 8, 1079000.00, 13),
(33, 9, 111000.00, 31),
(34, 3, 151000.00, 42),
(34, 4, 178000.00, 32),
(34, 8, 1308000.00, 31),
(35, 7, 1167000.00, 10),
(35, 8, 845000.00, 15),
(35, 9, 138000.00, 29),
(36, 2, 221000.00, 30),
(36, 9, 125000.00, 54),
(37, 2, 181000.00, 25),
(37, 5, 426000.00, 13),
(37, 7, 1195000.00, 22),
(38, 3, 89000.00, 42),
(38, 5, 573000.00, 26),
(39, 1, 233000.00, 14),
(39, 2, 249000.00, 59),
(39, 9, 163000.00, 22),
(40, 2, 250000.00, 36),
(40, 5, 393000.00, 37),
(40, 7, 817000.00, 29),
(40, 9, 247000.00, 14),
(41, 2, 145000.00, 54),
(41, 5, 544000.00, 17),
(42, 2, 125000.00, 29),
(42, 5, 393000.00, 10),
(42, 6, 360000.00, 39),
(43, 1, 118000.00, 43),
(43, 2, 106000.00, 31),
(43, 4, 86000.00, 15),
(43, 6, 390000.00, 33),
(44, 2, 205000.00, 37),
(44, 5, 306000.00, 54),
(44, 8, 1087000.00, 14),
(45, 3, 180000.00, 23),
(45, 4, 218000.00, 19),
(45, 5, 524000.00, 10),
(45, 7, 1424000.00, 60),
(46, 3, 116000.00, 11),
(46, 6, 361000.00, 42),
(46, 10, 174000.00, 39),
(47, 6, 338000.00, 50),
(47, 9, 137000.00, 28),
(48, 3, 148000.00, 41),
(48, 6, 361000.00, 60),
(48, 8, 1199000.00, 36),
(49, 1, 126000.00, 25),
(49, 7, 1469000.00, 33),
(50, 2, 157000.00, 25),
(50, 3, 159000.00, 24),
(50, 5, 370000.00, 57),
(50, 10, 140000.00, 15),
(51, 5, 570000.00, 12),
(51, 8, 1380000.00, 34),
(51, 9, 207000.00, 45),
(51, 10, 173000.00, 30),
(52, 2, 220000.00, 57),
(52, 4, 202000.00, 20),
(52, 10, 158000.00, 36),
(53, 7, 1484000.00, 33),
(53, 9, 182000.00, 22),
(53, 10, 245000.00, 48),
(54, 3, 86000.00, 21),
(54, 6, 599000.00, 36),
(54, 9, 231000.00, 10),
(54, 10, 221000.00, 13),
(55, 3, 128000.00, 13),
(55, 4, 245000.00, 26),
(55, 6, 376000.00, 38),
(56, 3, 116000.00, 36),
(56, 5, 464000.00, 36),
(56, 6, 516000.00, 39),
(56, 9, 243000.00, 59),
(57, 2, 198000.00, 39),
(57, 3, 199000.00, 14),
(57, 4, 250000.00, 55),
(57, 7, 920000.00, 50),
(58, 3, 126000.00, 52),
(58, 5, 423000.00, 25),
(59, 2, 121000.00, 16),
(59, 5, 520000.00, 46),
(59, 7, 1257000.00, 48),
(60, 2, 144000.00, 44),
(60, 4, 246000.00, 47),
(60, 6, 308000.00, 55),
(60, 9, 165000.00, 49),
(61, 1, 185000.00, 56),
(61, 6, 465000.00, 53),
(61, 9, 117000.00, 30),
(62, 3, 120000.00, 47),
(62, 7, 938000.00, 44),
(62, 8, 1191000.00, 27),
(62, 10, 248000.00, 44),
(63, 6, 323000.00, 46),
(63, 10, 228000.00, 10),
(64, 2, 104000.00, 34),
(64, 9, 247000.00, 40),
(65, 3, 238000.00, 12),
(65, 5, 414000.00, 32),
(65, 6, 318000.00, 59),
(65, 10, 157000.00, 42),
(66, 4, 184000.00, 15),
(66, 5, 416000.00, 44),
(66, 6, 408000.00, 21),
(66, 8, 1109000.00, 37),
(67, 4, 244000.00, 26),
(67, 5, 540000.00, 57),
(67, 8, 1108000.00, 56),
(68, 2, 154000.00, 49),
(68, 5, 526000.00, 40),
(68, 6, 445000.00, 48),
(68, 8, 890000.00, 18),
(69, 3, 199000.00, 19),
(69, 8, 1225000.00, 40),
(70, 1, 86000.00, 21),
(70, 8, 1265000.00, 59),
(70, 9, 186000.00, 18),
(71, 1, 83000.00, 17),
(71, 9, 228000.00, 35),
(72, 1, 195000.00, 20),
(72, 5, 589000.00, 59),
(72, 6, 514000.00, 15),
(72, 9, 129000.00, 59),
(73, 1, 205000.00, 48),
(73, 9, 179000.00, 43),
(74, 2, 242000.00, 39),
(74, 5, 421000.00, 12),
(74, 6, 340000.00, 16),
(74, 9, 115000.00, 32),
(75, 2, 240000.00, 39),
(75, 9, 167000.00, 11),
(76, 4, 183000.00, 10),
(76, 8, 1043000.00, 16),
(76, 9, 194000.00, 55),
(77, 2, 172000.00, 33),
(77, 6, 399000.00, 52),
(77, 7, 1315000.00, 10),
(77, 9, 133000.00, 44),
(78, 2, 206000.00, 13),
(78, 3, 224000.00, 47),
(78, 9, 91000.00, 10),
(78, 10, 109000.00, 21),
(79, 7, 836000.00, 54),
(79, 9, 228000.00, 55),
(79, 10, 145000.00, 28),
(80, 4, 219000.00, 47),
(80, 6, 511000.00, 11),
(80, 9, 152000.00, 57),
(81, 1, 221000.00, 13),
(81, 3, 139000.00, 38),
(81, 6, 482000.00, 20),
(81, 8, 1475000.00, 14),
(82, 2, 131000.00, 13),
(82, 7, 1344000.00, 38),
(83, 4, 181000.00, 14),
(83, 5, 311000.00, 47),
(83, 6, 375000.00, 56),
(84, 5, 409000.00, 55),
(84, 10, 169000.00, 21),
(85, 3, 111000.00, 13),
(85, 5, 572000.00, 52),
(85, 9, 152000.00, 18),
(86, 6, 421000.00, 48),
(86, 8, 982000.00, 23),
(86, 10, 124000.00, 38),
(87, 7, 1265000.00, 22),
(87, 9, 208000.00, 42),
(88, 4, 201000.00, 28),
(88, 5, 484000.00, 34),
(88, 8, 1009000.00, 39),
(88, 10, 189000.00, 24),
(89, 3, 155000.00, 10),
(89, 7, 1471000.00, 28),
(90, 5, 557000.00, 59),
(90, 7, 1223000.00, 50),
(91, 1, 144000.00, 13),
(91, 6, 593000.00, 57),
(92, 2, 164000.00, 10),
(92, 9, 250000.00, 60),
(93, 7, 1482000.00, 32),
(93, 9, 193000.00, 10),
(94, 1, 195000.00, 40),
(94, 6, 420000.00, 23),
(95, 3, 206000.00, 58),
(95, 4, 216000.00, 25),
(95, 7, 1081000.00, 12),
(95, 9, 92000.00, 37),
(96, 2, 164000.00, 31),
(96, 5, 445000.00, 22),
(96, 7, 1321000.00, 43),
(96, 8, 1347000.00, 56),
(97, 1, 201000.00, 56),
(97, 8, 926000.00, 32),
(98, 1, 93000.00, 33),
(98, 8, 970000.00, 57),
(98, 9, 227000.00, 44),
(99, 4, 141000.00, 33),
(99, 5, 320000.00, 38),
(99, 10, 169000.00, 15),
(100, 2, 232000.00, 10),
(100, 6, 461000.00, 18),
(101, 2, 223000.00, 22),
(101, 5, 582000.00, 24),
(102, 8, 1489000.00, 55),
(102, 9, 173000.00, 36),
(102, 10, 131000.00, 51),
(103, 7, 1277000.00, 12),
(103, 8, 1430000.00, 29),
(104, 5, 452000.00, 23),
(104, 7, 1083000.00, 41),
(104, 9, 189000.00, 13),
(104, 10, 87000.00, 54),
(105, 3, 238000.00, 36),
(105, 5, 529000.00, 41),
(105, 8, 919000.00, 26),
(105, 9, 225000.00, 44),
(106, 4, 122000.00, 36),
(106, 6, 344000.00, 23),
(106, 8, 1461000.00, 23),
(106, 9, 93000.00, 31),
(107, 1, 162000.00, 43),
(107, 2, 106000.00, 22),
(107, 10, 239000.00, 40),
(108, 5, 557000.00, 22),
(108, 9, 118000.00, 53),
(109, 3, 96000.00, 10),
(109, 7, 1098000.00, 36),
(110, 5, 516000.00, 11),
(110, 7, 1314000.00, 46),
(110, 8, 1166000.00, 43),
(110, 10, 217000.00, 34),
(111, 5, 408000.00, 14),
(111, 9, 191000.00, 25),
(112, 1, 84000.00, 57),
(112, 5, 439000.00, 50),
(112, 7, 857000.00, 26),
(113, 2, 122000.00, 11),
(113, 6, 418000.00, 55),
(113, 8, 821000.00, 54),
(114, 2, 150000.00, 46),
(114, 5, 311000.00, 58),
(114, 7, 1260000.00, 32),
(115, 5, 454000.00, 33),
(115, 7, 849000.00, 50),
(115, 8, 1450000.00, 60),
(115, 10, 230000.00, 38),
(116, 1, 224000.00, 42),
(116, 6, 412000.00, 24),
(117, 2, 226000.00, 15),
(117, 3, 172000.00, 26),
(118, 2, 127000.00, 36),
(118, 5, 468000.00, 50),
(118, 7, 951000.00, 13),
(118, 8, 1254000.00, 47),
(119, 1, 110000.00, 12),
(119, 2, 139000.00, 21),
(119, 6, 377000.00, 16),
(119, 7, 1135000.00, 54),
(120, 3, 108000.00, 21),
(120, 7, 1344000.00, 26),
(120, 9, 169000.00, 32),
(120, 10, 106000.00, 20),
(121, 5, 486000.00, 60),
(121, 10, 165000.00, 45),
(122, 3, 163000.00, 45),
(122, 4, 130000.00, 35),
(122, 5, 387000.00, 18),
(122, 9, 149000.00, 36),
(123, 2, 108000.00, 24),
(123, 3, 164000.00, 47),
(123, 4, 178000.00, 22),
(123, 8, 1440000.00, 10),
(124, 1, 161000.00, 13),
(124, 6, 463000.00, 56),
(125, 2, 135000.00, 26),
(125, 8, 1102000.00, 12),
(125, 10, 214000.00, 52),
(126, 1, 192000.00, 12),
(126, 2, 221000.00, 46),
(126, 5, 517000.00, 11),
(126, 10, 85000.00, 45),
(127, 4, 134000.00, 59),
(127, 6, 570000.00, 56),
(127, 8, 956000.00, 35),
(128, 8, 891000.00, 55),
(128, 10, 192000.00, 54),
(129, 6, 359000.00, 43),
(129, 8, 969000.00, 40),
(130, 2, 131000.00, 60),
(130, 5, 322000.00, 49),
(130, 7, 883000.00, 15),
(130, 10, 103000.00, 41),
(131, 2, 202000.00, 22),
(131, 3, 164000.00, 47),
(131, 6, 597000.00, 47),
(131, 10, 220000.00, 41),
(132, 1, 90000.00, 31),
(132, 3, 188000.00, 43),
(132, 5, 354000.00, 54),
(132, 7, 1129000.00, 10),
(133, 1, 121000.00, 49),
(133, 5, 522000.00, 29),
(133, 6, 568000.00, 50),
(133, 10, 201000.00, 53),
(134, 3, 95000.00, 43),
(134, 5, 518000.00, 41),
(134, 8, 1401000.00, 26),
(135, 3, 140000.00, 20),
(135, 5, 331000.00, 38),
(135, 8, 1202000.00, 46),
(135, 10, 176000.00, 26),
(136, 2, 248000.00, 31),
(136, 7, 1346000.00, 50),
(136, 10, 196000.00, 54),
(137, 2, 126000.00, 41),
(137, 4, 140000.00, 44),
(137, 7, 1288000.00, 31),
(137, 10, 165000.00, 55),
(138, 1, 116000.00, 37),
(138, 6, 357000.00, 41),
(138, 7, 1031000.00, 15),
(138, 8, 1300000.00, 18),
(139, 4, 232000.00, 48),
(139, 6, 345000.00, 48),
(139, 10, 101000.00, 34),
(140, 4, 89000.00, 23),
(140, 8, 1001000.00, 10),
(141, 3, 213000.00, 21),
(141, 7, 993000.00, 11),
(142, 2, 95000.00, 15),
(142, 6, 581000.00, 37),
(142, 10, 246000.00, 50),
(143, 3, 176000.00, 18),
(143, 6, 414000.00, 41),
(143, 7, 1078000.00, 10),
(143, 9, 227000.00, 24),
(144, 1, 217000.00, 49),
(144, 4, 174000.00, 19),
(144, 10, 142000.00, 57),
(145, 1, 212000.00, 16),
(145, 4, 114000.00, 12),
(145, 7, 836000.00, 26),
(146, 2, 209000.00, 51),
(146, 6, 564000.00, 46),
(147, 5, 598000.00, 11),
(147, 8, 1362000.00, 56),
(148, 1, 216000.00, 43),
(148, 4, 194000.00, 55),
(148, 5, 557000.00, 23),
(149, 1, 84000.00, 34),
(149, 5, 579000.00, 46),
(149, 7, 859000.00, 14),
(149, 9, 133000.00, 24),
(150, 4, 221000.00, 39),
(150, 5, 588000.00, 58),
(150, 7, 1453000.00, 34),
(151, 2, 130000.00, 35),
(151, 4, 135000.00, 13),
(151, 6, 488000.00, 45),
(151, 9, 127000.00, 39),
(152, 1, 91000.00, 39),
(152, 2, 123000.00, 21),
(152, 7, 1092000.00, 16),
(152, 8, 845000.00, 51),
(153, 2, 164000.00, 32),
(153, 6, 532000.00, 34),
(153, 9, 119000.00, 18),
(154, 6, 339000.00, 16),
(154, 7, 874000.00, 26),
(154, 10, 190000.00, 60),
(155, 3, 215000.00, 41),
(155, 10, 182000.00, 59),
(156, 3, 93000.00, 10),
(156, 5, 505000.00, 40),
(157, 1, 191000.00, 13),
(157, 2, 194000.00, 20),
(157, 3, 115000.00, 42),
(157, 4, 221000.00, 23),
(158, 1, 132000.00, 23),
(158, 4, 96000.00, 42),
(158, 9, 132000.00, 55),
(159, 4, 141000.00, 16),
(159, 10, 203000.00, 42),
(160, 1, 231000.00, 22),
(160, 10, 185000.00, 57),
(161, 1, 179000.00, 11),
(161, 3, 105000.00, 49),
(161, 6, 492000.00, 37),
(161, 10, 175000.00, 40),
(162, 1, 224000.00, 32),
(162, 2, 92000.00, 25),
(162, 5, 346000.00, 51),
(163, 2, 156000.00, 45),
(163, 6, 506000.00, 54),
(163, 8, 990000.00, 29),
(164, 1, 101000.00, 60),
(164, 3, 98000.00, 23),
(164, 6, 574000.00, 60),
(165, 7, 1450000.00, 26),
(165, 9, 226000.00, 15),
(165, 10, 85000.00, 44),
(166, 2, 138000.00, 31),
(166, 9, 244000.00, 18),
(167, 2, 147000.00, 40),
(167, 5, 421000.00, 12),
(167, 10, 150000.00, 23),
(168, 2, 155000.00, 47),
(168, 5, 454000.00, 19),
(168, 9, 135000.00, 30),
(168, 10, 181000.00, 38),
(169, 6, 525000.00, 18),
(169, 8, 866000.00, 52),
(170, 7, 1235000.00, 34),
(170, 8, 1166000.00, 12),
(170, 10, 214000.00, 28),
(171, 5, 383000.00, 37),
(171, 6, 382000.00, 48),
(171, 8, 825000.00, 36),
(172, 3, 244000.00, 35),
(172, 4, 174000.00, 52),
(172, 5, 502000.00, 35),
(172, 6, 339000.00, 37),
(173, 1, 190000.00, 38),
(173, 9, 119000.00, 51),
(173, 10, 180000.00, 57),
(174, 1, 139000.00, 32),
(174, 2, 108000.00, 54),
(174, 7, 1391000.00, 56),
(174, 9, 244000.00, 47),
(175, 1, 232000.00, 55),
(175, 5, 498000.00, 47),
(175, 9, 102000.00, 17),
(176, 2, 164000.00, 30),
(176, 3, 214000.00, 24),
(176, 5, 527000.00, 21),
(177, 3, 130000.00, 11),
(177, 6, 449000.00, 48),
(177, 7, 1339000.00, 42),
(178, 4, 218000.00, 48),
(178, 7, 986000.00, 59),
(178, 8, 1260000.00, 27),
(179, 1, 213000.00, 52),
(179, 5, 409000.00, 30),
(179, 6, 469000.00, 13),
(180, 3, 209000.00, 28),
(180, 5, 428000.00, 59),
(180, 10, 173000.00, 34),
(181, 1, 250000.00, 24),
(181, 2, 118000.00, 58),
(181, 3, 81000.00, 30),
(181, 7, 1215000.00, 52),
(182, 3, 188000.00, 20),
(182, 5, 378000.00, 11),
(183, 2, 223000.00, 12),
(183, 4, 140000.00, 47),
(183, 8, 1323000.00, 13),
(184, 3, 201000.00, 44),
(184, 7, 1272000.00, 48),
(184, 9, 143000.00, 48),
(185, 2, 98000.00, 52),
(185, 4, 85000.00, 24),
(185, 5, 532000.00, 38),
(185, 6, 505000.00, 40),
(186, 4, 96000.00, 26),
(186, 7, 997000.00, 56),
(186, 8, 1357000.00, 11),
(186, 9, 83000.00, 54),
(187, 2, 207000.00, 42),
(187, 4, 149000.00, 28),
(187, 6, 408000.00, 26),
(187, 9, 117000.00, 60),
(188, 2, 222000.00, 32),
(188, 7, 1226000.00, 60),
(189, 2, 106000.00, 56),
(189, 6, 488000.00, 36),
(189, 7, 930000.00, 50),
(189, 8, 965000.00, 23),
(190, 1, 201000.00, 32),
(190, 4, 205000.00, 31),
(190, 7, 1334000.00, 33),
(190, 9, 141000.00, 17),
(191, 2, 100000.00, 16),
(191, 3, 202000.00, 32),
(191, 6, 492000.00, 42),
(192, 4, 210000.00, 19),
(192, 6, 514000.00, 26),
(193, 5, 386000.00, 24),
(193, 10, 114000.00, 26),
(194, 6, 565000.00, 32),
(194, 9, 175000.00, 36),
(195, 6, 492000.00, 11),
(195, 8, 1237000.00, 14),
(195, 9, 203000.00, 53),
(196, 4, 207000.00, 23),
(196, 9, 175000.00, 34),
(197, 7, 935000.00, 17),
(197, 8, 1341000.00, 48),
(198, 3, 194000.00, 13),
(198, 5, 333000.00, 58),
(198, 6, 447000.00, 27),
(198, 7, 1007000.00, 12),
(199, 9, 207000.00, 48),
(199, 10, 239000.00, 41),
(200, 2, 99000.00, 32),
(200, 6, 437000.00, 52),
(200, 8, 1327000.00, 38),
(200, 9, 104000.00, 22),
(201, 5, 482000.00, 36),
(201, 7, 977000.00, 41),
(201, 9, 191000.00, 11),
(201, 10, 82000.00, 55),
(202, 2, 225000.00, 49),
(202, 5, 318000.00, 27),
(203, 4, 185000.00, 18),
(203, 5, 338000.00, 23),
(204, 3, 116000.00, 25),
(204, 8, 1090000.00, 47),
(205, 4, 101000.00, 10),
(205, 7, 1119000.00, 22),
(206, 1, 200000.00, 42),
(206, 9, 223000.00, 15),
(207, 3, 136000.00, 50),
(207, 7, 1034000.00, 31),
(207, 8, 1446000.00, 57),
(208, 1, 189000.00, 19),
(208, 5, 546000.00, 57),
(208, 6, 335000.00, 28),
(208, 10, 98000.00, 14),
(209, 1, 211000.00, 52),
(209, 4, 135000.00, 54),
(209, 8, 1348000.00, 50),
(210, 1, 103000.00, 56),
(210, 4, 100000.00, 56),
(210, 5, 311000.00, 49),
(210, 9, 171000.00, 14),
(211, 3, 154000.00, 27),
(211, 7, 1012000.00, 52),
(212, 1, 214000.00, 42),
(212, 4, 130000.00, 31),
(212, 9, 153000.00, 53),
(213, 2, 225000.00, 15),
(213, 4, 186000.00, 25),
(213, 8, 985000.00, 21),
(214, 6, 509000.00, 15),
(214, 8, 1173000.00, 45),
(214, 10, 246000.00, 53),
(215, 2, 193000.00, 49),
(215, 5, 457000.00, 13),
(216, 4, 145000.00, 19),
(216, 6, 318000.00, 27),
(216, 7, 1220000.00, 52),
(216, 10, 169000.00, 19),
(217, 1, 92000.00, 30),
(217, 3, 237000.00, 60),
(217, 4, 137000.00, 32),
(218, 4, 187000.00, 44),
(218, 5, 355000.00, 19),
(218, 6, 339000.00, 26),
(219, 3, 214000.00, 44),
(219, 7, 1223000.00, 22),
(220, 3, 207000.00, 22),
(220, 8, 831000.00, 45),
(220, 10, 164000.00, 34),
(221, 2, 81000.00, 51),
(221, 3, 173000.00, 45),
(221, 9, 190000.00, 48),
(222, 2, 248000.00, 49),
(222, 4, 201000.00, 14),
(223, 2, 195000.00, 23),
(223, 3, 145000.00, 50),
(223, 4, 128000.00, 20),
(223, 7, 893000.00, 19),
(224, 1, 84000.00, 59),
(224, 3, 224000.00, 28),
(224, 5, 444000.00, 33),
(224, 6, 351000.00, 33),
(225, 2, 225000.00, 57),
(225, 7, 804000.00, 41),
(226, 8, 1243000.00, 45),
(226, 9, 186000.00, 17),
(226, 10, 161000.00, 58),
(227, 5, 516000.00, 23),
(227, 7, 1150000.00, 51),
(227, 9, 179000.00, 10),
(228, 1, 157000.00, 60),
(228, 2, 222000.00, 11),
(228, 9, 90000.00, 12),
(229, 2, 179000.00, 44),
(229, 3, 201000.00, 13),
(229, 4, 198000.00, 39),
(229, 8, 1483000.00, 20),
(230, 2, 92000.00, 42),
(230, 5, 428000.00, 32),
(230, 6, 392000.00, 57),
(231, 2, 152000.00, 46),
(231, 7, 881000.00, 43),
(232, 5, 459000.00, 19),
(232, 8, 828000.00, 45),
(233, 2, 200000.00, 37),
(233, 8, 1451000.00, 27),
(233, 10, 207000.00, 12),
(234, 1, 160000.00, 51),
(234, 3, 232000.00, 18),
(234, 4, 131000.00, 10),
(235, 1, 156000.00, 10),
(235, 4, 244000.00, 14),
(235, 6, 346000.00, 17),
(235, 10, 154000.00, 53),
(236, 6, 401000.00, 22),
(236, 9, 232000.00, 57),
(237, 2, 153000.00, 32),
(237, 5, 457000.00, 46),
(238, 7, 1206000.00, 16),
(238, 9, 190000.00, 17),
(238, 10, 137000.00, 43),
(239, 4, 151000.00, 28),
(239, 6, 378000.00, 24),
(239, 8, 1095000.00, 49),
(239, 10, 149000.00, 40),
(240, 1, 97000.00, 28),
(240, 4, 103000.00, 23),
(240, 6, 400000.00, 28),
(240, 9, 195000.00, 36),
(241, 2, 101000.00, 31),
(241, 5, 332000.00, 51),
(241, 8, 1203000.00, 28),
(242, 2, 144000.00, 30),
(242, 4, 199000.00, 52),
(242, 8, 1010000.00, 40),
(243, 2, 141000.00, 51),
(243, 8, 877000.00, 33),
(244, 1, 195000.00, 27),
(244, 6, 422000.00, 43),
(245, 3, 214000.00, 52),
(245, 6, 589000.00, 45),
(246, 8, 1168000.00, 34),
(246, 9, 88000.00, 47),
(247, 2, 121000.00, 30),
(247, 6, 304000.00, 18),
(247, 9, 189000.00, 59),
(248, 1, 209000.00, 36),
(248, 6, 300000.00, 21),
(248, 7, 909000.00, 57),
(249, 1, 175000.00, 35),
(249, 6, 326000.00, 40),
(249, 10, 206000.00, 41),
(250, 1, 119000.00, 36),
(250, 5, 438000.00, 46),
(250, 8, 841000.00, 14),
(251, 1, 126000.00, 46),
(251, 3, 95000.00, 11),
(251, 8, 1099000.00, 14),
(252, 2, 232000.00, 53),
(252, 7, 975000.00, 50),
(252, 9, 158000.00, 41),
(253, 1, 216000.00, 56),
(253, 2, 166000.00, 24),
(253, 3, 237000.00, 48),
(253, 8, 1224000.00, 10),
(254, 3, 189000.00, 56),
(254, 5, 332000.00, 55),
(255, 2, 215000.00, 12),
(255, 5, 518000.00, 25),
(255, 8, 1385000.00, 15),
(256, 5, 530000.00, 17),
(256, 9, 191000.00, 14),
(257, 5, 445000.00, 40),
(257, 8, 1152000.00, 11),
(258, 1, 83000.00, 22),
(258, 2, 130000.00, 13),
(259, 4, 175000.00, 52),
(259, 5, 418000.00, 22),
(259, 9, 111000.00, 26),
(260, 7, 1037000.00, 59),
(260, 9, 248000.00, 53),
(261, 1, 211000.00, 12),
(261, 8, 924000.00, 43),
(261, 9, 145000.00, 41),
(262, 2, 146000.00, 41),
(262, 4, 244000.00, 20),
(262, 5, 389000.00, 13),
(262, 10, 215000.00, 30),
(263, 2, 217000.00, 32),
(263, 9, 189000.00, 31),
(264, 6, 524000.00, 17),
(264, 8, 1487000.00, 52),
(265, 4, 206000.00, 39),
(265, 6, 561000.00, 12),
(265, 7, 1026000.00, 31),
(265, 8, 1021000.00, 14),
(266, 1, 80000.00, 52),
(266, 2, 192000.00, 24),
(266, 9, 122000.00, 13),
(266, 10, 118000.00, 58),
(267, 1, 247000.00, 48),
(267, 9, 132000.00, 13),
(267, 10, 99000.00, 23),
(268, 2, 224000.00, 52),
(268, 3, 224000.00, 30),
(268, 5, 501000.00, 45),
(268, 10, 227000.00, 53),
(269, 1, 142000.00, 59),
(269, 3, 188000.00, 27),
(269, 5, 576000.00, 57),
(270, 2, 230000.00, 28),
(270, 7, 808000.00, 29),
(270, 10, 216000.00, 35),
(271, 2, 84000.00, 58),
(271, 3, 177000.00, 43),
(271, 6, 388000.00, 47),
(271, 9, 185000.00, 36),
(272, 1, 123000.00, 45),
(272, 10, 232000.00, 30),
(273, 2, 147000.00, 34),
(273, 6, 398000.00, 25),
(273, 7, 1242000.00, 52),
(274, 1, 80000.00, 51),
(274, 4, 157000.00, 40),
(274, 5, 427000.00, 50),
(274, 10, 224000.00, 56),
(275, 9, 209000.00, 41),
(275, 10, 153000.00, 47),
(276, 4, 175000.00, 48),
(276, 5, 376000.00, 54),
(276, 6, 431000.00, 56),
(276, 9, 214000.00, 55),
(277, 2, 250000.00, 39),
(277, 6, 543000.00, 54),
(277, 7, 1071000.00, 40),
(277, 9, 126000.00, 49),
(278, 3, 117000.00, 57),
(278, 4, 183000.00, 57),
(278, 10, 220000.00, 51),
(279, 1, 105000.00, 26),
(279, 2, 127000.00, 37),
(279, 6, 551000.00, 10),
(279, 10, 91000.00, 51),
(280, 4, 178000.00, 17),
(280, 5, 352000.00, 13),
(281, 5, 323000.00, 39),
(281, 8, 1458000.00, 43),
(282, 1, 231000.00, 49),
(282, 2, 141000.00, 13),
(282, 7, 985000.00, 50),
(282, 8, 1047000.00, 44),
(283, 6, 561000.00, 30),
(283, 10, 161000.00, 33),
(284, 5, 316000.00, 26),
(284, 6, 373000.00, 21),
(285, 1, 224000.00, 20),
(285, 5, 469000.00, 26),
(285, 6, 433000.00, 19),
(285, 7, 926000.00, 57),
(286, 1, 109000.00, 58),
(286, 3, 141000.00, 12),
(286, 7, 1064000.00, 26),
(287, 1, 197000.00, 38),
(287, 7, 1138000.00, 13),
(288, 2, 189000.00, 58),
(288, 6, 583000.00, 11),
(288, 7, 826000.00, 22),
(288, 10, 134000.00, 41),
(289, 4, 168000.00, 52),
(289, 9, 176000.00, 51),
(289, 10, 195000.00, 19),
(290, 8, 1106000.00, 54),
(290, 9, 249000.00, 26),
(290, 10, 80000.00, 43),
(291, 1, 190000.00, 59),
(291, 2, 140000.00, 42),
(291, 3, 163000.00, 37),
(292, 2, 86000.00, 28),
(292, 4, 209000.00, 59),
(292, 8, 1020000.00, 41),
(293, 4, 103000.00, 60),
(293, 5, 572000.00, 24),
(293, 6, 354000.00, 48),
(293, 7, 1320000.00, 42),
(294, 5, 600000.00, 50),
(294, 6, 539000.00, 22),
(294, 7, 952000.00, 39),
(295, 1, 219000.00, 21),
(295, 6, 516000.00, 35),
(295, 7, 958000.00, 14),
(296, 5, 347000.00, 30),
(296, 7, 977000.00, 31),
(296, 10, 107000.00, 32),
(297, 1, 215000.00, 14),
(297, 7, 1208000.00, 28),
(298, 2, 114000.00, 39),
(298, 9, 240000.00, 43),
(298, 10, 123000.00, 30),
(299, 3, 103000.00, 24),
(299, 4, 193000.00, 29),
(299, 9, 192000.00, 11),
(300, 2, 120000.00, 46),
(300, 4, 173000.00, 41),
(300, 6, 311000.00, 50),
(301, 2, 189000.00, 17),
(301, 3, 158000.00, 57),
(302, 2, 190000.00, 45),
(302, 9, 219000.00, 46),
(303, 1, 210000.00, 56),
(303, 4, 232000.00, 50),
(304, 1, 248000.00, 56),
(304, 7, 1191000.00, 44),
(304, 9, 192000.00, 21),
(305, 1, 128000.00, 53),
(305, 4, 122000.00, 12),
(305, 5, 402000.00, 33),
(306, 5, 407000.00, 32),
(306, 7, 1184000.00, 26),
(306, 9, 239000.00, 56),
(307, 3, 195000.00, 27),
(307, 6, 544000.00, 33),
(307, 7, 999000.00, 11),
(307, 9, 166000.00, 38),
(308, 1, 129000.00, 58),
(308, 2, 139000.00, 57),
(309, 2, 135000.00, 33),
(309, 3, 170000.00, 25),
(309, 4, 119000.00, 35),
(309, 5, 480000.00, 13),
(310, 3, 194000.00, 53),
(310, 5, 591000.00, 16),
(310, 9, 145000.00, 38),
(311, 3, 141000.00, 46),
(311, 4, 182000.00, 47),
(311, 6, 353000.00, 14),
(311, 10, 120000.00, 47),
(312, 3, 209000.00, 36),
(312, 4, 139000.00, 23),
(312, 5, 455000.00, 33),
(312, 6, 456000.00, 31),
(313, 3, 175000.00, 36),
(313, 5, 468000.00, 11),
(313, 10, 249000.00, 52),
(314, 3, 216000.00, 45),
(314, 4, 147000.00, 24),
(315, 5, 420000.00, 46),
(315, 6, 499000.00, 41),
(315, 8, 1303000.00, 43),
(316, 5, 511000.00, 50),
(316, 8, 856000.00, 24),
(316, 9, 247000.00, 52),
(317, 2, 225000.00, 21),
(317, 5, 412000.00, 52),
(317, 6, 431000.00, 60),
(317, 10, 209000.00, 12),
(318, 5, 313000.00, 30),
(318, 8, 1025000.00, 51),
(319, 2, 158000.00, 44),
(319, 3, 154000.00, 57),
(319, 4, 97000.00, 54),
(319, 7, 1175000.00, 35),
(320, 5, 401000.00, 35),
(320, 9, 232000.00, 33),
(321, 1, 129000.00, 45),
(321, 6, 469000.00, 31),
(321, 8, 1226000.00, 50),
(322, 5, 540000.00, 57),
(322, 6, 310000.00, 10),
(323, 1, 235000.00, 49),
(323, 2, 194000.00, 26),
(323, 9, 125000.00, 37),
(324, 1, 105000.00, 54),
(324, 8, 1497000.00, 56),
(324, 9, 86000.00, 55),
(325, 7, 956000.00, 56),
(325, 8, 896000.00, 43),
(326, 1, 145000.00, 39),
(326, 4, 95000.00, 58),
(326, 8, 917000.00, 17),
(326, 10, 111000.00, 46),
(327, 3, 244000.00, 37),
(327, 8, 985000.00, 18),
(327, 10, 96000.00, 16),
(328, 4, 190000.00, 14),
(328, 7, 1026000.00, 22),
(328, 9, 177000.00, 44),
(328, 10, 202000.00, 50),
(329, 6, 574000.00, 56),
(329, 7, 803000.00, 51),
(329, 8, 844000.00, 28),
(330, 3, 203000.00, 48),
(330, 4, 89000.00, 16),
(330, 7, 1044000.00, 43),
(330, 10, 154000.00, 30),
(331, 2, 220000.00, 40),
(331, 7, 1350000.00, 39),
(332, 1, 235000.00, 26),
(332, 4, 129000.00, 38),
(332, 5, 300000.00, 52),
(332, 10, 237000.00, 11),
(333, 1, 159000.00, 57),
(333, 2, 201000.00, 21),
(333, 4, 108000.00, 31),
(334, 3, 211000.00, 53),
(334, 4, 234000.00, 14),
(334, 8, 924000.00, 47),
(334, 9, 216000.00, 27),
(335, 2, 132000.00, 26),
(335, 7, 1193000.00, 21),
(336, 1, 183000.00, 23),
(336, 3, 164000.00, 39),
(336, 4, 145000.00, 26),
(337, 5, 506000.00, 54),
(337, 10, 183000.00, 21),
(338, 2, 209000.00, 41),
(338, 6, 584000.00, 15),
(338, 9, 143000.00, 48),
(339, 2, 118000.00, 47),
(339, 6, 441000.00, 42),
(339, 7, 1046000.00, 51),
(340, 4, 239000.00, 12),
(340, 5, 373000.00, 48),
(340, 7, 1289000.00, 59),
(340, 9, 200000.00, 26),
(341, 1, 227000.00, 31),
(341, 2, 202000.00, 15),
(342, 2, 141000.00, 27),
(342, 9, 160000.00, 43),
(343, 1, 128000.00, 25),
(343, 2, 144000.00, 14),
(343, 7, 1400000.00, 31),
(343, 9, 230000.00, 42),
(344, 1, 148000.00, 52),
(344, 5, 562000.00, 28),
(345, 1, 108000.00, 41),
(345, 3, 176000.00, 20),
(345, 7, 1463000.00, 50),
(345, 10, 225000.00, 20),
(346, 3, 124000.00, 12),
(346, 7, 1041000.00, 35),
(346, 10, 126000.00, 19),
(347, 1, 187000.00, 31),
(347, 3, 241000.00, 55),
(347, 6, 589000.00, 27),
(347, 10, 200000.00, 30),
(348, 1, 123000.00, 13),
(348, 6, 420000.00, 21),
(348, 9, 98000.00, 17),
(348, 10, 85000.00, 43),
(349, 2, 88000.00, 26),
(349, 6, 316000.00, 49),
(349, 7, 1117000.00, 41),
(349, 8, 819000.00, 47),
(350, 7, 1143000.00, 59),
(350, 8, 1201000.00, 53),
(350, 10, 80000.00, 10),
(351, 1, 117000.00, 57),
(351, 6, 330000.00, 26),
(352, 1, 146000.00, 55),
(352, 5, 585000.00, 42),
(352, 6, 319000.00, 31),
(352, 10, 207000.00, 31),
(353, 7, 1037000.00, 49),
(353, 9, 86000.00, 11),
(354, 6, 496000.00, 36),
(354, 8, 1242000.00, 37),
(354, 10, 232000.00, 52),
(355, 4, 158000.00, 23),
(355, 8, 900000.00, 57),
(356, 4, 91000.00, 45),
(356, 6, 589000.00, 46),
(357, 2, 226000.00, 51),
(357, 7, 883000.00, 27),
(357, 10, 167000.00, 41),
(358, 3, 107000.00, 36),
(358, 4, 114000.00, 37),
(358, 9, 219000.00, 27),
(359, 2, 84000.00, 34),
(359, 3, 139000.00, 42),
(359, 10, 131000.00, 46),
(360, 1, 202000.00, 11),
(360, 2, 231000.00, 18),
(360, 6, 577000.00, 27),
(360, 10, 174000.00, 36),
(361, 1, 185000.00, 17),
(361, 7, 1121000.00, 42),
(361, 8, 844000.00, 58),
(361, 10, 182000.00, 44),
(362, 5, 594000.00, 47),
(362, 7, 1352000.00, 16),
(362, 9, 198000.00, 21),
(362, 10, 107000.00, 47),
(363, 2, 151000.00, 34),
(363, 6, 544000.00, 40),
(364, 3, 138000.00, 22),
(364, 9, 134000.00, 28),
(364, 10, 182000.00, 36),
(365, 2, 151000.00, 18),
(365, 8, 1168000.00, 46),
(366, 2, 93000.00, 50),
(366, 4, 117000.00, 33),
(366, 8, 1205000.00, 60),
(367, 6, 543000.00, 12),
(367, 8, 941000.00, 48),
(368, 5, 599000.00, 35),
(368, 9, 106000.00, 28),
(369, 3, 227000.00, 18),
(369, 7, 866000.00, 19),
(369, 8, 1109000.00, 14),
(369, 10, 82000.00, 43),
(370, 1, 111000.00, 41),
(370, 7, 1167000.00, 43),
(370, 10, 166000.00, 20),
(371, 1, 165000.00, 19),
(371, 4, 184000.00, 56),
(372, 9, 143000.00, 48),
(372, 10, 235000.00, 41),
(373, 2, 105000.00, 48),
(373, 5, 341000.00, 24),
(373, 9, 117000.00, 53),
(374, 7, 1484000.00, 34),
(374, 9, 91000.00, 60),
(375, 9, 228000.00, 57),
(375, 10, 102000.00, 18),
(376, 5, 390000.00, 38),
(376, 7, 1141000.00, 57),
(376, 8, 1007000.00, 19),
(377, 3, 95000.00, 26),
(377, 6, 473000.00, 29),
(377, 7, 1037000.00, 60),
(377, 8, 1004000.00, 50),
(378, 2, 92000.00, 52),
(378, 6, 342000.00, 12),
(378, 7, 968000.00, 48),
(379, 5, 519000.00, 21),
(379, 8, 836000.00, 55),
(379, 9, 200000.00, 47),
(379, 10, 120000.00, 28),
(380, 1, 111000.00, 27),
(380, 6, 463000.00, 53),
(380, 8, 872000.00, 19),
(381, 5, 501000.00, 58),
(381, 10, 159000.00, 15),
(382, 2, 130000.00, 48),
(382, 5, 346000.00, 13),
(383, 1, 176000.00, 36),
(383, 3, 237000.00, 14),
(383, 7, 1442000.00, 41),
(383, 8, 1474000.00, 12),
(384, 4, 211000.00, 22),
(384, 9, 110000.00, 49),
(385, 2, 140000.00, 35),
(385, 8, 881000.00, 12),
(385, 9, 152000.00, 17),
(386, 7, 1457000.00, 26),
(386, 10, 185000.00, 31),
(387, 2, 203000.00, 55),
(387, 5, 330000.00, 46),
(387, 9, 100000.00, 29),
(387, 10, 194000.00, 28),
(388, 2, 181000.00, 39),
(388, 5, 546000.00, 35),
(388, 6, 501000.00, 49),
(389, 3, 174000.00, 55),
(389, 4, 128000.00, 49),
(389, 5, 325000.00, 21),
(389, 9, 164000.00, 31),
(390, 1, 131000.00, 19),
(390, 4, 92000.00, 30),
(390, 8, 1158000.00, 21),
(391, 4, 134000.00, 26),
(391, 5, 310000.00, 21),
(391, 9, 218000.00, 32),
(391, 10, 133000.00, 17),
(392, 1, 80000.00, 43),
(392, 7, 946000.00, 49),
(392, 8, 1400000.00, 49),
(393, 1, 186000.00, 19),
(393, 8, 1175000.00, 56),
(393, 10, 127000.00, 16),
(394, 5, 485000.00, 36),
(394, 7, 1312000.00, 26),
(395, 4, 102000.00, 29),
(395, 6, 532000.00, 32),
(395, 10, 215000.00, 48),
(396, 1, 168000.00, 39),
(396, 2, 179000.00, 14),
(396, 7, 1141000.00, 16),
(396, 10, 157000.00, 27),
(397, 5, 336000.00, 29),
(397, 8, 1071000.00, 47),
(397, 9, 170000.00, 49),
(398, 2, 143000.00, 40),
(398, 3, 135000.00, 44),
(398, 10, 214000.00, 21),
(399, 2, 210000.00, 14),
(399, 6, 424000.00, 37),
(399, 7, 1341000.00, 56),
(400, 1, 90000.00, 15),
(400, 4, 179000.00, 37),
(400, 5, 402000.00, 46),
(400, 8, 1181000.00, 22),
(401, 2, 173000.00, 53),
(401, 5, 539000.00, 14),
(401, 6, 556000.00, 30),
(402, 4, 93000.00, 26),
(402, 6, 414000.00, 25),
(402, 9, 219000.00, 27),
(403, 2, 232000.00, 35),
(403, 4, 183000.00, 10),
(403, 9, 246000.00, 51),
(403, 10, 106000.00, 20),
(404, 2, 243000.00, 11),
(404, 6, 395000.00, 53),
(404, 8, 1083000.00, 12),
(404, 10, 97000.00, 18),
(405, 1, 194000.00, 12),
(405, 2, 89000.00, 41),
(405, 7, 1218000.00, 21),
(405, 9, 233000.00, 20),
(406, 2, 192000.00, 35),
(406, 3, 91000.00, 45),
(406, 5, 470000.00, 47),
(406, 7, 1193000.00, 53),
(407, 3, 87000.00, 24),
(407, 4, 230000.00, 30),
(407, 9, 219000.00, 51),
(407, 10, 196000.00, 16),
(408, 4, 245000.00, 35),
(408, 5, 341000.00, 16),
(408, 9, 149000.00, 19),
(409, 4, 172000.00, 48),
(409, 8, 818000.00, 42),
(409, 10, 90000.00, 57),
(410, 4, 123000.00, 11),
(410, 7, 918000.00, 50),
(410, 8, 1199000.00, 38),
(410, 10, 216000.00, 10),
(411, 4, 165000.00, 49),
(411, 7, 1309000.00, 42),
(411, 8, 1383000.00, 36),
(412, 2, 242000.00, 22),
(412, 3, 242000.00, 43),
(412, 5, 591000.00, 47),
(412, 10, 206000.00, 33),
(413, 3, 171000.00, 45),
(413, 7, 1134000.00, 49),
(413, 8, 1075000.00, 10),
(413, 10, 136000.00, 42),
(414, 6, 468000.00, 36),
(414, 7, 1187000.00, 51),
(415, 1, 173000.00, 20),
(415, 2, 173000.00, 42),
(415, 6, 571000.00, 18),
(415, 9, 227000.00, 12),
(416, 1, 249000.00, 35),
(416, 10, 198000.00, 56),
(417, 4, 96000.00, 33),
(417, 6, 489000.00, 14),
(417, 7, 1360000.00, 21),
(418, 3, 196000.00, 15),
(418, 6, 472000.00, 46),
(418, 7, 952000.00, 53),
(418, 9, 173000.00, 14),
(419, 3, 140000.00, 42),
(419, 4, 209000.00, 10),
(419, 5, 422000.00, 51),
(419, 6, 538000.00, 42),
(420, 2, 107000.00, 11),
(420, 3, 158000.00, 59),
(420, 10, 89000.00, 57),
(421, 2, 250000.00, 32),
(421, 5, 512000.00, 41),
(421, 7, 1053000.00, 35),
(422, 1, 137000.00, 58),
(422, 5, 304000.00, 11),
(423, 3, 144000.00, 44),
(423, 4, 93000.00, 23),
(423, 6, 442000.00, 17),
(424, 4, 188000.00, 24),
(424, 7, 993000.00, 36),
(424, 8, 1072000.00, 38),
(425, 1, 90000.00, 22),
(425, 2, 230000.00, 58),
(425, 7, 1181000.00, 29),
(425, 9, 226000.00, 22),
(426, 2, 89000.00, 29),
(426, 5, 371000.00, 34),
(426, 6, 327000.00, 33),
(426, 8, 1193000.00, 57),
(427, 2, 183000.00, 17),
(427, 4, 206000.00, 13),
(428, 2, 135000.00, 37),
(428, 5, 348000.00, 48),
(428, 6, 554000.00, 47),
(428, 9, 244000.00, 40),
(429, 4, 115000.00, 22),
(429, 6, 340000.00, 31),
(429, 7, 1116000.00, 11),
(429, 10, 160000.00, 54),
(430, 1, 176000.00, 38),
(430, 5, 515000.00, 33),
(430, 8, 1055000.00, 41),
(431, 1, 167000.00, 34),
(431, 4, 141000.00, 27),
(431, 7, 985000.00, 55),
(431, 9, 148000.00, 16),
(432, 7, 1075000.00, 41),
(432, 9, 151000.00, 36),
(433, 7, 1294000.00, 26),
(433, 8, 1407000.00, 21),
(434, 1, 245000.00, 56),
(434, 4, 121000.00, 17),
(434, 5, 362000.00, 40),
(434, 7, 1393000.00, 19),
(435, 3, 125000.00, 34),
(435, 9, 102000.00, 33),
(436, 2, 123000.00, 57),
(436, 10, 187000.00, 60),
(437, 1, 213000.00, 21),
(437, 4, 194000.00, 41),
(437, 5, 355000.00, 39),
(437, 10, 166000.00, 47),
(438, 1, 191000.00, 25),
(438, 4, 127000.00, 12),
(438, 5, 309000.00, 12),
(438, 9, 144000.00, 14),
(439, 1, 173000.00, 17),
(439, 8, 1349000.00, 53),
(440, 6, 395000.00, 45),
(440, 8, 1056000.00, 24),
(440, 10, 92000.00, 10),
(441, 1, 142000.00, 32),
(441, 2, 151000.00, 54),
(441, 9, 144000.00, 16),
(441, 10, 144000.00, 53),
(442, 1, 103000.00, 11),
(442, 5, 327000.00, 13),
(442, 6, 393000.00, 44),
(443, 5, 327000.00, 31),
(443, 6, 385000.00, 10),
(443, 10, 116000.00, 46),
(444, 3, 168000.00, 20),
(444, 4, 122000.00, 34),
(445, 1, 152000.00, 49),
(445, 3, 101000.00, 19),
(445, 6, 406000.00, 14),
(445, 10, 167000.00, 33),
(446, 1, 214000.00, 55),
(446, 9, 123000.00, 15),
(447, 4, 98000.00, 41),
(447, 7, 897000.00, 33),
(447, 8, 954000.00, 21),
(447, 9, 204000.00, 30),
(448, 1, 156000.00, 10),
(448, 6, 324000.00, 58),
(448, 10, 101000.00, 34),
(449, 3, 115000.00, 48),
(449, 4, 142000.00, 13),
(449, 6, 379000.00, 24),
(449, 7, 1393000.00, 27),
(450, 1, 170000.00, 47),
(450, 10, 84000.00, 51),
(451, 2, 180000.00, 49),
(451, 3, 89000.00, 51),
(451, 9, 193000.00, 36),
(452, 1, 130000.00, 38),
(452, 2, 228000.00, 58),
(452, 7, 1384000.00, 32),
(452, 10, 123000.00, 44),
(453, 8, 1272000.00, 17),
(453, 10, 105000.00, 24),
(454, 2, 100000.00, 24),
(454, 3, 141000.00, 18),
(454, 6, 546000.00, 26),
(455, 4, 241000.00, 42),
(455, 7, 1106000.00, 49),
(456, 4, 240000.00, 14),
(456, 5, 545000.00, 35),
(456, 8, 899000.00, 29),
(456, 9, 99000.00, 49),
(457, 5, 333000.00, 30),
(457, 7, 1256000.00, 16),
(457, 8, 1190000.00, 56),
(457, 10, 187000.00, 26),
(458, 3, 166000.00, 60),
(458, 9, 198000.00, 47),
(458, 10, 111000.00, 11),
(459, 4, 122000.00, 29),
(459, 6, 524000.00, 24),
(459, 8, 838000.00, 17),
(460, 1, 199000.00, 12),
(460, 5, 550000.00, 48),
(460, 6, 387000.00, 29),
(460, 8, 1352000.00, 14),
(461, 1, 122000.00, 37),
(461, 2, 165000.00, 43),
(461, 4, 214000.00, 52),
(461, 7, 1036000.00, 11),
(462, 4, 144000.00, 19),
(462, 10, 174000.00, 51),
(463, 2, 145000.00, 21),
(463, 6, 435000.00, 26),
(463, 8, 1422000.00, 47),
(464, 1, 179000.00, 39),
(464, 5, 407000.00, 33),
(464, 8, 1025000.00, 49),
(465, 1, 99000.00, 34),
(465, 4, 235000.00, 25),
(465, 9, 223000.00, 39),
(466, 3, 86000.00, 40),
(466, 6, 441000.00, 11),
(466, 7, 1476000.00, 58),
(467, 6, 570000.00, 41),
(467, 10, 135000.00, 44),
(468, 1, 83000.00, 40),
(468, 3, 89000.00, 14),
(468, 7, 1386000.00, 26),
(468, 10, 192000.00, 52),
(469, 2, 170000.00, 41),
(469, 4, 98000.00, 32),
(469, 6, 533000.00, 44),
(469, 9, 95000.00, 47),
(470, 1, 248000.00, 29),
(470, 4, 203000.00, 26),
(470, 6, 337000.00, 20),
(470, 9, 247000.00, 51),
(471, 6, 374000.00, 51),
(471, 10, 184000.00, 55),
(472, 1, 146000.00, 14),
(472, 4, 124000.00, 51),
(472, 8, 884000.00, 58),
(473, 2, 217000.00, 26),
(473, 7, 1029000.00, 46),
(473, 10, 103000.00, 20),
(474, 1, 159000.00, 21),
(474, 2, 164000.00, 35),
(474, 4, 107000.00, 44),
(474, 8, 961000.00, 42),
(475, 3, 181000.00, 39),
(475, 7, 1473000.00, 45),
(475, 10, 95000.00, 60),
(476, 2, 214000.00, 44),
(476, 9, 242000.00, 15),
(477, 4, 196000.00, 57),
(477, 6, 310000.00, 52),
(477, 7, 1472000.00, 29),
(478, 4, 175000.00, 25),
(478, 5, 384000.00, 37),
(479, 3, 81000.00, 28),
(479, 4, 136000.00, 15),
(479, 5, 375000.00, 41),
(479, 10, 87000.00, 14),
(480, 2, 177000.00, 13),
(480, 6, 562000.00, 56),
(481, 1, 228000.00, 37),
(481, 6, 542000.00, 32),
(481, 8, 1341000.00, 19),
(482, 1, 124000.00, 23),
(482, 3, 120000.00, 33),
(482, 4, 236000.00, 10),
(482, 5, 394000.00, 54),
(483, 3, 109000.00, 59),
(483, 6, 517000.00, 28),
(484, 4, 171000.00, 47),
(484, 6, 435000.00, 29),
(484, 7, 1262000.00, 14),
(484, 8, 925000.00, 58),
(485, 4, 139000.00, 36),
(485, 6, 522000.00, 17),
(485, 8, 1032000.00, 39),
(485, 9, 122000.00, 49),
(486, 2, 136000.00, 42),
(486, 8, 1020000.00, 20),
(487, 3, 111000.00, 29),
(487, 6, 472000.00, 43),
(487, 7, 1412000.00, 55),
(487, 8, 986000.00, 17),
(488, 1, 237000.00, 43),
(488, 2, 228000.00, 40),
(488, 6, 502000.00, 14),
(488, 8, 846000.00, 10),
(489, 1, 112000.00, 28),
(489, 5, 436000.00, 12),
(489, 9, 114000.00, 40),
(490, 6, 531000.00, 56),
(490, 8, 1019000.00, 52),
(490, 9, 182000.00, 12),
(490, 10, 225000.00, 20),
(491, 5, 424000.00, 13),
(491, 6, 379000.00, 50),
(491, 8, 921000.00, 10),
(491, 9, 169000.00, 47),
(492, 3, 209000.00, 13),
(492, 5, 417000.00, 41),
(492, 7, 850000.00, 33),
(492, 9, 228000.00, 50),
(493, 1, 167000.00, 40),
(493, 6, 580000.00, 12),
(493, 10, 88000.00, 48),
(494, 2, 240000.00, 56),
(494, 3, 160000.00, 49),
(494, 6, 423000.00, 16),
(494, 9, 229000.00, 43),
(495, 3, 91000.00, 34),
(495, 4, 221000.00, 43),
(495, 5, 333000.00, 55),
(495, 7, 1403000.00, 57),
(496, 3, 200000.00, 53),
(496, 5, 331000.00, 45),
(497, 4, 184000.00, 26),
(497, 6, 506000.00, 15),
(497, 7, 1422000.00, 56),
(498, 2, 198000.00, 39),
(498, 3, 204000.00, 48),
(498, 9, 90000.00, 44),
(499, 4, 207000.00, 27),
(499, 5, 541000.00, 24),
(499, 8, 971000.00, 34),
(499, 9, 126000.00, 46),
(500, 5, 358000.00, 18),
(500, 6, 582000.00, 50),
(501, 2, 128000.00, 38),
(501, 8, 1441000.00, 19),
(502, 2, 125000.00, 49),
(502, 6, 533000.00, 16),
(502, 9, 101000.00, 16),
(503, 1, 117000.00, 18),
(503, 5, 323000.00, 60),
(503, 6, 364000.00, 55),
(503, 9, 240000.00, 25),
(504, 3, 180000.00, 32),
(504, 4, 167000.00, 25),
(505, 4, 143000.00, 24),
(505, 6, 369000.00, 18),
(505, 8, 1487000.00, 12),
(505, 10, 116000.00, 24),
(506, 2, 194000.00, 21),
(506, 7, 1137000.00, 32),
(506, 9, 106000.00, 11),
(507, 3, 220000.00, 23),
(507, 6, 311000.00, 51),
(507, 7, 1168000.00, 32),
(507, 8, 928000.00, 39),
(508, 7, 1171000.00, 57),
(508, 9, 190000.00, 40),
(509, 1, 175000.00, 36),
(509, 5, 353000.00, 28),
(509, 8, 1235000.00, 21),
(510, 2, 133000.00, 60),
(510, 3, 124000.00, 51),
(510, 4, 94000.00, 40),
(511, 1, 92000.00, 19),
(511, 4, 103000.00, 37),
(511, 7, 1418000.00, 32),
(512, 5, 424000.00, 51),
(512, 6, 492000.00, 21),
(512, 10, 210000.00, 57),
(513, 5, 438000.00, 12),
(513, 6, 344000.00, 24),
(513, 7, 1043000.00, 11),
(514, 2, 81000.00, 47),
(514, 3, 85000.00, 40),
(514, 8, 1161000.00, 57),
(514, 9, 98000.00, 43),
(515, 2, 191000.00, 50),
(515, 3, 184000.00, 19),
(515, 8, 1317000.00, 13),
(516, 1, 248000.00, 54),
(516, 4, 89000.00, 52),
(516, 9, 242000.00, 51),
(516, 10, 132000.00, 52),
(517, 2, 192000.00, 36),
(517, 4, 226000.00, 34),
(517, 5, 544000.00, 59),
(518, 2, 210000.00, 49),
(518, 5, 597000.00, 21),
(518, 9, 202000.00, 34),
(518, 10, 190000.00, 12),
(519, 3, 212000.00, 38),
(519, 7, 1346000.00, 13),
(519, 8, 1260000.00, 29),
(520, 1, 192000.00, 24),
(520, 7, 1421000.00, 11),
(520, 8, 1455000.00, 41),
(521, 1, 158000.00, 60),
(521, 2, 89000.00, 12),
(522, 6, 309000.00, 56),
(522, 7, 937000.00, 32),
(522, 8, 1472000.00, 24),
(523, 1, 105000.00, 35),
(523, 4, 245000.00, 28),
(523, 9, 116000.00, 29),
(523, 10, 100000.00, 27),
(524, 2, 126000.00, 47),
(524, 5, 320000.00, 44),
(524, 8, 1436000.00, 42),
(525, 4, 112000.00, 20),
(525, 6, 501000.00, 13),
(525, 9, 121000.00, 11),
(526, 1, 126000.00, 57),
(526, 2, 127000.00, 16),
(526, 6, 405000.00, 60),
(527, 6, 440000.00, 20),
(527, 7, 961000.00, 32),
(528, 4, 152000.00, 21),
(528, 7, 1457000.00, 26),
(528, 8, 1001000.00, 18),
(529, 1, 89000.00, 45),
(529, 6, 456000.00, 17),
(530, 2, 194000.00, 22),
(530, 4, 174000.00, 13),
(530, 5, 482000.00, 49),
(531, 3, 161000.00, 24),
(531, 5, 412000.00, 34),
(531, 8, 1471000.00, 14),
(532, 6, 475000.00, 32),
(532, 9, 169000.00, 52),
(533, 5, 424000.00, 25),
(533, 6, 541000.00, 31),
(533, 7, 964000.00, 40),
(533, 9, 121000.00, 30),
(534, 4, 216000.00, 10),
(534, 7, 1052000.00, 21),
(534, 8, 1094000.00, 36),
(534, 10, 98000.00, 60),
(535, 8, 819000.00, 60),
(535, 9, 223000.00, 16),
(536, 4, 217000.00, 44),
(536, 6, 482000.00, 10),
(536, 7, 1329000.00, 60),
(536, 9, 148000.00, 19),
(537, 2, 175000.00, 43),
(537, 4, 93000.00, 37),
(537, 6, 415000.00, 31),
(537, 8, 1317000.00, 17),
(538, 2, 99000.00, 52),
(538, 4, 174000.00, 56),
(538, 6, 515000.00, 37),
(539, 2, 180000.00, 44),
(539, 5, 520000.00, 35),
(540, 2, 98000.00, 20),
(540, 5, 452000.00, 34),
(540, 9, 195000.00, 45),
(541, 2, 86000.00, 43),
(541, 9, 105000.00, 60),
(542, 4, 106000.00, 51),
(542, 6, 370000.00, 27),
(542, 9, 126000.00, 22),
(542, 10, 199000.00, 52),
(543, 3, 121000.00, 33),
(543, 5, 483000.00, 22),
(543, 6, 483000.00, 48),
(543, 7, 1286000.00, 26),
(544, 2, 96000.00, 27),
(544, 4, 103000.00, 56),
(545, 1, 111000.00, 17),
(545, 2, 122000.00, 53),
(545, 5, 515000.00, 34),
(545, 6, 478000.00, 27),
(546, 1, 154000.00, 58),
(546, 4, 106000.00, 35),
(546, 6, 593000.00, 36),
(547, 4, 104000.00, 57),
(547, 6, 332000.00, 44),
(547, 7, 942000.00, 45),
(548, 1, 161000.00, 35),
(548, 3, 243000.00, 35),
(548, 7, 1312000.00, 31),
(549, 5, 322000.00, 10),
(549, 6, 376000.00, 59),
(549, 7, 967000.00, 17),
(550, 1, 208000.00, 43),
(550, 3, 164000.00, 55),
(550, 10, 209000.00, 56),
(551, 5, 386000.00, 49),
(551, 8, 950000.00, 53),
(551, 9, 106000.00, 53),
(552, 6, 311000.00, 30),
(552, 8, 999000.00, 46),
(552, 9, 111000.00, 14),
(553, 2, 213000.00, 60),
(553, 7, 983000.00, 28),
(553, 8, 1459000.00, 30),
(554, 3, 224000.00, 14),
(554, 7, 1257000.00, 30),
(554, 9, 129000.00, 60),
(554, 10, 243000.00, 52),
(555, 2, 230000.00, 13),
(555, 4, 155000.00, 18),
(555, 9, 245000.00, 33),
(555, 10, 164000.00, 40),
(556, 4, 246000.00, 27),
(556, 6, 303000.00, 57),
(556, 9, 120000.00, 27),
(556, 10, 144000.00, 43),
(557, 2, 219000.00, 49),
(557, 8, 1364000.00, 39),
(557, 9, 134000.00, 39),
(557, 10, 153000.00, 41),
(558, 2, 233000.00, 58),
(558, 4, 146000.00, 14),
(559, 3, 179000.00, 31),
(559, 4, 170000.00, 25),
(559, 8, 1082000.00, 20),
(560, 2, 224000.00, 36),
(560, 6, 436000.00, 14),
(560, 7, 1475000.00, 37),
(561, 3, 226000.00, 26),
(561, 6, 543000.00, 15),
(561, 8, 1169000.00, 12),
(562, 1, 113000.00, 12),
(562, 6, 478000.00, 24),
(562, 7, 922000.00, 26),
(562, 8, 1107000.00, 60),
(563, 1, 220000.00, 46),
(563, 7, 931000.00, 32),
(564, 2, 227000.00, 10),
(564, 8, 1500000.00, 53),
(564, 9, 120000.00, 38),
(564, 10, 151000.00, 40),
(565, 3, 150000.00, 46),
(565, 6, 529000.00, 22),
(565, 7, 1104000.00, 42),
(566, 1, 170000.00, 22),
(566, 10, 117000.00, 43),
(567, 2, 131000.00, 24),
(567, 6, 492000.00, 25),
(567, 8, 1431000.00, 40),
(568, 4, 167000.00, 16),
(568, 6, 451000.00, 28),
(568, 7, 1434000.00, 40),
(568, 9, 232000.00, 50),
(569, 1, 108000.00, 39),
(569, 8, 881000.00, 37),
(570, 2, 83000.00, 51),
(570, 3, 170000.00, 12),
(570, 5, 560000.00, 36),
(570, 9, 81000.00, 54),
(571, 2, 106000.00, 39),
(571, 3, 243000.00, 26),
(571, 6, 371000.00, 24),
(571, 7, 1093000.00, 27),
(572, 4, 191000.00, 43),
(572, 8, 1048000.00, 34),
(572, 9, 225000.00, 11),
(573, 6, 382000.00, 40),
(573, 7, 1075000.00, 34),
(574, 1, 234000.00, 41),
(574, 3, 178000.00, 14),
(574, 4, 241000.00, 27),
(574, 7, 1141000.00, 22),
(575, 1, 120000.00, 33),
(575, 7, 1295000.00, 32),
(575, 9, 102000.00, 16),
(576, 1, 137000.00, 27),
(576, 2, 183000.00, 17),
(577, 2, 158000.00, 22),
(577, 5, 439000.00, 15),
(577, 6, 456000.00, 56),
(577, 9, 193000.00, 21),
(578, 3, 92000.00, 27),
(578, 7, 1036000.00, 50),
(578, 9, 171000.00, 32),
(579, 4, 197000.00, 37),
(579, 8, 1056000.00, 27),
(579, 9, 196000.00, 24),
(580, 5, 452000.00, 36),
(580, 8, 1422000.00, 10),
(581, 2, 131000.00, 55),
(581, 6, 511000.00, 57),
(582, 5, 460000.00, 41),
(582, 6, 433000.00, 31),
(583, 2, 89000.00, 60),
(583, 10, 134000.00, 37),
(584, 1, 86000.00, 40),
(584, 2, 222000.00, 18),
(584, 7, 1135000.00, 51),
(585, 1, 182000.00, 20),
(585, 5, 474000.00, 11),
(585, 7, 1282000.00, 15),
(585, 8, 972000.00, 14),
(586, 2, 96000.00, 23),
(586, 4, 130000.00, 17),
(587, 2, 227000.00, 60),
(587, 6, 406000.00, 32),
(588, 1, 185000.00, 60),
(588, 4, 193000.00, 54),
(588, 8, 832000.00, 51),
(588, 10, 137000.00, 35),
(589, 2, 101000.00, 48),
(589, 3, 230000.00, 34),
(589, 8, 1244000.00, 48),
(590, 7, 1226000.00, 26),
(590, 8, 1103000.00, 45),
(591, 3, 142000.00, 41),
(591, 6, 362000.00, 37),
(591, 9, 166000.00, 11),
(592, 3, 115000.00, 45),
(592, 10, 195000.00, 17),
(593, 3, 205000.00, 32),
(593, 5, 587000.00, 13),
(593, 6, 581000.00, 12),
(593, 8, 1152000.00, 32),
(594, 6, 489000.00, 32),
(594, 8, 1220000.00, 34),
(595, 3, 181000.00, 38),
(595, 4, 246000.00, 26),
(596, 4, 202000.00, 43),
(596, 5, 506000.00, 60),
(596, 7, 1384000.00, 56),
(596, 8, 830000.00, 32),
(597, 4, 212000.00, 16),
(597, 6, 580000.00, 14),
(597, 8, 1394000.00, 17),
(598, 2, 231000.00, 41),
(598, 3, 140000.00, 60),
(598, 4, 143000.00, 60),
(598, 9, 159000.00, 53),
(599, 4, 161000.00, 50),
(599, 6, 456000.00, 53),
(600, 1, 201000.00, 35),
(600, 7, 1308000.00, 20),
(600, 9, 167000.00, 22),
(601, 9, 98000.00, 18),
(601, 10, 126000.00, 49),
(602, 3, 171000.00, 58),
(602, 6, 594000.00, 55),
(602, 9, 100000.00, 13),
(603, 1, 212000.00, 12),
(603, 6, 533000.00, 32),
(603, 7, 1499000.00, 56),
(603, 10, 245000.00, 53),
(604, 2, 245000.00, 25),
(604, 3, 244000.00, 58),
(604, 6, 424000.00, 58),
(604, 7, 806000.00, 19),
(605, 1, 86000.00, 13),
(605, 4, 230000.00, 20),
(605, 10, 239000.00, 30),
(606, 3, 110000.00, 55),
(606, 4, 180000.00, 56),
(606, 7, 832000.00, 33),
(606, 8, 1013000.00, 49),
(607, 3, 139000.00, 60),
(607, 8, 1154000.00, 55),
(607, 10, 238000.00, 48),
(608, 4, 126000.00, 59),
(608, 9, 183000.00, 29),
(608, 10, 171000.00, 48),
(609, 2, 179000.00, 30),
(609, 3, 237000.00, 33),
(609, 8, 864000.00, 16),
(609, 10, 250000.00, 14),
(610, 2, 193000.00, 13),
(610, 8, 1262000.00, 54),
(610, 9, 139000.00, 39),
(611, 1, 187000.00, 44),
(611, 2, 181000.00, 39),
(611, 10, 115000.00, 47),
(612, 1, 122000.00, 14),
(612, 6, 460000.00, 25),
(612, 9, 173000.00, 54),
(612, 10, 110000.00, 27),
(613, 1, 204000.00, 50),
(613, 7, 863000.00, 44),
(613, 8, 1499000.00, 21),
(613, 9, 136000.00, 15),
(614, 1, 95000.00, 34),
(614, 5, 313000.00, 39),
(614, 6, 353000.00, 32),
(615, 2, 195000.00, 55),
(615, 7, 1273000.00, 38),
(615, 9, 80000.00, 42),
(616, 3, 131000.00, 11),
(616, 6, 578000.00, 40),
(616, 9, 177000.00, 58),
(617, 8, 1167000.00, 34),
(617, 9, 93000.00, 42),
(618, 1, 205000.00, 26),
(618, 6, 412000.00, 25),
(619, 4, 236000.00, 52),
(619, 8, 889000.00, 44),
(619, 9, 87000.00, 41),
(619, 10, 209000.00, 41),
(620, 4, 145000.00, 36),
(620, 6, 597000.00, 44),
(620, 8, 1225000.00, 21),
(620, 10, 174000.00, 41),
(621, 4, 164000.00, 38),
(621, 6, 532000.00, 12),
(621, 10, 119000.00, 13),
(622, 2, 222000.00, 27),
(622, 6, 422000.00, 37),
(623, 2, 151000.00, 10),
(623, 5, 440000.00, 45),
(623, 6, 580000.00, 42),
(623, 7, 1412000.00, 51),
(624, 3, 237000.00, 37),
(624, 8, 1223000.00, 39),
(625, 7, 1017000.00, 42),
(625, 10, 211000.00, 38),
(626, 2, 133000.00, 29),
(626, 9, 163000.00, 40),
(627, 3, 239000.00, 35),
(627, 7, 821000.00, 21),
(627, 9, 161000.00, 60),
(627, 10, 225000.00, 35),
(628, 4, 178000.00, 43),
(628, 7, 1214000.00, 43),
(628, 9, 163000.00, 24),
(628, 10, 106000.00, 52),
(629, 6, 332000.00, 16),
(629, 9, 147000.00, 52),
(630, 2, 89000.00, 56),
(630, 6, 580000.00, 43),
(630, 7, 1141000.00, 58),
(631, 2, 225000.00, 19),
(631, 8, 802000.00, 40),
(632, 2, 94000.00, 20),
(632, 4, 194000.00, 23),
(632, 7, 975000.00, 24),
(632, 10, 218000.00, 22),
(633, 2, 134000.00, 39),
(633, 5, 357000.00, 46),
(634, 2, 184000.00, 57),
(634, 3, 171000.00, 14),
(634, 5, 330000.00, 56),
(634, 10, 235000.00, 17),
(635, 3, 228000.00, 56),
(635, 4, 239000.00, 17),
(635, 5, 407000.00, 36),
(635, 9, 162000.00, 26),
(636, 2, 98000.00, 13),
(636, 6, 572000.00, 35),
(636, 8, 1489000.00, 25),
(637, 1, 162000.00, 17),
(637, 3, 121000.00, 38),
(637, 8, 1433000.00, 44),
(638, 4, 112000.00, 11),
(638, 9, 236000.00, 41),
(638, 10, 151000.00, 34),
(639, 4, 250000.00, 35),
(639, 6, 355000.00, 47),
(639, 7, 1221000.00, 39),
(639, 8, 1285000.00, 13),
(640, 2, 237000.00, 45),
(640, 3, 154000.00, 31),
(640, 4, 159000.00, 50),
(641, 1, 203000.00, 21),
(641, 5, 538000.00, 45),
(641, 7, 1445000.00, 27),
(642, 3, 82000.00, 59),
(642, 5, 555000.00, 10),
(643, 1, 167000.00, 30),
(643, 2, 103000.00, 39),
(643, 3, 141000.00, 34),
(643, 4, 185000.00, 12),
(644, 4, 96000.00, 46),
(644, 7, 1203000.00, 35),
(644, 8, 1077000.00, 21),
(645, 8, 1061000.00, 39),
(645, 9, 93000.00, 34),
(645, 10, 119000.00, 16),
(646, 2, 195000.00, 47),
(646, 3, 170000.00, 42),
(646, 5, 365000.00, 11),
(646, 7, 1481000.00, 38),
(647, 4, 199000.00, 45),
(647, 5, 414000.00, 52),
(647, 9, 214000.00, 38),
(648, 2, 237000.00, 24),
(648, 7, 1287000.00, 35),
(649, 4, 142000.00, 43),
(649, 6, 487000.00, 52),
(649, 9, 224000.00, 21),
(650, 1, 102000.00, 48),
(650, 4, 147000.00, 32);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `aeronave`
--
ALTER TABLE `aeronave`
  ADD PRIMARY KEY (`id_aeronave`),
  ADD UNIQUE KEY `matricula` (`matricula`);

--
-- Indices de la tabla `aeropuerto`
--
ALTER TABLE `aeropuerto`
  ADD PRIMARY KEY (`id_aeropuerto`),
  ADD UNIQUE KEY `codigo_iata` (`codigo_iata`),
  ADD KEY `id_ciudad` (`id_ciudad`);

--
-- Indices de la tabla `asiento`
--
ALTER TABLE `asiento`
  ADD PRIMARY KEY (`id_asiento`),
  ADD UNIQUE KEY `id_aeronave` (`id_aeronave`,`numero_asiento`),
  ADD KEY `id_clase` (`id_clase`);

--
-- Indices de la tabla `auditoria_seguridad`
--
ALTER TABLE `auditoria_seguridad`
  ADD PRIMARY KEY (`id_auditoria`),
  ADD KEY `idx_auditoria_fecha` (`fecha_hora`),
  ADD KEY `idx_auditoria_usuario` (`usuario_texto`),
  ADD KEY `id_usuario` (`id_usuario`);

--
-- Indices de la tabla `bono_corporativo`
--
ALTER TABLE `bono_corporativo`
  ADD PRIMARY KEY (`id_bono`),
  ADD UNIQUE KEY `codigo` (`codigo`);

--
-- Indices de la tabla `cambio_reserva`
--
ALTER TABLE `cambio_reserva`
  ADD PRIMARY KEY (`id_cambio`),
  ADD KEY `id_reserva` (`id_reserva`),
  ADD KEY `id_vuelo_anterior` (`id_vuelo_anterior`,`id_tarifa_anterior`),
  ADD KEY `id_vuelo_nuevo` (`id_vuelo_nuevo`,`id_tarifa_nuevo`);

--
-- Indices de la tabla `categoria_pasajero`
--
ALTER TABLE `categoria_pasajero`
  ADD PRIMARY KEY (`id_categoria`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `ciudad`
--
ALTER TABLE `ciudad`
  ADD PRIMARY KEY (`id_ciudad`),
  ADD UNIQUE KEY `nombre` (`nombre`,`pais`);

--
-- Indices de la tabla `clase_servicio`
--
ALTER TABLE `clase_servicio`
  ADD PRIMARY KEY (`id_clase`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `cuenta_millas`
--
ALTER TABLE `cuenta_millas`
  ADD PRIMARY KEY (`id_persona`);

--
-- Indices de la tabla `medio_pago`
--
ALTER TABLE `medio_pago`
  ADD PRIMARY KEY (`id_medio_pago`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `notificacion`
--
ALTER TABLE `notificacion`
  ADD PRIMARY KEY (`id_notificacion`),
  ADD KEY `id_reserva` (`id_reserva`),
  ADD KEY `id_persona` (`id_persona`),
  ADD KEY `id_vuelo` (`id_vuelo`);

--
-- Indices de la tabla `pago`
--
ALTER TABLE `pago`
  ADD PRIMARY KEY (`id_pago`),
  ADD KEY `id_reserva` (`id_reserva`),
  ADD KEY `id_medio_pago` (`id_medio_pago`),
  ADD KEY `id_bono` (`id_bono`),
  ADD KEY `id_cambio` (`id_cambio`);

--
-- Indices de la tabla `persona`
--
ALTER TABLE `persona`
  ADD PRIMARY KEY (`id_persona`),
  ADD UNIQUE KEY `tipo_documento` (`tipo_documento`,`numero_documento`),
  ADD KEY `id_ciudad_residencia` (`id_ciudad_residencia`);

--
-- Indices de la tabla `politica_seguridad`
--
ALTER TABLE `politica_seguridad`
  ADD PRIMARY KEY (`clave`);

--
-- Indices de la tabla `reembolso`
--
ALTER TABLE `reembolso`
  ADD PRIMARY KEY (`id_reembolso`),
  ADD KEY `id_reserva` (`id_reserva`),
  ADD KEY `id_cambio` (`id_cambio`);

--
-- Indices de la tabla `reserva`
--
ALTER TABLE `reserva`
  ADD PRIMARY KEY (`id_reserva`),
  ADD UNIQUE KEY `codigo_reserva` (`codigo_reserva`),
  ADD KEY `id_comprador` (`id_comprador`);

--
-- Indices de la tabla `reserva_pasajero`
--
ALTER TABLE `reserva_pasajero`
  ADD PRIMARY KEY (`id_reserva_pasajero`),
  ADD UNIQUE KEY `id_reserva` (`id_reserva`,`id_persona`),
  ADD KEY `id_persona` (`id_persona`),
  ADD KEY `id_categoria` (`id_categoria`);

--
-- Indices de la tabla `rol`
--
ALTER TABLE `rol`
  ADD PRIMARY KEY (`id_rol`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `rol_modulo`
--
ALTER TABLE `rol_modulo`
  ADD PRIMARY KEY (`id_rol`,`modulo`);

--
-- Indices de la tabla `ruta`
--
ALTER TABLE `ruta`
  ADD PRIMARY KEY (`id_ruta`),
  ADD UNIQUE KEY `id_aeropuerto_origen` (`id_aeropuerto_origen`,`id_aeropuerto_destino`),
  ADD KEY `id_aeropuerto_destino` (`id_aeropuerto_destino`);

--
-- Indices de la tabla `servicio`
--
ALTER TABLE `servicio`
  ADD PRIMARY KEY (`id_servicio`);

--
-- Indices de la tabla `servicio_contratado`
--
ALTER TABLE `servicio_contratado`
  ADD PRIMARY KEY (`id_servicio_contratado`),
  ADD UNIQUE KEY `id_tramo_pasajero` (`id_tramo_pasajero`,`id_servicio`),
  ADD KEY `id_servicio` (`id_servicio`);

--
-- Indices de la tabla `servicio_precio`
--
ALTER TABLE `servicio_precio`
  ADD PRIMARY KEY (`id_servicio_precio`),
  ADD KEY `id_servicio` (`id_servicio`),
  ADD KEY `id_ciudad_destino` (`id_ciudad_destino`),
  ADD KEY `id_tarifa` (`id_tarifa`);

--
-- Indices de la tabla `tarifa`
--
ALTER TABLE `tarifa`
  ADD PRIMARY KEY (`id_tarifa`),
  ADD KEY `id_clase` (`id_clase`);

--
-- Indices de la tabla `tiquete`
--
ALTER TABLE `tiquete`
  ADD PRIMARY KEY (`id_tiquete`),
  ADD UNIQUE KEY `id_reserva_pasajero` (`id_reserva_pasajero`),
  ADD UNIQUE KEY `numero_tiquete` (`numero_tiquete`);

--
-- Indices de la tabla `tramo_pasajero`
--
ALTER TABLE `tramo_pasajero`
  ADD PRIMARY KEY (`id_tramo_pasajero`),
  ADD UNIQUE KEY `id_reserva_pasajero` (`id_reserva_pasajero`,`sentido`),
  ADD UNIQUE KEY `id_vuelo` (`id_vuelo`,`id_asiento`),
  ADD KEY `id_vuelo_2` (`id_vuelo`,`id_tarifa`),
  ADD KEY `id_asiento` (`id_asiento`);

--
-- Indices de la tabla `usuario_sistema`
--
ALTER TABLE `usuario_sistema`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `usuario` (`usuario`),
  ADD UNIQUE KEY `numero_documento` (`numero_documento`),
  ADD UNIQUE KEY `correo_institucional` (`correo_institucional`),
  ADD KEY `id_rol` (`id_rol`);

--
-- Indices de la tabla `vuelo`
--
ALTER TABLE `vuelo`
  ADD PRIMARY KEY (`id_vuelo`),
  ADD UNIQUE KEY `numero_vuelo` (`numero_vuelo`,`fecha_hora_salida`),
  ADD KEY `id_ruta` (`id_ruta`),
  ADD KEY `id_aeronave` (`id_aeronave`);

--
-- Indices de la tabla `vuelo_tarifa`
--
ALTER TABLE `vuelo_tarifa`
  ADD PRIMARY KEY (`id_vuelo`,`id_tarifa`),
  ADD KEY `id_tarifa` (`id_tarifa`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `aeronave`
--
ALTER TABLE `aeronave`
  MODIFY `id_aeronave` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `aeropuerto`
--
ALTER TABLE `aeropuerto`
  MODIFY `id_aeropuerto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `asiento`
--
ALTER TABLE `asiento`
  MODIFY `id_asiento` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT de la tabla `auditoria_seguridad`
--
ALTER TABLE `auditoria_seguridad`
  MODIFY `id_auditoria` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=84;

--
-- AUTO_INCREMENT de la tabla `bono_corporativo`
--
ALTER TABLE `bono_corporativo`
  MODIFY `id_bono` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `cambio_reserva`
--
ALTER TABLE `cambio_reserva`
  MODIFY `id_cambio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `categoria_pasajero`
--
ALTER TABLE `categoria_pasajero`
  MODIFY `id_categoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `ciudad`
--
ALTER TABLE `ciudad`
  MODIFY `id_ciudad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `clase_servicio`
--
ALTER TABLE `clase_servicio`
  MODIFY `id_clase` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `medio_pago`
--
ALTER TABLE `medio_pago`
  MODIFY `id_medio_pago` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `notificacion`
--
ALTER TABLE `notificacion`
  MODIFY `id_notificacion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT de la tabla `pago`
--
ALTER TABLE `pago`
  MODIFY `id_pago` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;

--
-- AUTO_INCREMENT de la tabla `persona`
--
ALTER TABLE `persona`
  MODIFY `id_persona` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=46;

--
-- AUTO_INCREMENT de la tabla `reembolso`
--
ALTER TABLE `reembolso`
  MODIFY `id_reembolso` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- AUTO_INCREMENT de la tabla `reserva`
--
ALTER TABLE `reserva`
  MODIFY `id_reserva` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- AUTO_INCREMENT de la tabla `reserva_pasajero`
--
ALTER TABLE `reserva_pasajero`
  MODIFY `id_reserva_pasajero` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

--
-- AUTO_INCREMENT de la tabla `rol`
--
ALTER TABLE `rol`
  MODIFY `id_rol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `ruta`
--
ALTER TABLE `ruta`
  MODIFY `id_ruta` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `servicio`
--
ALTER TABLE `servicio`
  MODIFY `id_servicio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `servicio_contratado`
--
ALTER TABLE `servicio_contratado`
  MODIFY `id_servicio_contratado` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `servicio_precio`
--
ALTER TABLE `servicio_precio`
  MODIFY `id_servicio_precio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `tarifa`
--
ALTER TABLE `tarifa`
  MODIFY `id_tarifa` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `tiquete`
--
ALTER TABLE `tiquete`
  MODIFY `id_tiquete` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- AUTO_INCREMENT de la tabla `tramo_pasajero`
--
ALTER TABLE `tramo_pasajero`
  MODIFY `id_tramo_pasajero` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=52;

--
-- AUTO_INCREMENT de la tabla `usuario_sistema`
--
ALTER TABLE `usuario_sistema`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `vuelo`
--
ALTER TABLE `vuelo`
  MODIFY `id_vuelo` int(11) NOT NULL AUTO_INCREMENT;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `aeropuerto`
--
ALTER TABLE `aeropuerto`
  ADD CONSTRAINT `aeropuerto_ibfk_1` FOREIGN KEY (`id_ciudad`) REFERENCES `ciudad` (`id_ciudad`);

--
-- Filtros para la tabla `asiento`
--
ALTER TABLE `asiento`
  ADD CONSTRAINT `asiento_ibfk_1` FOREIGN KEY (`id_aeronave`) REFERENCES `aeronave` (`id_aeronave`) ON DELETE CASCADE,
  ADD CONSTRAINT `asiento_ibfk_2` FOREIGN KEY (`id_clase`) REFERENCES `clase_servicio` (`id_clase`);

--
-- Filtros para la tabla `auditoria_seguridad`
--
ALTER TABLE `auditoria_seguridad`
  ADD CONSTRAINT `auditoria_seguridad_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuario_sistema` (`id_usuario`) ON DELETE SET NULL;

--
-- Filtros para la tabla `cambio_reserva`
--
ALTER TABLE `cambio_reserva`
  ADD CONSTRAINT `cambio_reserva_ibfk_1` FOREIGN KEY (`id_reserva`) REFERENCES `reserva` (`id_reserva`) ON DELETE CASCADE,
  ADD CONSTRAINT `cambio_reserva_ibfk_2` FOREIGN KEY (`id_vuelo_anterior`,`id_tarifa_anterior`) REFERENCES `vuelo_tarifa` (`id_vuelo`, `id_tarifa`),
  ADD CONSTRAINT `cambio_reserva_ibfk_3` FOREIGN KEY (`id_vuelo_nuevo`,`id_tarifa_nuevo`) REFERENCES `vuelo_tarifa` (`id_vuelo`, `id_tarifa`);

--
-- Filtros para la tabla `cuenta_millas`
--
ALTER TABLE `cuenta_millas`
  ADD CONSTRAINT `cuenta_millas_ibfk_1` FOREIGN KEY (`id_persona`) REFERENCES `persona` (`id_persona`) ON DELETE CASCADE;

--
-- Filtros para la tabla `notificacion`
--
ALTER TABLE `notificacion`
  ADD CONSTRAINT `notificacion_ibfk_1` FOREIGN KEY (`id_reserva`) REFERENCES `reserva` (`id_reserva`) ON DELETE CASCADE,
  ADD CONSTRAINT `notificacion_ibfk_2` FOREIGN KEY (`id_persona`) REFERENCES `persona` (`id_persona`),
  ADD CONSTRAINT `notificacion_ibfk_3` FOREIGN KEY (`id_vuelo`) REFERENCES `vuelo` (`id_vuelo`);

--
-- Filtros para la tabla `pago`
--
ALTER TABLE `pago`
  ADD CONSTRAINT `pago_ibfk_1` FOREIGN KEY (`id_reserva`) REFERENCES `reserva` (`id_reserva`) ON DELETE CASCADE,
  ADD CONSTRAINT `pago_ibfk_2` FOREIGN KEY (`id_medio_pago`) REFERENCES `medio_pago` (`id_medio_pago`),
  ADD CONSTRAINT `pago_ibfk_3` FOREIGN KEY (`id_bono`) REFERENCES `bono_corporativo` (`id_bono`),
  ADD CONSTRAINT `pago_ibfk_4` FOREIGN KEY (`id_cambio`) REFERENCES `cambio_reserva` (`id_cambio`);

--
-- Filtros para la tabla `persona`
--
ALTER TABLE `persona`
  ADD CONSTRAINT `persona_ibfk_1` FOREIGN KEY (`id_ciudad_residencia`) REFERENCES `ciudad` (`id_ciudad`);

--
-- Filtros para la tabla `reembolso`
--
ALTER TABLE `reembolso`
  ADD CONSTRAINT `reembolso_ibfk_1` FOREIGN KEY (`id_reserva`) REFERENCES `reserva` (`id_reserva`) ON DELETE CASCADE,
  ADD CONSTRAINT `reembolso_ibfk_2` FOREIGN KEY (`id_cambio`) REFERENCES `cambio_reserva` (`id_cambio`);

--
-- Filtros para la tabla `reserva`
--
ALTER TABLE `reserva`
  ADD CONSTRAINT `reserva_ibfk_1` FOREIGN KEY (`id_comprador`) REFERENCES `persona` (`id_persona`);

--
-- Filtros para la tabla `reserva_pasajero`
--
ALTER TABLE `reserva_pasajero`
  ADD CONSTRAINT `reserva_pasajero_ibfk_1` FOREIGN KEY (`id_reserva`) REFERENCES `reserva` (`id_reserva`) ON DELETE CASCADE,
  ADD CONSTRAINT `reserva_pasajero_ibfk_2` FOREIGN KEY (`id_persona`) REFERENCES `persona` (`id_persona`),
  ADD CONSTRAINT `reserva_pasajero_ibfk_3` FOREIGN KEY (`id_categoria`) REFERENCES `categoria_pasajero` (`id_categoria`);

--
-- Filtros para la tabla `rol_modulo`
--
ALTER TABLE `rol_modulo`
  ADD CONSTRAINT `rol_modulo_ibfk_1` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`) ON DELETE CASCADE;

--
-- Filtros para la tabla `ruta`
--
ALTER TABLE `ruta`
  ADD CONSTRAINT `ruta_ibfk_1` FOREIGN KEY (`id_aeropuerto_origen`) REFERENCES `aeropuerto` (`id_aeropuerto`),
  ADD CONSTRAINT `ruta_ibfk_2` FOREIGN KEY (`id_aeropuerto_destino`) REFERENCES `aeropuerto` (`id_aeropuerto`);

--
-- Filtros para la tabla `servicio_contratado`
--
ALTER TABLE `servicio_contratado`
  ADD CONSTRAINT `servicio_contratado_ibfk_1` FOREIGN KEY (`id_tramo_pasajero`) REFERENCES `tramo_pasajero` (`id_tramo_pasajero`) ON DELETE CASCADE,
  ADD CONSTRAINT `servicio_contratado_ibfk_2` FOREIGN KEY (`id_servicio`) REFERENCES `servicio` (`id_servicio`);

--
-- Filtros para la tabla `servicio_precio`
--
ALTER TABLE `servicio_precio`
  ADD CONSTRAINT `servicio_precio_ibfk_1` FOREIGN KEY (`id_servicio`) REFERENCES `servicio` (`id_servicio`) ON DELETE CASCADE,
  ADD CONSTRAINT `servicio_precio_ibfk_2` FOREIGN KEY (`id_ciudad_destino`) REFERENCES `ciudad` (`id_ciudad`),
  ADD CONSTRAINT `servicio_precio_ibfk_3` FOREIGN KEY (`id_tarifa`) REFERENCES `tarifa` (`id_tarifa`);

--
-- Filtros para la tabla `tarifa`
--
ALTER TABLE `tarifa`
  ADD CONSTRAINT `tarifa_ibfk_1` FOREIGN KEY (`id_clase`) REFERENCES `clase_servicio` (`id_clase`);

--
-- Filtros para la tabla `tiquete`
--
ALTER TABLE `tiquete`
  ADD CONSTRAINT `tiquete_ibfk_1` FOREIGN KEY (`id_reserva_pasajero`) REFERENCES `reserva_pasajero` (`id_reserva_pasajero`);

--
-- Filtros para la tabla `tramo_pasajero`
--
ALTER TABLE `tramo_pasajero`
  ADD CONSTRAINT `tramo_pasajero_ibfk_1` FOREIGN KEY (`id_reserva_pasajero`) REFERENCES `reserva_pasajero` (`id_reserva_pasajero`) ON DELETE CASCADE,
  ADD CONSTRAINT `tramo_pasajero_ibfk_2` FOREIGN KEY (`id_vuelo`,`id_tarifa`) REFERENCES `vuelo_tarifa` (`id_vuelo`, `id_tarifa`),
  ADD CONSTRAINT `tramo_pasajero_ibfk_3` FOREIGN KEY (`id_asiento`) REFERENCES `asiento` (`id_asiento`);

--
-- Filtros para la tabla `usuario_sistema`
--
ALTER TABLE `usuario_sistema`
  ADD CONSTRAINT `usuario_sistema_ibfk_1` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`);

--
-- Filtros para la tabla `vuelo`
--
ALTER TABLE `vuelo`
  ADD CONSTRAINT `vuelo_ibfk_1` FOREIGN KEY (`id_ruta`) REFERENCES `ruta` (`id_ruta`),
  ADD CONSTRAINT `vuelo_ibfk_2` FOREIGN KEY (`id_aeronave`) REFERENCES `aeronave` (`id_aeronave`);

--
-- Filtros para la tabla `vuelo_tarifa`
--
ALTER TABLE `vuelo_tarifa`
  ADD CONSTRAINT `vuelo_tarifa_ibfk_1` FOREIGN KEY (`id_vuelo`) REFERENCES `vuelo` (`id_vuelo`) ON DELETE CASCADE,
  ADD CONSTRAINT `vuelo_tarifa_ibfk_2` FOREIGN KEY (`id_tarifa`) REFERENCES `tarifa` (`id_tarifa`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
