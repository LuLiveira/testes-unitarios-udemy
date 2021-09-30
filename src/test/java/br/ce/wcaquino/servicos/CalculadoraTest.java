package br.ce.wcaquino.servicos;

import br.ce.wcaquino.exceptions.NaoPodeDividirPorZeroException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

public class CalculadoraTest {

    private Calculadora calculadora;

    @Spy
    private Calculadora calculadoraSpy;

    @Before
    public void setup(){
        calculadora = new Calculadora();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testeSpy(){
        Mockito.when(calculadoraSpy.somar(1, 2)).thenReturn(8);
        System.out.println(calculadora.somar(1,5));
    }

    @Test
    public void deveSomarDoisValores(){
        //cenario
        int a = 5;
        int b = 3;

        //acao
        int resultado = calculadora.somar(a,b);

        //verificacao
        Assert.assertEquals(8, resultado);
    }

    @Test
    public void deveSubtrairDoisValores(){
        //cenario
        int a = 8;
        int b = 5;

        //acao
        int resultado = calculadora.subtrair(a, b);

        //verificacao
        Assert.assertEquals(3, resultado);
    }

    @Test
    public void deveDividirDoisValores() throws NaoPodeDividirPorZeroException {
        //cenario
        int a = 6;
        int b = 3;

        //acao
        int resultado = calculadora.dividir(a,b);

        //verficacao
        Assert.assertEquals(2, resultado);
    }

    @Test(expected = NaoPodeDividirPorZeroException.class)
    public void deveLancarExcecaoAoDividirPorZero() throws NaoPodeDividirPorZeroException {
        //cenario
        int a = 10;
        int b = 0;

        //acao
        calculadora.dividir(a, b);
    }
}
