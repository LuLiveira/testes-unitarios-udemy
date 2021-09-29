package br.ce.wcaquino.servicos;

import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;

@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

    @InjectMocks
    public LocacaoService service;

    @Parameter
    public List<Filme> filmes;

    @Parameter(value = 1)
    public Double valorLocacao;

    @Parameter(value = 2)
    public String cenario;

    @Mock
    public LocacaoDao dao;

    @Mock
    public SPCService spcService;

    @Before
    public void setup() {
        service = new LocacaoService();
        MockitoAnnotations.openMocks(this);
//        LocacaoDao dao = Mockito.mock(LocacaoDao.class);
//        SPCService spcService = Mockito.mock(SPCService.class);
//        service.setDao(dao);
//        service.setSpcService(spcService);
    }

    @Parameters(name = "Teste {index} = {2}")
    public static List<Object[]> getParametros() {
        return List.of(new Object[][]{
                {
                        List.of(new Filme("Filme 1", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0)), 11.0, "3 Filmes: 25%"
                },
                {
                        List.of(new Filme("Filme 1", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0)), 13.0, "4 Filmes: 50%"
                },
                {
                        List.of(new Filme("Filme 1", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0)), 14.0, "5 Filmes: 75%"
                },
                {
                        List.of(new Filme("Filme 1", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0),
                                new Filme("Filme 2", 2, 4.0)), 14.0, "6 Filmes: 100%"
                }
        });
    };

    @Test
    public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {

        //cenario
        Usuario usuario = new Usuario();

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        Assert.assertThat(locacao.getValor(), CoreMatchers.is(valorLocacao));
    }
}
