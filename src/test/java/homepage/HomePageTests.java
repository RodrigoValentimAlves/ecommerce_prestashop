package homepage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import base.BaseTests;
import pages.CarrinhoPage;
import pages.CheckoutPage;
import pages.LoginPage;
import pages.ModalProdutoPage;
import pages.PedidoPage;
import pages.ProdutoPage;
import util.Funcoes;

public class HomePageTests extends BaseTests {

	@Test
	public void testcontarProdutos_oitoProdutosDiferentes() {
		carregarPaginaInicial();
		assertThat(homePage.contarProdutos(), is(8));
	}

	@Test
	public void testValidarCarrrinhoZerado_ZeroItensNoCarrinho() {
		int produtosNoCarrinho = homePage.obterQuantidadeProdutosNoCarrinho();
		assertThat(produtosNoCarrinho, is(0));
	}

	ProdutoPage produtoPage;
	String nomeProduto_ProdutoPage;

	@Test
	public void testValidarDetalhesDoProduto_DescricaoEValorIguais() {
		int indice = 0;
		String nomeProduto_HomePage = homePage.obterNomeProduto(indice);
		String precoProduto_HomePage = homePage.obterPrecoProduto(indice);

		System.out.println(nomeProduto_HomePage);
		System.out.println(precoProduto_HomePage);

		produtoPage = homePage.clicarProduto(indice);

		nomeProduto_ProdutoPage = produtoPage.obterNomeProduto();
		String precoProduto_ProdutoPage = produtoPage.obterPrecoProduto();

		System.out.println(nomeProduto_ProdutoPage);
		System.out.println(precoProduto_ProdutoPage);

		assertThat(nomeProduto_HomePage.toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));
		assertThat(precoProduto_HomePage, is(precoProduto_ProdutoPage));

	}

	LoginPage loginPage;

	@Test
	public void testLoginComSucesso_UsuarioLogado() {
		loginPage = homePage.clicarBotaoSignin();

		loginPage.preencherEmail("rodrigo.teste@teste.com");
		loginPage.preencherPassword("123456");

		loginPage.clicarBotaoSignIn();

		assertThat(homePage.estaLogado("Rodrigo Valentim"), is(true));

		carregarPaginaInicial();
	}
	@ParameterizedTest
	@CsvFileSource(resources = "/massaTeste_login.csv.csv", numLinesToSkip = 1, delimiter = ';')
	public void testLogin_UsuarioLogadoComDadosValidos(String nomeTeste, String email, String password, String nomeUsuario, String resultado) {
		loginPage = homePage.clicarBotaoSignin();

		loginPage.preencherEmail(email);
		loginPage.preencherPassword(password);

		loginPage.clicarBotaoSignIn();
		
		boolean esperado_loginOk;
		if (resultado.equals("positivo"))
			esperado_loginOk = true;
		else
			esperado_loginOk = false;

		assertThat(homePage.estaLogado(nomeUsuario), is(esperado_loginOk));
		
		capturarTela(nomeTeste, resultado);
		
		if(esperado_loginOk)
			homePage.clicarBotaoSignOut();

		carregarPaginaInicial();
	}
	
	
	

	ModalProdutoPage modalProdutoPage;

	@Test
	public void testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso() {
		String tamanhoProduto = "M";
		String corProduto = "Black";
		int quantidadeProduto = 3;

		// Pré-condição
		// Usuário logado
		if (!homePage.estaLogado("Rodrigo Valentim")) {
			testLoginComSucesso_UsuarioLogado();
		}

		// Selecionando produto
		testValidarDetalhesDoProduto_DescricaoEValorIguais();

		// Selecionar tamanho
		List<String> listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista : " + listaOpcoes.size());

		produtoPage.selecionarOpcaoDropDown(tamanhoProduto);

		listaOpcoes = produtoPage.obterOpcoesSelecionadas();
		System.out.println(listaOpcoes.get(0));
		System.out.println("Tamanho da lista : " + listaOpcoes.size());

		// Selecionando cor

		produtoPage.selecionarCorPreta();

		// Alterando Quantidade
		produtoPage.alterarQuantidade(quantidadeProduto);

		// Adicionar no carrinho
		modalProdutoPage = produtoPage.clicarBotaoAddToCart();

		// Validações
		assertTrue(modalProdutoPage.obterMensagemProdutoAdicionado()
				.endsWith("Product successfully added to your shopping cart"));

		System.out.println(modalProdutoPage.obterDescricaoProduto());

		assertThat(modalProdutoPage.obterDescricaoProduto().toUpperCase(), is(nomeProduto_ProdutoPage.toUpperCase()));

		String precoProdutoString = modalProdutoPage.obterPrecoProduto();
		precoProdutoString = precoProdutoString.replace("$", "");
		Double precoProduto = Double.parseDouble(precoProdutoString);

		assertThat(modalProdutoPage.obterTamanhoProduto(), is(tamanhoProduto));
		assertThat(modalProdutoPage.obterCorProduto(), is(corProduto));
		assertThat(modalProdutoPage.obterQuantidadeProduto(), is(Integer.toString(quantidadeProduto)));

		String subtotalString = modalProdutoPage.obterSubTotal();
		subtotalString = subtotalString.replace("$", "");
		Double subtotal = Double.parseDouble(subtotalString);

		Double subtotalCalculado = quantidadeProduto * precoProduto;

		assertThat(subtotal, is(subtotalCalculado));

	}
	//Valores esperados
	
	String esperado_nomeProduto = "Hummingbird printed t-shirt";
	Double esperado_precoProduto = 19.12;
	String esperado_tamanhoProduto = "M";
	String esperado_corProduto = "Black";
	int esperado_input_quantidadeProduto = 3;
	Double esperado_subtotalProduto = esperado_precoProduto * esperado_input_quantidadeProduto;
	
	int esperado_numeroItensTotal = esperado_input_quantidadeProduto;
	Double esperado_subTotalTotal = esperado_subtotalProduto;
	Double esperado_shippingTotal = 7.00;
	Double esperado_totalTaxExclTotal = esperado_subTotalTotal + esperado_shippingTotal;
	Double esperado_totalTaxInclTotal = esperado_totalTaxExclTotal;
	Double esperado_taxesTotal = 0.00;
	
	String esperadoNomeCliente = "Rodrigo Valentim";
	
	
	CarrinhoPage carrinhoPage;
	@Test
	public void testIrParaCarrinho_informacoesPersistidas() {
		// Pre-Condição
		testIncluirProdutoNoCarrinho_ProdutoIncluidoComSucesso();
		carrinhoPage = modalProdutoPage.clicarBotaoProceedToCheckout();

		// Teste
		// Elementos da lista
		System.out.println("***TELA DO CARRINHO***");
		System.out.println(carrinhoPage.obter_nomeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()));
		System.out.println(carrinhoPage.obter_tamanhoProduto());
		System.out.println(carrinhoPage.obter_corProduto());
		System.out.println(carrinhoPage.obter_input_quantidadeProduto());
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subTotalProduto()));

		System.out.println("*** ITENS TOTAIS ***");
		System.out.println(Funcoes.removeTextoItemDevolveInt(carrinhoPage.obter_numeroItensTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subTotalTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxInclTotal()));
		System.out.println(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()));
		
		//Asserções Hamcrest
		assertThat(carrinhoPage.obter_nomeProduto(),is(esperado_nomeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()), is(esperado_precoProduto));
		assertThat(carrinhoPage.obter_tamanhoProduto(),is(esperado_tamanhoProduto));
		assertThat(carrinhoPage.obter_corProduto(),is(esperado_corProduto));
		assertThat(Integer.parseInt(carrinhoPage.obter_input_quantidadeProduto()),is(esperado_input_quantidadeProduto));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subTotalProduto()),is(esperado_subtotalProduto));
		
		
		
		assertThat(Funcoes.removeTextoItemDevolveInt(carrinhoPage.obter_numeroItensTotal()),is(esperado_numeroItensTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subTotalTotal()),is(esperado_subTotalTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()),is(esperado_shippingTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()),is(esperado_totalTaxExclTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxInclTotal()),is(esperado_totalTaxInclTotal));
		assertThat(Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()),is(esperado_taxesTotal));
					 						
		//Asserções Junit
		/*
		assertEquals(esperado_nomeProduto, carrinhoPage.obter_nomeProduto());
		assertEquals(esperado_precoProduto, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_precoProduto()));
		assertEquals(esperado_tamanhoProduto, carrinhoPage.obter_tamanhoProduto());
		assertEquals(esperado_corProduto, carrinhoPage.obter_corProduto());
		assertEquals(esperado_input_quantidadeProduto, Integer.parseInt(carrinhoPage.obter_input_quantidadeProduto()));
		assertEquals(esperado_subtotalProduto, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subTotalProduto()));
		
		
		
		assertEquals(esperado_numeroItensTotal, Funcoes.removeTextoItemDevolveInt(carrinhoPage.obter_numeroItensTotal()));
		assertEquals(esperado_subTotalTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_subTotalTotal()));
		assertEquals(esperado_shippingTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_shippingTotal()));
		assertEquals(esperado_totalTaxExclTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxExclTotal()));
		assertEquals(esperado_totalTaxInclTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_totalTaxInclTotal()));
		assertEquals(esperado_taxesTotal, Funcoes.removeCifraoDevolveDouble(carrinhoPage.obter_taxesTotal()));
		*/
	 }
	CheckoutPage checkoutPage;
	@Test
	public void testIrParaCheckout_FreteMeioPagamentoEnderecoListadosOK() {
		//Pré-condições
		
		
		//Produto disponível no carrinho de compras
		testIrParaCarrinho_informacoesPersistidas();
		
		//Teste
		
		//Clicar no botão
		checkoutPage = carrinhoPage.clicarBotaoProceedToCheckout();
		
		//Preencher informações
		
		//Validar informações na tela
		assertThat(Funcoes.removeCifraoDevolveDouble(checkoutPage.obter_totalTaxIncTotal()), is(esperado_totalTaxInclTotal));
		//assertThat(checkoutPage.obter_nomeCliente(), is (esperadoNomeCliente));
		assertTrue(checkoutPage.obter_nomeCliente().startsWith(esperadoNomeCliente));
		
		
		checkoutPage.clicarBotaoContinueAddress();
		
		String encontrado_shippingValor = checkoutPage.obter_shippingValor();
		encontrado_shippingValor = Funcoes.removeTexto(encontrado_shippingValor, " tax excl.");
		Double encontrado_shippingValor_Double = Funcoes.removeCifraoDevolveDouble(encontrado_shippingValor);
		
		assertThat(encontrado_shippingValor_Double, is(esperado_shippingTotal));
		
		checkoutPage.clicarBotaoContinueshipping();
		
		//Selecionar opção "Pay By Check"
		checkoutPage.selecionarRadioPayByCheck();
		//Validar valor do cheque(amount)
		String encontrado_amountPayByCheck = checkoutPage.obter_amountPayByCheck();
		encontrado_amountPayByCheck = Funcoes.removeTexto(encontrado_amountPayByCheck, " (tax incl.)");
		Double encontrado_amountPayByCheck_Double = Funcoes.removeCifraoDevolveDouble(encontrado_amountPayByCheck);
		
		assertThat(encontrado_amountPayByCheck_Double, is(esperado_totalTaxInclTotal));
		// Clicar na opções "I Agree"
		checkoutPage.selecionarCheckboxIAgree();
		
		assertTrue(checkoutPage.estaSelecionadoCheckboxIAgree());	
		
	}
	
	@Test
	public void testFinalizarPedido_pedidoFinalizadoComSucesso() {
		//Pré-Condições
		//Checkout completo concluído
		testIrParaCheckout_FreteMeioPagamentoEnderecoListadosOK();
		
		//Teste
		//Clicar no botão confirmar o pedido
		PedidoPage pedidoPage =checkoutPage.clicarBotaoConfirmaPedido();
		
		
		//Validar valores da tela
		assertTrue(pedidoPage.obter_textoPedidoConfirmado().endsWith("YOUR ORDER IS CONFIRMED"));		
//		assertThat(pedidoPage.obter_textoPedidoConfirmado().toUpperCase(),is("YOUR ORDER IS CONFIRMED"));
		
		assertThat(pedidoPage.obter_email(), is("rodrigo.teste@teste.com"));
		
		assertThat(pedidoPage.obter_totalProdutos(), is(esperado_subtotalProduto));
		
		assertThat(pedidoPage.obter_totalTaxIncl(), is(esperado_totalTaxInclTotal));
		
		assertThat(pedidoPage.obter_metodoPagamento(), is("check"));
	}
	

}
