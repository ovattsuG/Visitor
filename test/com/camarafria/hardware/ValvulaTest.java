package com.camarafria.hardware;

import com.camarafria.visitor.ComponenteVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Valvula de Expansao")
class ValvulaTest {

    private Valvula valvulaOp;
    private Valvula valvulaDesg;
    private Valvula valvulaDef;

    @BeforeEach
    void setUp() {
        valvulaOp = new Valvula("VALV-001", "Zona A", 65.0, 32000,
                Valvula.Estado.PARCIALMENTE_ABERTA, "R-404A", 4.5);
        valvulaDesg = new Valvula("VALV-002", "Zona B", 0.0, 55000,
                Valvula.Estado.FECHADA, "R-404A", 4.2);
        valvulaDef = new Valvula("VALV-003", "Principal", 0.0, 18000,
                Valvula.Estado.DEFEITO, "R-134a", 0.0);
    }

    @Test @DisplayName("Deve retornar atributos corretos")
    void atributos() {
        assertEquals("VALV-001", valvulaOp.getId());
        assertEquals("Zona A", valvulaOp.getLocalizacao());
        assertEquals(65.0, valvulaOp.getAberturaPercentual(), 0.001);
        assertEquals(32000, valvulaOp.getCiclosOperacao());
        assertEquals(Valvula.Estado.PARCIALMENTE_ABERTA, valvulaOp.getEstado());
        assertEquals("R-404A", valvulaOp.getTipoFluido());
        assertEquals(4.5, valvulaOp.getPressaoEntradaBar(), 0.001);
    }

    @Nested @DisplayName("Enum Estado")
    class EnumEstado {
        @Test void aberta() { assertEquals("Aberta", Valvula.Estado.ABERTA.getDescricao()); }
        @Test void fechada() { assertEquals("Fechada", Valvula.Estado.FECHADA.getDescricao()); }
        @Test void parcial() { assertEquals("Parcialmente Aberta", Valvula.Estado.PARCIALMENTE_ABERTA.getDescricao()); }
        @Test void defeito() { assertEquals("Defeito Detectado", Valvula.Estado.DEFEITO.getDescricao()); }
        @Test void total() { assertEquals(4, Valvula.Estado.values().length); }
    }

    @Nested @DisplayName("Desgaste")
    class Desgaste {
        @Test void semDesgaste() { assertFalse(valvulaOp.apresentaDesgaste(50000)); }
        @Test void comDesgaste() { assertTrue(valvulaDesg.apresentaDesgaste(50000)); }
        @Test void noLimite() {
            Valvula v = new Valvula("X", "T", 50.0, 50000, Valvula.Estado.ABERTA, "R-410A", 3.0);
            assertTrue(v.apresentaDesgaste(50000));
        }
    }

    @Nested @DisplayName("Operacional")
    class Operacional {
        @Test void opTrue() { assertTrue(valvulaOp.isOperacional()); }
        @Test void fechadaOp() { assertTrue(valvulaDesg.isOperacional()); }
        @Test void defeitoNaoOp() { assertFalse(valvulaDef.isOperacional()); }
    }

    @Test @DisplayName("Double-dispatch: aceitar invoca visitar(Valvula)")
    void doubleDispatch() {
        final boolean[] ok = {false};
        ComponenteVisitor mock = new ComponenteVisitor() {
            public void visitar(Sensor s) { fail("Nao deveria"); }
            public void visitar(Compressor c) { fail("Nao deveria"); }
            public void visitar(Valvula v) { ok[0] = true; assertEquals("VALV-001", v.getId()); }
        };
        valvulaOp.aceitar(mock);
        assertTrue(ok[0]);
    }

    @Test @DisplayName("toString contem informacoes")
    void toStringTest() {
        String r = valvulaOp.toString();
        assertTrue(r.contains("VALV-001"));
        assertTrue(r.contains("65"));
    }
}
