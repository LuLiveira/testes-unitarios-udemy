package br.ce.wcaquino.matchers;

import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Date;

public class MeuMatcherProprio extends TypeSafeMatcher<Date> {

    private final Integer dia;

    public MeuMatcherProprio(Integer dia){
        this.dia = dia;
    }

    @Override
    protected boolean matchesSafely(Date data) {
        return DataUtils.isMesmaData(data, DataUtils.obterDataComDiferencaDias(dia));
    }

    @Override
    public void describeTo(Description description) {

    }
}
