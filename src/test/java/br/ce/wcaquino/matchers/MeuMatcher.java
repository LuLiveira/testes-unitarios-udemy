package br.ce.wcaquino.matchers;

public class MeuMatcher {
    public static MeuMatcherProprio ehHojeComDiferencaDias(int dia) {
        return new MeuMatcherProprio(dia);
    }

    public static MeuSegundoMatcherProprio ehHoje() {
        return new MeuSegundoMatcherProprio();
    }
}
