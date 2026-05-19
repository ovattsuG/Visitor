package com.camarafria.hardware;

import com.camarafria.visitor.ComponenteVisitor;

/**
 * Elemento Concreto — Sensor de Temperatura da Câmara Fria.
 *
 * <p>Representa um sensor IoT responsável por monitorar a temperatura
 * em uma zona específica da câmara. Armazena a leitura atual, o limite
 * operacional configurado e a precisão do instrumento.</p>
 *
 * @author Gustavo
 * @version 1.0
 */
public class Sensor implements ComponenteHardware {

    private final String id;
    private final String localizacao;
    private final double leituraAtual;       // °C
    private final double limiteOperacional;  // °C — temperatura máxima segura
    private final double precisao;           // ±°C — margem de erro do sensor
    private final String tipo;               // ex: "PT100", "NTC", "Termopar K"

    /**
     * Constrói um Sensor com todos os seus atributos operacionais.
     *
     * @param id                 identificador único (ex: "SENS-001")
     * @param localizacao        posição física na câmara
     * @param leituraAtual       última leitura registrada em °C
     * @param limiteOperacional  temperatura máxima permitida em °C
     * @param precisao           margem de erro em ±°C
     * @param tipo               modelo/tipo do sensor
     */
    public Sensor(String id, String localizacao, double leituraAtual,
                  double limiteOperacional, double precisao, String tipo) {
        this.id = id;
        this.localizacao = localizacao;
        this.leituraAtual = leituraAtual;
        this.limiteOperacional = limiteOperacional;
        this.precisao = precisao;
        this.tipo = tipo;
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

    public double getLeituraAtual() {
        return leituraAtual;
    }

    public double getLimiteOperacional() {
        return limiteOperacional;
    }

    public double getPrecisao() {
        return precisao;
    }

    public String getTipo() {
        return tipo;
    }

    /**
     * Verifica se a leitura atual está dentro do limite operacional seguro.
     *
     * @return {@code true} se a temperatura está dentro do limite
     */
    public boolean isDentroDoLimite() {
        return leituraAtual <= limiteOperacional;
    }

    /**
     * Calcula o desvio percentual em relação ao limite operacional.
     *
     * @return desvio em percentual (positivo = acima do limite)
     */
    public double getDesvioPorcentagem() {
        if (limiteOperacional == 0) return 0;
        return ((leituraAtual - limiteOperacional) / Math.abs(limiteOperacional)) * 100.0;
    }

    @Override
    public String toString() {
        return String.format("Sensor[%s | %s | %.1f°C | Limite: %.1f°C | Tipo: %s]",
                id, localizacao, leituraAtual, limiteOperacional, tipo);
    }
}
