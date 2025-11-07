-- 1.1) Crear esquema del proyecto
CREATE DATABASE IF NOT EXISTS inventario_it
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE inventario_it;

-- 1.2) Asegurar motor y modo estricto
SET SESSION sql_mode = 'STRICT_ALL_TABLES,ONLY_FULL_GROUP_BY';
