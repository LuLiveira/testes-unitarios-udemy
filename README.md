# Referência

https://www.udemy.com/course/testes-unitarios-em-java/

## **Aula 9-10 - jUnit**
### **Tratando exceções:**

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

## **Aula 11 - jUnit**
### **Before e After:**

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

**Obs: Também existem as anotações ```@After``` e ```@AfterClass```
que seguem as mesmas regras do **Bofore** porém são executadas
no término dos testes.**

## **Aula 12 - jUnit**
### **Ordem de execução dos teste**

- Exemplo de uso do ```@FixMethodOrder``` para garantir a ordem dos testes.

```java
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OrdemTeste {
}
```

Usamos a anotação ```FixMethodOrder``` em cima do nome da classe acompanhada
dos valores fornecidos pela classe ``MethodSorters`` para determinarmos
algumas formas de ordenação dos nossos testes.

**Obs: No exemplo estamos ordenando de forma alfabética**-

## **Aula 14-15-16 - jUnit**
### **TDD (Test Driven Development)**

- A técnica consiste em criar primeiro o teste e em seguida fazer ele
 passar.

  - Primeiro o teste *(vai falhar)*:  
```java
@Test
public void deveSomarDoisValores(){
    int a = 5;
    int b = 3;
    Calculadora calculadora = new Calculadora();

    //acao
    int resultado = calculadora.somar(a,b);

    //verificacao
    Assert.assertEquals(8, resultado);
}
```
- Segundo o código:
```java
public class Calculadora {
    public int somar(int a, int b) {
        return a+b;
    }
}
```

## **Aula 17 - jUnit**
### **@Ignore e Assumptions**

- Exemplo do uso do ``@Ignore``

```java
@Ignore
@Test
public void teste(){

}
```

Utilizando o ``@Ignore`` o jUnit ao executar os testes ira mostrar todos que foram
executados e todos que foram ignorados/passados o que é melhor do que remover a anotação
``@Test`` que faria o código nem ser visto pelo jUnit podendo ser esquecido e virar código morto.

- Exemplo de `Assumptions`

```java
@Test
public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
    Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

    Usuario usuario = new Usuario();
    List<Filme> filmes = List.of(new Filme("ABC", 2, 4.0));

    Locacao locacao = service.alugarFilme(usuario, filmes);

    boolean isSegunda = DataUtils.verificarDiaSemana(locacao.getDataRetorno(), Calendar.MONDAY);

    Assert.assertTrue(isSegunda);
}
```

O `Assume.assumeTrue` ira garantir que o nosso teste seja executado apenas quando o retorno
for ***verdadeiro*** dessa forma, no teste de exemplo sempre que não for sabádo o teste será passado/ignorado


## **Aula 18 - jUnit**
### **Testes parametrizaveis**

- Exemplo de Data Driven Test que consiste com um unico teste validar cenários de acordo com os parametros passados

```java
@RunWith(Parameterized.class)
public class CalculoValorLocacaoTest {

 @Parameter
 public List<Filme> filmes;

 @Parameter(value = 1)
 public Double valorLocacao;

 @Parameters(name = "Teste {index} = {0} - {1}")
 public static Collection<Object[]> getParametros() {
  return List.of(new Object[][]{
          {
                  List.of(new Filme("Filme 1", 2, 4.0),
                          new Filme("Filme 2", 2, 4.0),
                          new Filme("Filme 2", 2, 4.0)), 11.0
          }
  });
 }

 @Test
 public void deveCalcularValorLocacaoConsiderandoDescontos() throws FilmeSemEstoqueException, LocadoraException {

  Usuario usuario = new Usuario();

  Locacao locacao = service.alugarFilme(usuario, filmes);

  Assert.assertThat(locacao.getValor(), CoreMatchers.is(valorLocacao));
 }
}
```

Dessa forma para o teste serão usados os parametros e caso existam mais de um serão executados o numero de testes correspondente ao numero de parametros

Obs: O primeiro parametro não necessita de value pois ele tem como valor o 0 (primeiro item da lista).

## **Aula 19 - jUnit**
### **Matchers Próprios**

- Exemplo de Matcher próprio

```java
public class DiaSemanaMatcher extends TypeSafeMatcher<Date> {
 private Integer diaSemana;

 public DiaSemanaMatcher(Integer diaSemana) {
     this.diaSemana = diaSemana;
 }

 @Override
 protected boolean matchesSafely(Date data) {
    return DataUtils.verificarDiaSemana(data, diaSemana);
 }

 @Override
 public void describeTo(Description description) {}
}
```

- Exemplo de uso do Matcher
```java
@Test
public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException {
    Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));

    Usuario usuario = new Usuario();
    List<Filme> filmes = List.of(new Filme("ABC", 2, 4.0));

    Locacao locacao = service.alugarFilme(usuario, filmes);

    assertThat(locacao.getDataRetorno(), new DiaSemanaMatcher(Calendar.MONDAY));
}
```

- Melhorando a legibilidade com um metodo `static`
```java
public class MatchersProprios {
 public static DiaSemanaMatcher caiEm(Integer diaSemana){
     return new DiaSemanaMatcher(diaSemana);
 }
}
```

- O resultado final ficaria
````java
assertThat(locacao.getDataRetorno(), MatchersProprios.caiEm(Calendar.MONDAY));
assertThat(locacao.getDataRetorno(), caiEm(Calendar.MONDAY));
````

Obs: O uso do matcher proprio se faz necessário quando queremos validar um regra
muito especifica do nosso código e queremos que a validação fique tenha uma leitura clara. 

- Melhorando a mensagem de erro do matcher próprio

````java
@Override
public void describeTo(Description description) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.DAY_OF_WEEK, diaSemana);
    String dataExtenso = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, new Locale("pt", "BR"));
    description.appendText(dataExtenso);
}
````

Obs: Basta colocar a mensagem de erro que queremos no método describeTo da classe `DiaSemanaMatcher`

## **Aula 21 - jUnit**
### **Suite de testes**

- Exemplo de suite de teste

````java
@RunWith(Suite.class)
@SuiteClasses({
        CalculadoraTest.class,
        CalculoValorLocacaoTest.class,
        LocacaoServiceTest.class
})
public class SuiteExecucao {

 @BeforeClass
 public void setup(){
  System.out.println("Também conseguimos usar @Before e @After");
 }
}
````

Obs: Utilizamos a classe com as anotações `@RunWith(Suite.class)` e `@SuiteClasses({})` e informamos ao `junit` quais são as classes de testes que queremos que sejam executadas.
É possível também utilizarmos as anotações `@Before` e `@After` para executarmos código antes e depois da bateria de testes


## **Aula 27 - Mocks**
### **Mockito**

- Exemplo de uso do mockito para mockar uma classa

````java
LocacaoDao dao = Mockito.mock(LocacaoDao.class);
````

Obs: Dessa forma criamos um objeto "falso" para que nossos testes possam ser executados sem que aja a necessidade de chamar a dependencia externa do mesmo.

## **Aula 28 - Mocks**
### **Gravando Expectativas**

- Exemplo de alteração de comportamento utilizando mockito.

````java
Mockito.when(spcService.possuiNegativacao(usuario)).thenReturn(true);
````

## **Aula 29 - Mocks**
### **Verificando Comportamentos**

- Exemplo de verificação de comportamento com Mockito

````java
Mockito.verify(emailService).notificarAtraso(usuario);
````

Obs: dessa forma conseguimos garantir que o método `notificar` do `emailService` foi chamado

- Exemplo de como garantir que o método **não** foi chamado para um usuário

```java
Mockito.verify(emailService, Mockito.never()).notificarAtraso(usuario2);
```

Obs: O `Mockito.never()` garante que o método **não** foi chamado para aquele *usuario2*

- Exemplo de como garantir que o método **não** foi chamado mais nenhuma vez

````java
Mockito.verifyNoMoreInteractions(emailService);
````

Obs: dessa forma garantimos que não foi chamado o emailService nenhuma outra vez

- Exemplo de verificação de quantos email foram enviados

````java
Mockito.verify(emailService, Mockito.times(1)).notificarAtraso(Mockito.any(Usuario.class));
````

## **Aula 31 - Mocks**
### **Anotações**

- Anotação para que um objecto seja um ``mock``

```java

@Mock
public SPCService spcService;
```
Obs: Dessa forma dizemos que a classe é um mock

- Anotação para indicar onde os mocks devem ser injetados

````java
@InjectMocks
public LocacaoService service;
````

Obs: Dessa forma informamos que os ``@mocks`` devem ser injetados em `locacaoService`

- Inicializar os `mocks`

````java
@Before
public void init(){
    MockitoAnnotations.openMocks(this);
}
````

Obs: Dessa forma os mocks são iniciados antes de cada teste


## **Aula 32 - Mocks**
### **Lançando ExceçÕes**

- Para verificarmos uma exceção vinda de um mock usamos o `thenThrow` do `Mockito.when`

````java
Mockito.when(spcService.possuiNegativacao(usuario)).thenThrow(new Exception("Falha"));
````