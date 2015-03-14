package br.com.boleto;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.Days;

/**Classe para constru��o do c�digo de barras para o banco Caixa
 * @author Jo�o Victor B. Magalh�es
 * @version 1.02
 * @since Release 1.02 da aplica��o
 */
public class CBCaixa implements CodigoDeBarras {
	private static final int TIPO_DE_COBRANCA = 2; //1 - COM REGISTRO, 2 - SEM REGISTRO
	private static final int ID_EMISSOR_BOLETO = 4; //4 - BENEFICI�RIO
	private static final int ID_BANCO = 104;
	private static final int TIPO_MOEDA = 9;
	private Pagamento pagamento;
	private Beneficiario beneficiario;
	private Sistema sistema;
	private String codigoDeBarras;
	private String linhaDigitavel;
	
	/**Construtor com o m�nimo de informa��es para a constru��o do c�digo de barras*/
	public CBCaixa(Pagamento pagamento, Beneficiario beneficiario, Sistema sistema) {
		super();
		this.pagamento = pagamento;
		this.beneficiario = beneficiario;
		this.sistema = sistema;
		buildCodigoDeBarras();
		buildLinhaDigitavel();
	}
	/**@see CodigoDeBarras#buildCodigoDeBarras()*/
	@Override
	public void buildCodigoDeBarras() {
		StringBuilder valor = new StringBuilder()
		.append(ID_BANCO)
		.append(TIPO_MOEDA)
		.append(getDVGeral())
		.append(getFatorVencimento())
		.append(getCampoValor())
		.append(getBeneficiarioCodigoCedente())
		.append(getDVBeneficiario())
		.append(getSequencia1())
		.append(TIPO_DE_COBRANCA)
		.append(getSequencia2())
		.append(ID_EMISSOR_BOLETO)
		.append(getSequencia3())
		.append(getDVCampoLivre());
		this.codigoDeBarras = valor.toString();
	}
	/**@see CodigoDeBarras#buildLinhaDigitavel()*/
	@Override
	public void buildLinhaDigitavel() {
		String cb = this.codigoDeBarras;
		StringBuilder campo1 = new StringBuilder();
		campo1.append(ID_BANCO).append(TIPO_MOEDA).append(cb.substring(19, 24));
			//CAMPO 1
		StringBuilder valor = new StringBuilder()
			.append(campo1)
			.append(getDVModulo10(campo1.toString()))
			//CAMPO 2
			.append(cb.substring(24, 34))
			.append(getDVModulo10(cb.substring(24, 34)))
			//CAMPO 3
			.append(cb.substring(34, 44))
			.append(getDVModulo10(cb.substring(34, 44)))
			//CAMPO 4
			.append(getDVGeral())
			//CAMPO 5
			.append(getFatorVencimento())
			.append(getCampoValor());
		this.linhaDigitavel = valor.toString();
	}
	/**@see CodigoDeBarras#getCodigoDeBarras()*/
	@Override
	public String getCodigoDeBarras() {
		return this.codigoDeBarras;
	}
	/**@see CodigoDeBarras#getLinhaDigitavel()*/
	@Override
	public String getLinhaDigitavel(){
		return this.linhaDigitavel;
	}
	/**@see CodigoDeBarras#isValido()*/
	@Override
	public boolean isValido(){
		if(this.linhaDigitavel == null){
			return false;
		}
		else if(this.linhaDigitavel.length() != 47){
			return false;
		}
		if(this.codigoDeBarras == null){
			return false;
		}
		else if(this.codigoDeBarras.length() != 44){
			return false;
		}
		return true;
	}
	/**M�todo para gerar o D�gito Verificador Geral utilizando o {@link CBCaixa#getDVModulo11SemZero(String)}
	 * @return String - Valor D�gito Verificador Geral*/
	public String getDVGeral(){
		StringBuilder valor = new StringBuilder()
		.append(ID_BANCO) // padr�o
		.append(TIPO_MOEDA) // 9 = real
		.append(getFatorVencimento())
		.append(getCampoValor())
		.append(this.beneficiario.getCodigoCedente())
		.append(getDVBeneficiario())
		.append(getSequencia1())
		.append(TIPO_DE_COBRANCA)
		.append(getSequencia2())
		.append(ID_EMISSOR_BOLETO)
		.append(getSequencia3())
		.append(getDVCampoLivre());
		return getDVModulo11SemZero(valor.toString());
	}
	/**M�todo para gerar o D�gito Verificador do Benefici�rio utilizando o {@link CBCaixa#getDVModulo11(String)}
	 * @return String - Valor D�gito Verificador do Benefici�rio*/
	public String getDVBeneficiario(){
		StringBuilder valor = new StringBuilder().append(getBeneficiarioCodigoCedente());
		return getDVModulo11(valor.toString());
	}
	/**M�todo para gerar o D�gito Verificador do Campo Livre utilizando o {@link CBCaixa#getDVModulo11(String)}
	 * @return String - Valor D�gito Verificador do Campo Livre*/
	public String getDVCampoLivre(){
		StringBuilder valor = new StringBuilder()
				.append(this.beneficiario.getCodigoCedente())
				.append(getDVBeneficiario())
				.append(getSequencia1())
				.append(TIPO_DE_COBRANCA)
				.append(getSequencia2())
				.append(ID_EMISSOR_BOLETO)
				.append(getSequencia3());
		return getDVModulo11(valor.toString());
	}
	/**M�todo para obter o fator do vencimento utilizando como data base 07.10.1997, 
	 * calculando o n�mero de dias entre essa data e a do vencimento, obtendo-se o valor m�nimo 1000*/
	public int getFatorVencimento() {
		DateTime constante = new DateTime(2000, 07, 03, 0, 0);
		DateTime vencimento = new DateTime(this.pagamento.getDataVencimento());
		int diferenca = Days.daysBetween(constante, vencimento).getDays();

		if ((1000 + diferenca) >= 9999) {
			return 9999;
		}else if ((1000 + diferenca) < 1000) {
			return 1000;
		}else{
			return 1000 + (int) (diferenca);
		}
	}
	/**M�todo para obter o valor filtrado com 10 d�gitos*/
	public String getCampoValor() {
		StringBuilder valor = new StringBuilder();
		String valorPagamento = String.valueOf(getPagamentoValor());
		if(valorPagamento.length() > 10){
			throw new IllegalArgumentException("Valor do pagamento muito grande");
		}
		valor.append(valorPagamento);
		if (valor.length() != 10) {
			while (valor.length() <= 10) {
				valor.insert(0, "0");
			}
		}
		return valor.toString().replaceAll("\\D+","");
	}
	/**M�todo para obter os 3 primeiros d�gitos do Nosso Numero do Sistema
	 * @return String - 3 primeiros d�gitos do nosso numero*/
	private String getSequencia1() {
		return getSistemaNossoNumero().substring(0, 3);
	}
	/**M�todo para obter do 3� ao 6� d�gito do Nosso Numero do Sistema
	 * @return String - 3� ao 6� d�gito do nosso numero*/
	private String getSequencia2() {
		return getSistemaNossoNumero().substring(3, 6);
	}
	/**M�todo para obter os 9 �ltimos d�gitos do Nosso Numero do Sistema
	 * @return String - 9 �ltimos d�gitos do nosso numero*/
	private String getSequencia3() {
		return getSistemaNossoNumero().substring(6, 15);
	}
	/**M�todo para obter o m�dulo 11.<br> 
	 * 1 - o primeiro d�gito da direita para a esquerda ser� multiplicado por 2, 
	 * o segundo por 3 e assim sucessivamente at� o 9<br>
	 * 2 - Somar o resultado da multiplica��o. <br>
	 * 3 - Dividir o Total da Soma por 11.<br>
	 * 4 - O Resto da divis�o deve ser subtra�do de 11.<br>
	 * Nota: Se o RESULTADO for maior que 9 (nove) o DV ser� 0 (zero), caso contr�rio o RESULTADO ser� o DV.
	 * @return String - Valor do m�dulo 11
	 * */
	private static String getDVModulo11(String valor){
		StringBuilder valorFiltrado = new StringBuilder().append(valor);
		int indiceMultiplicacao = 2;
		int soma = 0;
		//1� PASSO
		for (int i = valorFiltrado.length()-1; i > -1; i--) {
			int numero = Character.getNumericValue(valorFiltrado.toString().charAt(i));
			int multiplicacao = (numero * indiceMultiplicacao);
			//2� PASSO
			soma = soma + multiplicacao;
			if(indiceMultiplicacao < 9) {
				indiceMultiplicacao++;
			}else{
				indiceMultiplicacao = 2;
			}
		}
		//3� PASSO
		int resultado = soma % 11;
		//4� PASSO
		resultado = 11 - resultado;
		if(resultado > 9){
			return String.valueOf(0);
		}else{
			return String.valueOf(resultado);
		}
	}
	/**M�todo para obter o m�dulo 11, n�o admitindo-se o 0(zero) como valor.<br> 
	 * 1 - o primeiro d�gito da direita para a esquerda ser� multiplicado por 2, 
	 * o segundo por 3 e assim sucessivamente at� o 9<br>
	 * 2 - Somar o resultado da multiplica��o. <br>
	 * 3 - Dividir o Total da Soma por 11.<br>
	 * 4 - O Resto da divis�o deve ser subtra�do de 11.<br>
	 * Nota: Se o RESULTADO for maior que 9 (nove) o DV ser� 1 (um), caso contr�rio o RESULTADO ser� o DV.
	 * @return String - Valor do m�dulo 11
	 * */
	private static String getDVModulo11SemZero(String valor){
		StringBuilder valorFiltrado = new StringBuilder().append(valor);
		int indiceMultiplicacao = 2;
		int soma = 0;
		//1� PASSO
		for (int i = valorFiltrado.length()-1; i > -1; i--) {
			int numero = Character.getNumericValue(valorFiltrado.toString().charAt(i));
			int multiplicacao = (numero * indiceMultiplicacao);
			//2� PASSO
			soma = soma + multiplicacao;
			if(indiceMultiplicacao < 9) {
				indiceMultiplicacao++;
			}else{
				indiceMultiplicacao = 2;
			}
		}
		//3� PASSO
		int resultado = soma % 11;
		//4� PASSO
		resultado = 11 - resultado;
		if(resultado == 0 || resultado > 9){
			return String.valueOf(1);
		}else{
			return String.valueOf(resultado);
		}
	}
	/**M�todo para obter o m�dulo 10<br>
	 * 1 - O primeiro d�gito da direita para a esquerda ser� 
	 * multiplicado por 2,  o segundo por 1 e assim sucessivamente.<br>
	 * 2 - Somar o resultado da multiplica��o. <br>
	 * 3 - Dividir o resultado da multiplica��o por 10. <br>
	 * 4 - Subtrair o resto da divis�o de 10. <br>
	 * Nota 1: Quando o resultado da multiplica��o 
	 * for um n�mero com 2 d�gitos, somar os 2 algarismos.<br>
	 * Nota 2: Se o Total da Soma for inferior a 10, 
	 * o DV corresponde � diferen�a entre 10 e o Total da Soma.<br>
	 * Nota 3: Se o resto da divis�o for 0 (zero), o DV ser� 0 (zero).<br>
	 * @return String - Valor do m�dulo 10
	 * */
	private static String getDVModulo10(String valor){
		StringBuilder valorFiltrado = new StringBuilder().append(valor);
		int indiceMultiplicacao = 2;
		int soma = 0;
		for (int i = valorFiltrado.length() - 1; i > -1; i--) {
			int numero = Character.getNumericValue(valorFiltrado.toString().charAt(i));
			int multiplicacao = (numero * indiceMultiplicacao);
			if(multiplicacao >= 10){
				String text = String.valueOf(multiplicacao);
				String d1 = String.valueOf(text.charAt(0));
				String d2 = String.valueOf(text.charAt(1));
				multiplicacao = Integer.valueOf(d1) + Integer.valueOf(d2);
			}
			soma = soma + multiplicacao;
			if (indiceMultiplicacao == 2){
				indiceMultiplicacao = 1;
			}else{
				indiceMultiplicacao = 2;
			}
		}
		int resultado = soma % 10;
		//Nota 3
		if(soma < 10){
			return String.valueOf(10 - soma);
		}
		else if(resultado == 0){
			return String.valueOf(0);
		}else{
			resultado = 10 - resultado;
			return String.valueOf(resultado);	
		}
	}
	/**@see Pagamento#getDataVencimento()*/
	public Date getPagamentoDataVencimento() {
		return this.pagamento.getDataVencimento();
	}
	/**@see Pagamento#getValor()*/
	public double getPagamentoValor() {
		return this.pagamento.getValor();
	}
	/**@see Beneficiario#getCodigoCedente()*/
	public String getBeneficiarioCodigoCedente() {
		return this.beneficiario.getCodigoCedente();
	}
	/**@see Sistema#getNossoNumeroValor()*/
	public String getSistemaNossoNumero(){
		return this.sistema.getNossoNumeroValor();
	}

}

