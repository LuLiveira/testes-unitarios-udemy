package br.ce.wcaquino.servicos;


import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;
import org.hamcrest.CoreMatchers;
import org.junit.*;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static br.ce.wcaquino.matchers.MatchersProprios.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocacaoServiceTest {

    @Rule
    public ErrorCollector error = new ErrorCollector();

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @InjectMocks
    public LocacaoService service;

    @Mock
    public SPCService spcService;

    @Mock
    public LocacaoDao locacaoDao;

    @Mock
    public EmailService emailService;

    @Before
    public void init() {
        service = new LocacaoService();

        MockitoAnnotations.openMocks(this);
//        locacaoDao = Mockito.mock(LocacaoDao.class);
//        spcService = Mockito.mock(SPCService.class);
//        emailService = Mockito.mock(EmailService.class);

//        service.setDao(locacaoDao);
//        service.setSpcService(spcService);
//        service.setEmailService(emailService);
    }

    @Test
    public void deveTratarErroNoSPC() throws Exception {
        //cenario
        Usuario usuario = Usuario.builder().nome("Usuario").build();
        List<Filme> filmes = List.of(
                Filme.builder().precoLocacao(4.0).estoque(2).nome("Filme 1").build()
        );

        Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha"));

        //verificacao
        exception.expect(LocadoraException.class);
        exception.expectMessage("Problemas com SPC, tente novamente");

        //acao
        service.alugarFilme(usuario, filmes);
    }

    @Test
    public void deveEnviarEmailParaLocacoesAtrasadas(){
        //cenario
        Usuario usuario = Usuario.builder().nome("Joao").build();
        Usuario usuario2 = Usuario.builder().nome("Lucas").build();
        List<Locacao> locacoes = List.of(
                Locacao.builder()
                        .usuario(usuario).dataRetorno(DataUtils.obterDataComDiferencaDias(-2)).build(),
                Locacao.builder()
                        .usuario(usuario2).dataRetorno(DataUtils.obterDataComDiferencaDias(1)).build());

        Mockito.when(locacaoDao.obterLocacoesPendentes()).thenReturn(locacoes);

        //acao
        service.notificarAtrasos();

        //verificacao
        Mockito.verify(emailService, Mockito.times(1)).notificarAtraso(Mockito.any(Usuario.class));
        Mockito.verify(emailService, Mockito.times(1)).notificarAtraso(usuario);
        Mockito.verify(emailService, Mockito.never()).notificarAtraso(usuario2);
        Mockito.verifyNoMoreInteractions(emailService);
    }

    @Test
    public void naoDeveAlugarFilmeParaUsuarioNegativadoSPC() throws Exception {
        //cenario
        Usuario usuario = Usuario.builder().build();
        List<Filme> filmes = List.of(Filme.builder().estoque(2).nome("Filme 1").precoLocacao(4.0).build());

        Mockito.when(spcService.possuiNegativacao(usuario)).thenReturn(true);

        //acao
        try {
            service.alugarFilme(usuario, filmes);
            Assert.fail();
        } catch (LocadoraException e) {
            Assert.assertThat(e.getMessage(), is("Usuário Negativado"));
        }

        //verificacao
        Mockito.verify(spcService).possuiNegativacao(usuario);
    }

    @Test
    public void deveAlugarFilme() throws Exception {
        Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1", 2, 5.0);

        //acao
        Locacao locacao = service.alugarFilme(usuario, List.of(filme));

        //verificacao
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(locacao.getDataLocacao(), ehHoje());
        error.checkThat(locacao.getDataRetorno(), ehHojeComDiferencaDias(1));
    }

    @Test(expected = FilmeSemEstoqueException.class)
    public void testeLocacao_filmeSemEsstoque() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1", 0, 5.0);

        //acao
        service.alugarFilme(usuario, List.of(filme));
    }

    @Test
    public void testeLocacao_usuarioVazio() throws FilmeSemEstoqueException {
        //cenario
        LocacaoService service = new LocacaoService();
        Filme filme = new Filme("Filme 1", 1, 5.0);

        //acao
        try {
            service.alugarFilme(null, List.of(filme));
            Assert.fail();
        } catch (LocadoraException e) {
            assertThat(e.getMessage(), CoreMatchers.is("Usuário vázio"));
        }
    }

    @Test
    public void testeLocacao_filmeVazio() throws FilmeSemEstoqueException, LocadoraException {

        //cenario
        LocacaoService service = new LocacaoService();
        Usuario usuario = new Usuario("Usuario 1");

        exception.expectMessage("Filme vázio");
        exception.expect(LocadoraException.class);

        //acao
        service.alugarFilme(usuario, null);
    }

    @Test
    public void devePagar75pctNoFilme3() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario();
        List<Filme> filmes = List.of(new Filme("Filme 1", 2, 4.0), new Filme("Filme 2", 2, 4.0), new Filme("Filme 3", 2, 4.0));

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(locacao.getValor(), CoreMatchers.is(11.0));
    }

    @Test
    public void devePagar50pctNoFilme4() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario();
        List<Filme> filmes = List.of(new Filme("Filme 1", 2, 4.0),
                new Filme("Filme 1", 2, 4.0),
                new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 3", 2, 4.0));

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(locacao.getValor(), CoreMatchers.is(13.0));
    }

    @Test
    public void devePagar25pctNoFilme5() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario();
        List<Filme> filmes = List.of(new Filme("Filme 1", 2, 4.0),
                new Filme("Filme 1", 2, 4.0),
                new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 3", 2, 4.0));

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(locacao.getValor(), CoreMatchers.is(14.0));
    }

    @Test
    public void devePagar0pctNoFilme6() throws FilmeSemEstoqueException, LocadoraException {
        //cenario
        Usuario usuario = new Usuario();
        List<Filme> filmes = List.of(new Filme("Filme 1", 2, 4.0),
                new Filme("Filme 1", 2, 4.0),
                new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 2", 2, 4.0),
                new Filme("Filme 3", 2, 4.0));

        //acao
        Locacao locacao = service.alugarFilme(usuario, filmes);

        //verificacao
        assertThat(locacao.getValor(), CoreMatchers.is(14.0));
    }

    @Test
    public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
        Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

        Usuario usuario = new Usuario();
        List<Filme> filmes = List.of(new Filme("ABC", 2, 4.0));

        Locacao locacao = service.alugarFilme(usuario, filmes);

//        boolean isSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);
//
//        Assert.assertTrue(isSegunda);
//        assertThat(locacao.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
//        assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
        assertThat(locacao.getDataRetorno(), caiEmUmaSegunda());
    }


//	@Test
//	public void testeLocacao_filmeSemEstoque_2(){
//		//cenario
//		LocacaoService service = new LocacaoService();
//		Usuario usuario = new Usuario("Usuario 1");
//		Filme filme = new Filme("Filme 1", 0, 5.0);
////		Filme filme = new Filme("Filme 1", 2, 5.0);
//
//		//acao
//		try {
//			service.alugarFilme(usuario, filme);
//			Assert.fail("Deveria ter lançado uma exception");
//		} catch (Exception e) {
//			Assert.assertThat(e.getMessage(), CoreMatchers.is("Filme sem estoque"));
//		}
//	}
//
//	@Test
//	public void testeLocacao_filmeSemEstoque_3() throws Exception {
//		//cenario
//		LocacaoService service = new LocacaoService();
//		Usuario usuario = new Usuario("Usuario 1");
//		Filme filme = new Filme("Filme 1", 0, 5.0);
//
//		exception.expect(Exception.class);
//		exception.expectMessage("Filme sem estoque");
//
//		//acao
//		service.alugarFilme(usuario, filme);
//
////		exception.expect(Exception.class);
////		exception.expectMessage("Filme sem estoque");
//	}
}
