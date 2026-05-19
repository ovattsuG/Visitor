package com.camarafria.hardware;

import com.camarafria.visitor.ComponenteVisitor;

/**
 * Elemento Concreto — Válvula de Expansão do sistema de refrigeração.
 *
 * <p>Representa a válvula responsável por controlar o fluxo de fluido
 * refrigerante no ciclo frigorífico. Armazena dados de abertura atual,
 * ciclos de operação e estado de funcionamento.</p>
 *
 * @author Gustavo
 * @version 1.0
 */
public class Valvula implements ComponenteHardware {

    /** Enumeração dos estados possíveis da válvula. */
    public enum Estado {
        ABERTA("Aberta"),
        FECHADA("Fechada"),
        PARCIALMENTE_ABERTA("Parcialmente Aberta"),
        DEFEITO("Defeito Detectado");

        private final String descricao;

        Estado(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    private final String id;
    private final String localizacao;
    private final double aberturaPercentual;   // 0-100% de abertura
    private final int ciclosOperacao;          // total de ciclos abrir/fechar
    private final Estado estado;               // estado atual da válvula
    private final String tipoFluido;           // fluido refrigerante utilizado
    private final double pressaoEntradaBar;    // pressão na entrada em bar

    /**
     * Constrói uma Válvula com todos os seus atributos operacionais.
     *
     * @param id                  identificador único (ex: "VALV-001")
     * @param localizacao         posição física na câmara
     * @param aberturaPercentual  percentual de abertura (0-100%)
     * @param ciclosOperacao      número total de ciclos de operação
     * @param estado              estado atual da válvula
     * @param tipoFluido          tipo de fluido refrigerante
     * @param pressaoEntradaBar   pressão na entrada em bar
     */
    public Valvula(String id, String localizacao, double aberturaPercentual,
                   int ciclosOperacao, Estado estado, String tipoFluido,
                   double pressaoEntradaBar) {
        this.id = id;
        this.localizacao = localizacao;
        this.aberturaPercentual = aberturaPercentual;
        this.ciclosOperacao = ciclosOperacao;
        this.estado = estado;
        this.tipoFluido = tipoFluido;
        this.pressaoEntradaBar = pressaoEntradaBar;
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

    public double getAberturaPercentual() {
        return aberturaPercentual;
    }

    public int getCiclosOperacao() {
        return ciclosOperacao;
    }

    public Estado getEstado() {
        return estado;
    }

    public String getTipoFluido() {
        return tipoFluido;
    }

    public double getPressaoEntradaBar() {
        return pressaoEntradaBar;
    }

    /**
     * Verifica se a válvula apresenta desgaste com base
     * no número máximo de ciclos recomendado.
     *
     * @param limiteCiclos limite de ciclos antes de troca recomendada
     * @return {@code true} se os ciclos excederam o limite
     */
    public boolean apresentaDesgaste(int limiteCiclos) {
        return ciclosOperacao >= limiteCiclos;
    }

    /**
     * Verifica se a válvula está em estado operacional funcional.
     *
     * @return {@code true} se o estado não é DEFEITO
     */
    public boolean isOperacional() {
        return estado != Estado.DEFEITO;
    }

    @Override
    public String toString() {
        return String.format("Valvula[%s | %s | %.0f%% aberta | %s | %d ciclos]",
                id, localizacao, aberturaPercentual, estado.getDescricao(), ciclosOperacao);
    }
}
