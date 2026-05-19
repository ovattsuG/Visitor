package com.camarafria.visitor;

import com.camarafria.hardware.Compressor;
import com.camarafria.hardware.Sensor;
import com.camarafria.hardware.Valvula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("RelatorioStatusVisitor")
class RelatorioStatusVisitorTest {

    private RelatorioStatusVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new RelatorioStatusVisitor();
    }

    @Nested @DisplayName("Relatorio com Sensor")
    class ComSensor {

        @Test @DisplayName("Sensor dentro do limite gera status DENTRO DO LIMITE")
        void sensorDentroDoLimite() {
            Sensor s = new Sensor("SENS-001", "Zona A", -20.0, -15.0, 0.3, "PT100");
            visitor.visitar(s);
            String rel = visitor.gerarRelatorio();
            assertTrue(rel.contains("SENS-001"));
            assertTrue(rel.contains("DENTRO DO LIMITE"));
            assertTrue(rel.contains("Componentes com Alerta"));
        }

        @Test @DisplayName("Sensor fora do limite gera status FORA DO LIMITE")
        void sensorForaDoLimite() {
            Sensor s = new Sensor("SENS-002", "Zona B", -12.0, -15.0, 0.5, "NTC");
            visitor.visitar(s);
            String rel = visitor.gerarRelatorio();
            assertTrue(rel.contains("FORA DO LIMITE"));
        }
    }

    @Nested @DisplayName("Relatorio com Compressor")
    class ComCompressor {

        @Test @DisplayName("Compressor OK gera status OK")
        void compressorOk() {
            Compressor c = new Compressor("COMP-001", "Sala 1", 3200.0, 5.0, 12800.0, 3.2, 1800);
            visitor.visitar(c);
            String rel = visitor.gerarRelatorio();
            assertTrue(rel.contains("COMP-001"));
            assertTrue(rel.contains("OK"));
        }

        @Test @DisplayName("Compressor com muitas horas gera MANUTENCAO NECESSARIA")
        void compressorManutencao() {
            Compressor c = new Compressor("COMP-002", "Sala 2", 9500.0, 7.5, 45200.0, 2.8, 4200);
            visitor.visitar(c);
            String rel = visitor.gerarRelatorio();
            assertTrue(rel.contains("MANUTENCAO NECESSARIA"));
        }
    }

    @Nested @DisplayName("Relatorio com Valvula")
    class ComValvula {

        @Test @DisplayName("Valvula operacional gera status OPERACIONAL")
        void valvulaOk() {
            Valvula v = new Valvula("VALV-001", "Zona A", 65.0, 32000,
                    Valvula.Estado.PARCIALMENTE_ABERTA, "R-404A", 4.5);
            visitor.visitar(v);
            String rel = visitor.gerarRelatorio();
            assertTrue(rel.contains("OPERACIONAL"));
        }

        @Test @DisplayName("Valvula com defeito gera DEFEITO DETECTADO")
        void valvulaDefeito() {
            Valvula v = new Valvula("VALV-003", "Principal", 0.0, 18000,
                    Valvula.Estado.DEFEITO, "R-134a", 0.0);
            visitor.visitar(v);
            String rel = visitor.gerarRelatorio();
            assertTrue(rel.contains("DEFEITO DETECTADO"));
        }

        @Test @DisplayName("Valvula com desgaste gera TROCA RECOMENDADA")
        void valvulaDesgaste() {
            Valvula v = new Valvula("VALV-002", "Zona B", 0.0, 55000,
                    Valvula.Estado.FECHADA, "R-404A", 4.2);
            visitor.visitar(v);
            String rel = visitor.gerarRelatorio();
            assertTrue(rel.contains("TROCA RECOMENDADA"));
        }
    }

    @Nested @DisplayName("Resumo consolidado")
    class ResumoConsolidado {

        @Test @DisplayName("Relatorio vazio deve ter zero componentes")
        void relatorioVazio() {
            String rel = visitor.gerarRelatorio();
            assertTrue(rel.contains("Total de Componentes Analisados .. : 0"));
        }

        @Test @DisplayName("Contagem correta de componentes e alertas")
        void contagemCorreta() {
            // 1 sensor OK, 1 sensor alerta, 1 compressor alerta = 3 total, 2 alertas
            visitor.visitar(new Sensor("S1", "A", -20.0, -15.0, 0.3, "PT100"));
            visitor.visitar(new Sensor("S2", "B", -12.0, -15.0, 0.5, "NTC"));
            visitor.visitar(new Compressor("C1", "M", 9500.0, 7.5, 45200.0, 2.8, 4200));

            String rel = visitor.gerarRelatorio();
            assertTrue(rel.contains("Total de Componentes Analisados .. : 3"));
            assertTrue(rel.contains("Componentes com Alerta .......... : 2"));
            assertTrue(rel.contains("Componentes OK .................. : 1"));
        }
    }

    @Test @DisplayName("Relatorio contem cabecalho com data/hora")
    void cabecalhoComData() {
        String rel = visitor.gerarRelatorio();
        assertTrue(rel.contains("RELATORIO DE STATUS"));
        assertTrue(rel.contains("Data/Hora:"));
    }
}
