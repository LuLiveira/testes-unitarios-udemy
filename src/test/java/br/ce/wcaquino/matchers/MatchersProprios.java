package br.ce.wcaquino.matchers;

import java.util.Calendar;

public class MatchersProprios {

    public static DiaSemanaMatcher caiEm(Integer diaSemana){
        return new DiaSemanaMatcher(diaSemana);
    }

    public static DiaSemanaMatcher caiEmUmaSegunda(){
        return new DiaSemanaMatcher(Calendar.MONDAY);
    }

    public static MeuMatcherProprio ehHoje(){
        return new MeuMatcherProprio(0);
    }

    public static MeuMatcherProprio ehHojeComDiferencaDias(Integer dias){
        return new MeuMatcherProprio(dias);
    }
}
