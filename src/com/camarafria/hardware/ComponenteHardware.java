package com.camarafria.hardware;

import com.camarafria.visitor.ComponenteVisitor;

/**
 * Interface Elemento — contrato para todos os componentes de hardware
 * da Câmara Fria que podem ser visitados por algoritmos de análise.
 *
 * <p>Implementa o lado "Element" do padrão Visitor, definindo o método
 * {@code aceitar} que recebe um {@link ComponenteVisitor} e delega
 * a chamada via double-dispatch.</p>
 *
 * @author Gustavo
 * @version 1.0
 */
public interface ComponenteHardware {

    /**
     * Aceita um visitante, permitindo que ele execute sua operação
     * específica sobre este componente.
     *
     * @param visitor o algoritmo de análise a ser aplicado
     */
    void aceitar(ComponenteVisitor visitor);

    /**
     * Retorna o identificador único do componente no sistema.
     *
     * @return ID alfanumérico do componente
     */
    String getId();

    /**
     * Retorna a localização física do componente na câmara fria.
     *
     * @return descrição da localização (ex: "Zona A - Painel Norte")
     */
    String getLocalizacao();
}
