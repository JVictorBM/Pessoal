package br.com.boleto;
/**Interface para Codigo de Barras
 * @author Jo�o Victor B. Magalh�es
 * @version 1.02
 * @since Release 1.02 da aplica��o
 */
public interface CodigoDeBarras {
	/**M�todo para gerar o c�digo de barras atrav�s de diversos calculos*/
	public void buildCodigoDeBarras();
	/**M�todo para gerar a linha digit�vel atrav�s de diversos calculos*/
	public void buildLinhaDigitavel();
	/**M�todo para obter o c�digo de barras atrav�s de diversos calculos
	 * @return String - Valor do c�digo de barras
	 * */
	public String getCodigoDeBarras();
	/**M�todo para obter a linha digit�vel do c�digo de barras
	 * @return String - Valor da linha digit�vel
	 * */
	public String getLinhaDigitavel();
	/**M�todo para verifica��o dos argumentos que comp�em o c�digo de barras e a linha digit�vel
	 * @return Boolean - True, caso v�lido, False, caso for v�lido
	 * */
	public boolean isValido();
}
