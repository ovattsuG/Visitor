package com.camarafria.visitor;

import com.camarafria.hardware.Compressor;
import com.camarafria.hardware.Sensor;
import com.camarafria.hardware.Valvula;

/**
 * Interface Visitor — define operações para cada tipo concreto de hardware.
 *
 * <p>Cada método sobrecarregado {@code visitar} recebe um elemento concreto,
 * permitindo que novos algoritmos de análise sejam adicionados sem alterar
 * as classes de hardware existentes (Open/Closed Principle).</p>
 *
 * @author Gustavo
 * @version 1.0
 */
public interface ComponenteVisitor {

    /**
     * Visita um sensor de temperatura, acessando suas leituras e metadados.
     *
     * @param sensor instância do sensor a ser analisado
     */
    void visitar(Sensor sensor);

    /**
     * Visita um compressor, acessando dados de operação e consumo.
     *
     * @param compressor instância do compressor a ser analisado
     */
    void visitar(Compressor compressor);

    /**
     * Visita uma válvula de expansão, acessando estado e ciclos de operação.
     *
     * @param valvula instância da válvula a ser analisada
     */
    void visitar(Valvula valvula);
}
