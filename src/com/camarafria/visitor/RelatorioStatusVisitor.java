package com.camarafria.visitor;

import com.camarafria.hardware.Compressor;
import com.camarafria.hardware.Sensor;
import com.camarafria.hardware.Valvula;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Visitor Concreto — Gerador de Relatório de Status dos componentes.
 *
 * <p>Percorre todos os componentes de hardware da câmara fria, coletando
 * dados operacionais e gerando um relatório consolidado em formato textual.</p>
 *
 * @author Gustavo
 * @version 1.0
 */
public class RelatorioStatusVisitor implements ComponenteVisitor {

    private static final String SEP = "─".repeat(60);
    private static final String SEP2 = "═".repeat(60);
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final StringBuilder rel;
    private int total;
    private int alertas;

    public RelatorioStatusVisitor() {
        this.rel = new StringBuilder();
        this.total = 0;
        this.alertas = 0;
        rel.append("\n").append(SEP2).append("\n");
        rel.append("  RELATORIO DE STATUS — CAMARA FRIA INTELIGENTE\n");
        rel.append("  Data/Hora: ").append(LocalDateTime.now().format(FMT)).append("\n");
        rel.append(SEP2).append("\n");
    }

    @Override
    public void visitar(Sensor sensor) {
        total++;
        boolean ok = sensor.isDentroDoLimite();
        if (!ok) alertas++;
        rel.append("\n").append(SEP).append("\n");
        rel.append(String.format("  SENSOR DE TEMPERATURA — %s\n", sensor.getId()));
        rel.append(SEP).append("\n");
        rel.append(String.format("  Localizacao ....... : %s\n", sensor.getLocalizacao()));
        rel.append(String.format("  Tipo .............. : %s\n", sensor.getTipo()));
        rel.append(String.format("  Leitura Atual ..... : %.2f C\n", sensor.getLeituraAtual()));
        rel.append(String.format("  Limite Operacional  : %.2f C\n", sensor.getLimiteOperacional()));
        rel.append(String.format("  Precisao .......... : +/-%.2f C\n", sensor.getPrecisao()));
        rel.append(String.format("  Desvio do Limite .. : %.1f%%\n", sensor.getDesvioPorcentagem()));
        rel.append(String.format("  Status ............ : %s\n", ok ? "DENTRO DO LIMITE" : "FORA DO LIMITE"));
    }

    @Override
    public void visitar(Compressor compressor) {
        total++;
        boolean manut = compressor.necessitaManutencao(8000);
        if (manut) alertas++;
        rel.append("\n").append(SEP).append("\n");
        rel.append(String.format("  COMPRESSOR — %s\n", compressor.getId()));
        rel.append(SEP).append("\n");
        rel.append(String.format("  Localizacao ....... : %s\n", compressor.getLocalizacao()));
        rel.append(String.format("  Horas Ligado ...... : %.0f h\n", compressor.getHorasLigado()));
        rel.append(String.format("  Potencia Nominal .. : %.1f kW\n", compressor.getPotenciaNominalKW()));
        rel.append(String.format("  Consumo Atual ..... : %.1f kWh\n", compressor.getConsumoAtualKWh()));
        rel.append(String.format("  COP (Eficiencia) .. : %.2f\n", compressor.getCop()));
        rel.append(String.format("  Ciclos Liga/Desl. . : %d\n", compressor.getCiclosLigaDesliga()));
        rel.append(String.format("  Eficiencia Real ... : %.1f%% (ref. COP ideal 3.5)\n",
                compressor.getEficienciaPercentual(3.5)));
        rel.append(String.format("  Manutencao ........ : %s\n", manut ? "MANUTENCAO NECESSARIA" : "OK"));
    }

    @Override
    public void visitar(Valvula valvula) {
        total++;
        boolean desgaste = valvula.apresentaDesgaste(50000);
        boolean defeito = !valvula.isOperacional();
        if (desgaste || defeito) alertas++;
        rel.append("\n").append(SEP).append("\n");
        rel.append(String.format("  VALVULA DE EXPANSAO — %s\n", valvula.getId()));
        rel.append(SEP).append("\n");
        rel.append(String.format("  Localizacao ....... : %s\n", valvula.getLocalizacao()));
        rel.append(String.format("  Abertura .......... : %.1f%%\n", valvula.getAberturaPercentual()));
        rel.append(String.format("  Estado ............ : %s\n", valvula.getEstado().getDescricao()));
        rel.append(String.format("  Ciclos de Operacao  : %d\n", valvula.getCiclosOperacao()));
        rel.append(String.format("  Fluido Refrigerante : %s\n", valvula.getTipoFluido()));
        rel.append(String.format("  Pressao Entrada ... : %.1f bar\n", valvula.getPressaoEntradaBar()));
        rel.append(String.format("  Operacional ....... : %s\n",
                defeito ? "DEFEITO DETECTADO" : (desgaste ? "DESGASTE — TROCA RECOMENDADA" : "OPERACIONAL")));
    }

    /**
     * Gera o relatorio final consolidado com rodape de resumo.
     * @return relatorio completo formatado como String
     */
    public String gerarRelatorio() {
        rel.append("\n").append(SEP2).append("\n");
        rel.append("  RESUMO CONSOLIDADO\n");
        rel.append(SEP2).append("\n");
        rel.append(String.format("  Total de Componentes Analisados .. : %d\n", total));
        rel.append(String.format("  Componentes com Alerta .......... : %d\n", alertas));
        rel.append(String.format("  Componentes OK .................. : %d\n", total - alertas));
        rel.append(SEP2).append("\n");
        return rel.toString();
    }
}
