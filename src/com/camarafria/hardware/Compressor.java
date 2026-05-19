package com.camarafria.hardware;

import com.camarafria.visitor.ComponenteVisitor;

/**
 * Elemento Concreto — Compressor do sistema de refrigeração da Câmara Fria.
 *
 * <p>Representa o compressor responsável pelo ciclo frigorífico.
 * Armazena dados de operação como horas ligado, consumo energético,
 * potência nominal e indicador de eficiência (COP).</p>
 *
 * @author Gustavo
 * @version 1.0
 */
public class Compressor implements ComponenteHardware {

    private final String id;
    private final String localizacao;
    private final double horasLigado;         // horas acumuladas de operação
    private final double potenciaNominalKW;   // potência nominal em kW
    private final double consumoAtualKWh;     // consumo energético atual em kWh
    private final double cop;                 // Coeficiente de Performance (COP)
    private final int ciclosLigaDesliga;      // total de ciclos de liga/desliga

    /**
     * Constrói um Compressor com todos os seus atributos operacionais.
     *
     * @param id                 identificador único (ex: "COMP-001")
     * @param localizacao        posição física na câmara
     * @param horasLigado        horas acumuladas de funcionamento
     * @param potenciaNominalKW  potência nominal em kW
     * @param consumoAtualKWh    consumo energético acumulado em kWh
     * @param cop                coeficiente de performance atual
     * @param ciclosLigaDesliga  número de ciclos de liga/desliga
     */
    public Compressor(String id, String localizacao, double horasLigado,
                      double potenciaNominalKW, double consumoAtualKWh,
                      double cop, int ciclosLigaDesliga) {
        this.id = id;
        this.localizacao = localizacao;
        this.horasLigado = horasLigado;
        this.potenciaNominalKW = potenciaNominalKW;
        this.consumoAtualKWh = consumoAtualKWh;
        this.cop = cop;
        this.ciclosLigaDesliga = ciclosLigaDesliga;
    }

    /** {@inheritDoc} — Double-dispatch: delega ao visitor o método correto. */
    @Override
    public void aceitar(ComponenteVisitor visitor) {
        visitor.visitar(this);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLocalizacao() {
        return localizacao;
    }

    public double getHorasLigado() {
        return horasLigado;
    }

    public double getPotenciaNominalKW() {
        return potenciaNominalKW;
    }

    public double getConsumoAtualKWh() {
        return consumoAtualKWh;
    }

    public double getCop() {
        return cop;
    }

    public int getCiclosLigaDesliga() {
        return ciclosLigaDesliga;
    }

    /**
     * Verifica se o compressor precisa de manutenção preventiva
     * com base nas horas de operação acumuladas.
     *
     * @param limiteHoras limite de horas para manutenção preventiva
     * @return {@code true} se ultrapassou o limite de horas
     */
    public boolean necessitaManutencao(double limiteHoras) {
        return horasLigado >= limiteHoras;
    }

    /**
     * Calcula a eficiência real comparando o COP atual com o COP ideal.
     *
     * @param copIdeal COP de referência do fabricante
     * @return eficiência em percentual (0-100+)
     */
    public double getEficienciaPercentual(double copIdeal) {
        if (copIdeal == 0) return 0;
        return (cop / copIdeal) * 100.0;
    }

    @Override
    public String toString() {
        return String.format("Compressor[%s | %s | %.0fh ligado | %.1fkW | COP: %.2f]",
                id, localizacao, horasLigado, potenciaNominalKW, cop);
    }
}
