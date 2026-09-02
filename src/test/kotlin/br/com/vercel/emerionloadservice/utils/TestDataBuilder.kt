package br.com.vercel.emerionloadservice.utils

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime

@Component
class TestDataBuilder(
    private val jdbcTemplate: JdbcTemplate,
) {
    fun clearRepositoryData() {
        listOf(
            "PEDRE2",
            "PEDRES",
            "PEDLB2",
            "PEDLIB",
            "FATPED",
            "FINCRP",
            "FINCRE",
            "FINCDE",
            "ESTITE",
            "ESTQTE",
            "ESTPRO",
            "ESTICM",
            "ESTIPI",
            "ESTSIP",
            "ESTPIS",
            "ESTCOF",
            "FINPLA",
            "FINCOM",
            "FINTDO",
            "FINBAN",
            "FINREGTRIB",
            "FINVEN",
            "FINCLI",
            "GEREMP",
        ).forEach { table ->
            jdbcTemplate.update("DELETE FROM $table")
        }
    }

    fun clearVendedores() {
        jdbcTemplate.update("DELETE FROM FINVEN")
    }

    fun insertVendedor(
        codVen: Long,
        nome: String,
    ) {
        jdbcTemplate.update(
            "INSERT INTO FINVEN (CODVEN, NOMVEN) VALUES (?, ?)",
            codVen,
            nome,
        )
    }

    fun insertCompany(
        codEmp: Int = 1,
        nome: String = "Empresa $codEmp",
    ) {
        jdbcTemplate.update(
            "INSERT INTO GEREMP (CODEMP, NOMEMP, CGCEMP) VALUES (?, ?, ?)",
            codEmp,
            nome,
            "123456780001$codEmp",
        )
    }

    fun insertRegimeTributario(
        codigo: Int,
        nome: String,
    ) {
        jdbcTemplate.update(
            "INSERT INTO FINREGTRIB (NUMREGTRIB, NOMREGTRIB) VALUES (?, ?)",
            codigo,
            nome,
        )
    }

    fun insertCustomer(
        codCli: Long,
        nome: String = "Cliente $codCli",
        cpfCnpj: String = "123456780001$codCli",
        codVen: Long? = null,
        regimeTributario: Int? = null,
        bloqueado: Boolean = false,
    ) {
        jdbcTemplate.update(
            """
            INSERT INTO FINCLI (
                CODCLI, NOMCLI, APECLI, CGCCLI, INSCLI, FLBCLI, DTNCLI, DCACLI, DTEATU,
                EM1CLI, EM2CLI, WEBCLI, LIMCLI, OBSCLI, CNAE, CODVEN, REGTRB, CODTCL,
                CODGCL, CODCCL, UFFCLI, CODMCR, CODMRG, CODSET, CEFCLI, TEFCLI, ENFCLI,
                NRFCLI, RFFCLI, BAFCLI, CIFCLI, PT1CLI, FO1CLI, PF1CLI, FA1CLI, COFCLI,
                PC1CLI, FC1CLI, CECCLI, TECCLI, ENCCLI, NRCCLI, RFCCLI, BACCLI, CICCLI,
                UFCCLI, PT2CLI, FO2CLI, PF2CLI, FA2CLI, COCCLI, PC2CLI, FC2CLI, CEECLI,
                TEECLI, ENECLI, NRECLI, RFECLI, BAECLI, CIECLI, UFECLI, PT4CLI, FO4CLI,
                PF4CLI, FA4CLI, COECLI, PC4CLI, FC4CLI
            ) VALUES (
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
            )
            """.trimIndent(),
            codCli,
            nome,
            "Fantasia $codCli",
            cpfCnpj,
            "IE-$codCli",
            if (bloqueado) "*" else " ",
            Date.valueOf(LocalDate.of(1980, 1, 2)),
            Date.valueOf(LocalDate.of(2020, 3, 4)),
            Date.valueOf(LocalDate.of(2024, 5, 6)),
            "principal$codCli@example.com",
            "financeiro$codCli@example.com",
            "https://cliente$codCli.example.com",
            BigDecimal("1234.56"),
            "Observação $codCli",
            "1234567",
            codVen,
            regimeTributario,
            10,
            20,
            30,
            "SP",
            40,
            50,
            60,
            "01001000",
            "Rua",
            "Faturamento $codCli",
            "10",
            "Sala 1",
            "Centro",
            "São Paulo",
            "11",
            "11111111",
            "11",
            "22222222",
            "Contato faturamento",
            "11",
            "999999999",
            "02002000",
            "Avenida",
            "Cobrança $codCli",
            "20",
            "Sala 2",
            "Bairro cobrança",
            "Rio de Janeiro",
            "RJ",
            "21",
            "33333333",
            "21",
            "44444444",
            "Contato cobrança",
            "21",
            "988888888",
            "03003000",
            "Travessa",
            "Entrega $codCli",
            "30",
            "Casa",
            "Bairro entrega",
            "Curitiba",
            "PR",
            "41",
            "55555555",
            "41",
            "66666666",
            "Contato entrega",
            "41",
            "977777777",
        )
    }

    fun insertOrder(
        codEmp: Int,
        dataPedido: LocalDate,
        numeroPedido: Int,
        codCli: Long,
        vendedorExternalId: Long? = 99,
    ) {
        jdbcTemplate.update(
            """
            INSERT INTO PEDRES (
                CODEMP, DTERES, NUMRES, CODCLI, SITRES, TOTGER, TOTRES, TOTIPI, TOTICM,
                TOTPIS, TOTCOF, TOTSUB, TOTDESCINC, TOTFRT, TOTSEG, TOTOUTDESP, CODVEN,
                CODATD, DTFRES, CODTRA, PEDANT, REGTRB, CODPFA
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            codEmp,
            Date.valueOf(dataPedido),
            numeroPedido,
            codCli,
            "ABERTO",
            BigDecimal("120.00"),
            BigDecimal("100.00"),
            BigDecimal("5.00"),
            BigDecimal("12.00"),
            BigDecimal("1.65"),
            BigDecimal("7.60"),
            BigDecimal("3.00"),
            BigDecimal("2.00"),
            BigDecimal("4.00"),
            BigDecimal("1.00"),
            BigDecimal("0.50"),
            vendedorExternalId,
            7,
            Date.valueOf(dataPedido.plusDays(5)),
            "TRANS-1",
            "ANT-1",
            1,
            "PFA-1",
        )
    }

    fun insertOrderItem(
        codEmp: Int,
        dataPedido: LocalDate,
        numeroPedido: Int,
        sequencia: Int,
        codClp: Int = 1,
        codGru: Int = 10,
        codSub: Int = 20,
        codPro: Int = 30,
    ) {
        jdbcTemplate.update(
            """
            INSERT INTO PEDRE2 (
                CODEMP, DTERES, NUMRES, SEQRE2, CODCLP, CODGRU, CODSUB, CODPRO, DESRE2,
                QTPRE2, VLQRE2, TOTRE2, CODST1, CODUND, ICMRE2, BASICM, TOTICM, IPIRE2,
                BASIPI, TOTIPI, BASPIS, ALIQPIS, TOTPIS, BASCOF, ALIQCOF, TOTCOF, TOTDSR,
                TOTFRT, TOTSEG, TOTOUTDESP, TOTITETRB, TOTREN, TOTGE2, OBSRE2,
                NUMPEDCOMPRA, NUMITEMCOMPRA, NRORE2, FLGVAL, FLGPAC, FLGLIB, CODCFO,
                DSPRE2, LIQRE2, BRTRE2, REFRE2, QTFRE2, QTSRE2, TOTCST, LUCROL, LUCROP,
                SLDRE2, VDSRE2, TOTDSC
            ) VALUES (
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?
            )
            """.trimIndent(),
            codEmp,
            Date.valueOf(dataPedido),
            numeroPedido,
            sequencia,
            codClp,
            codGru,
            codSub,
            codPro,
            "Item $sequencia",
            BigDecimal("2.00"),
            BigDecimal("50.00"),
            BigDecimal("100.00"),
            "00",
            "UN",
            BigDecimal("12.00"),
            BigDecimal("100.00"),
            BigDecimal("12.00"),
            BigDecimal("5.00"),
            BigDecimal("100.00"),
            BigDecimal("5.00"),
            BigDecimal("100.00"),
            BigDecimal("1.65"),
            BigDecimal("1.65"),
            BigDecimal("100.00"),
            BigDecimal("7.60"),
            BigDecimal("7.60"),
            BigDecimal("2.00"),
            BigDecimal("4.00"),
            BigDecimal("1.00"),
            BigDecimal("0.50"),
            BigDecimal("120.00"),
            BigDecimal("0.00"),
            BigDecimal("120.00"),
            "Observação item",
            "COMPRA-1",
            8,
            9,
            "S",
            "N",
            "S",
            "5102",
            "Item NFe",
            BigDecimal("1.50"),
            BigDecimal("2.00"),
            "REF-1",
            BigDecimal("2.00"),
            BigDecimal("1.00"),
            BigDecimal("70.00"),
            BigDecimal("30.00"),
            BigDecimal("25.00"),
            BigDecimal("0.00"),
            BigDecimal("1.00"),
            BigDecimal("2.00"),
        )
    }

    fun insertInvoice(
        codEmp: Int,
        dataPedido: LocalDate,
        numeroPedido: Int,
        numeroNota: String,
    ) {
        jdbcTemplate.update(
            "INSERT INTO FATPED (CODEMP, DTERES, NUMRES, NRONFS, DTAFAT, TOTFAT) VALUES (?, ?, ?, ?, ?, ?)",
            codEmp,
            Date.valueOf(dataPedido),
            numeroPedido,
            numeroNota,
            Date.valueOf(dataPedido.plusDays(1)),
            BigDecimal("120.50"),
        )
    }

    fun insertCreditMovement(
        codCli: Long,
        sequencia: String,
        dataLancamento: LocalDateTime = LocalDateTime.of(2024, 1, 2, 3, 4, 5),
        valorUtilizado: BigDecimal = BigDecimal("25.00"),
    ) {
        jdbcTemplate.update(
            """
            INSERT INTO FINCDE (CODCLI, SEQCDE, DTECDE, DTEPED, VALCDE, USACDE, SLDCDE, SITCDE)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            codCli,
            sequencia,
            Timestamp.valueOf(dataLancamento),
            Timestamp.valueOf(dataLancamento.minusDays(1)),
            BigDecimal("100.00"),
            valorUtilizado,
            BigDecimal("75.00"),
            "ABERTO",
        )
    }

    fun insertFinCre(
        codEmp: Int,
        dataEmissao: LocalDate,
        documento: Long,
        codCli: Long,
        codVen: Long,
    ) {
        jdbcTemplate.update(
            """
            INSERT INTO FINCRE (CODEMP, DTECRE, NUMCRE, CODPLA, CODCOM, CODCLI, CODVEN, CODTDO)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            codEmp,
            Date.valueOf(dataEmissao),
            documento,
            "30D",
            "COM-1",
            codCli,
            codVen,
            "DUP",
        )
    }

    fun insertFinCreReferences(codEmp: Int) {
        insertCompany(codEmp)
        jdbcTemplate.update("INSERT INTO FINPLA (CODPLA, NOMPLA) VALUES (?, ?)", "30D", "30 dias")
        jdbcTemplate.update("INSERT INTO FINCOM (CODCOM, PERCOM) VALUES (?, ?)", "COM-1", BigDecimal("4.50"))
        jdbcTemplate.update("INSERT INTO FINTDO (CODTDO, NOMTDO) VALUES (?, ?)", "DUP", "Duplicata")
        jdbcTemplate.update("INSERT INTO FINBAN (CODBAN, NOMBAN) VALUES (?, ?)", "001", "Banco Um")
    }

    fun insertFinCreInstallment(
        codEmp: Int,
        dataEmissao: LocalDate,
        documento: Long,
        numeroParcela: Int,
    ) {
        jdbcTemplate.update(
            """
            INSERT INTO FINCRP (
                CODEMP, DTECRE, NUMCRE, NUMCRP, FLGINC, DTEINC, DTVCRP, PRACRP, VLPCRP,
                NOSNUM, CODBAN, OBSCRP, FLGANU, DTEANU, FLPCRP
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            codEmp,
            Date.valueOf(dataEmissao),
            documento,
            numeroParcela,
            "N",
            null,
            Date.valueOf(dataEmissao.plusDays(30)),
            30,
            BigDecimal("150.00"),
            "NOSSO-1",
            "001",
            "Parcela $numeroParcela",
            "S",
            Date.valueOf(dataEmissao.plusDays(2)),
            "N",
        )
    }

    fun insertPedlib(
        codEmp: Int,
        dataPedido: LocalDate,
        numeroPedido: Int,
        numeroLiberacao: Int,
        codCli: Long,
    ) {
        jdbcTemplate.update(
            """
            INSERT INTO PEDLIB (
                CODEMP, DTERES, NUMRES, SEQLIB, DTELIB, HRELIB, CODCLI, QTSLIB, TOTLIB,
                TOTGER, SITLIB, CODVEN, PCOLIB, TOTCST
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            codEmp,
            Date.valueOf(dataPedido),
            numeroPedido,
            numeroLiberacao,
            Date.valueOf(dataPedido.plusDays(1)),
            "10:30:00",
            codCli,
            2,
            BigDecimal("100.00"),
            BigDecimal("120.00"),
            "LIBERADO",
            99,
            BigDecimal("4.50"),
            BigDecimal("70.00"),
        )
    }

    fun insertPedlibDetail(
        codEmp: Int,
        dataPedido: LocalDate,
        numeroPedido: Int,
        numeroLiberacao: Int,
        sequencia: Int,
    ) {
        jdbcTemplate.update(
            """
            INSERT INTO PEDLB2 (
                CODEMP, DTERES, NUMRES, SEQLIB, SEQLB2, CODCLP, CODGRU, CODSUB, CODPRO,
                DESLB2, QTPLB2, QTSLB2, SLDLB2, TOTLB2, TOTGE2, DSCLB2, TOTCST, PACLB2,
                VLULB2, VLQLB2, VCSLB2
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            codEmp,
            Date.valueOf(dataPedido),
            numeroPedido,
            numeroLiberacao,
            sequencia,
            "1",
            "10",
            "20",
            "30",
            "Detalhe $sequencia",
            BigDecimal("3.00"),
            BigDecimal("2.00"),
            BigDecimal("1.00"),
            BigDecimal("100.00"),
            BigDecimal("120.00"),
            BigDecimal("5.00"),
            BigDecimal("70.00"),
            BigDecimal("2.00"),
            BigDecimal("50.00"),
            BigDecimal("49.00"),
            BigDecimal("35.00"),
        )
    }

    fun insertIcms(
        codigo: String,
        tipo: String,
    ) {
        jdbcTemplate.update(
            """
            INSERT INTO ESTICM (
                CODICM, TIPICM, NOMICM, UFEMITENTE, CODREGTRIB, PERICM, REDICM, BASICM, CODST2
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            codigo,
            tipo,
            "ICMS $codigo",
            "SP",
            "REG-1",
            BigDecimal("18.00"),
            BigDecimal("10.00"),
            BigDecimal("90.00"),
            "102",
        )
    }

    fun insertIpiReferences(vararg tipos: String) {
        tipos.distinct().forEach { tipo ->
            jdbcTemplate.update("INSERT INTO ESTSIP (SIGNFE, TIPSIP, NOMSIP) VALUES (?, ?, ?)", "50", tipo, "IPI tributado")
        }
        jdbcTemplate.update("INSERT INTO ESTPIS (SIGNFE, NOMPIS) VALUES (?, ?)", "01", "PIS tributado")
        jdbcTemplate.update("INSERT INTO ESTCOF (SIGNFE, NOMCOF) VALUES (?, ?)", "01", "COFINS tributado")
    }

    fun insertIpi(
        codigo: String,
        tipo: String,
    ) {
        jdbcTemplate.update(
            """
            INSERT INTO ESTIPI (
                FLGATIVO, CODIPI, TIPIPI, NOMIPI, CLSIPI, COD_ENQ, CSTIPI, PERIPI, BASIPI,
                FLG_SINEIF20, CODTXF, CSTPIS, ALIQ_PIS, FLG_DESC_ZF_PIS, CSTCOF, ALIQ_COF,
                FLG_DESC_ZF_COF
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            "S",
            codigo,
            tipo,
            "IPI $codigo",
            "1234.56.78",
            "999",
            "50",
            BigDecimal("5.00"),
            BigDecimal("100.00"),
            "N",
            "TXT-1",
            "01",
            BigDecimal("1.65"),
            "N",
            "01",
            BigDecimal("7.60"),
            "S",
        )
    }

    fun insertProduct(
        codClp: Int,
        codGru: Int,
        codSub: Int,
        codPro: Int,
        nome: String = "Produto $codPro",
    ) {
        jdbcTemplate.update(
            """
            INSERT INTO ESTPRO (
                CODCLP, CODGRU, CODSUB, CODPRO, DSCPRO, DSRPRO, REFPRO, CODNCM, CEST, CODST1,
                CODCAT, CODTIP, CODMRC, CODUNE, CODUNS, PESLIQ, PESBRT, FLBPRO, CBAPRO, CODBAR,
                IDEPRO, SIMPRO, QTDVOL, QTDEMB, LOCPRO, PESCUB, CBAEMB, IBSCBS_C_CLASS_TRIB,
                IBSCBS_CST, COD_FCP_ENTRADA, COD_FCP_SAIDA, IPISAI, IPIENT, ICMSAI, ICMENT,
                CODSTS, CODSTE, OBSPRO
            ) VALUES (
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
            )
            """.trimIndent(),
            codClp,
            codGru,
            codSub,
            codPro,
            nome,
            "Descrição reduzida",
            "REF-$codPro",
            "12345678",
            "1234567",
            "0",
            "CAT",
            "TIP",
            "MARCA",
            "CX",
            "UN",
            BigDecimal("1.50"),
            BigDecimal("2.00"),
            "D",
            "789000000001",
            "789000000002",
            BigDecimal("5.00"),
            "SIMILAR",
            BigDecimal("2.00"),
            BigDecimal("3.00"),
            "A-01",
            BigDecimal("4.00"),
            "789000000003",
            "000001",
            "010",
            "FCP-E",
            "FCP-S",
            "IPI-S",
            "IPI-E",
            "ICMS-S",
            "ICMS-E",
            "ST-S",
            "ST-E",
            "Produto de teste",
        )
        jdbcTemplate.update(
            """
            INSERT INTO ESTITE (
                CODCLP, CODGRU, CODSUB, CODPRO, CODEMP, VB1ITE, VB2ITE, VB3ITE, VB4ITE, VB5ITE
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            codClp,
            codGru,
            codSub,
            codPro,
            1,
            BigDecimal("10.00"),
            BigDecimal("11.00"),
            BigDecimal("12.00"),
            BigDecimal("13.00"),
            BigDecimal("14.00"),
        )
        jdbcTemplate.update(
            """
            INSERT INTO ESTQTE (
                CODCLP, CODGRU, CODSUB, CODPRO, CODEMP, QTDQTE, QTMQTE, QMAQTE, QTRQTE, QTAQTE,
                QTDRMA
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """.trimIndent(),
            codClp,
            codGru,
            codSub,
            codPro,
            1,
            BigDecimal("10.00"),
            BigDecimal("2.00"),
            BigDecimal("20.00"),
            BigDecimal("1.00"),
            BigDecimal("15.00"),
            BigDecimal("2.00"),
        )
    }
}
