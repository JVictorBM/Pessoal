package br.com.boleto;
/**Interface para Codigo de Barras
 * @author João Victor B. Magalhães
 * @version 1.02
 * @since Release 1.02 da aplicação
 */
public interface CodigoDeBarras {
	/**Método para gerar o código de barras através de diversos calculos*/
	public void buildCodigoDeBarras();
	/**Método para gerar a linha digitável através de diversos calculos*/
	public void buildLinhaDigitavel();
	/**Método para obter o código de barras através de diversos calculos
	 * @return String - Valor do código de barras
	 * */
	public String getCodigoDeBarras();
	/**Método para obter a linha digitável do código de barras
	 * @return String - Valor da linha digitável
	 * */
	public String getLinhaDigitavel();
	/**Método para verificação dos argumentos que compõem o código de barras e a linha digitável
	 * @return Boolean - True, caso válido, False, caso for válido
	 * */
	public boolean isValido();
}
