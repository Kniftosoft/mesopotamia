CREATE DATABASE  IF NOT EXISTS `euphratis` /*!40100 DEFAULT CHARACTER SET latin1 */;
USE `euphratis`;
-- MySQL dump 10.13  Distrib 5.6.13, for Win32 (x86)
--
-- Host: localhost    Database: euphratis
-- ------------------------------------------------------
-- Server version	5.6.16

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `app`
--

DROP TABLE IF EXISTS `app`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `app` (
  `idapp` int(11) NOT NULL AUTO_INCREMENT,
  `beschreibung` varchar(45) NOT NULL,
  PRIMARY KEY (`idapp`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app`
--

LOCK TABLES `app` WRITE;
/*!40000 ALTER TABLE `app` DISABLE KEYS */;
INSERT INTO `app` VALUES (1,'test'),(2,'');
/*!40000 ALTER TABLE `app` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auftrag`
--

DROP TABLE IF EXISTS `auftrag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `auftrag` (
  `idauftrag` int(11) NOT NULL AUTO_INCREMENT,
  `groesse` int(11) NOT NULL,
  `startzeit` datetime DEFAULT NULL,
  `produkt` int(11) NOT NULL,
  PRIMARY KEY (`idauftrag`),
  KEY `produkt_idx` (`produkt`),
  CONSTRAINT `produkt` FOREIGN KEY (`produkt`) REFERENCES `produkt` (`idprodukt`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auftrag`
--

LOCK TABLES `auftrag` WRITE;
/*!40000 ALTER TABLE `auftrag` DISABLE KEYS */;
INSERT INTO `auftrag` VALUES (1,10000,NULL,1),(2,12222,NULL,2),(3,82831,NULL,3),(4,12352,NULL,4),(5,52341,NULL,5),(6,63456,NULL,1),(7,74567,NULL,2),(8,87346,NULL,3),(9,83452,NULL,4),(10,62456,NULL,5),(11,23453,NULL,1),(12,75464,NULL,3),(13,56254,NULL,5),(14,23453,NULL,1),(15,34563,NULL,3),(16,45674,NULL,5);
/*!40000 ALTER TABLE `auftrag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configtypes`
--

DROP TABLE IF EXISTS `configtypes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `configtypes` (
  `idConfigtypes` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `Description` varchar(400) NOT NULL,
  PRIMARY KEY (`idConfigtypes`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configtypes`
--

LOCK TABLES `configtypes` WRITE;
/*!40000 ALTER TABLE `configtypes` DISABLE KEYS */;
INSERT INTO `configtypes` VALUES (1,'CONFIG_VERSION','The version of the Tigris client that last accessed the config file. Stored so future versions of the client may update outdated configuration'),(2,'INTRO_FINISHED','Used to give new users an introduction. Set to true after first login. (This feature might be added later)'),(3,'LOCALE','A string identifying the language the user has selected for his GUI'),(4,'TILES','A JSON string containing information on the tiles the user created on his dashbord. The exact layout of this JSON object is not part of MCP but the GUI specification. Therefore, it is not documented here.');
/*!40000 ALTER TABLE `configtypes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `log`
--

DROP TABLE IF EXISTS `log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `log` (
  `idlog` int(11) NOT NULL AUTO_INCREMENT,
  `maschine` int(11) NOT NULL,
  `produziert` int(11) NOT NULL,
  `auftrag` int(11) NOT NULL,
  `zustand` int(11) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`idlog`),
  KEY `_idx` (`maschine`),
  KEY `auftrag_idx` (`auftrag`),
  KEY `zustand_idx` (`zustand`),
  CONSTRAINT `auftrag` FOREIGN KEY (`auftrag`) REFERENCES `auftrag` (`idauftrag`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `maschine` FOREIGN KEY (`maschine`) REFERENCES `maschine` (`idmaschine`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `zustand` FOREIGN KEY (`zustand`) REFERENCES `zustand` (`idzustand`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=730374 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `log`
--

LOCK TABLES `log` WRITE;
/*!40000 ALTER TABLE `log` DISABLE KEYS */;
/*!40000 ALTER TABLE `log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `maschine`
--

DROP TABLE IF EXISTS `maschine`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `maschine` (
  `idmaschine` int(11) NOT NULL AUTO_INCREMENT,
  `standort` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `Maximumspeed` double NOT NULL,
  PRIMARY KEY (`idmaschine`),
  UNIQUE KEY `idmaschine_UNIQUE` (`idmaschine`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `maschine`
--

LOCK TABLES `maschine` WRITE;
/*!40000 ALTER TABLE `maschine` DISABLE KEYS */;
INSERT INTO `maschine` VALUES (1,'R100','1',1000),(2,'R100','2',1000),(3,'R105','3',2000),(4,'R110','4',2000),(5,'R111','5',3000),(6,'R200','6',3000),(7,'R200','7',500),(8,'R200','8',500),(9,'R201','9',400),(10,'R202','10',400),(11,'R202','11',800),(12,'R203','12',800),(13,'R203','13',1500);
/*!40000 ALTER TABLE `maschine` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produkt`
--

DROP TABLE IF EXISTS `produkt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `produkt` (
  `idprodukt` int(11) NOT NULL AUTO_INCREMENT,
  `beschreibung` varchar(45) NOT NULL,
  PRIMARY KEY (`idprodukt`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produkt`
--

LOCK TABLES `produkt` WRITE;
/*!40000 ALTER TABLE `produkt` DISABLE KEYS */;
INSERT INTO `produkt` VALUES (1,'Schraube 1'),(2,'Schraube 2'),(3,'Schraube 3'),(4,'Schraube 4'),(5,'Schraube 5');
/*!40000 ALTER TABLE `produkt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sequence`
--

DROP TABLE IF EXISTS `sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sequence` (
  `SEQ_NAME` varchar(50) NOT NULL,
  `SEQ_COUNT` decimal(38,0) DEFAULT NULL,
  PRIMARY KEY (`SEQ_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sequence`
--

LOCK TABLES `sequence` WRITE;
/*!40000 ALTER TABLE `sequence` DISABLE KEYS */;
INSERT INTO `sequence` VALUES ('SEQ_GEN',673400);
/*!40000 ALTER TABLE `sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sessions`
--

DROP TABLE IF EXISTS `sessions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sessions` (
  `idSessions` int(11) NOT NULL AUTO_INCREMENT,
  `user` int(11) NOT NULL,
  PRIMARY KEY (`idSessions`),
  KEY `loggedinuser_idx` (`user`),
  CONSTRAINT `loggedinuser` FOREIGN KEY (`user`) REFERENCES `user` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=644042 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sessions`
--

LOCK TABLES `sessions` WRITE;
/*!40000 ALTER TABLE `sessions` DISABLE KEYS */;
/*!40000 ALTER TABLE `sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscribe`
--

DROP TABLE IF EXISTS `subscribe`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `subscribe` (
  `idSubscribe` int(11) NOT NULL AUTO_INCREMENT,
  `user` int(11) NOT NULL,
  `app` int(11) NOT NULL,
  `objektID` int(11) NOT NULL,
  PRIMARY KEY (`idSubscribe`),
  KEY `user_idx` (`user`),
  KEY `app_idx` (`app`),
  CONSTRAINT `app` FOREIGN KEY (`app`) REFERENCES `app` (`idapp`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `user` FOREIGN KEY (`user`) REFERENCES `user` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=657799 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscribe`
--

LOCK TABLES `subscribe` WRITE;
/*!40000 ALTER TABLE `subscribe` DISABLE KEYS */;
/*!40000 ALTER TABLE `subscribe` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `USER_ID` int(11) NOT NULL AUTO_INCREMENT,
  `ACC_TYPE` int(11) NOT NULL,
  `EMAIL` varchar(45) NOT NULL,
  `NACHNAME` varchar(45) DEFAULT NULL,
  `ORT` varchar(30) DEFAULT NULL,
  `PASSWORD` varchar(64) NOT NULL,
  `PLZ` varchar(7) DEFAULT NULL,
  `STRASSE` varchar(45) DEFAULT NULL,
  `VORNAME` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `USER_ID` (`USER_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,0,'julian@mes.de','lorra','mülheim','ef5ac5f275ecc5a04efc00ad9d13da55e15453e900823ea52b0ab2e732049c79','45481','Lindenhof 52','julian'),(2,0,'Dennis@mes.de','szczesny','essen','67890','7898','7890','Dennis');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `useraccess`
--

DROP TABLE IF EXISTS `useraccess`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `useraccess` (
  `iduseraccess` int(11) NOT NULL AUTO_INCREMENT,
  `user` int(11) NOT NULL,
  `maschine` int(11) NOT NULL,
  PRIMARY KEY (`iduseraccess`),
  KEY `user_idx` (`user`),
  KEY `maschine_idx` (`maschine`),
  CONSTRAINT `accessmaschine` FOREIGN KEY (`maschine`) REFERENCES `maschine` (`idmaschine`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `accessuser` FOREIGN KEY (`user`) REFERENCES `user` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `useraccess`
--

LOCK TABLES `useraccess` WRITE;
/*!40000 ALTER TABLE `useraccess` DISABLE KEYS */;
INSERT INTO `useraccess` VALUES (1,1,1),(2,1,3),(6,1,5),(7,1,4),(8,1,7),(9,1,8),(10,1,10);
/*!40000 ALTER TABLE `useraccess` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `userconfig`
--

DROP TABLE IF EXISTS `userconfig`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `userconfig` (
  `iduserconfig` int(11) NOT NULL AUTO_INCREMENT,
  `user` int(11) NOT NULL,
  `config` int(11) NOT NULL,
  `value` varchar(200) NOT NULL,
  PRIMARY KEY (`iduserconfig`),
  KEY `userconfig_idx` (`user`),
  KEY `configtyps_idx` (`config`),
  CONSTRAINT `configtyps` FOREIGN KEY (`config`) REFERENCES `configtypes` (`idConfigtypes`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `userconfig` FOREIGN KEY (`user`) REFERENCES `user` (`USER_ID`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `userconfig`
--

LOCK TABLES `userconfig` WRITE;
/*!40000 ALTER TABLE `userconfig` DISABLE KEYS */;
INSERT INTO `userconfig` VALUES (29,1,4,'{\"id\":3,\"category\":1,\"column\":0}'),(30,1,4,'{\"id\":4,\"category\":1,\"column\":0}'),(31,1,4,'{\"id\":8,\"category\":1,\"column\":0}');
/*!40000 ALTER TABLE `userconfig` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `zustand`
--

DROP TABLE IF EXISTS `zustand`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `zustand` (
  `idzustand` int(11) NOT NULL AUTO_INCREMENT,
  `beschreibung` varchar(45) NOT NULL,
  PRIMARY KEY (`idzustand`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `zustand`
--

LOCK TABLES `zustand` WRITE;
/*!40000 ALTER TABLE `zustand` DISABLE KEYS */;
INSERT INTO `zustand` VALUES (1,'Läuft'),(2,'Störung'),(3,'Wartung'),(4,'Umbau auf einen anderen Typ Schrauben'),(5,'Reinigung');
/*!40000 ALTER TABLE `zustand` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2014-03-24 15:54:27
