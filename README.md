# Referência

https://www.udemy.com/course/testes-unitarios-em-java/

## Aula 9-10 - jUnit
### Tratando exceções:

- Exemplo de uso do ```Assert.assertThat``` para verificar uma exception esperada

```java
@Test
public void testeLocacao_usuarioVazio() throws FilmeSemEstoqueException {
    LocacaoService service = new LocacaoService();
    Filme filme = new Filme("Filme 1", 1, 5.0);
    try {
        service.alugarFilme(null, filme);
        Assert.fail();
    } catch (LocadoraException e) {
        Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuário vázio"));
    }
}
```

Usamos a classe CoreMatchers para passarmos o valor esperado<br>

**Obs: devemos passar o ```Assert.fail()``` para que o teste não passe
se caso a exceção não for lançada**

- Exemplo de uso do ```@Test(expected = Exception.class)``` para verificar 
uma exception esperada

```java
@Test(expected = FilmeSemEstoqueException.class)
public void testeLocacao_filmeSemEsstoque() throws FilmeSemEstoqueException, LocadoraException {
    LocacaoService service = new LocacaoService();
    Usuario usuario = new Usuario("Usuario 1");
    Filme filme = new Filme("Filme 1", 0, 5.0);

    service.alugarFilme(usuario, filme);
}
```

Usamos a própria anotação para informar ao teste que esperamos um
exceção.<br>

**Obs: É interessante de se usar quando queremos validar apenas a exceção**

- Exemplo de uso da  ```@Role ExpectedException``` para verificar uma exception

```java
@Rule
public ExpectedException exception = ExpectedException.none();

@Test
public void testeLocacao_filmeVazio() throws FilmeSemEstoqueException, LocadoraException {

    LocacaoServ*ice service = new LocacaoService();
    Usuario usuario = new Usuario("Usuario 1");

    exception.expectMessage("Filme vázio");
    exception.expect(LocadoraException.class);

    service.alugarFilme(usuario, null);
}
```

Usamos o atributo para falarmos a exceção e informações da mesma que
são esperadas.

## Aula 11 - jUnit
### Before e After:

- Exemplo de uso do ```@Before```

```java
public LocacaoService service;
@Before
public void init(){
    service = new LocacaoService();
}
```

Usamos quando queremos inicializar variaveis antes de cada teste

**Obs: O ```@Before``` é executado sempre antes de cada método de teste**

- Exemplo de uso do ```@BeforeClass```

```java
public static LocacaoService service;
@BeforeClass
public static void init(){
    service = new LocacaoService();
}
```
Usamos quando queremos inicializar variaveis que terão seu valores
mantidos durante toda a bateria de testes.

**Obs: O método com anotação ```@BeforeClass``` deve ser static**





