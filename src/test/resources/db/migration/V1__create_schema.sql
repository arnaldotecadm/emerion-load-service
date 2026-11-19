CREATE TABLE FINCLI (
                        CODCLI             INTEGER NOT NULL,
                        NOMCLI             VARCHAR(70),
                        APECLI             VARCHAR(20),
                        INSCLI             VARCHAR(18),
                        CGCCLI             VARCHAR(18),
                        DTNCLI             DATE,
                        LIMCLI             NUMERIC(15,2),
                        TIPCLI             CHAR(1),
                        FLBCLI             CHAR(1),
                        CEFCLI             CHAR(8),
                        TEFCLI             VARCHAR(10),
                        ENFCLI             VARCHAR(70),
                        RFFCLI             VARCHAR(40),
                        NRFCLI             VARCHAR(10),
                        BAFCLI             VARCHAR(20),
                        CIFCLI             VARCHAR(20),
                        UFFCLI             CHAR(2),
                        CEACLI             CHAR(8),
                        TEACLI             VARCHAR(10),
                        ENACLI             VARCHAR(70),
                        RFACLI             VARCHAR(40),
                        NRACLI             VARCHAR(10),
                        BAACLI             VARCHAR(20),
                        CIACLI             VARCHAR(20),
                        UFACLI             CHAR(2),
                        CEECLI             CHAR(8),
                        TEECLI             VARCHAR(10),
                        ENECLI             VARCHAR(70),
                        RFECLI             VARCHAR(40),
                        NRECLI             VARCHAR(10),
                        BAECLI             VARCHAR(20),
                        CIECLI             VARCHAR(20),
                        UFECLI             CHAR(2),
                        INECLI             VARCHAR(18),
                        CGECLI             VARCHAR(18),
                        COMCLI             VARCHAR(40),
                        CECCLI             CHAR(8),
                        TECCLI             VARCHAR(10),
                        ENCCLI             VARCHAR(70),
                        RFCCLI             VARCHAR(40),
                        NRCCLI             VARCHAR(10),
                        BACCLI             VARCHAR(20),
                        CICCLI             VARCHAR(20),
                        UFCCLI             CHAR(2),
                        PT1CLI             VARCHAR(4),
                        FO1CLI             VARCHAR(20),
                        PT2CLI             VARCHAR(4),
                        FO2CLI             VARCHAR(20),
                        PT3CLI             VARCHAR(4),
                        FO3CLI             VARCHAR(20),
                        PF1CLI             VARCHAR(4),
                        FA1CLI             VARCHAR(20),
                        PF2CLI             VARCHAR(4),
                        FA2CLI             VARCHAR(20),
                        PF3CLI             VARCHAR(4),
                        FA3CLI             VARCHAR(20),
                        OBSCLI             VARCHAR(2000),
                        DCACLI             DATE,
                        CODUSU             INTEGER,
                        CODVEN             INTEGER,
                        CODATD             INTEGER,
                        CODGCL             INTEGER,
                        CODBAN             INTEGER,
                        CODTRA             INTEGER,
                        CODCCL             INTEGER,
                        CODTCL             INTEGER,
                        CODCOM             VARCHAR(3),
                        CODMCR             INTEGER,
                        CODMRG             INTEGER,
                        CODSET             INTEGER,
                        CODMST             INTEGER,
                        DTLCRE             DATE,
                        INCPAG             CHAR(1),
                        CLITAB             VARCHAR(23),
                        CLIDEV             NUMERIC(15,2),
                        FLGTRG             CHAR(1),
                        COFCLI             VARCHAR(40),
                        COCCLI             VARCHAR(40),
                        COECLI             VARCHAR(40),
                        PT4CLI             VARCHAR(4),
                        FO4CLI             VARCHAR(20),
                        PF4CLI             VARCHAR(4),
                        FA4CLI             VARCHAR(20),
                        NROSUF             VARCHAR(20),
                        FLGPSQ             CHAR(3),
                        DTEACM             DATE,
                        TOTACM             NUMERIC(15,2) DEFAULT 0,
                        DTEATU             DATE,
                        HREATU             CHAR(8),
                        USUATU             INTEGER,
                        MUNSUF             INTEGER,
                        TENCOB             VARCHAR(21),
                        TENCOM             VARCHAR(21),
                        TENENT             VARCHAR(21),
                        FLGPRO             CHAR(3),
                        CODPFA             VARCHAR(15),
                        TIPPFA             VARCHAR(15),
                        FLGINF             CHAR(3),
                        CODREP             INTEGER,
                        FLGCA1             CHAR(3),
                        FLGCA2             CHAR(3),
                        INCPAL             CHAR(3),
                        CODPAL             INTEGER,
                        FLGCA3             CHAR(3),
                        FLGINT             CHAR(3),
                        DTVSUF             DATE,
                        DCALIM             DATE,
                        HRELIM             CHAR(8),
                        USULIM             INTEGER,
                        DATLIM             DATE,
                        HRALIM             CHAR(8),
                        USALIM             INTEGER,
                        QTDICL             INTEGER DEFAULT 0,
                        PC1CLI             CHAR(4),
                        FC1CLI             CHAR(20),
                        PC2CLI             CHAR(4),
                        FC2CLI             CHAR(20),
                        PC3CLI             CHAR(4),
                        FC3CLI             CHAR(20),
                        PC4CLI             CHAR(4),
                        FC4CLI             CHAR(20),
                        ID_FINHOL          INTEGER,
                        ID_FINUFF          INTEGER,
                        ID_FINCIF          INTEGER,
                        ID_FINUFA          INTEGER,
                        ID_FINCIA          INTEGER,
                        ID_FINUFE          INTEGER,
                        ID_FINCIE          INTEGER,
                        ID_FINUFC          INTEGER,
                        ID_FINCIC          INTEGER,
                        MENBL1             VARCHAR(80),
                        MENBL2             VARCHAR(80),
                        MENBL3             VARCHAR(80),
                        MENBL4             VARCHAR(80),
                        MENBL5             VARCHAR(80),
                        MENCR1             VARCHAR(80),
                        MENCR2             VARCHAR(80),
                        MENCR3             VARCHAR(80),
                        MENCR4             VARCHAR(80),
                        MENCR5             VARCHAR(80),
                        ID_FINPAI          INTEGER,
                        EM1CLI             VARCHAR(70),
                        EM2CLI             VARCHAR(70),
                        EM3CLI             VARCHAR(70),
                        WEBCLI             VARCHAR(70),
                        ID_FINCTB          INTEGER,
                        ID_FINCT2          INTEGER,
                        ID_FINCT3          INTEGER,
                        ID_FINCT4          INTEGER,
                        ID_FINCT5          INTEGER,
                        ID_FINCT6          INTEGER,
                        CRE_DEV_USU        INTEGER,
                        CRE_DEV_OBS        VARCHAR(200),
                        CRE_DEV_DTE        DATE,
                        TIPO_PESSOA        CHAR(2),
                        REGTRB             INTEGER,
                        INDIC_ESTRANGEIRO  VARCHAR(20),
                        INDIC_IE           INTEGER DEFAULT 0,
                        INSC_MUNICIPAL     CHAR(15),
                        CNAE               CHAR(20),
                        CEST               CHAR(7),
                        EMAIL_INTERNO_XML  CHAR(60),
                        OBSFIN             VARCHAR(2000),
    SENT_TO_WEBSERVICE INT DEFAULT 0
);
ALTER TABLE FINCLI ADD CONSTRAINT FINCLI_PK PRIMARY KEY (CODCLI);
CREATE TABLE ESTPRO
(
    CODCLP INTEGER NOT NULL,
    CODGRU INTEGER NOT NULL,
    CODSUB INTEGER NOT NULL,
    CODPRO INTEGER NOT NULL,
    DESCRI VARCHAR(100),
    DSCPRO VARCHAR(100),
    DSRPRO VARCHAR(100),
    REFPRO VARCHAR(40),
    CODNCM VARCHAR(20),
    CEST VARCHAR(10),
    CODST1 VARCHAR(10),
    CODCAT VARCHAR(10),
    CODTIP VARCHAR(10),
    CODMRC VARCHAR(20),
    CODUNE VARCHAR(10),
    CODUNS VARCHAR(10),
    PESLIQ NUMERIC(15, 4),
    PESBRT NUMERIC(15, 4),
    FLBPRO CHAR(1),
    CBAPRO VARCHAR(30),
    CODBAR VARCHAR(30),
    IDEPRO NUMERIC(15, 4),
    SIMPRO VARCHAR(30),
    QTDVOL NUMERIC(15, 4),
    QTDEMB NUMERIC(15, 4),
    LOCPRO VARCHAR(30),
    PESCUB NUMERIC(15, 4),
    CBAEMB VARCHAR(30),
    IBSCBS_C_CLASS_TRIB VARCHAR(30),
    IBSCBS_CST VARCHAR(30),
    COD_FCP_ENTRADA VARCHAR(30),
    COD_FCP_SAIDA VARCHAR(30),
    IPISAI VARCHAR(10),
    IPIENT VARCHAR(10),
    ICMSAI VARCHAR(10),
    ICMENT VARCHAR(10),
    CODSTS VARCHAR(10),
    CODSTE VARCHAR(10),
    OBSPRO VARCHAR(1000),
    CONSTRAINT PK_ESTPRO
        PRIMARY KEY (CODCLP, CODGRU, CODSUB, CODPRO)
);

CREATE TABLE PEDRES
(
    CODEMP INTEGER NOT NULL,
    DTERES DATE    NOT NULL,
    NUMRES INTEGER NOT NULL,
    CODCLI INTEGER NOT NULL,
    VALTOT DECIMAL(15, 2),
    UFERES CHAR(2),
    SITRES VARCHAR(10),
    TOTGER NUMERIC(15, 2),
    TOTRES NUMERIC(15, 2),
    TOTIPI NUMERIC(15, 2),
    TOTICM NUMERIC(15, 2),
    TOTPIS NUMERIC(15, 2),
    TOTCOF NUMERIC(15, 2),
    TOTSUB NUMERIC(15, 2),
    TOTDESCINC NUMERIC(15, 2),
    TOTFRT NUMERIC(15, 2),
    TOTSEG NUMERIC(15, 2),
    TOTOUTDESP NUMERIC(15, 2),
    CODVEN INTEGER,
    CODATD INTEGER,
    DTFRES DATE,
    CODTRA VARCHAR(20),
    PEDANT VARCHAR(20),
    REGTRB INTEGER,
    CODPFA VARCHAR(20),
    CODTIP INTEGER,
    CONSTRAINT PK_PEDRES
        PRIMARY KEY (CODEMP, DTERES, NUMRES),

    CONSTRAINT FK_PEDRES_FINCLI
        FOREIGN KEY (CODCLI)
            REFERENCES FINCLI (CODCLI)
);

CREATE TABLE PEDRE2
(
    CODEMP     INTEGER NOT NULL,
    DTERES     DATE    NOT NULL,
    NUMRES     INTEGER NOT NULL,
    SEQRE2     INTEGER NOT NULL,

    CODCLP     INTEGER NOT NULL,
    CODGRU     INTEGER NOT NULL,
    CODSUB     INTEGER NOT NULL,
    CODPRO     INTEGER NOT NULL,

    CODTAM     INTEGER,
    CODCOR     INTEGER,

    QUANTIDADE DECIMAL(15, 3),
    DESRE2 VARCHAR(100),
    QTPRE2 NUMERIC(15, 4),
    VLQRE2 NUMERIC(15, 4),
    TOTRE2 NUMERIC(15, 4),
    CODST1 VARCHAR(10),
    CODUND VARCHAR(10),
    VLURE2 NUMERIC(15, 4),
    DSCRE2 NUMERIC(15, 4),
    DSRRE2 NUMERIC(15, 4),
    ICMRE2 NUMERIC(15, 4),
    BASICM NUMERIC(15, 4),
    TOTICM NUMERIC(15, 4),
    REDICM NUMERIC(15, 4),
    BASSUB NUMERIC(15, 4),
    TOTSUB NUMERIC(15, 4),
    ICMSUB NUMERIC(15, 4),
    MRGSUB NUMERIC(15, 4),
    REDSUB NUMERIC(15, 4),
    IPIRE2 NUMERIC(15, 4),
    BASIPI NUMERIC(15, 4),
    TOTIPI NUMERIC(15, 4),
    CLSIPI VARCHAR(20),
    CSTIPI VARCHAR(10),
    BASPIS NUMERIC(15, 4),
    ALIQPIS NUMERIC(15, 4),
    TOTPIS NUMERIC(15, 4),
    CSTPIS VARCHAR(10),
    BASCOF NUMERIC(15, 4),
    ALIQCOF NUMERIC(15, 4),
    TOTCOF NUMERIC(15, 4),
    CSTCOF VARCHAR(10),
    TOTDSR NUMERIC(15, 4),
    TOTFRT NUMERIC(15, 4),
    TOTSEG NUMERIC(15, 4),
    TOTOUTDESP NUMERIC(15, 4),
    TOTITETRB NUMERIC(15, 4),
    TOTREN NUMERIC(15, 4),
    TOTGE2 NUMERIC(15, 4),
    OBSRE2 VARCHAR(1000),
    NUMPEDCOMPRA VARCHAR(30),
    NUMITEMCOMPRA INTEGER,
    NRORE2 INTEGER,
    FLGVAL CHAR(1),
    FLGPAC CHAR(1),
    FLGLIB CHAR(1),
    CODCFO VARCHAR(10),
    DSPRE2 VARCHAR(100),
    LIQRE2 NUMERIC(15, 4),
    BRTRE2 NUMERIC(15, 4),
    REFRE2 VARCHAR(40),
    QTFRE2 NUMERIC(15, 4),
    QTSRE2 NUMERIC(15, 4),
    TOTCST NUMERIC(15, 4),
    LUCROL NUMERIC(15, 4),
    LUCROP NUMERIC(15, 4),
    SLDRE2 NUMERIC(15, 4),
    VDSRE2 NUMERIC(15, 4),
    TOTDSC NUMERIC(15, 4),
    CONSTRAINT PK_PEDRE2
        PRIMARY KEY (CODEMP, DTERES, NUMRES, SEQRE2),

    CONSTRAINT FK_PEDRE2_PEDRES
        FOREIGN KEY (CODEMP, DTERES, NUMRES)
            REFERENCES PEDRES (CODEMP, DTERES, NUMRES),

    CONSTRAINT FK_PEDRE2_ESTPRO
        FOREIGN KEY (CODCLP, CODGRU, CODSUB, CODPRO)
            REFERENCES ESTPRO (CODCLP, CODGRU, CODSUB, CODPRO)
);

CREATE TABLE GEREMP (
                        CODEMP     INTEGER,
                        CEPEMP     CHAR(8),
                        ENDEMP     VARCHAR(40),
                        REFEMP     VARCHAR(20),
                        TENEMP     VARCHAR(10),
                        NUMEMP     VARCHAR(10),
                        BAIEMP     VARCHAR(20),
                        CIDEMP     VARCHAR(20),
                        SIGUFE     CHAR(2),
                        CGCEMP     VARCHAR(18),
                        INSEMP     VARCHAR(18),
                        PRTEMP     VARCHAR(4),
                        PRFEMP     VARCHAR(4),
                        EMAEMP     VARCHAR(50),
                        WEBEMP     VARCHAR(50),
                        QTDNFS     INTEGER DEFAULT 0,
                        NUMRES     INTEGER,
                        NUMPED     INTEGER,
                        FLSGER     CHAR(1),
                        FLGTRG     CHAR(1),
                        CODFIL     INTEGER,
                        LANEST     CHAR(3),
                        LANCAI     CHAR(3),
                        FLGPRE     CHAR(3),
                        EXIEST     CHAR(3),
                        QTDACP     INTEGER DEFAULT 0,
                        EXIEMP     CHAR(3),
                        FLGSER     CHAR(3),
                        FLGFIL     CHAR(3),
                        NFSLOJ     INTEGER DEFAULT 0,
                        NOMEMP     CHAR(60),
                        APEEMP     CHAR(30),
                        QTDNOT     INTEGER DEFAULT 0,
                        FONEMP     CHAR(30),
                        FAXEMP     CHAR(30),
                        NRONFS     INTEGER,
                        NRONOT     INTEGER,
                        QTDLIN     INTEGER DEFAULT 0,
                        ID_FINUFE  INTEGER,
                        ID_FINCIE  INTEGER,
                        FLGNFE     CHAR(3),
                        ID_FINPAI  INTEGER,
                        FLGSEL     CHAR(3),
                        IMUEMP     CHAR(18),
                        IGUALAR    SMALLINT,
                        TIPEMP     VARCHAR(30),
                        INSESTST   VARCHAR(20),
                        CONTATO    VARCHAR(28)
);

CREATE TABLE FINREGTRIB (
                            NUMREGTRIB  INTEGER,
                            NOMREGTRIB  VARCHAR(70)
);

CREATE TABLE FINVEN (
                        CODVEN     INTEGER NOT NULL ,
                        NOMVEN     VARCHAR(40),
                        APEVEN     VARCHAR(20),
                        CEPVEN     CHAR(8),
                        ENDVEN     VARCHAR(50),
                        TENVEN     VARCHAR(10),
                        NUMVEN     VARCHAR(10),
                        BAIVEN     VARCHAR(20),
                        CIDVEN     VARCHAR(20),
                        SIGUFE     CHAR(2),
                        CGCVEN     VARCHAR(18),
                        INSVEN     VARCHAR(18),
                        PRFVEN     VARCHAR(4),
                        FONVEN     VARCHAR(20),
                        CODTVE     INTEGER,
                        CODGVE     INTEGER,
                        CODCVE     INTEGER,
                        CODCOM     VARCHAR(3),
                        EMAVEN     VARCHAR(50),
                        WEBVEN     VARCHAR(50),
                        REFVEN     VARCHAR(40),
                        CODBAN     INTEGER,
                        CODAGB     VARCHAR(15),
                        CODCNB     VARCHAR(15),
                        NOMCNB     VARCHAR(50),
                        PFAVEN     VARCHAR(4),
                        FAXVEN     VARCHAR(20),
                        SEQREP     INTEGER DEFAULT 0,
                        FLGTRG     CHAR(1),
                        SLDVEN     NUMERIC(15,2) DEFAULT 0,
                        QTDREP     INTEGER DEFAULT 0,
                        METREP     NUMERIC(15,2) DEFAULT 0,
                        FLGATI     CHAR(3),
                        CODCLP     CHAR(1),
                        DCAVEN     DATE,
                        NOMRES     VARCHAR(40),
                        PRCVEN     CHAR(4),
                        CELVEN     CHAR(20),
                        ID_FINUFE  INTEGER,
                        ID_FINCIE  INTEGER,
                        ID_FINPAI  INTEGER
);

ALTER TABLE FINVEN ADD CONSTRAINT FINVEN_PK PRIMARY KEY (CODVEN);

CREATE TABLE FATPED (
    CODEMP INTEGER NOT NULL,
    DTERES DATE NOT NULL,
    NUMRES INTEGER NOT NULL,
    NRONFS VARCHAR(20),
    DTAFAT DATE,
    TOTFAT NUMERIC(15, 2)
);

CREATE TABLE FINCDE (
    CODCLI INTEGER NOT NULL,
    SEQCDE VARCHAR(20),
    DTECDE TIMESTAMP NOT NULL,
    DTEPED TIMESTAMP,
    VALCDE NUMERIC(15, 2),
    USACDE NUMERIC(15, 2),
    SLDCDE NUMERIC(15, 2),
    SITCDE VARCHAR(10)
);

CREATE TABLE FINPLA (
    CODPLA VARCHAR(20) NOT NULL,
    NOMPLA VARCHAR(100)
);

CREATE TABLE FINCOM (
    CODCOM VARCHAR(20) NOT NULL,
    PERCOM NUMERIC(15, 4)
);

CREATE TABLE FINTDO (
    CODTDO VARCHAR(20) NOT NULL,
    NOMTDO VARCHAR(100)
);

CREATE TABLE FINBAN (
    CODBAN VARCHAR(20) NOT NULL,
    NOMBAN VARCHAR(100)
);

CREATE TABLE FINCRE (
    CODEMP INTEGER NOT NULL,
    DTECRE DATE,
    NUMCRE NUMERIC(15, 0) NOT NULL,
    CODPLA VARCHAR(20),
    CODCOM VARCHAR(20),
    CODCLI INTEGER,
    CODVEN INTEGER,
    CODTDO VARCHAR(20),
    SENT_TO_WEBSERVICE SMALLINT
);

CREATE TABLE FINCRP (
    CODEMP INTEGER NOT NULL,
    DTECRE DATE,
    NUMCRE NUMERIC(15, 0) NOT NULL,
    NUMCRP INTEGER,
    FLGINC CHAR(1),
    DTEINC DATE,
    DTVCRP DATE,
    PRACRP INTEGER,
    VLPCRP NUMERIC(15, 2),
    NOSNUM VARCHAR(30),
    CODBAN VARCHAR(20),
    OBSCRP VARCHAR(1000),
    FLGANU CHAR(1),
    DTEANU DATE,
    FLPCRP CHAR(1)
);

CREATE TABLE PEDLIB (
    CODEMP INTEGER NOT NULL,
    DTERES DATE,
    NUMRES INTEGER NOT NULL,
    SEQLIB INTEGER NOT NULL,
    DTELIB DATE,
    HRELIB VARCHAR(10),
    CODCLI INTEGER,
    QTSLIB INTEGER,
    TOTLIB NUMERIC(15, 2),
    TOTGER NUMERIC(15, 2),
    SITLIB VARCHAR(10),
    CODVEN INTEGER,
    PCOLIB NUMERIC(15, 4),
    TOTCST NUMERIC(15, 2),
    DTEFAT DATE,
    SENT_TO_WEBSERVICE INTEGER
);

CREATE TABLE PEDLB2 (
    CODEMP INTEGER NOT NULL,
    DTERES DATE,
    NUMRES INTEGER NOT NULL,
    SEQLIB INTEGER NOT NULL,
    SEQLB2 INTEGER NOT NULL,
    CODCLP VARCHAR(10),
    CODGRU VARCHAR(10),
    CODSUB VARCHAR(10),
    CODPRO VARCHAR(10),
    DESLB2 VARCHAR(100),
    QTPLB2 NUMERIC(15, 4),
    QTSLB2 NUMERIC(15, 4),
    SLDLB2 NUMERIC(15, 4),
    TOTLB2 NUMERIC(15, 4),
    TOTGE2 NUMERIC(15, 4),
    DSCLB2 NUMERIC(15, 4),
    TOTCST NUMERIC(15, 4),
    PACLB2 NUMERIC(15, 4),
    VLULB2 NUMERIC(15, 4),
    VLQLB2 NUMERIC(15, 4),
    VCSLB2 NUMERIC(15, 4)
);

CREATE TABLE ESTICM (
    CODICM VARCHAR(20) NOT NULL,
    TIPICM VARCHAR(20) NOT NULL,
    NOMICM VARCHAR(100),
    UFEMITENTE VARCHAR(2),
    CODREGTRIB VARCHAR(20),
    PERICM NUMERIC(15, 4),
    REDICM NUMERIC(15, 4),
    BASICM NUMERIC(15, 4),
    CODST2 VARCHAR(20)
);

CREATE TABLE ESTIPI (
    FLGATIVO CHAR(1),
    CODIPI VARCHAR(20) NOT NULL,
    TIPIPI VARCHAR(20) NOT NULL,
    NOMIPI VARCHAR(100),
    CLSIPI VARCHAR(20),
    COD_ENQ VARCHAR(20),
    CSTIPI VARCHAR(20),
    PERIPI NUMERIC(15, 4),
    BASIPI NUMERIC(15, 4),
    FLG_SINEIF20 CHAR(1),
    CODTXF VARCHAR(20),
    CSTPIS VARCHAR(20),
    ALIQ_PIS NUMERIC(15, 4),
    FLG_DESC_ZF_PIS CHAR(1),
    CSTCOF VARCHAR(20),
    ALIQ_COF NUMERIC(15, 4),
    FLG_DESC_ZF_COF CHAR(1)
);

CREATE TABLE ESTSIP (
    SIGNFE VARCHAR(20),
    TIPSIP VARCHAR(20),
    NOMSIP VARCHAR(100)
);

CREATE TABLE ESTPIS (
    SIGNFE VARCHAR(20),
    NOMPIS VARCHAR(100)
);

CREATE TABLE ESTCOF (
    SIGNFE VARCHAR(20),
    NOMCOF VARCHAR(100)
);

CREATE TABLE ESTITE (
    CODCLP INTEGER,
    CODGRU INTEGER,
    CODSUB INTEGER,
    CODPRO INTEGER,
    CODEMP INTEGER,
    VB1ITE NUMERIC(15, 4),
    VB2ITE NUMERIC(15, 4),
    VB3ITE NUMERIC(15, 4),
    VB4ITE NUMERIC(15, 4),
    VB5ITE NUMERIC(15, 4),
    VPFITE NUMERIC(15, 4),
    VPRITE NUMERIC(15, 4),
    MK1ITE NUMERIC(15, 4)
);

CREATE TABLE ESTQTE (
    CODCLP INTEGER,
    CODGRU INTEGER,
    CODSUB INTEGER,
    CODPRO INTEGER,
    CODEMP INTEGER,
    QTDQTE NUMERIC(15, 4),
    QTMQTE NUMERIC(15, 4),
    QMAQTE NUMERIC(15, 4),
    QTRQTE NUMERIC(15, 4),
    QTAQTE NUMERIC(15, 4),
    QTDRMA NUMERIC(15, 4)
);

SET TERM ^ ;
CREATE PROCEDURE RESERVAS (
    P_CODEMP INTEGER,
    P_CODCLP INTEGER,
    P_CODGRU INTEGER,
    P_CODSUB INTEGER,
    P_CODPRO INTEGER
)
RETURNS (
    QTD_RESERVADA NUMERIC(15, 4)
)
AS
BEGIN
    QTD_RESERVADA = 0;
    SUSPEND;
END^
SET TERM ; ^

CREATE TABLE PEDCTA (
    CODEMP INTEGER NOT NULL,
    DTERES DATE NOT NULL,
    NUMCTA INTEGER,
    NUMRES INTEGER NOT NULL,
    CODCLI INTEGER NOT NULL,
    SITCTA VARCHAR(10),
    TOTGER NUMERIC(15, 2),
    TOTITE NUMERIC(15, 2),
    TOTIPI NUMERIC(15, 2),
    TOTICM NUMERIC(15, 2),
    TOTPIS NUMERIC(15, 2),
    TOTCOF NUMERIC(15, 2),
    TOTSUB NUMERIC(15, 2),
    TOTDESCINC NUMERIC(15, 2),
    TOTFRT NUMERIC(15, 2),
    TOTSEG NUMERIC(15, 2),
    TOTOUTDESP NUMERIC(15, 2),
    CODVEN INTEGER,
    CODATD INTEGER,
    REGTRB INTEGER,
    CODPFA VARCHAR(20),
    SENT_TO_WEBSERVICE SMALLINT,
    CONSTRAINT PK_PEDCTA PRIMARY KEY (CODEMP, DTERES, NUMRES),
    CONSTRAINT FK_PEDCTA_FINCLI FOREIGN KEY (CODCLI) REFERENCES FINCLI (CODCLI)
);

CREATE TABLE PEDTIP (
    CODTIP INTEGER NOT NULL PRIMARY KEY,
    NOMTIP VARCHAR(100)
);

CREATE TABLE ESTST1 (
    CODST1  CHAR(1) NOT NULL /* CHAR_01 = CHAR(1) */,
    NOMST1  VARCHAR(250) /* DNOMST1 = VARCHAR(250) */,
    SNEF20  CHAR(1) /* CHAR_01 = CHAR(1) */
);
ALTER TABLE ESTST1 ADD CONSTRAINT ESTST1_PK PRIMARY KEY (CODST1);

-- Firebird must commit schema metadata before the seed insert can see FINCLI.
COMMIT RETAIN;

INSERT INTO FINCLI (CODCLI, NOMCLI, APECLI, INSCLI, CGCCLI)
VALUES (123, 'CONSTRUTORA CATALDO E CIA LTDA', 'CONSTR CATALDO', '110.773.918.110', '43809227000195');

--INSERT INTO FINVEN (CODVEN, NOMVEN, APEVEN) VALUES (101, 'ADILSON', 'ADILSON');
