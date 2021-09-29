package br.ce.wcaquino.servicos;

import static br.ce.wcaquino.utils.DataUtils.adicionarDias;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.ce.wcaquino.dao.LocacaoDao;
import br.ce.wcaquino.entidades.Filme;
import br.ce.wcaquino.entidades.Locacao;
import br.ce.wcaquino.entidades.Usuario;
import br.ce.wcaquino.exceptions.FilmeSemEstoqueException;
import br.ce.wcaquino.exceptions.LocadoraException;
import br.ce.wcaquino.utils.DataUtils;

public class LocacaoService {

    public LocacaoDao dao;
    public SPCService spcService;
    public EmailService emailService;

    public Locacao alugarFilme(Usuario usuario, List<Filme> filmes) throws FilmeSemEstoqueException, LocadoraException {
        if (usuario == null) {
            throw new LocadoraException("Usuário vázio");
        }

        if (filmes == null || filmes.size() == 0) {
            throw new LocadoraException("Filme vázio");
        }

        for (Filme filme : filmes) {
            if (filme.getEstoque() == 0) {
                throw new FilmeSemEstoqueException();
            }
        }

        if (spcService.possuiNegativacao(usuario)) {
            throw new LocadoraException("Usuário Negativado");
        }

        Locacao locacao = new Locacao();
        locacao.setFilme(filmes);
        locacao.setUsuario(usuario);
        locacao.setDataLocacao(new Date());
//        locacao.setValor(
//                filmes.stream().map(Filme::getPrecoLocacao).reduce(0.0, (subtotal, preco) -> subtotal + preco)
//        );

        Double valorTotal = 0d;
        for (int i = 0; i < filmes.size(); i++) {
            Filme filme = filmes.get(i);
            Double valor = filme.getPrecoLocacao();

            switch (i) {
                case 2 -> valor *= 0.75;
                case 3 -> valor *= 0.50;
                case 4 -> valor *= 0.25;
                case 5 -> valor *= 0.0;
            }

            valorTotal += valor;
        }

        locacao.setValor(valorTotal);
        //Entrega no dia seguinte
        Date dataEntrega = new Date();
        dataEntrega = adicionarDias(dataEntrega, 1);

        if (DataUtils.verificarDiaSemana(dataEntrega, Calendar.SUNDAY)) {
            dataEntrega = adicionarDias(dataEntrega, 1);
        }

        locacao.setDataRetorno(dataEntrega);

        dao.salvar(locacao);

        return locacao;
    }

    public void notificarAtrasos(){
        List<Locacao> locacoes = dao.obterLocacoesPendentes();
        locacoes.forEach(locacao -> {
            if(locacao.getDataRetorno().before(new Date())){
                emailService.notificarAtraso(locacao.getUsuario());
            }
        });
//        locacoes.forEach(locacao -> emailService.notificarAtraso(locacao.getUsuario()));
    }

    public void setDao(LocacaoDao dao) {
        this.dao = dao;
    }

    public void setSpcService(SPCService spcService) {
        this.spcService = spcService;
    }

    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}