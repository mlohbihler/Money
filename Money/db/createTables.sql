DROP TABLE DividendProjections;
DROP TABLE Transactions;
DROP TABLE Accounts;
DROP TABLE Assets;

CREATE TABLE Assets (
  symbol VARCHAR(30) NOT NULL,
  name VARCHAR(255) NOT NULL,
  marketPrice DECIMAL(12,4),
  marketTime BIGINT,
  marketSymbol VARCHAR(30),
  divAmount DECIMAL(12,6),
  divDay int,
  divMonth int,
  divPerYear int,
  divXaType VARCHAR(20),
  divCountry VARCHAR(10),
  divSymbolId int,
  notes text,
  PRIMARY KEY (symbol)
) ENGINE=InnoDB;

CREATE TABLE Accounts (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(10) NOT NULL,
  notes text,
  colour VARCHAR(6),
  PRIMARY KEY (id)
) ENGINE=InnoDB;

CREATE TABLE Transactions (
  id INT NOT NULL AUTO_INCREMENT,
  accountId INT NOT NULL,
  xaDate DATE NOT NULL,
  xaType VARCHAR(20) NOT NULL,
  symbol VARCHAR(30),
  symbol2 VARCHAR(30),
  shares DECIMAL(12,4),
  price DECIMAL(12,6),
  exchange DECIMAL(12,4),
  fee DECIMAL(12,4),
  book DECIMAL(12,4),
  PRIMARY KEY (id)
) ENGINE=InnoDB;
ALTER TABLE Transactions ADD CONSTRAINT TransactionsFk1 FOREIGN KEY (accountId) REFERENCES Accounts(id);

CREATE TABLE DividendProjections (
  id INT NOT NULL AUTO_INCREMENT,
  accountId INT NOT NULL,
  xaDate DATE NOT NULL,
  exDivDate DATE,
  symbol VARCHAR(30) NOT NULL,
  xaType VARCHAR(20),
  shares DECIMAL(12,4) NOT NULL,
  divAmount DECIMAL(12,6) NOT NULL,
  amount DECIMAL(12,2) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB;
ALTER TABLE DividendProjections ADD CONSTRAINT DividendProjectionsFk1 FOREIGN KEY (accountId) REFERENCES Accounts(id);
