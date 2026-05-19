package com.camarafria.hardware;

import com.camarafria.visitor.ComponenteVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para a classe Compressor.
 *
 * Valida atributos, logica de manutencao, calculo de eficiencia
 * e o mecanismo de double-dispatch (aceitar).
 */
@DisplayName("Compressor")
class CompressorTest {

    private Compressor compressorNovo;
    private Compressor compressorDesgastado;

    @BeforeEach
    void setUp() {
        // Compressor com poucas horas — sem necessidade de manutencao
        compressorNovo = new Compressor(
                "COMP-001", "Casa de Maquinas - Sala 1",
                3200.0, 5.0, 12800.0, 3.2, 1800
        );

        // Compressor com muitas horas — necessita manutencao
        compressorDesgastado = new Compressor(
                "COMP-002", "Casa de Maquinas - Sala 2",
                9500.0, 7.5, 45200.0, 2.1, 6200
        );
    }

    @Nested
    @DisplayName("Atributos basicos")
    class AtributosBasicos {

        @Test
        @DisplayName("Deve retornar o ID correto")
        void deveRetornarId() {
            assertEquals("COMP-001", compressorNovo.getId());
        }

        @Test
        @DisplayName("Deve retornar a localizacao correta")
        void deveRetornarLocalizacao() {
            assertEquals("Casa de Maquinas - Sala 1", compressorNovo.getLocalizacao());
        }

        @Test
        @DisplayName("Deve retornar as horas ligado")
        void deveRetornarHorasLigado() {
            assertEquals(3200.0, compressorNovo.getHorasLigado(), 0.001);
        }

        @Test
        @DisplayName("Deve retornar a potencia nominal")
        void deveRetornarPotenciaNominal() {
            assertEquals(5.0, compressorNovo.getPotenciaNominalKW(), 0.001);
        }

        @Test
        @DisplayName("Deve retornar o consumo atual")
        void deveRetornarConsumoAtual() {
            assertEquals(12800.0, compressorNovo.getConsumoAtualKWh(), 0.001);
        }

        @Test
        @DisplayName("Deve retornar o COP")
        void deveRetornarCop() {
            assertEquals(3.2, compressorNovo.getCop(), 0.001);
        }

        @Test
        @DisplayName("Deve retornar os ciclos liga/desliga")
        void deveRetornarCiclos() {
            assertEquals(1800, compressorNovo.getCiclosLigaDesliga());
        }
    }

    @Nested
    @DisplayName("Logica de manutencao")
    class ManutencaoPreventiva {

        @Test
        @DisplayName("Compressor novo NAO necessita manutencao (limite 8000h)")
        void compressorNovoNaoNecessitaManutencao() {
            assertFalse(compressorNovo.necessitaManutencao(8000));
        }

        @Test
        @DisplayName("Compressor desgastado necessita manutencao (limite 8000h)")
        void compressorDesgastadoNecessitaManutencao() {
            assertTrue(compressorDesgastado.necessitaManutencao(8000));
        }

        @Test
        @DisplayName("Compressor exatamente no limite deve necessitar manutencao")
        void compressorNoLimiteNecessitaManutencao() {
            Compressor noLimite = new Compressor(
                    "COMP-X", "Teste", 8000.0, 5.0, 30000.0, 3.0, 3000
            );
            assertTrue(noLimite.necessitaManutencao(8000));
        }

        @Test
        @DisplayName("Compressor abaixo do limite com margem minima")
        void compressorAbaixoDoLimite() {
            Compressor quaseNoLimite = new Compressor(
                    "COMP-X", "Teste", 7999.0, 5.0, 30000.0, 3.0, 3000
            );
            assertFalse(quaseNoLimite.necessitaManutencao(8000));
        }
    }

    @Nested
    @DisplayName("Calculo de eficiencia")
    class CalculoEficiencia {

        @Test
        @DisplayName("Eficiencia correta com COP ideal 3.5")
        void eficienciaCorreta() {
            // COP 3.2 / 3.5 = 91.43%
            double eficiencia = compressorNovo.getEficienciaPercentual(3.5);
            assertEquals(91.43, eficiencia, 0.1);
        }

        @Test
        @DisplayName("Eficiencia baixa para compressor desgastado")
        void eficienciaBaixaParaDesgastado() {
            // COP 2.1 / 3.5 = 60.0%
            double eficiencia = compressorDesgastado.getEficienciaPercentual(3.5);
            assertEquals(60.0, eficiencia, 0.1);
        }

        @Test
        @DisplayName("Eficiencia 100% quando COP atual igual ao ideal")
        void eficiencia100Porcento() {
            Compressor perfeito = new Compressor(
                    "COMP-X", "Teste", 100.0, 5.0, 500.0, 3.5, 50
            );
            assertEquals(100.0, perfeito.getEficienciaPercentual(3.5), 0.001);
        }

        @Test
        @DisplayName("Eficiencia zero quando COP ideal eh zero")
        void eficienciaZeroComCopIdealZero() {
            assertEquals(0.0, compressorNovo.getEficienciaPercentual(0.0), 0.001);
        }
    }

    @Nested
    @DisplayName("Double-dispatch (aceitar)")
    class DoubleDispatch {

        @Test
        @DisplayName("aceitar() deve invocar visitor.visitar(Compressor) via double-dispatch")
        void aceitarDeveInvocarVisitarCompressor() {
            final boolean[] visitado = {false};

            ComponenteVisitor visitorMock = new ComponenteVisitor() {
                @Override
                public void visitar(Sensor sensor) {
                    fail("Nao deveria visitar Sensor");
                }

                @Override
                public void visitar(Compressor compressor) {
                    visitado[0] = true;
                    assertEquals("COMP-001", compressor.getId());
                }

                @Override
                public void visitar(Valvula valvula) {
                    fail("Nao deveria visitar Valvula");
                }
            };

            compressorNovo.aceitar(visitorMock);
            assertTrue(visitado[0], "O metodo visitar(Compressor) deveria ter sido chamado");
        }
    }

    @Test
    @DisplayName("toString deve conter informacoes do compressor")
    void toStringDeveConterInformacoes() {
        String resultado = compressorNovo.toString();
        assertTrue(resultado.contains("COMP-001"));
        assertTrue(resultado.contains("3200"));
    }
}
