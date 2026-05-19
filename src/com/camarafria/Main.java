package com.camarafria;

import com.camarafria.hardware.ComponenteHardware;
import com.camarafria.hardware.Compressor;
import com.camarafria.hardware.Sensor;
import com.camarafria.hardware.Valvula;
import com.camarafria.visitor.OtimizacaoEnergiaVisitor;
import com.camarafria.visitor.RelatorioStatusVisitor;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe de demonstracao do Padrao Visitor aplicado ao sistema de
 * Analise Avancada e Auditoria da Camara Fria Inteligente.
 *
 * <p>Cria uma estrutura com componentes de hardware (Sensores, Compressores
 * e Valvulas) e aplica dois algoritmos de visitacao distintos:</p>
 * <ol>
 *   <li>{@code RelatorioStatusVisitor} — Relatorio consolidado de status</li>
 *   <li>{@code OtimizacaoEnergiaVisitor} — Analise de eficiencia energetica</li>
 * </ol>
 *
 * @author Gustavo
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {

        // ============================================================
        // 1. MONTAGEM DA ESTRUTURA DE HARDWARE DA CAMARA FRIA
        // ============================================================

        List<ComponenteHardware> componentes = new ArrayList<>();

        // --- Sensores de Temperatura ---
        componentes.add(new Sensor(
                "SENS-001", "Zona A - Painel Norte",
                -18.5,   // leitura atual (°C)
                -15.0,   // limite operacional (°C)
                0.3,     // precisao (±°C)
                "PT100"  // tipo
        ));

        componentes.add(new Sensor(
                "SENS-002", "Zona B - Painel Sul",
                -20.1,   // leitura atual (°C) — dentro do limite
                -15.0,   // limite operacional (°C)
                0.5,     // precisao (±°C)
                "NTC"    // tipo
        ));

        componentes.add(new Sensor(
                "SENS-003", "Zona C - Teto Central",
                -12.8,   // leitura atual (°C) — ACIMA do limite (alerta!)
                -15.0,   // limite operacional (°C)
                0.2,     // precisao (±°C)
                "Termopar K"
        ));

        // --- Compressores ---
        componentes.add(new Compressor(
                "COMP-001", "Casa de Maquinas - Sala 1",
                9500.0,  // horas ligado (acima do preventivo)
                7.5,     // potencia nominal (kW)
                45200.0, // consumo acumulado (kWh)
                2.8,     // COP atual
                4200     // ciclos liga/desliga
        ));

        componentes.add(new Compressor(
                "COMP-002", "Casa de Maquinas - Sala 2",
                3200.0,  // horas ligado (OK)
                5.0,     // potencia nominal (kW)
                12800.0, // consumo acumulado (kWh)
                3.2,     // COP atual
                1800     // ciclos liga/desliga
        ));

        // --- Valvulas de Expansao ---
        componentes.add(new Valvula(
                "VALV-001", "Linha de Succao - Zona A",
                65.0,    // abertura (%)
                32000,   // ciclos de operacao
                Valvula.Estado.PARCIALMENTE_ABERTA,
                "R-404A",
                4.5      // pressao entrada (bar)
        ));

        componentes.add(new Valvula(
                "VALV-002", "Linha de Succao - Zona B",
                0.0,     // abertura (%)
                55000,   // ciclos (desgaste!)
                Valvula.Estado.FECHADA,
                "R-404A",
                4.2      // pressao entrada (bar)
        ));

        componentes.add(new Valvula(
                "VALV-003", "Linha Principal",
                0.0,
                18000,
                Valvula.Estado.DEFEITO,  // defeito detectado!
                "R-134a",
                0.0
        ));

        // ============================================================
        // 2. APLICACAO DO VISITOR: RELATORIO DE STATUS
        // ============================================================

        RelatorioStatusVisitor relatorioVisitor = new RelatorioStatusVisitor();

        System.out.println(">>> Aplicando RelatorioStatusVisitor...");
        for (ComponenteHardware componente : componentes) {
            componente.aceitar(relatorioVisitor);
        }

        System.out.println(relatorioVisitor.gerarRelatorio());

        // ============================================================
        // 3. APLICACAO DO VISITOR: OTIMIZACAO ENERGETICA
        // ============================================================

        OtimizacaoEnergiaVisitor otimizacaoVisitor = new OtimizacaoEnergiaVisitor();

        System.out.println(">>> Aplicando OtimizacaoEnergiaVisitor...");
        for (ComponenteHardware componente : componentes) {
            componente.aceitar(otimizacaoVisitor);
        }

        System.out.println(otimizacaoVisitor.gerarAnalise());

        // ============================================================
        // 4. DEMONSTRACAO DA EXTENSIBILIDADE DO PADRAO
        // ============================================================

        System.out.println("=".repeat(60));
        System.out.println("  DEMONSTRACAO DO PADRAO VISITOR");
        System.out.println("=".repeat(60));
        System.out.println();
        System.out.println("  -> Dois algoritmos distintos foram aplicados sobre a");
        System.out.println("     MESMA estrutura de componentes, SEM modificar as");
        System.out.println("     classes Sensor, Compressor ou Valvula.");
        System.out.println();
        System.out.println("  -> Para adicionar uma nova analise (ex: AuditoriaSegurancaVisitor),");
        System.out.println("     basta criar uma nova classe que implemente ComponenteVisitor.");
        System.out.println();
        System.out.println("  -> Principio Open/Closed: aberto para extensao,");
        System.out.println("     fechado para modificacao.");
        System.out.println();
        System.out.println("=".repeat(60));
    }
}
