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








