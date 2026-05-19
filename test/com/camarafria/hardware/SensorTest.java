package com.camarafria.hardware;

import com.camarafria.visitor.ComponenteVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para a classe Sensor.
 *
 * Valida atributos, logica de limite operacional, calculo de desvio
 * e o mecanismo de double-dispatch (aceitar).
 */
@DisplayName("Sensor de Temperatura")
class SensorTest {

    private Sensor sensorDentroLimite;
    private Sensor sensorForaLimite;
    private Sensor sensorNoLimite;

    @BeforeEach
    void setUp() {
        // Leitura -20°C, limite -15°C → DENTRO do limite (mais frio que o maximo)
        sensorDentroLimite = new Sensor(
                "SENS-001", "Zona A - Painel Norte",
                -20.0, -15.0, 0.3, "PT100"
        );

        // Leitura -12°C, limite -15°C → FORA do limite (mais quente que o maximo)
        sensorForaLimite = new Sensor(
                "SENS-002", "Zona B - Painel Sul",
                -12.0, -15.0, 0.5, "NTC"
        );

        // Leitura exatamente no limite
        sensorNoLimite = new Sensor(
                "SENS-003", "Zona C - Teto Central",
                -15.0, -15.0, 0.2, "Termopar K"
        );
    }

    @Nested
    @DisplayName("Atributos basicos")
    class AtributosBasicos {

        @Test
        @DisplayName("Deve retornar o ID correto")
        void deveRetornarId() {
            assertEquals("SENS-001", sensorDentroLimite.getId());
        }

        @Test
        @DisplayName("Deve retornar a localizacao correta")
        void deveRetornarLocalizacao() {
            assertEquals("Zona A - Painel Norte", sensorDentroLimite.getLocalizacao());
        }

        @Test
        @DisplayName("Deve retornar a leitura atual")
        void deveRetornarLeituraAtual() {
            assertEquals(-20.0, sensorDentroLimite.getLeituraAtual(), 0.001);
        }

        @Test
        @DisplayName("Deve retornar o limite operacional")
        void deveRetornarLimiteOperacional() {
            assertEquals(-15.0, sensorDentroLimite.getLimiteOperacional(), 0.001);
        }

        @Test
        @DisplayName("Deve retornar a precisao")
        void deveRetornarPrecisao() {
            assertEquals(0.3, sensorDentroLimite.getPrecisao(), 0.001);
        }

        @Test
        @DisplayName("Deve retornar o tipo do sensor")
        void deveRetornarTipo() {
            assertEquals("PT100", sensorDentroLimite.getTipo());
        }
    }

    @Nested
    @DisplayName("Logica de limite operacional")
    class LimiteOperacional {

        @Test
        @DisplayName("Sensor com leitura abaixo do limite deve estar DENTRO do limite")
        void sensorAbaixoLimiteDevEstarDentro() {
            assertTrue(sensorDentroLimite.isDentroDoLimite());
        }

        @Test
        @DisplayName("Sensor com leitura acima do limite deve estar FORA do limite")
        void sensorAcimaLimiteDeveEstarFora() {
            assertFalse(sensorForaLimite.isDentroDoLimite());
        }

        @Test
        @DisplayName("Sensor com leitura no exato limite deve estar DENTRO do limite")
        void sensorNoLimiteDeveEstarDentro() {
            assertTrue(sensorNoLimite.isDentroDoLimite());
        }
    }

    @Nested
    @DisplayName("Calculo de desvio percentual")
    class DesvioPorcentagem {

        @Test
        @DisplayName("Desvio negativo quando leitura esta abaixo do limite")
        void desvioNegativoQuandoAbaixo() {
            double desvio = sensorDentroLimite.getDesvioPorcentagem();
            assertTrue(desvio < 0, "Desvio deveria ser negativo (abaixo do limite)");
        }

        @Test
        @DisplayName("Desvio positivo quando leitura esta acima do limite")
        void desvioPositivoQuandoAcima() {
            double desvio = sensorForaLimite.getDesvioPorcentagem();
            assertTrue(desvio > 0, "Desvio deveria ser positivo (acima do limite)");
        }

        @Test
        @DisplayName("Desvio zero quando leitura esta no limite")
        void desvioZeroQuandoNoLimite() {
            assertEquals(0.0, sensorNoLimite.getDesvioPorcentagem(), 0.001);
        }

        @Test
        @DisplayName("Desvio com limite zero nao deve lancar excecao")
        void desvioComLimiteZeroNaoLancaExcecao() {
            Sensor sensorLimiteZero = new Sensor("S-X", "Teste", 5.0, 0.0, 0.1, "NTC");
            assertEquals(0.0, sensorLimiteZero.getDesvioPorcentagem(), 0.001);
        }
    }

    @Nested
    @DisplayName("Double-dispatch (aceitar)")
    class DoubleDispatch {

        @Test
        @DisplayName("aceitar() deve invocar visitor.visitar(Sensor) via double-dispatch")
        void aceitarDeveInvocarVisitarSensor() {
            final boolean[] visitado = {false};

            ComponenteVisitor visitorMock = new ComponenteVisitor() {
                @Override
                public void visitar(Sensor sensor) {
                    visitado[0] = true;
                    assertEquals("SENS-001", sensor.getId());
                }

                @Override
                public void visitar(Compressor compressor) {
                    fail("Nao deveria visitar Compressor");
                }

                @Override
                public void visitar(Valvula valvula) {
                    fail("Nao deveria visitar Valvula");
                }
            };

            sensorDentroLimite.aceitar(visitorMock);
            assertTrue(visitado[0], "O metodo visitar(Sensor) deveria ter sido chamado");
        }
    }

    @Test
    @DisplayName("toString deve conter informacoes do sensor")
    void toStringDeveConterInformacoes() {
        String resultado = sensorDentroLimite.toString();
        assertTrue(resultado.contains("SENS-001"));
        assertTrue(resultado.contains("PT100"));
    }
}
