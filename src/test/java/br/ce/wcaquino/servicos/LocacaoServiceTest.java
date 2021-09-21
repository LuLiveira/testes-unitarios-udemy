package br.ce.wcaquino.servicos;


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

import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class LocacaoServiceTest {

    @Rule
    public ErrorCollector error = new ErrorCollector();

	@Rule
	public ExpectedException exception = ExpectedException.none();

    public LocacaoService service;

    @Before
    public void init(){
        service = new LocacaoService();
    }

    @Test
    public void testeLocacao() throws Exception {
        //cenario
        Usuario usuario = new Usuario("Usuario 1");
        Filme filme = new Filme("Filme 1", 2, 5.0);

        //acao
        Locacao locacao = service.alugarFilme(usuario, List.of(filme));

        //verificacao
        error.checkThat(locacao.getValor(), is(equalTo(5.0)));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), is(true));
        error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), is(true));
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
            Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuário vázio"));
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
