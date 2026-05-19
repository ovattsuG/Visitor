package com.camarafria.visitor;

import com.camarafria.hardware.Compressor;
import com.camarafria.hardware.Sensor;
import com.camarafria.hardware.Valvula;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitor Concreto — Analisador de Otimizacao Energetica.
 *
 * <p>Analisa cada componente da camara fria sob a perspectiva de eficiencia
 * energetica, sugerindo acoes de manutencao preventiva, ajustes operacionais
 * e melhorias para reducao de consumo.</p>
 *
 * @author Gustavo
 * @version 1.0
 */
public class OtimizacaoEnergiaVisitor implements ComponenteVisitor {

    // Limites configuráveis para análise
    private static final double HORAS_MANUTENCAO_PREVENTIVA = 8000.0;
    private static final double HORAS_MANUTENCAO_CRITICA = 12000.0;
    private static final double COP_MINIMO_ACEITAVEL = 2.5;
    private static final int CICLOS_COMPRESSOR_EXCESSIVOS = 5000;
    private static final int CICLOS_VALVULA_DESGASTE = 50000;
    private static final double DESVIO_TEMPERATURA_CRITICO = 15.0; // %

    private final List<String> sugestoes;
    private final StringBuilder analise;
    private double potencialEconomiaKWh;

    public OtimizacaoEnergiaVisitor() {
        this.sugestoes = new ArrayList<>();
        this.analise = new StringBuilder();
        this.potencialEconomiaKWh = 0;

        analise.append("\n");
        analise.append("=".repeat(60)).append("\n");
        analise.append("  ANALISE DE OTIMIZACAO ENERGETICA — CAMARA FRIA\n");
        analise.append("=".repeat(60)).append("\n");
    }

    @Override
    public void visitar(Sensor sensor) {
        analise.append("\n").append("-".repeat(60)).append("\n");
        analise.append(String.format("  [SENSOR] %s — %s\n", sensor.getId(), sensor.getLocalizacao()));
        analise.append("-".repeat(60)).append("\n");

        double desvio = Math.abs(sensor.getDesvioPorcentagem());

        if (!sensor.isDentroDoLimite()) {
            String msg = String.format(
                    "[CRITICO] Sensor %s: Leitura %.2fC ACIMA do limite %.2fC. "
                    + "O compressor esta compensando o desvio, gerando consumo excessivo.",
                    sensor.getId(), sensor.getLeituraAtual(), sensor.getLimiteOperacional());
            sugestoes.add(msg);
            analise.append("  -> ALERTA: Temperatura fora do limite operacional!\n");
            analise.append(String.format("     Desvio: %.1f%% acima do limite\n", desvio));
            analise.append("     Impacto: Compressor operando em sobrecarga para compensar.\n");
            analise.append("     Acao: Verificar isolamento termico e vedacao da zona.\n");
            potencialEconomiaKWh += 15.0;
        } else if (desvio > DESVIO_TEMPERATURA_CRITICO) {
            String msg = String.format(
                    "[ATENCAO] Sensor %s: Leitura proximo ao limite (desvio %.1f%%). "
                    + "Recomendar recalibracao do sensor tipo %s.",
                    sensor.getId(), desvio, sensor.getTipo());
            sugestoes.add(msg);
            analise.append("  -> ATENCAO: Leitura proxima do limite operacional.\n");
            analise.append("     Acao: Agendar recalibracao do sensor.\n");
            potencialEconomiaKWh += 5.0;
        } else {
            analise.append("  -> OK: Leitura dentro dos parametros ideais.\n");
            analise.append(String.format("     Margem de seguranca: %.1f%%\n", 100.0 - desvio));
        }
    }

    @Override
    public void visitar(Compressor compressor) {
        analise.append("\n").append("-".repeat(60)).append("\n");
        analise.append(String.format("  [COMPRESSOR] %s — %s\n",
                compressor.getId(), compressor.getLocalizacao()));
        analise.append("-".repeat(60)).append("\n");

        double horas = compressor.getHorasLigado();
        double cop = compressor.getCop();
        double eficiencia = compressor.getEficienciaPercentual(3.5);
        int ciclos = compressor.getCiclosLigaDesliga();

        // Analise de horas de operacao
        if (horas >= HORAS_MANUTENCAO_CRITICA) {
            String msg = String.format(
                    "[CRITICO] Compressor %s: %.0fh de operacao (limite critico: %.0fh). "
                    + "Manutencao corretiva URGENTE para evitar falha catastrofica.",
                    compressor.getId(), horas, HORAS_MANUTENCAO_CRITICA);
            sugestoes.add(msg);
            analise.append(String.format("  -> CRITICO: %.0fh de operacao — manutencao urgente!\n", horas));
            potencialEconomiaKWh += 50.0;
        } else if (horas >= HORAS_MANUTENCAO_PREVENTIVA) {
            String msg = String.format(
                    "[ATENCAO] Compressor %s: %.0fh de operacao. "
                    + "Agendar manutencao preventiva (troca de oleo, filtros e verificacao de valvulas).",
                    compressor.getId(), horas);
            sugestoes.add(msg);
            analise.append(String.format("  -> ATENCAO: %.0fh de operacao — manutencao preventiva recomendada.\n", horas));
            potencialEconomiaKWh += 20.0;
        } else {
            analise.append(String.format("  -> OK: %.0fh de operacao (dentro do intervalo seguro).\n", horas));
        }

        // Analise de eficiencia (COP)
        if (cop < COP_MINIMO_ACEITAVEL) {
            String msg = String.format(
                    "[CRITICO] Compressor %s: COP %.2f abaixo do minimo (%.1f). "
                    + "Eficiencia real: %.1f%%. Avaliar troca do equipamento.",
                    compressor.getId(), cop, COP_MINIMO_ACEITAVEL, eficiencia);
            sugestoes.add(msg);
            analise.append(String.format("  -> CRITICO: COP %.2f — eficiencia muito baixa (%.1f%%).\n", cop, eficiencia));
            analise.append("     Acao: Avaliar substituicao ou recondicionamento.\n");
            potencialEconomiaKWh += 40.0;
        } else {
            analise.append(String.format("  -> COP: %.2f — eficiencia: %.1f%% (aceitavel).\n", cop, eficiencia));
        }

        // Analise de ciclos
        if (ciclos > CICLOS_COMPRESSOR_EXCESSIVOS) {
            String msg = String.format(
                    "[ATENCAO] Compressor %s: %d ciclos liga/desliga (excessivo). "
                    + "Considerar ajuste do diferencial de temperatura para reduzir ciclagem.",
                    compressor.getId(), ciclos);
            sugestoes.add(msg);
            analise.append(String.format("  -> ATENCAO: %d ciclos — ciclagem excessiva.\n", ciclos));
            potencialEconomiaKWh += 10.0;
        }
    }

    @Override
    public void visitar(Valvula valvula) {
        analise.append("\n").append("-".repeat(60)).append("\n");
        analise.append(String.format("  [VALVULA] %s — %s\n",
                valvula.getId(), valvula.getLocalizacao()));
        analise.append("-".repeat(60)).append("\n");

        if (!valvula.isOperacional()) {
            String msg = String.format(
                    "[CRITICO] Valvula %s: DEFEITO detectado. "
                    + "Fluido %s com vazao comprometida. Substituicao imediata.",
                    valvula.getId(), valvula.getTipoFluido());
            sugestoes.add(msg);
            analise.append("  -> CRITICO: Valvula com defeito — substituicao imediata!\n");
            potencialEconomiaKWh += 30.0;
            return;
        }

        if (valvula.apresentaDesgaste(CICLOS_VALVULA_DESGASTE)) {
            String msg = String.format(
                    "[ATENCAO] Valvula %s: %d ciclos de operacao (desgaste). "
                    + "Agendar substituicao preventiva.",
                    valvula.getId(), valvula.getCiclosOperacao());
            sugestoes.add(msg);
            analise.append(String.format("  -> ATENCAO: %d ciclos — desgaste detectado.\n",
                    valvula.getCiclosOperacao()));
            potencialEconomiaKWh += 10.0;
        } else {
            analise.append(String.format("  -> OK: %d ciclos (dentro da vida util).\n",
                    valvula.getCiclosOperacao()));
        }

        double abertura = valvula.getAberturaPercentual();
        if (abertura > 90.0) {
            analise.append(String.format("  -> ATENCAO: Abertura em %.1f%% — verificar demanda termica.\n", abertura));
        } else if (abertura < 10.0 && valvula.getEstado() != Valvula.Estado.FECHADA) {
            analise.append(String.format("  -> ATENCAO: Abertura em %.1f%% — possivel obstrucao.\n", abertura));
        } else {
            analise.append(String.format("  -> Abertura: %.1f%% — operacao normal.\n", abertura));
        }
    }

    /**
     * Gera a analise completa com sugestoes consolidadas.
     * @return analise formatada como String
     */
    public String gerarAnalise() {
        analise.append("\n").append("=".repeat(60)).append("\n");
        analise.append("  SUGESTOES DE OTIMIZACAO\n");
        analise.append("=".repeat(60)).append("\n");

        if (sugestoes.isEmpty()) {
            analise.append("  Nenhuma acao corretiva necessaria. Sistema operando\n");
            analise.append("  dentro dos parametros ideais de eficiencia.\n");
        } else {
            for (int i = 0; i < sugestoes.size(); i++) {
                analise.append(String.format("\n  %d. %s\n", i + 1, sugestoes.get(i)));
            }
        }

        analise.append("\n").append("=".repeat(60)).append("\n");
        analise.append(String.format("  Potencial de Economia Estimado: %.1f kWh/mes\n", potencialEconomiaKWh));
        analise.append(String.format("  Total de Sugestoes Geradas: %d\n", sugestoes.size()));
        analise.append("=".repeat(60)).append("\n");

        return analise.toString();
    }

    public List<String> getSugestoes() {
        return List.copyOf(sugestoes);
    }

    public double getPotencialEconomiaKWh() {
        return potencialEconomiaKWh;
    }
}
