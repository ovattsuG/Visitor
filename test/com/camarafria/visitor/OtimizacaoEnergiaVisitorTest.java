package com.camarafria.visitor;

import com.camarafria.hardware.Compressor;
import com.camarafria.hardware.Sensor;
import com.camarafria.hardware.Valvula;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OtimizacaoEnergiaVisitor")
class OtimizacaoEnergiaVisitorTest {

    private OtimizacaoEnergiaVisitor visitor;

    @BeforeEach
    void setUp() {
        visitor = new OtimizacaoEnergiaVisitor();
    }

    @Nested @DisplayName("Analise de Sensor")
    class AnaliseSensor {

        @Test @DisplayName("Sensor dentro do limite e longe: nenhuma sugestao")
        void sensorOk() {
            Sensor s = new Sensor("S1", "A", -16.0, -15.0, 0.3, "PT100");
            visitor.visitar(s);
            assertTrue(visitor.getSugestoes().isEmpty());
        }

        @Test @DisplayName("Sensor fora do limite gera sugestao CRITICO")
        void sensorForaDoLimite() {
            Sensor s = new Sensor("S2", "B", -12.0, -15.0, 0.5, "NTC");
            visitor.visitar(s);
            List<String> sug = visitor.getSugestoes();
            assertEquals(1, sug.size());
            assertTrue(sug.get(0).contains("[CRITICO]"));
            assertTrue(visitor.getPotencialEconomiaKWh() > 0);
        }

        @Test @DisplayName("Sensor proximo do limite gera sugestao ATENCAO")
        void sensorProximoDoLimite() {
            // Desvio > 15% mas dentro do limite
            Sensor s = new Sensor("S3", "C", -18.0, -15.0, 0.3, "PT100");
            visitor.visitar(s);
            List<String> sug = visitor.getSugestoes();
            assertEquals(1, sug.size());
            assertTrue(sug.get(0).contains("[ATENCAO]"));
        }
    }

    @Nested @DisplayName("Analise de Compressor")
    class AnaliseCompressor {

        @Test @DisplayName("Compressor novo sem problemas: nenhuma sugestao")
        void compressorOk() {
            Compressor c = new Compressor("C1", "M", 3200.0, 5.0, 12800.0, 3.2, 1800);
            visitor.visitar(c);
            assertTrue(visitor.getSugestoes().isEmpty());
        }

        @Test @DisplayName("Compressor com horas preventivas gera sugestao")
        void compressorPreventivo() {
            Compressor c = new Compressor("C2", "M", 9500.0, 7.5, 45200.0, 3.0, 2000);
            visitor.visitar(c);
            assertFalse(visitor.getSugestoes().isEmpty());
            assertTrue(visitor.getSugestoes().stream().anyMatch(s -> s.contains("manutencao preventiva")));
        }

        @Test @DisplayName("Compressor com horas criticas gera CRITICO")
        void compressorCritico() {
            Compressor c = new Compressor("C3", "M", 13000.0, 7.5, 60000.0, 3.0, 2000);
            visitor.visitar(c);
            assertTrue(visitor.getSugestoes().stream().anyMatch(s -> s.contains("[CRITICO]")));
        }

        @Test @DisplayName("Compressor com COP baixo gera sugestao de troca")
        void compressorCopBaixo() {
            Compressor c = new Compressor("C4", "M", 1000.0, 5.0, 5000.0, 2.0, 500);
            visitor.visitar(c);
            assertTrue(visitor.getSugestoes().stream().anyMatch(s -> s.contains("COP")));
        }

        @Test @DisplayName("Compressor com ciclos excessivos gera sugestao")
        void compressorCiclosExcessivos() {
            Compressor c = new Compressor("C5", "M", 2000.0, 5.0, 8000.0, 3.2, 6000);
            visitor.visitar(c);
            assertTrue(visitor.getSugestoes().stream().anyMatch(s -> s.contains("ciclos")));
        }
    }

    @Nested @DisplayName("Analise de Valvula")
    class AnaliseValvula {

        @Test @DisplayName("Valvula operacional sem desgaste: nenhuma sugestao")
        void valvulaOk() {
            Valvula v = new Valvula("V1", "A", 65.0, 32000,
                    Valvula.Estado.PARCIALMENTE_ABERTA, "R-404A", 4.5);
            visitor.visitar(v);
            assertTrue(visitor.getSugestoes().isEmpty());
        }

        @Test @DisplayName("Valvula com defeito gera CRITICO")
        void valvulaDefeito() {
            Valvula v = new Valvula("V2", "P", 0.0, 18000,
                    Valvula.Estado.DEFEITO, "R-134a", 0.0);
            visitor.visitar(v);
            assertEquals(1, visitor.getSugestoes().size());
            assertTrue(visitor.getSugestoes().get(0).contains("[CRITICO]"));
        }

        @Test @DisplayName("Valvula com desgaste gera ATENCAO")
        void valvulaDesgaste() {
            Valvula v = new Valvula("V3", "B", 0.0, 55000,
                    Valvula.Estado.FECHADA, "R-404A", 4.2);
            visitor.visitar(v);
            assertTrue(visitor.getSugestoes().stream().anyMatch(s -> s.contains("[ATENCAO]")));
        }
    }

    @Nested @DisplayName("Relatorio consolidado")
    class Consolidado {

        @Test @DisplayName("Analise vazia deve ter zero sugestoes")
        void analiseVazia() {
            String analise = visitor.gerarAnalise();
            assertTrue(analise.contains("Total de Sugestoes Geradas: 0"));
            assertEquals(0.0, visitor.getPotencialEconomiaKWh(), 0.001);
        }

        @Test @DisplayName("Potencial de economia acumula corretamente")
        void economiaAcumula() {
            // sensor fora = 15, compressor preventivo = 20
            visitor.visitar(new Sensor("S1", "A", -12.0, -15.0, 0.5, "NTC"));
            visitor.visitar(new Compressor("C1", "M", 9500.0, 7.5, 45200.0, 3.0, 2000));
            assertTrue(visitor.getPotencialEconomiaKWh() >= 35.0);
        }

        @Test @DisplayName("gerarAnalise contem cabecalho")
        void cabecalho() {
            String analise = visitor.gerarAnalise();
            assertTrue(analise.contains("ANALISE DE OTIMIZACAO ENERGETICA"));
        }

        @Test @DisplayName("getSugestoes retorna copia imutavel")
        void sugestoesImutaveis() {
            visitor.visitar(new Valvula("V1", "P", 0.0, 18000,
                    Valvula.Estado.DEFEITO, "R-134a", 0.0));
            List<String> sug = visitor.getSugestoes();
            assertThrows(UnsupportedOperationException.class, () -> sug.add("hack"));
        }
    }

    @Test @DisplayName("Cenario completo com multiplos componentes")
    void cenarioCompleto() {
        visitor.visitar(new Sensor("S1", "A", -20.0, -15.0, 0.3, "PT100"));
        visitor.visitar(new Sensor("S2", "B", -12.0, -15.0, 0.5, "NTC"));
        visitor.visitar(new Compressor("C1", "M", 9500.0, 7.5, 45200.0, 2.0, 6000));
        visitor.visitar(new Valvula("V1", "P", 0.0, 18000,
                Valvula.Estado.DEFEITO, "R-134a", 0.0));

        String analise = visitor.gerarAnalise();
        assertFalse(visitor.getSugestoes().isEmpty());
        assertTrue(analise.contains("SUGESTOES DE OTIMIZACAO"));
        assertTrue(visitor.getPotencialEconomiaKWh() > 0);
    }
}
