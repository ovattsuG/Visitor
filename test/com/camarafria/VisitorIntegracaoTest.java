package com.camarafria;

import com.camarafria.hardware.ComponenteHardware;
import com.camarafria.hardware.Compressor;
import com.camarafria.hardware.Sensor;
import com.camarafria.hardware.Valvula;
import com.camarafria.visitor.ComponenteVisitor;
import com.camarafria.visitor.OtimizacaoEnergiaVisitor;
import com.camarafria.visitor.RelatorioStatusVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integracao do padrao Visitor.
 *
 * Valida que a estrutura completa de componentes pode ser visitada
 * por diferentes visitors sem modificar as classes de hardware.
 */
@DisplayName("Integracao do Padrao Visitor")
class VisitorIntegracaoTest {

    private List<ComponenteHardware> componentes;

    @BeforeEach
    void setUp() {
        componentes = new ArrayList<>();
        componentes.add(new Sensor("S1", "Zona A", -18.5, -15.0, 0.3, "PT100"));
        componentes.add(new Sensor("S2", "Zona B", -20.1, -15.0, 0.5, "NTC"));
        componentes.add(new Compressor("C1", "Sala 1", 9500.0, 7.5, 45200.0, 2.8, 4200));
        componentes.add(new Compressor("C2", "Sala 2", 3200.0, 5.0, 12800.0, 3.2, 1800));
        componentes.add(new Valvula("V1", "Zona A", 65.0, 32000,
                Valvula.Estado.PARCIALMENTE_ABERTA, "R-404A", 4.5));
        componentes.add(new Valvula("V2", "Principal", 0.0, 18000,
                Valvula.Estado.DEFEITO, "R-134a", 0.0));
    }

    @Test @DisplayName("RelatorioStatusVisitor visita todos os componentes da lista")
    void relatorioVisitaTodos() {
        RelatorioStatusVisitor visitor = new RelatorioStatusVisitor();
        for (ComponenteHardware c : componentes) {
            c.aceitar(visitor);
        }
        String rel = visitor.gerarRelatorio();
        assertTrue(rel.contains("S1"));
        assertTrue(rel.contains("S2"));
        assertTrue(rel.contains("C1"));
        assertTrue(rel.contains("C2"));
        assertTrue(rel.contains("V1"));
        assertTrue(rel.contains("V2"));
        assertTrue(rel.contains("Total de Componentes Analisados .. : 6"));
    }

    @Test @DisplayName("OtimizacaoEnergiaVisitor visita todos os componentes da lista")
    void otimizacaoVisitaTodos() {
        OtimizacaoEnergiaVisitor visitor = new OtimizacaoEnergiaVisitor();
        for (ComponenteHardware c : componentes) {
            c.aceitar(visitor);
        }
        String analise = visitor.gerarAnalise();
        assertTrue(analise.contains("[SENSOR]"));
        assertTrue(analise.contains("[COMPRESSOR]"));
        assertTrue(analise.contains("[VALVULA]"));
        assertFalse(visitor.getSugestoes().isEmpty());
    }

    @Test @DisplayName("Dois visitors diferentes na mesma estrutura produzem resultados independentes")
    void visitorsIndependentes() {
        RelatorioStatusVisitor v1 = new RelatorioStatusVisitor();
        OtimizacaoEnergiaVisitor v2 = new OtimizacaoEnergiaVisitor();

        for (ComponenteHardware c : componentes) {
            c.aceitar(v1);
            c.aceitar(v2);
        }

        String rel = v1.gerarRelatorio();
        String opt = v2.gerarAnalise();

        // Cada visitor gera saida diferente
        assertTrue(rel.contains("RELATORIO DE STATUS"));
        assertTrue(opt.contains("ANALISE DE OTIMIZACAO ENERGETICA"));

        // Dados nao se misturam
        assertFalse(rel.contains("SUGESTOES DE OTIMIZACAO"));
        assertFalse(opt.contains("RESUMO CONSOLIDADO"));
    }

    @Test @DisplayName("Double-dispatch garante chamada correta para cada tipo")
    void doubleDispatchVerificacao() {
        final int[] contadores = {0, 0, 0}; // sensor, compressor, valvula

        ComponenteVisitor counter = new ComponenteVisitor() {
            public void visitar(Sensor s) { contadores[0]++; }
            public void visitar(Compressor c) { contadores[1]++; }
            public void visitar(Valvula v) { contadores[2]++; }
        };

        for (ComponenteHardware c : componentes) {
            c.aceitar(counter);
        }

        assertEquals(2, contadores[0], "Deveria ter visitado 2 sensores");
        assertEquals(2, contadores[1], "Deveria ter visitado 2 compressores");
        assertEquals(2, contadores[2], "Deveria ter visitado 2 valvulas");
    }

    @Test @DisplayName("Estrutura vazia nao causa erros nos visitors")
    void estruturaVazia() {
        List<ComponenteHardware> vazia = new ArrayList<>();
        RelatorioStatusVisitor v1 = new RelatorioStatusVisitor();
        OtimizacaoEnergiaVisitor v2 = new OtimizacaoEnergiaVisitor();

        for (ComponenteHardware c : vazia) {
            c.aceitar(v1);
            c.aceitar(v2);
        }

        assertDoesNotThrow(v1::gerarRelatorio);
        assertDoesNotThrow(v2::gerarAnalise);
        assertTrue(v2.getSugestoes().isEmpty());
    }
}
